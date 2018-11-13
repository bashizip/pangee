/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;

/**
 *
 * @author pbash
 */
public class StreamUtil {

    public static final String PREFIX = "bashtemp" + System.currentTimeMillis();
    public static final String SUFFIX = ".tmp";

    public static File stream2file(InputStream in) throws IOException {
        final File tempFile = File.createTempFile(PREFIX, SUFFIX);
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }
        return tempFile;
    }

    public static File stream2file(InputStream in, String suffix) throws IOException {
        final File tempFile = File.createTempFile(PREFIX, suffix);
        //  tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }
        return tempFile;
    }

    public static String getFileMimeType(String file) {
        Path source = Paths.get(file);
        Tika tika = new Tika();
        String mimeType = tika.detect(file);
        if (mimeType == null) {
            mimeType = "file/unkown";
        }
        return mimeType;
    }

}
