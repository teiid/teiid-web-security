/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
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
