package org.keycloak.example.oauth;

import org.jboss.resteasy.annotations.cache.NoCache;
import org.jboss.resteasy.spi.HttpRequest;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import java.util.ArrayList;
import java.util.List;

@Path("sample")
public class CustomerService {

    @Context
    private HttpRequest httpRequest;
    
    @GET
    @Produces("application/text")
    @NoCache
    public String getSample() {
        // Just to show how to user info from access token in REST endpoint
        KeycloakSecurityContext securityContext = (KeycloakSecurityContext) httpRequest.getAttribute(KeycloakSecurityContext.class.getName());
        AccessToken accessToken = securityContext.getToken();
        System.out.println(String.format("User '%s' with email '%s' made request to SampleService REST endpoint", accessToken.getPreferredUsername(), accessToken.getEmail()));

        String rtn = "<note>"+
        		"<to>Tove</to>"+
        		"<from>Jani</from>"+
        		"<heading>Reminder</heading>"+
        		"<body>Don't forget me this weekend!</body>"+
        		"</note>";	
        return rtn;
    }    
}
