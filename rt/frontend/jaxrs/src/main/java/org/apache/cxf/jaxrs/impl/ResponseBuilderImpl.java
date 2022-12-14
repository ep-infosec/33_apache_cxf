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

package org.apache.cxf.jaxrs.impl;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.Variant;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;

public class ResponseBuilderImpl extends ResponseBuilder implements Cloneable {

    private int status = 200;
    private String reasonPhrase;
    private boolean statusSet;
    private Object entity;
    private MultivaluedMap<String, Object> metadata = new MetadataMap<>();
    private Annotation[] annotations;

    public ResponseBuilderImpl() {
    }

    private ResponseBuilderImpl(ResponseBuilderImpl copy) {
        status = copy.status;
        statusSet = copy.statusSet;
        reasonPhrase = copy.reasonPhrase;
        metadata.putAll(copy.metadata);
        entity = copy.entity;
    }

    public Response build() {
        if (entity == null && !statusSet) {
            status = 204;
        }
        ResponseImpl r = new ResponseImpl(status, null, reasonPhrase);
        MetadataMap<String, Object> m =
            new MetadataMap<>(metadata, false, true);
        r.addMetadata(m);
        r.setEntity(entity, annotations);
        reset();
        return r;
    }

    public ResponseBuilder status(int statusCode) {
        validateStatusCode(statusCode);
        status = statusCode;
        statusSet = true;
        return this;
    }

    public ResponseBuilder status(int statusCode, String reason) {
        validateStatusCode(statusCode);
        status = statusCode;
        statusSet = true;
        reasonPhrase = reason;
        return this;
    }

    private void validateStatusCode(int statusCode) {
        if (statusCode < 100 || statusCode > 599) {
            throw new IllegalArgumentException("Illegal status value : " + statusCode);
        }
    }

    public ResponseBuilder entity(Object e) {
        entity = e;
        return this;
    }

    public ResponseBuilder type(MediaType type) {
        return setHeader(HttpHeaders.CONTENT_TYPE, type);
    }

    public ResponseBuilder type(String type) {
        return setHeader(HttpHeaders.CONTENT_TYPE, type);
    }

    @Override
    public ResponseBuilder language(Locale locale) {
        return setHeader(HttpHeaders.CONTENT_LANGUAGE, locale);
    }

    public ResponseBuilder language(String language) {
        return setHeader(HttpHeaders.CONTENT_LANGUAGE, language);
    }

    public ResponseBuilder location(URI loc) {
        if (!loc.isAbsolute()) {
            Message currentMessage = PhaseInterceptorChain.getCurrentMessage();
            if (currentMessage != null) {

                UriInfo ui = new UriInfoImpl(currentMessage.getExchange().getInMessage(), null);
                loc = ui.getBaseUriBuilder()
                        .path(loc.getRawPath())
                        .replaceQuery(loc.getRawQuery())
                        .fragment(loc.getRawFragment()).buildFromEncoded();
            }
        }
        return setHeader(HttpHeaders.LOCATION, loc);
    }

    public ResponseBuilder contentLocation(URI location) {
        return setHeader(HttpHeaders.CONTENT_LOCATION, location);
    }

    public ResponseBuilder tag(EntityTag tag) {
        return setHeader(HttpHeaders.ETAG, tag);
    }

    public ResponseBuilder tag(String tag) {
        final String doubleQuote = "\"";
        if (tag != null && !tag.startsWith(doubleQuote)) {
            tag = doubleQuote + tag + doubleQuote;
        }
        return setHeader(HttpHeaders.ETAG, tag);
    }

    public ResponseBuilder lastModified(Date date) {
        return setHeader(HttpHeaders.LAST_MODIFIED, date);
    }

    public ResponseBuilder cacheControl(CacheControl cacheControl) {
        return setHeader(HttpHeaders.CACHE_CONTROL, cacheControl);
    }

    @Override
    public ResponseBuilder expires(Date date) {
        return setHeader(HttpHeaders.EXPIRES, date);
    }

    @Override
    public ResponseBuilder cookie(NewCookie... cookies) {
        return addHeader(HttpHeaders.SET_COOKIE, (Object[])cookies);
    }

    public ResponseBuilder header(String name, Object value) {
        return addHeader(name, value);
    }


    @Override
    public ResponseBuilder variant(Variant variant) {
        type(variant == null ? null : variant.getMediaType());
        language(variant == null ? null : variant.getLanguage());
        setHeader(HttpHeaders.CONTENT_ENCODING, variant == null ? null : variant.getEncoding());
        return this;
    }


    @Override
    public ResponseBuilder variants(List<Variant> variants) {
        if (variants == null) {
            metadata.remove(HttpHeaders.VARY);
            return this;
        }
        String acceptVary = null;
        String acceptLangVary = null;
        String acceptEncVary = null;
        for (Variant v : variants) {
            MediaType mt = v.getMediaType();
            if (mt != null) {
                acceptVary = HttpHeaders.ACCEPT;
                addHeader(HttpHeaders.CONTENT_TYPE, mt);
            }
            Locale l = v.getLanguage();
            if (l != null) {
                acceptLangVary = HttpHeaders.ACCEPT_LANGUAGE;
                addHeader(HttpHeaders.CONTENT_LANGUAGE, l);
            }
            String enc = v.getEncoding();
            if (enc != null) {
                acceptEncVary = HttpHeaders.ACCEPT_ENCODING;
                addHeader(HttpHeaders.CONTENT_ENCODING, enc);
            }
        }
        handleVaryValue(acceptVary, acceptLangVary, acceptEncVary);
        return this;
    }

    private void handleVaryValue(String ...values) {
        List<Object> varyValues = metadata.get(HttpHeaders.VARY);
        for (String v : values) {
            if (v == null) {
                metadata.remove(null);
                if (varyValues != null) {
                    varyValues.remove(null);
                }
            } else {
                addHeader(HttpHeaders.VARY, v);
            }
        }
    }

//  CHECKSTYLE:OFF
    @Override
    public ResponseBuilder clone() { //NOPMD
        return new ResponseBuilderImpl(this);
    }
//  CHECKSTYLE:ON


    private void reset() {
        metadata.clear();
        entity = null;
        annotations = null;
        status = 200;
        reasonPhrase = null;
    }

    private ResponseBuilder setHeader(String name, Object value) {
        if (value == null) {
            metadata.remove(name);
        } else {
            metadata.putSingle(name, value);
        }
        return this;
    }

    private ResponseBuilder addHeader(String name, Object... values) {
        if (values != null && values.length >= 1 && values[0] != null) {
            boolean isAllowHeader = HttpHeaders.ALLOW.equals(name);
            for (Object value : values) {
                Object thevalue = isAllowHeader ? value.toString().toUpperCase() : value;
                if (!valueExists(name, thevalue)) {
                    metadata.add(name, thevalue);
                }
            }
        } else {
            metadata.remove(name);
        }
        return this;
    }

    private boolean valueExists(String key, Object value) {
        List<Object> values = metadata.get(key);
        return values != null && values.contains(value);
    }

    @Override
    public ResponseBuilder allow(String... methods) {
        return addHeader(HttpHeaders.ALLOW, (Object[])methods);
    }

    @Override
    public ResponseBuilder allow(Set<String> methods) {
        if (methods == null) {
            return allow();
        }
        return allow(methods.toArray(new String[0]));
    }

    @Override
    public ResponseBuilder encoding(String encoding) {
        return setHeader(HttpHeaders.CONTENT_ENCODING, encoding);
    }

    @Override
    public ResponseBuilder entity(Object ent, Annotation[] anns) {
        this.annotations = anns;
        this.entity = ent;
        return this;
    }

    @Override
    public ResponseBuilder link(URI href, String rel) {
        Link.Builder linkBuilder = new LinkBuilderImpl();
        return links(linkBuilder.uri(href).rel(rel).build());
    }

    @Override
    public ResponseBuilder link(String href, String rel) {
        Link.Builder linkBuilder = new LinkBuilderImpl();
        return links(linkBuilder.uri(href).rel(rel).build());
    }

    @Override
    public ResponseBuilder links(Link... links) {
        return addHeader(HttpHeaders.LINK, (Object[])links);
    }

    @Override
    public ResponseBuilder replaceAll(MultivaluedMap<String, Object> map) {
        metadata.clear();
        if (map != null) {
            metadata.putAll(map);
        }
        return this;
    }

    @Override
    public ResponseBuilder variants(Variant... variants) {
        if (variants == null) {
            return variants((List<Variant>)null);
        }
        return variants(Arrays.asList(variants));
    }
}
