package com.grew.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import javax.ws.rs.core.Response;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import com.grew.process.ProcessUploadResponse;

/**
 * @Since 2014
 * @author Bashizip
 */
public class UploadsUtils {

    private static final String UPLOADS_BASE_URL = getFilesBaseDir() + File.separator + "X-PROCESS_DATA";
    public final static String FILES_DIR = UPLOADS_BASE_URL + "/files/";
    private static final String CURRENT_UPLOAD_DIR = FILES_DIR + getConextDir();

    static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    /**
     * TODO mettre le chemin final en production
     *
     * @return
     */
    private static String getFilesBaseDir() {
        return System.getProperty("user.home");
    }

    private static String getFinalFileName(String sourceName) {
        // String test = "aaaammjj_name_flwId_seq.pdf";
        //Todo append aaaammjj

        String prefix = DATE_FORMAT.format(new Date());

        return prefix.concat("_").concat(sourceName);
    }

    private static String getConextDir() {
        //aaaammjj_name_flwId_seq.pdf
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        File yearDir = new File(year + "");
        File monthDir = new File(yearDir, month + "");

        if (!monthDir.exists()) {
            monthDir.mkdirs();
        }

        return monthDir.getPath();
    }

    public static String uploadFile(InputStream fis,String taskId,String siteId,  String nameWithExt, long seq, String fileType) {

        String baseName = FilenameUtils.getBaseName(nameWithExt);

        String filename = getFinalFileName(baseName)
                .concat("_").concat(taskId).concat("_").concat(siteId);

        String uploadedFileLocation = CURRENT_UPLOAD_DIR + File.separator;

        File finalFile = writeToFile(fis, uploadedFileLocation, filename, fileType);

        String output = "File uploaded to : " + finalFile.getAbsolutePath();
        System.out.println(output);

        return finalFile.getName();
    }

    public static File writeToFile(InputStream fis, String dir, String fileName, String fileType) {

        File parent = null;
        File file = null;

        try {

            FileOutputStream fos = null;

            parent = new File(dir);

            if (!parent.exists()) {
                parent.mkdirs();
            }

            file = new File(dir + File.separator + fileName + "." + fileType);
            fos = new FileOutputStream(file);

            IOUtils.copy(fis, fos);

            fos.close();
            fis.close();

            return file;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return file;
    }

    public static void main(String[] args) {
        System.out.println(UPLOADS_BASE_URL);
    }

    public static Response findFile(final String fileName) throws ParseException, FileNotFoundException {

        //Todo parse fileName
        // pattern : aaaammjj_name_flwId_seq.pdf
        File dir = new File(FILES_DIR, parseFileDirLocationFromName(fileName));

        File[] founds = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                // checks for a filename consisting of uuid with a custom extension
                return name.startsWith(fileName);
            }
        });

        if (founds.length < 1) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ProcessUploadResponse("File not found", fileName)).build();

        }

        File found = founds[0];

        Response.ResponseBuilder response = Response.ok(found);

        response.header("Content-Disposition",
                "attachment; filename=" + fileName);

        response.header("Content-Length", found.length());

        //response.header("Content-Type", "application/pdf");
        return response.build();
    }

    public static File findFile64(final String fineName) throws FileNotFoundException, ParseException {
        //Todo parse fileName
        // pattern : aaaammjj_name_flwId_seq.pdf
        File dir = new File(FILES_DIR, parseFileDirLocationFromName(fineName));

        File[] founds = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                // checks for a filename consisting of uuid with a custom extension
                return name.startsWith(fineName);
            }
        });

        if (founds.length < 1) {
            throw new FileNotFoundException();

        }

        File found = founds[0];

        return found;
    }

    private static String parseFileDirLocationFromName(String sourceName) throws ParseException {

        //   String test = "aaaammjj_name_flwId_seq.pdf";
        StringTokenizer tokenizer = new StringTokenizer(sourceName, "_");
        String ss = tokenizer.nextToken();

        Calendar cal = Calendar.getInstance();
        cal.setTime(DATE_FORMAT.parse(ss));

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        // int day = cal.get(Calendar.DAY_OF_MONTH);

        File yearDir = new File(year + "");
        File monthDir = new File(yearDir, month + "");

        return monthDir.getPath();
    }

}
