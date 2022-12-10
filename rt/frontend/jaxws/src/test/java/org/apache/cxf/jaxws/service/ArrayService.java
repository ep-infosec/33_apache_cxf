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
package org.apache.cxf.jaxws.service;

import java.util.List;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;



@WebService(name = "ArrayService",
            targetNamespace = "http://service.jaxws.cxf.apache.org/")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT,
             use = SOAPBinding.Use.LITERAL,
             parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface ArrayService {

    @WebMethod
    String[] arrayOutput();

    @WebMethod
    String arrayInput(
            @WebParam(name = "input") String[] inputs);

    @WebMethod
    String[] arrayInputAndOutput(
            @WebParam(name = "input") String[] inputs);

    @WebMethod
    List<String> listOutput();
    @WebMethod
    String listInput(@WebParam(name = "input",
        targetNamespace = "http://service.jaxws.cxf.apache.org/") List<String> inputs);

}