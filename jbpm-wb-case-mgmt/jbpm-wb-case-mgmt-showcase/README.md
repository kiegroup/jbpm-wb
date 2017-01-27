# jBPM Case Management Showcase

Showcase application for managing Case instances backed by a running [Kie Server](https://docs.jboss.org/drools/release/6.5.0.Final/drools-docs/html_single/index.html#d0e24201).

## Getting Started

You can clone the repository and build all the projects using: mvn clean install

In order to start the showcase for development you should execute the following Maven commands:
```
cd jbpm-wb-case-mgmt/
mvn clean install
cd jbpm-wb-case-mgmt-showcase/
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
  To forward the current authenticated user into Kie Server, a new login module should be added to the WildFly instance running the showcase app.
  Please add this new login module in the security domain *other*.
  ```
  <login-module code="org.kie.security.jaas.KieLoginModule" flag="required" module="deployment.jbpm-wb-case-mgmt-showcase.war"/>
  ```
  When using this mode, users must co-exist in both servers where the showcase app and Kie Server are running in order for the authentication to suceed.
  To enable this authentication mode, only the Kie Server location system property must be provided.
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
In order to create case instances, a list of case definitions is set via JSON format [case_definitions.json](./src/main/resources/case_definitions.json).

## Deploying to Wildfly 10

Download and unzip the WildFly 10 distribution. Let's call the root of the distribution WILDFLY_HOME. This directory is named after the WildFly version, so for example wildfly-10.0.0.Final.

Build the jbpm-wb-case-mgmt-showcase project using the following Maven command
```
cd jbpm-wb-case-mgmt/
mvn clean install -Dfull
```
Copy the newly create war file (jbpm-wb-case-mgmt-showcase/jbpm-wb-case-mgmt-showcase.war) into WILDFLY_HOME/standalone/deployments.

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

## Demo Docker image

To allow you to quickly get started with the jBPM Case Management Showcase fully integrated with a running Kie Server, a Docker image can be generated as part of the build.
This image contains both applications deployed to a single WildFly 10 instance. It also provides the settings necessary for showcase to retrieve the necessary data from the running Kie Server instance.
By default, a few demo containers are deployed to allow you to interact with the Case Management concepts as well as pre-defined users with the following credentials: admin/admin and user/user. 
Please note, that you need [Docker](https://www.docker.io/) installed in your system to successfully build the demo image.

To build the image, please execute the following command:
```
mvn clean package -Dfull -Ddocker
```
Once the build is complete you can check your local Docker repository for a new image with name: jboss/jbpm-case-mgmt-showcase-demo
```
docker images
```

To build and start the image, please execute the following command or simply use the Docker CLI
```
mvn clean package -Dfull -Ddocker docker:start
```
The newly created container image will be started and a random port will be assigned to in your localhost to the WildFly instance running on the container.
You can check the actual value by executing:
```
docker ps
```

For checking the WildFly instance logs via Maven, please execute the following command:  
```
mvn docker:logs -Ddocker.follow
```