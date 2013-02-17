package org.code_house.cpm.admin;

import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import javax.xml.bind.JAXB;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionUpdate;
import org.osgi.service.permissionadmin.PermissionInfo;

public class Activator implements BundleActivator {

    private ConditionalPermissionAdmin cpm;

    @SuppressWarnings("unchecked")
    public void start(BundleContext context) throws Exception {
        ServiceReference reference = context.getServiceReference(ConditionalPermissionAdmin.class.getName());
        cpm = (ConditionalPermissionAdmin) context.getService(reference);

        ConditionalPermissionUpdate update = cpm.newConditionalPermissionUpdate();

        List permissionInfos = update.getConditionalPermissionInfos();
        for (ConditionalPermissionInfo info : (List<ConditionalPermissionInfo>) permissionInfos) {
            System.out.println("Existing permission " + info);
        }
        System.out.println("Removing existing permissions");
        permissionInfos.clear();

        Enumeration<URL> entries = context.getBundle().findEntries("/", "*.xml", false);
        if (entries != null) {
            while (entries.hasMoreElements()) {
                URL url = entries.nextElement();
                Policies policies = JAXB.unmarshal(url, Policies.class);
                for (Policy policy : policies.getPolicy()) {
                    for (ConditionContainer container : policy.getAllowOrDeny()) {
                        ConditionInfo[] conditions = new ConditionInfo[container.getCondition().size()];
                        for (int i = 0; i < conditions.length; i++) {
                            Condition condition = container.getCondition().get(i);
                            List<String> arguments = condition.getArgument();
                            conditions[i] = new ConditionInfo(condition.getClazz(), arguments.toArray(new String[arguments.size()]));
                        }
                        PermissionInfo[] permissions = new PermissionInfo[container.getPermission().size()];
                        for (int i = 0; i < permissions.length; i++) {
                            Permission permission = container.getPermission().get(i);
                            permissions[i] = new PermissionInfo(permission.getClazz(), permission.getName(), permission.getAction());
                        }
                        String access = container instanceof Allow ? ConditionalPermissionInfo.ALLOW : ConditionalPermissionInfo.DENY;
                        String name = container.getName() != null && !container.getName().isEmpty() ? container.getName() : "entry-" + container.hashCode();
                        ConditionalPermissionInfo conditionalPermissionInfo = cpm.newConditionalPermissionInfo(name, conditions, permissions, access);
                        System.out.println("Submiting " + url.getPath() + " access rule " + conditionalPermissionInfo);
                        permissionInfos.add(conditionalPermissionInfo);
                    }
                }
            }
        }

        if (!update.commit()) {
            System.err.println("Failed to update CPM rules");
        }
    }

    public void stop(BundleContext context) throws Exception {
        // revoke all permissions
//        ConditionalPermissionUpdate update = cpm.newConditionalPermissionUpdate();
//        update.getConditionalPermissionInfos().clear();
//        update.commit();
    }

}
