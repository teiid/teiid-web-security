/*
 * Copyright Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags and
 * the COPYRIGHT.txt file distributed with this work.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.teiid.oauth.keycloak;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.teiid.OAuthCredential;
import org.teiid.OAuthCredentialContext;

public class AuthFilter implements Filter  {
    public static final String ACCESS_TOKEN = "oauth-access-token";
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        handleDelegation(httpRequest);
        chain.doFilter(httpRequest, response);
    }
    
    private void handleDelegation(final HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String accessToken = null;
        if (session != null && session.getAttribute(ACCESS_TOKEN) != null) {
            accessToken = (String)session.getAttribute(ACCESS_TOKEN);
        }
        
        if (request.getHeader("Authorization") != null) {
            accessToken = request.getHeader("Authorization");
            accessToken = accessToken.substring(7); // remove "Bearer " from header.
        }
        
        if (accessToken != null) {
            final String token = "Bearer " + accessToken;
            OAuthCredentialContext.setCredential(new OAuthCredential() {
                @Override
                public String getAuthrorizationProperty(String key) {
                    return null;
                }
                @Override
                public String getAuthorizationHeader(String resourceURI, String httpMethod) {
                    return token;
                }
            });
        }
    }

    @Override
    public void destroy() {
        
    }
}
