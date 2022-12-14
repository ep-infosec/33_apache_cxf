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

package org.apache.cxf.jaxrs.sse.interceptor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.sse.SseEventSink;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.apache.cxf.jaxrs.model.OperationResourceInfo;
import org.apache.cxf.jaxrs.provider.ServerProviderFactory;
import org.apache.cxf.jaxrs.utils.HttpUtils;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

public class SseInterceptor extends AbstractPhaseInterceptor<Message> {
    private static final Logger LOG = LogUtils.getL7dLogger(SseInterceptor.class);
    
    public SseInterceptor() {
        super(Phase.POST_LOGICAL);
    }
    public SseInterceptor(String phase) {
        super(phase);
    }

    public void handleMessage(Message message) {
        // Not an SSE invocation, skipping it in favor of normal processing
        if (message.get(SseEventSink.class) == null) {
            return;
        }

        if (!isRequestor(message) && message.get(SseInterceptor.class) == null) {
            message.put(SseInterceptor.class, this);

            final Exchange exchange = message.getExchange();
            OperationResourceInfo ori = (OperationResourceInfo)exchange.get(OperationResourceInfo.class.getName());
            if (ori != null) {
                Response.ResponseBuilder builder = Response.ok();
                HttpServletResponse servletResponse = null;
        
                final ServerProviderFactory providerFactory = ServerProviderFactory.getInstance(message);
                final Object response = message.get(AbstractHTTPDestination.HTTP_RESPONSE);
                if (response instanceof HttpServletResponse) {
                    servletResponse = (HttpServletResponse)response;
                    builder = Response.status(servletResponse.getStatus());

                    for (final String header: servletResponse.getHeaderNames()) {
                        final Collection<String> headers = servletResponse.getHeaders(header);
                        addHeader(builder, header, headers);
                    }
                } 
                
                // Run the filters
                try {
                    final ResponseImpl responseImpl = (ResponseImpl)builder.build();
                    final Message outMessage = getOutMessage(message);

                    JAXRSUtils.runContainerResponseFilters(providerFactory, responseImpl, 
                        outMessage, ori, ori.getAnnotatedMethod());

                    if (servletResponse != null) {
                        servletResponse.setStatus(responseImpl.getStatus());
                        
                        final Map<String, List<String>> userHeaders =  CastUtils.cast((Map<?, ?>)outMessage
                            .get(Message.PROTOCOL_HEADERS));
                        if (userHeaders != null) {
                            for (Map.Entry<String, List<String>> entry: userHeaders.entrySet()) {
                                setHeader(servletResponse, entry);
                            }
                        }
                        
                        final MultivaluedMap<String, String> headers = responseImpl.getStringHeaders();
                        if (headers != null) {
                            for (Map.Entry<String, List<String>> entry: headers.entrySet()) {
                                setHeader(servletResponse, entry);
                            }
                        }
                    }
                } catch (Throwable ex) {
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.log(Level.FINE, ex.getMessage(), ex);
                    }
                }
            }
        }
    }
    
    private Message getOutMessage(final Message message) {
        final Exchange exchange = message.getExchange();
        Message outMessage = message.getExchange().getOutMessage();
        
        if (outMessage == null) {
            final Endpoint ep = exchange.getEndpoint();
            outMessage = new MessageImpl();
            outMessage.setExchange(exchange);
            outMessage = ep.getBinding().createMessage(outMessage);
            message.getExchange().setOutMessage(outMessage);
        }

        return outMessage;
    }
    
    private void addHeader(Response.ResponseBuilder builder, final String header, final Collection<String> headers) {
        if (headers != null) {
            for (Object value: headers) {
                builder.header(header, value);
            }
        }
    }
    
    private void setHeader(HttpServletResponse servletResponse, Map.Entry<String, List<String>> entry) {
        if (entry.getValue() != null) {
            servletResponse.setHeader(entry.getKey(), 
                HttpUtils.getHeaderString(entry.getValue()));
        }
    }
}
