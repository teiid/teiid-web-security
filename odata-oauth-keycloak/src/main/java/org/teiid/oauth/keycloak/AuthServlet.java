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
            oAuthClient.redirectRelative("keycloak/token", req, resp);
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
