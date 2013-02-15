package org.code_house.cpm.test;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;

import javax.security.auth.SubjectDomainCombiner;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;

public class ListServicesAction implements PrivilegedExceptionAction<String> {

    private BundleContext context;
    private Class<?>[] types;

    public ListServicesAction(BundleContext ctx, Class<?> ... clz) {
        context = ctx;
        types = clz;
    }

    public String run() throws Exception {
        String result = "";
        AccessControlContext ac = AccessController.getContext();
        result += "Access control context " + ac + " ";
        if (ac.getDomainCombiner() != null) {
            result += "combiner " + ac.getDomainCombiner() + " ";
            if (ac.getDomainCombiner() instanceof SubjectDomainCombiner) {
                result += " subject " + ((SubjectDomainCombiner) ac.getDomainCombiner()).getSubject();
            }
        }
        result += "\n";

        for (Class<?> type : types) {
            result += "\nServices of type " + type.getName() + "\n";
            result += "Checking access privileges for service references .. " + grantCheck(new ServicePermission(type.getName(), "get")) + "\n";
            ServiceReference[] references = context.getAllServiceReferences(type.getName(), null);
            result += "Found " + (references != null ? references.length : 0) + " references\n";
            if (references != null) {
                for (ServiceReference ref : references) {
                    result += "Exporting bundle " + ref.getBundle().getSymbolicName() + " access " + grantCheck(new ServicePermission(ref, "get")) + "\n";
                    for (String key : ref.getPropertyKeys()) {
                        result += "\t";
                        Object value = ref.getProperty(key);
                        if (value instanceof Object[]) {
                            result += key +"="+ Arrays.toString((Object[]) value);
                        } else {
                            result += key +"="+ value;
                        }
                        result += "\n";
                    }
                    result += "\n";
                }
            }
        }

        return result + "\n";
    }

    private String grantCheck(ServicePermission servicePermission) {
        try {
            AccessController.checkPermission(servicePermission);
            return "allowed";
        } catch (Exception e) {
            return "denied";
        }
    }
}
