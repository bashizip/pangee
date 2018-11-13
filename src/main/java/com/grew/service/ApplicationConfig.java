/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.service;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.models.Contact;
import io.swagger.models.Info;
import io.swagger.models.License;
import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author bashizip
 */
@javax.ws.rs.ApplicationPath("v2")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {

        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        //resources.add(MultiPartFeature.class);

        //--------- for uploads to work
        //    resources.add(MultiPartFeature.class);
        resources.add(io.swagger.jaxrs.listing.ApiListingResource.class);
        resources.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);

        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("2.0");

        beanConfig.setSchemes(new String[]{"http","https"});
        beanConfig.setHost("http://localhost:8080");
        beanConfig.setBasePath("grew-api/v2");
        beanConfig.setResourcePackage("com.grew.service");

        Info info = new Info()
                .title("GREW-RCCM API")
                .description("Api exposant les services du GREW-RCCM")
                .version("2.1.3")
                .license(new License().name("Tous droits reservés"))
                .contact(new Contact().email("devteam@grew.com").name("Patrick Bashizi"));

        beanConfig.setInfo(info);

        beanConfig.setPrettyPrint(true);

        beanConfig.setScan(true);

        return resources;

    }

    /**
     * Do not modify addRestResourceClasses() method. It is automatically
     * populated with all resources defined in the project. If required, comment
     * out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.grew.filter.AuthenticationFilter.class);
        resources.add(com.grew.filter.CorsFilter.class);
        resources.add(com.grew.service.CodificationFacadeREST.class);
        resources.add(com.grew.service.EPTBCollectFacadeREST.class);
        resources.add(com.grew.service.EntityDocFacadeREST.class);
        resources.add(com.grew.service.FlowDocFacadeREST.class);
        resources.add(com.grew.service.FormsFacadeREST.class);
        resources.add(com.grew.service.MaintenanceFacadeREST.class);
        resources.add(com.grew.service.ProcessFacadeREST.class);
        resources.add(com.grew.service.ProcessInstanceFacade.class);
        resources.add(com.grew.service.TaskListFacadeREST.class);
        resources.add(com.grew.service.UserFacadeREST.class);
    }

}
