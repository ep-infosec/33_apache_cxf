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
package org.apache.cxf.systest.jms;

import jakarta.jws.WebService;

@WebService(serviceName = "SOAPService6",
            portName = "SoapPort6",
            endpointInterface = "org.apache.hello_world_doc_lit.Greeter",
            targetNamespace = "http://apache.org/hello_world_doc_lit",
            wsdlLocation = "testutils/hello_world_doc_lit.wsdl")
public class SoapService6SoapPort6Impl extends GreeterImplDocBase {

}
