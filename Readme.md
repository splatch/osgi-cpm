# Conditional permission admin (CPM)

Purpose of this small project is to check CPM usage together with Apache Karaf and JAAS layer.

The scenario to cover:
Code without subject running in test module should not have access to blueprint/servlet instances.
Principals logged by JAAS-unix login module should have access to servlets but still miss access to blueprint.

## Modules overview


* [condition](condition) Simple condition to verify Subject presence
* [admin](admin) Administration module which deploy conditional access rules
* [test](test) Test module deploying HttpServlet at /login path which initialize LoginConfiguration and print service details.
* [features](features) Karaf features descriptor.
* [jaas](jaas) JAAS modules for test purposes.
 

### Installation

Start karaf (this code was tested with Karaf 2.2.9)

```
features:addurl features:addurl mvn:org.code-house.cpm/feature/0.3.0-SNAPSHOT/xml/features
```
Then shutdown karaf to enable security (CTRL+D) or `shutdown` command.
```
export KARAF_OPTS="-Dorg.osgi.framework.security=osgi -Djava.security.policy=karaf.policy"
```
Create *karaf.policy* file in karaf root directory
```
grant {
  permission java.security.AllPermission;
};
```
Start karaf again by command `bin/karaf` and navigate to [localhost:8181/login](http://localhost:8181/login).

You should see following bundles installed in Karaf (output of `list` command):
```
START LEVEL 100 , List Threshold: 50
   ID   State         Blueprint      Level  Name
[  50] [Resolved   ] [            ] [   80] Apache Felix Security Provider (1.4.2)
[  51] [Resolved   ] [            ] [   80] Code-House :: CPM :: Security Condition (0.3.0.SNAPSHOT)
[  52] [Active     ] [            ] [   80] Code-House :: CPM :: Security Admin (0.3.0.SNAPSHOT)
[  53] [Active     ] [Created     ] [   80] Code-House :: CPM :: Security JAAS Modules (0.3.0.SNAPSHOT)
[  78] [Active     ] [            ] [   80] Code-House :: CPM :: Security Test (0.3.0.SNAPSHOT)
```
