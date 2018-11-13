/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bash
 */
public class PrintingTools {

    public static File imageToPDF(File input) throws IOException {

        Document document = new Document(PageSize.A4);

        File output = File.createTempFile("temp" + System.currentTimeMillis(), ".pdf");
        try {
            FileOutputStream fos = new FileOutputStream(output);
            PdfWriter writer = PdfWriter.getInstance(document, fos);
            writer.open();

            Image im = Image.getInstance(input.getAbsolutePath());
            im.setAbsolutePosition(0, 0);
            im.scalePercent(100);
            
            document.setPageSize(im);
            document.open();
            document.add(im);
            document.close();
            writer.close();

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        return output;
    }

    public static File createPdf(Image[] images) throws IOException, DocumentException {

        File destFile = File.createTempFile("scan" + System.currentTimeMillis(), "pdf");

        String[] IMAGES = {}; //base64 string  encoded images
        Image img = Image.getInstance(images[0]);
        Document document = new Document(img);

        PdfWriter.getInstance(document, new FileOutputStream(destFile));

        document.open();

        for (String image : IMAGES) {
            img = Image.getInstance(image);
            document.setPageSize(img);
            document.newPage();
            img.setAbsolutePosition(0, 0);
            document.add(img);
        }

        document.close();

        return destFile;
    }

    public static File mergePdfFiles(List<File> files) {
        File merged = null;

        List<InputStream> list = new ArrayList<>();

        try {
            merged = File.createTempFile("merge" + System.currentTimeMillis(), "pdf");

            for (File f : files) // Source pdfs
            {
                list.add(new FileInputStream(f));
            }

            // Resulting pdf
            OutputStream out = new FileOutputStream(merged);

            doMerge(list, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return merged;
    }

    /**
     * Merge multiple pdf into one pdf
     *
     * @param list of pdf input stream
     * @param outputStream output file output stream
     * @throws DocumentException
     * @throws IOException
     */
    public static void doMerge(List<InputStream> list, OutputStream outputStream)
            throws DocumentException, IOException {

        Document document = new Document();
        PdfCopy copy = new PdfCopy(document, outputStream);
        document.open();

        for (InputStream in : list) {

            PdfReader reader = new PdfReader(in);
            copy.addDocument(reader);

        }

        copy.close();
        document.close();
        outputStream.flush();
        outputStream.close();

    }

}
