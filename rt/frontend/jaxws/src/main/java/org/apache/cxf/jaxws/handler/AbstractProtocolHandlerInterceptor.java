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

package org.apache.cxf.jaxws.handler;

import jakarta.xml.ws.Binding;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.handler.MessageContext.Scope;
import org.apache.cxf.jaxws.context.WrappedMessageContext;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;


public abstract class AbstractProtocolHandlerInterceptor<T extends Message>
    extends AbstractJAXWSHandlerInterceptor<T> {

    protected AbstractProtocolHandlerInterceptor(Binding binding) {
        super(binding, Phase.USER_PROTOCOL);
    }
    protected AbstractProtocolHandlerInterceptor(Binding binding, String phase) {
        super(binding, phase);
    }

    public void handleMessage(T message) {
        if (binding.getHandlerChain().isEmpty()) {
            return;
        }
        MessageContext context = createProtocolMessageContext(message);
        HandlerChainInvoker invoker = getInvoker(message);
        invoker.setProtocolMessageContext(context);
        invoker.invokeProtocolHandlers(isRequestor(message), context);

        onCompletion(message);
    }

    protected MessageContext createProtocolMessageContext(T message) {
        return new WrappedMessageContext(message, Scope.HANDLER);
    }

}
