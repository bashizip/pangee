/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.service;

import com.google.gson.JsonObject;
import com.grew.security.Secured;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author bash
 */
@Stateless
@Path("maintenance")
public class MaintenanceFacadeREST {
    
    @EJB
    MaintenanceFacadeREST mfrest;
    
    @PUT
    @Path("clearall")
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject clearAll(){
        return mfrest.clearAll();
    }
    
}
