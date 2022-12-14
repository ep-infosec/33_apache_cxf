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

package org.apache.cxf.jaxrs.ext;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Providers;

/**
 * Represents an invocation context
 *
 */
public interface MessageContext {

    Object get(Object key);
    Object getContextualProperty(Object key);
    <T> T getContent(Class<T> format);

    void put(Object key, Object value);

    UriInfo getUriInfo();
    Request getRequest();
    HttpHeaders getHttpHeaders();
    SecurityContext getSecurityContext();
    Providers getProviders();

    HttpServletRequest getHttpServletRequest();
    HttpServletResponse getHttpServletResponse();
    ServletContext getServletContext();
    ServletConfig getServletConfig();

    <T> T getContext(Class<T> contextClass);
    <T, E> T getResolver(Class<T> resolverClass, Class<E> resolveClazz);
}
