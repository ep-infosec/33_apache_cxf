/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.cxf.systest.corba;

import jakarta.xml.ws.Endpoint;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.testutil.common.AbstractBusTestServerBase;


public class Server extends AbstractBusTestServerBase {
    public static final String PERSIST_PORT = allocatePort(Server.class);

    protected void run()  {
        System.setProperty("com.sun.CORBA.POA.ORBServerId", "1");
        System.setProperty("com.sun.CORBA.POA.ORBPersistentServerPort", PERSIST_PORT);
        setBus(new SpringBusFactory().createBus("org/apache/cxf/systest/corba/hello_world_server.xml"));
        Object implementor = new BaseGreeterImpl();
        String address = "file:./HelloWorld.ref";
        Endpoint.publish(address, implementor);
    }

    public static void main(String[] args) throws Exception {
        new Server().start();
    }
}
