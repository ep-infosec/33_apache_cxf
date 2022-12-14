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

package http2demo.server.h2;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.transport.http.HttpServerEngineSupport;

import http2demo.server.CustomerServiceImpl;

public class Server {

    static {
        // set the configuration file
        SpringBusFactory factory = new SpringBusFactory();
        Bus bus = factory.createBus("ServerConfig.xml");
        bus.setProperty(HttpServerEngineSupport.ENABLE_HTTP2, true);
        BusFactory.setDefaultBus(bus);
    }

    protected Server() throws Exception {
        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setResourceClasses(CustomerServiceImpl.class);
        sf.setResourceProvider(CustomerServiceImpl.class,
            new SingletonResourceProvider(new CustomerServiceImpl()));
        sf.setAddress("https://localhost:9000/");

        sf.create();
    }

    public static void main(String[] args) throws Exception {
        new Server();
        System.out.println("Server ready...");

        Thread.sleep(5 * 60 * 1000);
        System.out.println("Server exiting");
        System.exit(0);
    }
}
