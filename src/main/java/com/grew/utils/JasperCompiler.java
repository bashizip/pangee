/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.utils;

import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

/**
 *
 * @author pbash
 */
public class JasperCompiler {

    public static File getCompiledPdfFile(File source, Map data, Map<String, Object> params) throws JRException, IOException {
        //  File out = printToPdf(fromJasperFile(source, params));
        //File out = printToPdf(compile2(source, data, params));
        File out = printToPdf(compile2(source,data, params));
        return out;
    }

    private static JasperPrint fromJasperFile(File source, Map<String, Object> params) {
        JasperPrint jasperPrint = null;
        try {
            //  Object[] paramsArr = params.entrySet().toArray();
            //JRMapArrayDataSource ds= new JRMapArrayDataSource(paramsArr);
            jasperPrint = JasperFillManager.fillReport(source.getAbsolutePath(), params, new JREmptyDataSource());

        } catch (JRException ex) {
            ex.printStackTrace();
        }
        return jasperPrint;
    }

    @Deprecated
    private static JasperPrint compile(File jrxmlFile, Map<String, Object> parameters) throws JRException, IOException {
        JasperPrint jasperPrint = null;
        try {
            JasperDesign design = JRXmlLoader.load(jrxmlFile);
            JasperReport jasperReport = JasperCompileManager.compileReport(design);

            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
        } catch (JRException ex) {
            ex.printStackTrace();
        }
        return jasperPrint;
    }

    private static JasperPrint compile2(File jrxmlFile, Map data, Map<String, Object> parameters)
            throws JRException, IOException {

        JasperPrint jasperPrint = null;

        try {

            JasperDesign design = JRXmlLoader.load(jrxmlFile);
            JasperReport jasperReport = JasperCompileManager.compileReport(design);

            Object[] array = data.entrySet().toArray();
            System.out.println(Arrays.toString(array));
            String json = new Gson().toJson(data, Map.class);
            InputStream stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
            JsonDataSource ds = new JsonDataSource(stream);

            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, ds);

        } catch (JRException ex) {
            ex.printStackTrace();
        }
        return jasperPrint;
    }

    private static File printToPdf(JasperPrint jasperPrint) throws JRException, IOException {
        File outFile = File.createTempFile("file_" + System.currentTimeMillis(), ".pdf");
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outFile));
        SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
        configuration.setMetadataAuthor("bashizip@gmail.com");
        exporter.setConfiguration(configuration);
        exporter.exportReport();
        return outFile;
    }

    public static void main(String[] args) throws JRException, IOException {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        // params.put("Id", 323);
        params.put("user", "bash");
        data.put("raisonSociale", "Google Inc.");

        File f = new File("rapport_test.jrxml");
        File out = printToPdf(compile2(f, data, params));

        System.out.println(out.getAbsolutePath());
    }

}
