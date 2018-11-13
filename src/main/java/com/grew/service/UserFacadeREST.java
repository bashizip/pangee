/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.UnsupportedEncodingException;
import com.grew.control.UserManager;
import com.grew.model.UserDoc;
import com.grew.utils.UUIDGenerator;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import com.grew.model.PwdBean;
import com.grew.process.ProcessCustomResponse;
import com.grew.process.ProcessIdentityProvider;
import com.grew.security.Secured;
import com.grew.security.SecuritySettings;
import com.grew.utils.DateUtils;
import com.grew.utils.HashTool;
import org.joda.time.DateTime;

/**
 *
 * @author bashizip
 */
@Api(value = "Users")
@Path("users")
@Stateless
public class UserFacadeREST {

    UserManager manager = new UserManager();
    ProcessIdentityProvider processIdentityProvider;

    @Context
    UriInfo uriInfo;

    @Context
    HttpHeaders headers;

    @POST
    @ApiOperation(value = "Créer un nouvel utilisateur")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response createUser(UserDoc entity) throws URISyntaxException {

        //check username unicity
        UserManager um = new UserManager();
        List<UserDoc> clone = um.findByUsername(entity.getUsername());

        if (clone != null) {
            if (clone.size() > 0) {
                return Response.status(Response.Status.CONFLICT).entity(new ProcessCustomResponse("Conflit", "Username already exists")).build();
            }
        }
        entity.setId(UUIDGenerator.nextUserID());

        String securedPassword = HashTool.get_SHA_512_SecurePassword(entity.getPassword());
        entity.setPassword(securedPassword);

        manager.create(entity); //User in couchDB for global Authentification

        return Response.created(new URI(uriInfo.getPath().concat("/")
                .concat(entity.getId()))).entity(entity).build();

    }

    @PUT
    @Path("edit/pwd")
    @Secured
    @ApiOperation(value = "Changer son mot de passe")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response updatePwd(PwdBean pw) throws URISyntaxException {

        String username = headers.getHeaderString("username");
        String groups = headers.getHeaderString("usergroups");

        String oldPwd = pw.getOldPassword();
        String newPassW = pw.getNewPassword();

        List<UserDoc> docs = manager.findByUsername(username);

        UserDoc doc = null;

        if (docs != null) {
            if (docs.size() > 0) {
                doc = docs.get(0);
            }
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(new ProcessCustomResponse("Not found", "User Not found")).build();
        }

        if (manager.login(username, HashTool.get_SHA_512_SecurePassword(oldPwd)) == null) {
            return Response.status(Response.Status.FORBIDDEN).entity(new ProcessCustomResponse("Wrong password", "The password entered is not correct.")).build();
        }

        String securedPassword = HashTool.get_SHA_512_SecurePassword(newPassW);

        if (doc != null) {
            doc.setPassword(securedPassword);
            manager.edit(doc); //User in couchDB for global Authentification
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(new ProcessCustomResponse("Not found", "User Not found")).build();
        }
        return Response.status(Response.Status.OK).entity(new ProcessCustomResponse("Success", "User Password updated.")).build();

    }

    @PUT
    @Path("edit/settings")
    @Secured
    @ApiOperation(value = "Changer les préférences utilisateurs")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response updateSettings(UserDoc u) throws URISyntaxException {
        List<UserDoc> docs = manager.findByUsername(u.getUsername());
          UserDoc doc = null;

        if (docs != null) {
            if (docs.size() > 0) {
                doc = docs.get(0);
            }
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ProcessCustomResponse("Not found", "User Not found")).build();
        }
        if(doc != null){
            
        Map userSettings  = doc.getSettings();
        userSettings.putAll(u.getSettings());
        
        doc.setSettings(userSettings);
        
        UserManager umanager = new UserManager();
        
        umanager.edit(doc);
        
        }else{
                 return Response.status(Response.Status.NOT_FOUND).
                         entity(new ProcessCustomResponse("Not found", "User Not found")).build();
        }
            
         return Response.status(Response.Status.OK).entity(new ProcessCustomResponse("Success", "User settings updated.")).build();
    }
    
    
    

    @POST
    @ApiOperation(value = "Authentification de l'utilisateur")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("login")
    public Response login(UserDoc user) {

        UserDoc found = manager.login(user.getUsername(), HashTool.get_SHA_512_SecurePassword(user.getPassword()));

        if (found != null) {
            String token = null;
            try {
                token = issueToken(found);
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
            found.setLastLogin(DateUtils.getNowGMT());
            found.setToken(token);
            
            manager.edit(found);
            
            return Response.ok().entity(found).build();
        }
        return Response.status(Response.Status.FORBIDDEN).entity(new ProcessCustomResponse("Probleme de connexion", "Nom d'utilisateur ou mot de passe invalide")).build();
    }

    /**
     *
     * @param user
     * @return
     * @throws UnsupportedEncodingException
     */
    private String issueToken(UserDoc user) throws UnsupportedEncodingException {

        DateTime currentTime = new DateTime();

        Claims claims = Jwts.claims().setSubject(user.getUsername());

        claims.put("scopes", Arrays.toString(user.getGroups()));

        System.out.println("Login scopes:" + Arrays.toString(user.getGroups()));

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(SecuritySettings.getTokenIssuer())
                .setIssuedAt(currentTime.toDate())
                .setExpiration(currentTime.plusMinutes(SecuritySettings.getRefreshTokenExpTime()).toDate())
                .signWith(SignatureAlgorithm.HS256, SecuritySettings.getTokenSigningKey())
                .compact();

        return token;
    }

    @GET
    @ApiOperation(value = "Lister tous les utilisateurs")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response findAll() {
        List<UserDoc> list = manager.findAll();
        return Response.ok(list).build();
    }

}
