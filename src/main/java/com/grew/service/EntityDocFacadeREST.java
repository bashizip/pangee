package com.grew.service;

import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.io.FileUtils;
import org.ektorp.DocumentNotFoundException;
import com.grew.control.DataFlowManager;
import com.grew.control.EntityManager;
import com.grew.control.ProcessManager;
import com.grew.control.SearchResult;
import com.grew.dao.DatabaseViewNotFound;
import com.grew.model.DataFlowDoc;
import com.grew.model.EntityDoc;
import com.grew.model.ProcessDoc;
import com.grew.process.ProcessCustomResponse;
import com.grew.utils.ExecutionUtils;
import com.grew.utils.StringUtilities;
import com.grew.utils.UploadsUtils;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author bashizip
 */
@Api(value = "Entities")
@Stateless
@Path("entities")
public class EntityDocFacadeREST {

    @Context
    UriInfo uriInfo;

    @ApiOperation(value = "Chercher une entité par son ID", notes = "")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 404, message = "Not found")})
    @GET
    @Path("{entityId}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response findEntity(@PathParam("entityId") String entityId) throws DatabaseViewNotFound {
        EntityManager em = new EntityManager();
        EntityDoc doc = null;

        try {

            doc = em.find(entityId);

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).
                    entity(new ProcessCustomResponse("Not found", "Database error :" + e.getMessage()))
                    .build();
        }

        if (doc == null) {

            return Response.status(Response.Status.NOT_FOUND).
                    entity(new ProcessCustomResponse("Not found", "Entity not found"))
                    .build();
        }
        SearchResult sr = em.searchByEntityID(entityId);

        return Response.ok().entity(sr).build();

    }

    @ApiOperation(value = "ReChercher toutes les entités d'un type donné", notes = "")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 404, message = "Not found")})
    @GET
    @Path("oftype/{entityType}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response findAllEntityOfType(@PathParam("entityType") String type) throws DatabaseViewNotFound {

        EntityManager em = new EntityManager();
        List<EntityDoc> docs = null;
        List<SearchResult> searchResults = new ArrayList<>();

        try {

            docs = em.findByEntityType(type);

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).
                    entity(new ProcessCustomResponse("Not found", "Database error :" + e.getMessage()))
                    .build();
        }

        if (docs == null) {

            return Response.status(Response.Status.NOT_FOUND).
                    entity(new ProcessCustomResponse("Not found", "Entity not found"))
                    .build();
        }

        for (EntityDoc ed : docs) {
            searchResults.add(em.searchByEntityID(ed.getId()));
        }

        return Response.ok().entity(searchResults).build();

    }

    @ApiOperation(value = "Retourne toutes les entitées d'un type donné au format CSV", notes = "")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 404, message = "Not found")})
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("{type}/csv")
    public Response downloadDataCSV(@PathParam("type") String type) throws DatabaseViewNotFound, IOException {
        Response res = findAllEntityOfType(type);
        List<SearchResult> results = (List<SearchResult>) res.getEntity();
        
        if(results.isEmpty()){
          
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        String jsonString = "[";

        for (SearchResult sr : results) {
            Map r = (Map) sr.getResult();
            Map values = (Map) r.get("values");
            String finalValues = new Gson().toJson(values);
            jsonString = jsonString.concat(finalValues + ",");
        }

        jsonString = jsonString.substring(0, jsonString.length() - 1);
        jsonString = jsonString.concat("]");

        String path = UploadsUtils.FILES_DIR + "/" + type + ".csv";
        System.out.println(jsonString);

        JSONArray array = new JSONArray(jsonString);
        // System.out.println(array.toString());

        File f = new File(path);

        String csv = CDL.toString(array);
        FileUtils.writeStringToFile(f, csv);

        Response.ResponseBuilder response = Response.ok(f);

        response.header("Content-Disposition",
                "attachment; filename=" + f);

        response.header("Content-Length", f.length());

        return response.build();

    }

    @ApiOperation(value = "ReChercher une entité par son code terrain", notes = "")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 404, message = "Not found")})
    @POST
    @Path("by_localId")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response findByLocalId(SearchBean b) throws DatabaseViewNotFound {

        EntityManager em = new EntityManager();
        EntityDoc doc = null;
        SearchResult searchResults = null;

        try {
            doc = em.findByLocalId(b.getLocalId());

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).
                    entity(new ProcessCustomResponse("Not found", "Database error :" + e.getMessage()))
                    .build();
        }

        if (doc == null) {
            return Response.status(Response.Status.NOT_FOUND).
                    entity(new ProcessCustomResponse("Not found", "Entity not found"))
                    .build();
        }

        searchResults = em.searchByEntityID(doc.getId());

        return Response.ok().entity(searchResults).build();

    }

    @ApiOperation(value = "Chercher une entité par son Id et inclure les valeurs non encore validées", notes = "")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 404, message = "Not found")})
    @GET
    @Path("{entityId}/pending")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response findEntityWithPendingValues(@PathParam("entityId") String entityId) throws DatabaseViewNotFound {
        EntityManager em = new EntityManager();
        EntityDoc doc = null;

        try {

            doc = em.find(entityId);

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).
                    entity(new ProcessCustomResponse("Not found", "Database error :" + e.getMessage()))
                    .build();
        }

        if (doc == null) {

            return Response.status(Response.Status.NOT_FOUND).
                    entity(new ProcessCustomResponse("Not found", "Entity not found"))
                    .build();
        }

        SearchResult sr = em.searchByEntityID(entityId);

        Map values = (Map) sr.getResult();
        //search dataflow on that entity
        DataFlowManager dfm = new DataFlowManager();
        DataFlowDoc df = dfm.findByEntityId(entityId);

        Map pendingValues = df.getPending_values();

        values.put("pending_values", pendingValues);

        sr.setResult(values);

        return Response.ok().entity(sr).build();

    }

    @ApiOperation(value = "Chercher une entité par un ensemble de key-values avec LIKE comme critère", notes = "")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 404, message = "Research query not available")})
    @POST
    @Path("search/{entityType}")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response searchLike(@PathParam("entityType") String entityType, String values) {

        if (StringUtilities.isEmpty(values)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        EntityManager em = new EntityManager();
        List<SearchResult> t = null;

        HashMap<Object, Object> hash = ExecutionUtils.jsonStringToMapValues(values);

        TreeMap tm = new TreeMap(hash);

        String[] complexKey = new String[tm.size()];
        String[] multiValues = new String[tm.size()];

        int j = 0;

        for (Object key : tm.keySet()) {

            String mKey = key.toString();
            String mValue = tm.get(key).toString();

            complexKey[j] = mKey;
            multiValues[j] = mValue;
            j++;
        }

        List<SearchResult> intersection = null;

        try {

            t = em.searchEntitiesByLike(entityType, complexKey[0], multiValues[0]);
            intersection = new ArrayList<>(t); //initialisation

            for (int i = 1; i < complexKey.length; i++) {
                for (int k = 1; k < multiValues.length; k++) {
                    List<SearchResult> fullResults = em.searchEntitiesByLike(entityType, complexKey[i], multiValues[k]);
                    intersection.retainAll(fullResults);

                }
            }

        } catch (DatabaseViewNotFound | DocumentNotFoundException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).
                    entity(new ProcessCustomResponse("Not found", ex.getMessage()))
                    .build();
        }

        return Response.ok().entity(intersection).build();
    }

    @POST
    @ApiOperation(value = "Liste des entités disponibles pour une précedure", notes = "")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 404, message = "Not found")})

    @GET
    @Path("types/{processKey}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getProcessTypes(@PathParam("processKey") String processKey) {

        ProcessManager pm = new ProcessManager();

        ProcessDoc pdoc = pm.findByDefinitionKey(processKey);

        String[] keyPropos = pdoc.getSubEntitiesKeys();

        return Response.ok().entity(keyPropos).build();

    }

}
