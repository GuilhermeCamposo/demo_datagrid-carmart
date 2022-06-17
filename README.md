# carmart: Example Using Local and Remote Data Grid Cache

## What is it?

CarMart is a simple web application that uses Data Grid Cache instead of a relational database.

Users can list cars, add new cars, or remove them from the CarMart. Information about each car is stored in a cache. The application also shows cache statistics like stores, hits, retrievals, and more.

The CarMart quickstart can work in two modes:

* _Library mode_  - In this mode, the application and the data grid are running in the same JVM. All libraries (JAR files) are bundled with the application and deployed to Red Hat JBoss Enterprise Application Platform. The library mode enables fastest (local) access to the entries stored on the same node as the application instance, but also enables access to data stored in remote nodes (JVMs) that comprise the embedded distributed cluster.

* _Client-server mode_ - In this mode, the Cache is stored in  a managed, distributed and clusterable data grid server.  Applications can remotely access the data grid server using Hot Rod, memcached or REST client APIs. This web application bundles only the HotRod client and communicates with a remote Red Hat JBoss Data Grid (JDG) server.


## System requirements

All you need to build this project is Java SDK 1.8 or better, Maven 3.0 or better.

The application this project produces is designed to be run on Red Hat JBoss Enterprise Application Platform (EAP) 7.0 or later.


## Configure Maven

If you have not yet done so, you must [Configure Maven](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN.md#configure-maven-to-build-and-deploy-the-quickstarts) before testing the quickstarts.


## Start EAP

1. Open a command line and navigate to the root of the EAP server directory.
2. The following shows the command line to start the server with the web profile:

        For Linux:   $JBOSS_HOME/bin/standalone.sh
        For Windows: %JBOSS_HOME%\bin\standalone.bat


## Build and Deploy the Application in Library Mode

_NOTE: The following build command assumes you have configured your Maven user settings. If you have not, you must include Maven setting arguments on the command line. See [the main README](../README.md) for more information._

1. Make sure you have started EAP as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. Type this command to build and deploy the archive:

        mvn clean package wildfly:deploy

4. This will deploy `target/jboss-carmart.war` to the running instance of the server.


## Access the application

The application will be running at the following URL: <http://localhost:8080/jboss-carmart/>


## Undeploy the Archive

1. Make sure you have started EAP as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. When you are finished testing, type this command to undeploy the archive:

        mvn wildfly:undeploy


## Build and Start the Application in Client-Server Mode (using HotRod Client)

NOTE: The application must be deployed to Red Hat JBoss Enterprise Application Platform (EAP).

1. Obtain the DG server distribution. See the following for more information: <http://www.redhat.com/products/jbossenterprisemiddleware/data-grid/>. You can use this [server configuration](datagrid/infinispan.xml) to make sure that the demo will work.

2. You will need an DG user for this demo. We are using the username "admin" and password "admin" as credentials for an admin user. If you want to use a different user, just change the configuration in [RemoteCacheContainerProvider.java](src/remote/java/org/jboss/as/quickstarts/datagrid/carmart/session/RemoteCacheContainerProvider.java)

3. Start the DG server on localhost:

        $DG_HOME/bin/server.sh

4. Create a cache with the following configuration:

Cache name: carcache


      {
        "distributed-cache": {
          "mode": "SYNC",
          "statistics": true,
          "encoding": {
            "key": {
              "media-type": "application/x-java-serialized-object"
            },
            "value": {
              "media-type": "application/x-java-serialized-object"
            }
          }
        }
      }


5. Start EAP into which you want to deploy your application

        $EAP_HOME/bin/standalone.sh

5. The application finds the DG server using the values in the [RemoteCacheContainerProvider.java](src/remote/java/org/jboss/as/quickstarts/datagrid/carmart/session/RemoteCacheContainerProvider.java). If you are not running the DG server on the default host and port, you must modify this file to contain the correct values.

6. Build the application in the example's directory:

        mvn clean package -Premote-eap

7. Deploy the application

        mvn wildfly:deploy -Premote-eap

8. The application will be running at the following URL: <http://localhost:8080/jboss-carmart/>

9. Undeploy the application

        mvn wildfly:undeploy -Premote-eap
