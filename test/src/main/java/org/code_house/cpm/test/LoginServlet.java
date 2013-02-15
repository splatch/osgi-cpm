package org.code_house.cpm.test;

import java.io.IOException;
import java.security.PrivilegedActionException;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;

public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private BundleContext context;

    public LoginServlet(BundleContext ctx) {
        context = ctx;
    }

    protected void doGet(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        try {
            LoginContext loginContext = new LoginContext("unix");

            // callback handler - not used with Unix login module
            //final String username = req.getParameter("username");
            //final String password = req.getParameter("password");
            /*, new CallbackHandler() {
                public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
                    for (Callback callback : callbacks) {
                        if (callback instanceof NameCallback) {
                            resp.getWriter().println("User name: " + username);
                            ((NameCallback) callback).setName(username);
                        } else if (callback instanceof PasswordCallback) {
                            resp.getWriter().println("Password: " + password);
                            ((PasswordCallback) callback).setPassword(password.toCharArray());
                        }
                    }
                }
            })*/;

            loginContext.login();

            ListServicesAction action = new ListServicesAction(context, HttpServlet.class, BlueprintContainer.class);

            try {
                resp.getWriter().write("Action executed without security context");
                resp.getWriter().write(action.run());
            } catch (Exception e) {
                e.printStackTrace(resp.getWriter());
            }
            resp.getWriter().write("--------\n");

            try {
                resp.getWriter().write("Action executed normally");
                resp.getWriter().write(Subject.doAs(loginContext.getSubject(), action));
            } catch (PrivilegedActionException e) {
                e.printStackTrace(resp.getWriter());
            }
            resp.getWriter().write("--------\n");

            try {
                resp.getWriter().write("Action executed as subject");
                resp.getWriter().write(Subject.doAsPrivileged(loginContext.getSubject(), action, null));
            } catch (PrivilegedActionException e) {
                e.printStackTrace(resp.getWriter());
            }

            loginContext.logout();
        } catch (LoginException e) {
            resp.getWriter().println("Login attempt failed");
            e.printStackTrace(resp.getWriter());
        }
    }

}
