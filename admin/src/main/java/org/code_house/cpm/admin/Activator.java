package org.code_house.cpm.admin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionUpdate;

public class Activator implements BundleActivator {

    private ConditionalPermissionAdmin cpm;

    @SuppressWarnings("unchecked")
    public void start(BundleContext context) throws Exception {
        ServiceReference reference = context.getServiceReference(ConditionalPermissionAdmin.class.getName());
        cpm = (ConditionalPermissionAdmin) context.getService(reference);

        ConditionalPermissionUpdate update = cpm.newConditionalPermissionUpdate();

        for (ConditionalPermissionInfo info : (List<ConditionalPermissionInfo>) update.getConditionalPermissionInfos()) {
            System.out.println("Existing permission " + info);
        }
        System.out.println("Removing existing permissions");
        update.getConditionalPermissionInfos().clear();

        Enumeration<URL> entries = context.getBundle().findEntries("/", "*.cond-perm", false);
        while (entries.hasMoreElements()) {
            URL url = entries.nextElement();

            StringBuilder buffer = new StringBuilder();
            String line = null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.startsWith("#")) {
                    buffer.append(line).append("\n");
                }
            }

            String condition = buffer.toString();
            System.out.println("Submiting " + url + " access rule " + condition);
            update.getConditionalPermissionInfos().add(cpm.newConditionalPermissionInfo(condition));
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
