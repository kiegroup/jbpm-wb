# jBPM Case Management Showcase

Showcase application for managing Case instances backed by a running [Kie Server](https://docs.jboss.org/drools/release/6.5.0.Final/drools-docs/html_single/index.html#d0e24201).

## Getting Started

You can clone the repository and build all the projects using: mvn clean install

In order to start the showcase for development you should execute the following Maven commands:
```
cd jbpm-console-ng-case-mgmt/
mvn clean install
cd jbpm-console-ng-case-mgmt-showcase/
mvn clean gwt:run
```
The default login for the showcase app is admin/admin

## Connecting to the KIE Execution Server

There are a few supported ways for you to connect and authenticate to a running Kie Server.
These settings are provded using system variables to the running application server where the Case Management application is runnig.
If running on Wildfly, please check the different ways you can provide these settings:
- [General configuration concepts](https://docs.jboss.org/author/display/WFLY10/General+configuration+concepts)
- [Command line parameters](https://docs.jboss.org/author/display/WFLY10/Command+line+parameters)

### Authentication options

Independently from the method you choose, the url for the running Kie Server must be provided. This is done via the system property *org.kie.server.location*.

- Authenticate using a pre-defined user name and password
  Please note that using this method any user connected to the showcase application would share the credentials in the connected Kie Server.
  Example:
  *org.kie.server.location*=http://localhost:8230/kie-server/services/rest/server
  *org.kie.server.user*=kieserver
  *org.kie.server.pwd*=kieserver1!
- Authenticate using the current logged in user
  Example:
  *org.kie.server.location*=http://localhost:8230/kie-server/services/rest/server
- Authenticate using a Keyclock token
  For more information on how to set up Keyclock using token based authentication, please check the [Keycloak SSO integration](https://docs.jboss.org/drools/release/6.5.0.Final/drools-docs/html_single/index.html#kie.KeycloakSSOIntegration) documentaion.
  Example:
  *org.kie.server.location*=http://localhost:8230/kie-server/services/rest/server
  *org.kie.server.token*=kieserver1!

## Development mode

The development mode allows you to manage Case instances using an in memory database, whithout connecting to a running Kie Server.
To start the showcase in development mode, please make sure the system property *org.kie.server.location* is not provided.
In order to create case instances, a list of case definitions is set via JSON format [case_definitions.json](../jbpm-console-ng-case-mgmt-backend/src/main/resources/case_definitions.json).

## Deploying to Wildfly 10

Download and unzip the WildFly 10 distribution. Let's call the root of the distribution WILDFLY_HOME. This directory is named after the WildFly version, so for example wildfly-10.0.0.Final.

Build the jbpm-console-ng-case-mgmt-showcase project using the following Maven command
```
cd jbpm-console-ng-case-mgmt/
mvn clean install -Dfull
```
Copy the newly create war file (jbpm-console-ng-case-mgmt-showcase/jbpm-console-ng-case-mgmt-showcase.war) into WILDFLY_HOME/standalone/deployments.

Configure user(s) and role(s). Execute the following command:
```
WILDFLY_HOME/bin/add-user.[sh|bat] -a -u 'kieserver' -p 'kieserver1!' -ro 'user'
```
You can of course choose different username and password, just make sure that the user has role user.

Start the server by running:
```
./standalone.sh -Dorg.kie.server.location=http://localhost:8230/kie-server/services/rest/server -Dorg.kie.server.user=kieserver -Dorg.kie.server.pwd=kieserver1!
```

Verify the application is running. Go to http://SERVER:PORT/jbpm-cm/ and type the specified username and password. You should see simple XML message with basic information about the server.

For more details, please check the [Getting Started Guide](https://docs.jboss.org/author/display/WFLY10/Getting+Started+Guide).