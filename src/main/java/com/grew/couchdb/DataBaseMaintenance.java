/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.couchdb;

import com.google.gson.JsonObject;
import javax.ejb.Stateless;
import com.grew.control.DataFlowManager;
import com.grew.control.EntityManager;
import com.grew.control.FlowManager;
import com.grew.control.FragmentManager;
import com.grew.control.TaskManager;

/**
 *
 * @author bash
 */
@Stateless
public class DataBaseMaintenance {

    public JsonObject deleteAllNonDesignDoc() {
        
        int a = new EntityManager().deleteAll();
        int b = new FragmentManager().deleteAll();
        int c = new TaskManager().deleteAll();
        int d = new DataFlowManager().deleteAll();
        int e = new FlowManager().deleteAll();

        JsonObject jo = new JsonObject();
        jo.addProperty("entities", a);
        jo.addProperty("fragments", b);
        jo.addProperty("tasks", c);
        jo.addProperty("dataFlows", d);
        jo.addProperty("flows", e);

        return jo;
    }

}
