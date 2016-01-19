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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.keycloak.adapters.ServerRequest.HttpFailure;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.servlet.ServletOAuthClient;

@SuppressWarnings("serial")
public class AuthServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ServletOAuthClient oAuthClient = (ServletOAuthClient) req
                .getServletContext().getAttribute(ServletOAuthClient.class.getName());        
        if(req.getRequestURL().toString().endsWith("auth")) {
            oAuthClient.redirectRelative("token", req, resp);
        } else if (req.getRequestURL().toString().endsWith("token")) {
            try {
                AccessTokenResponse token = oAuthClient.getBearerToken(req);
                final String accessToken = token.getToken();
                req.getSession(true).setAttribute(AuthFilter.ACCESS_TOKEN, accessToken);
                String b = "<html><body>Congratualtions!!! Your login is sucessful. You can now execute the odata endpoints using this browser</body></html>";
                resp.getOutputStream().write(b.getBytes());
            } catch (HttpFailure e) {
                throw new ServletException("No authentication token can be found in the request");
            }
        }
    }
}
