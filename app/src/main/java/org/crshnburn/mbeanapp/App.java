/*
Copyright 2013 Andrew Smithson

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.crshnburn.mbeanapp;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;

public class App {

    /** z/OS Connect server hostname */
    private final static String hostname = "localhost";
    /** z/OS Connect server HTTPS port */
    private final static String httpsPort = "9443";
    /** z/OS Connect server administration role user */
    private final static String adminUserid = "admin";
    /** z/OS Connect server administration role user's password */
    private final static String adminPassword = "admin";
    /** Truststore containing z/OS Connect server's public certificate */
    private final static String trustStoreLocation = "/Users/smithson/Documents/gitprojects/mbean-api-lister/key.p12";
    /** Truststore password */
    private final static String trustStorePassword = "IFVQffwHptx9RT7Z0IPSiJp507VEw7SZKHobXVtY+bg=";

    public String getApiStatus() throws IOException {
        //Configure the SSL properties
        System.setProperty("com.sun.net.ssl.checkRevocation","false");
        System.setProperty("javax.net.ssl.trustStore", trustStoreLocation);
        System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
        //Configure the username and password
        Authenticator.setDefault (new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(adminUserid, adminPassword.toCharArray());
            }
        });
        //Get the list of ApplicationMBean instances in the server - there is one MBean per API
        JSONArray json = new JSONArray(IOUtils.toString(new URL("https://"+hostname+":"+httpsPort+"/IBMJMXConnectorREST/mbeans/?className=com.ibm.ws.app.manager.internal.ApplicationConfigurator$NamedApplication$2"), Charset.forName("UTF-8")));
        //Iterate over the list MBeans
        for (Iterator<Object> it = json.iterator(); it.hasNext(); ) {
            JSONObject obj = (JSONObject) it.next();
            String objName = obj.get("objectName").toString();
            //Get the status of the API
            JSONObject stateObj = new JSONObject(IOUtils.toString(new URL("https://" + hostname + ":"+ httpsPort + "/IBMJMXConnectorREST/mbeans/"+objName+"/attributes/State")));
            //Output the name of the API and its status
            System.out.println(objName.split("=")[2] + ":" + stateObj.getString("value"));
        }

        return "Done";
    }

    public static void main(String[] args) throws IOException {
        System.out.println(new App().getApiStatus());
    }
}
