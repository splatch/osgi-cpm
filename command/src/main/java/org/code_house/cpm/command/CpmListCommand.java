package org.code_house.cpm.command;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionUpdate;
import org.osgi.service.permissionadmin.PermissionInfo;

@SuppressWarnings("unchecked")
@Command(scope = "cpm", name = "list", description = "Lists access rules.")
public class CpmListCommand extends OsgiCommandSupport {

    private final ConditionalPermissionAdmin admin;

    public CpmListCommand(ConditionalPermissionAdmin admin) {
        this.admin = admin;
    }

    @Override
    protected Object doExecute() throws Exception {
        ConditionalPermissionUpdate update = admin.newConditionalPermissionUpdate();
        List<ConditionalPermissionInfo> infos = update.getConditionalPermissionInfos();

        PrintStream console = session.getConsole();
        for (ConditionalPermissionInfo info : infos) {
            console.println(String.format("Entry name: %s, decission: %s", info.getName(), info.getAccessDecision()));

            ConditionInfo[] conditions = info.getConditionInfos();
            if (conditions != null && conditions.length > 0) {
                console.println("Codntions: ");
                for (ConditionInfo conditionInfo : conditions) {
                    String argumentsStr = Arrays.toString(conditionInfo.getArgs());
                    String arguments = argumentsStr.substring(1, argumentsStr.length() - 1);
                    console.println(String.format("\t[%s %s]", conditionInfo.getType(), arguments));
                }
            }

            PermissionInfo[] permissions = info.getPermissionInfos();
            boolean allow = info.getAccessDecision().equals(ConditionalPermissionInfo.ALLOW);
            console.println(String.format("Permissions %s:", allow ? "granted" : "revoked"));
            for (PermissionInfo permissionInfo : permissions) {
                console.println(String.format("\t(%s %s %s)", permissionInfo.getType(), permissionInfo.getName(), permissionInfo.getActions()));
            }
            console.println();
        }
        update.commit();

        return null;
    }

}
