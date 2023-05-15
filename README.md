# MBean API Lister

A java application to list the status of APIs running in z/OS Connect using the MBean interface.

## Build the application

`./gradlew build`

## Run the application

`./gradlew run`

## How it works

* Get the ApplicationMBeans registered in the server

  `GET /IBMJMXConnectorREST/mbeans/?className=com.ibm.ws.app.manager.internal.ApplicationConfigurator$NamedApplication$2`
* Get the state of each API

  `GET /IBMJMXConnectorREST/mbeans/<objectName>/attributes/State`

