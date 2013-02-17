package org.code_house.cpm.condition;

import java.security.AccessController;
import java.security.Principal;
import java.util.Arrays;
import java.util.Dictionary;

import javax.security.auth.Subject;

import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;

@SuppressWarnings("all")
public class PrincipalCondition implements Condition {

    private final String[] userMask;

    public PrincipalCondition(ConditionInfo info) {
        this.userMask = info.getArgs();
    }

    public boolean isPostponed() {
        return false;
    }

    public boolean isSatisfied() {
        Subject subject = Subject.getSubject(AccessController.getContext());

        if (subject == null) {
            return false;
        }

        if (userMask.length == 0 || userMask[0].equals("*")) {
            return true;
        }

        String clazz = userMask[0];
        String name = userMask.length > 1 ? userMask[1] : "*";

        for (Principal principal : subject.getPrincipals()) {
            if (clazz.equals(principal.getClass().getName())) {
                if ("*".equals(name) || principal.getName().equals(name)) {
                    return true;
                }
            }
        }
        System.out.println("Failed to evaluate " + Arrays.toString(userMask) + " with " + subject);
        return false;
    }

    public boolean isMutable() {
        return true;
    }

    public boolean isSatisfied(Condition[] conditions, Dictionary context) {
        return false;
    }

    public static Condition getCondition(final Bundle bundle, final ConditionInfo info) {
        return new PrincipalCondition(info);
    }

    @Override
    public String toString() {
        return "SubjectCondition " + userMask;
    }
}
