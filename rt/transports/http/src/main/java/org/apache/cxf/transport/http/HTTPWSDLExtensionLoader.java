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

package org.apache.cxf.transport.http;

import jakarta.xml.bind.JAXBException;
import org.apache.cxf.Bus;
import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.wsdl.JAXBExtensionHelper;
import org.apache.cxf.wsdl.WSDLExtensionLoader;
import org.apache.cxf.wsdl.WSDLManager;
import org.apache.cxf.wsdl.http.AddressType;

/**
 *
 */
@NoJSR250Annotations
public final class HTTPWSDLExtensionLoader implements WSDLExtensionLoader {

    public HTTPWSDLExtensionLoader(Bus b) {
        WSDLManager manager = b.getExtension(WSDLManager.class);

        createExtensor(b, manager, javax.wsdl.Port.class,
                       org.apache.cxf.transports.http.configuration.HTTPClientPolicy.class);
        createExtensor(b, manager, javax.wsdl.Port.class,
                       org.apache.cxf.transports.http.configuration.HTTPServerPolicy.class);
        createExtensor(b, manager, javax.wsdl.Port.class,
                       AddressType.class);
    }
    public void createExtensor(Bus b, WSDLManager manager,
                                Class<?> parentType,
                                Class<?> elementType) {
        try {
            JAXBExtensionHelper.addExtensions(b, manager.getExtensionRegistry(),
                                              parentType,
                                              elementType, null,
                                              this.getClass().getClassLoader());
        } catch (JAXBException e) {
            //ignore, won't support XML
        }
    }

}
