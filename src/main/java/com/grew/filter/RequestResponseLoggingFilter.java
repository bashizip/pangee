package com.grew.filter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author bashizip
 */
//@Provider
public class RequestResponseLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    /**
     * Logging in console
     *
     * @param requestContext
     * @throws IOException
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        StringBuilder sb = new StringBuilder();
        sb.append("User: ").append(requestContext.getSecurityContext().getUserPrincipal() == null ? "unknown"
                : requestContext.getSecurityContext().getUserPrincipal());
        sb.append(" - Path: ").append(requestContext.getUriInfo().getPath());
        sb.append(" - Header: ").append(requestContext.getHeaders());

        if (requestContext.getMediaType() != MediaType.APPLICATION_OCTET_STREAM_TYPE) {
            sb.append(" - Entity: ").append(getEntityBody(requestContext));
        }
        
          if (requestContext.getMediaType() != MediaType.MULTIPART_FORM_DATA_TYPE) {
            sb.append(" - Entity: ").append(getEntityBody(requestContext));
        }

        System.out.println("HTTP REQUEST : " + sb.toString());
    }

    private String getEntityBody(ContainerRequestContext requestContext) {
        InputStream in = requestContext.getEntityStream();

        final StringBuilder b = new StringBuilder();
        try {

            byte[] requestEntity = IOUtils.toByteArray(in);
            if (requestEntity.length == 0) {
                b.append("").append("\n");
            } else {
                b.append(new String(requestEntity)).append("\n");
            }
            requestContext.setEntityStream(new ByteArrayInputStream(requestEntity));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return b.toString();
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("Header: ").append(responseContext.getHeaders());
        sb.append(" - Entity: ").append(responseContext.getEntity());
        System.out.println("HTTP RESPONSE : " + sb.toString());
    }

    String inputStreamToString(InputStream is, String encoding) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer, encoding);
        String theString = writer.toString();
        return theString;
    }

}
