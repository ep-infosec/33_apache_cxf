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

package org.apache.cxf.systest.jaxrs.security;

import org.apache.cxf.systest.jaxrs.AbstractSpringServer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;



public class BookServerSecuritySpringClass extends AbstractSpringServer {
    public static final int PORT = allocatePortAsInt(BookServerSecuritySpringClass.class);

    public BookServerSecuritySpringClass() {
        super("/jaxrs_security_cglib", PORT);
    }
    
    @Override
    protected void configureServer(Server server) throws Exception {
        final HandlerCollection collection = (HandlerCollection) server.getHandler();
        for (Handler handler: collection.getHandlers()) {
            if (handler instanceof WebAppContext) {
                final WebAppContext webappcontext = (WebAppContext) handler; 
                webappcontext.setClassLoader(getClass().getClassLoader());
            }
        }
    }

    public static void main(String[] args) {
        try {
            BookServerSecuritySpringClass s = new BookServerSecuritySpringClass();
            s.start();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        } finally {
            System.out.println("done!");
        }
    }

}
