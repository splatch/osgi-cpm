package org.code_house.cpm.test;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.http.HttpServlet;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private ServiceRegistration registration;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void start(BundleContext context) throws Exception {
        Dictionary properties = new Hashtable();
        properties.put("alias", "/login");
        properties.put("servlet-name", "login");
        System.out.println("Registering servlet /login");
        registration = context.registerService(HttpServlet.class.getName(), new LoginServlet(context), properties);
    }

    public void stop(BundleContext context) throws Exception {
        registration.unregister();
    }

}
