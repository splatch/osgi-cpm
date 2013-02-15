package org.code_house.cpm.condition;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Dictionary;

import javax.security.auth.Subject;

import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;

@SuppressWarnings("all")
public class SubjectCondition implements Condition {

    private final String[] userMask;
    private Bundle bundle;

    public SubjectCondition(Bundle bundle, ConditionInfo info) {
        this.bundle = bundle;
        this.userMask = info.getArgs();
    }

    public boolean isPostponed() {
        return false;
    }

    public boolean isSatisfied() {
        AccessControlContext ctx = AccessController.getContext();
        if ("org.code-house.cpm.test".equals(bundle.getSymbolicName())) {
            System.out.println(ctx + " " + ctx.getDomainCombiner());
        }
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            public Boolean run() {
                AccessControlContext ctx = AccessController.getContext();
                if ("org.code-house.cpm.test".equals(bundle.getSymbolicName())) {
                    System.out.println(ctx + " " + ctx.getDomainCombiner());
                }
                Subject subject = Subject.getSubject(ctx);
                if (subject != null) {
                    System.out.println("Context " + ctx + " with attached subject " + subject);
                }
                return subject != null;
            }
        });
    }

    public boolean isMutable() {
        return true;
    }

    public boolean isSatisfied(Condition[] conditions, Dictionary context) {
        return false;
    }

    public static Condition getCondition(final Bundle bundle, final ConditionInfo info) {
        return new SubjectCondition(bundle, info);
    }

    @Override
    public String toString() {
        return "SubjectCondition " + userMask;
    }
}
