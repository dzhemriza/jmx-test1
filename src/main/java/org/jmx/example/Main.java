/*
 * org.jmx.example
 *
 * File Name: Main.java
 *
 * Copyright 2017 Dzhem Riza
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jmx.example;

import javax.management.AttributeChangeNotification;
import javax.management.InstanceAlreadyExistsException;
import javax.management.JMX;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

class Main {

    private static void program() throws MalformedObjectNameException, NotCompliantMBeanException,
            InstanceAlreadyExistsException, MBeanRegistrationException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the word database!");
        List<String> database = new ArrayList<>();
        CommandsJournal commandsJournal = new CommandsJournal();

        ObjectName commandsJournalMxName = new ObjectName("org.jmx.example:type=CommandsJournal");
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

        mBeanServer.registerMBean(commandsJournal, commandsJournalMxName);

        while (true) {
            System.out.println("Menu:");
            System.out.println("1) Add word to the database");
            System.out.println("2) Remove word from the database");
            System.out.println("3) Show all words");
            System.out.println("4) Exit");

            int operation = scanner.nextInt();

            commandsJournal.addCommandNumber(operation);

            switch (operation) {
                case 1: {
                    System.out.println("Enter data:");
                    String data = scanner.next();
                    database.add(data);
                    break;
                }

                case 2: {
                    System.out.println("Enter item number:");
                    int num = scanner.nextInt();
                    database.remove(num);
                    break;
                }

                case 3: {
                    int i = 0;
                    for (String data : database) {
                        System.out.println("" + i + ") " + data);
                        ++i;
                    }
                    break;
                }

                case 4: {
                    System.out.println("Shutting down....");
                    return;
                }

                default: {
                    System.out.println("Invalid option!");
                }
            }
        }
    }

    static class JmxTestNotificationListener implements NotificationListener {

        @Override
        public void handleNotification(Notification notification, Object handback) {
            System.out.println("Received notification: ");
            System.out.println("\tClass name: " + notification.getClass().getName());
            System.out.println("\tSource: " + notification.getSource());
            System.out.println("\tType: " + notification.getType());
            System.out.println("\tMessage: " + notification.getMessage());

            if (notification instanceof AttributeChangeNotification) {
                AttributeChangeNotification acn = (AttributeChangeNotification) notification;
                System.out.println("\tAttribute Name: " + acn.getAttributeName());
                System.out.println("\tAttribute Type: " + acn.getAttributeType());
                System.out.println("\tNew Value: " + acn.getNewValue());
                System.out.println("\tOld Value: " + acn.getOldValue());
            }
        }
    }

    private static void client(String[] args) throws Exception {
        System.out.println("This is the program client!");

        JmxTestNotificationListener listener = new JmxTestNotificationListener();

        System.out.println("Connecting...");
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:9999/jmxrmi");
        JMXConnector jmxc = JMXConnectorFactory.connect(url, null);

        MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

        System.out.println("Domains:");
        System.out.println(Arrays.toString(mbsc.getDomains()));

        ObjectName mxbeanName = new ObjectName("org.jmx.example:type=CommandsJournal");
        CommandsJournalMXBean mxbeanProxy = JMX.newMBeanProxy(mbsc, mxbeanName, CommandsJournalMXBean.class);

        if (args.length > 1) {
            if ("clear".equalsIgnoreCase(args[1])) {
                System.out.println("Clearing all commands...");
                mxbeanProxy.clearAllCommands();
            }
        }

        System.out.println("Print all commands:");
        System.out.println(Arrays.toString(mxbeanProxy.getAllCommands()));
    }

    public static void main(String[] args) throws Exception {
        System.out.println(Arrays.toString(args));

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("client")) {
                client(args);
            }
        } else {
            // Start the program like that:
            // java -Dcom.sun.management.jmxremote.port=9999 \
            //   -Dcom.sun.management.jmxremote.authenticate=false \
            //   -Dcom.sun.management.jmxremote.ssl=false -jar \
            //   ./build/libs/jmx-test1-0.0.1.jar
            program();
        }
    }
}