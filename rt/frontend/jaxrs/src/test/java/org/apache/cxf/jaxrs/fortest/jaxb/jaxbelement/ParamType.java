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
package org.apache.cxf.jaxrs.fortest.jaxb.jaxbelement;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

//CHECKSTYLE:OFF
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "paramType", propOrder = {"comment"} )
public class ParamType implements Serializable {
    private static final long serialVersionUID = 4935166047393324749L;

    @XmlElement(name = "comment")
    protected String comment;


    public String getComment() {
        return comment;
    }

    public void setComment(String value) {
        this.comment = value;
    }

}
//CHECKSTYLE:ON
