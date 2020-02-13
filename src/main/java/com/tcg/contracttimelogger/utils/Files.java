package com.tcg.contracttimelogger.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface Files {

    static String readFile(File file, Charset encoding) throws IOException {
        return FileUtils.readFileToString(file, encoding);
    }

    static String readUTF8File(File file) throws IOException {
        return readFile(file, StandardCharsets.UTF_8);
    }

    static String getAppDirectory() {
        return System.getProperty("user.home") + File.separator + ".tcg" + File.separator + "contract" + File.separator;
    }

    static String getAppFilePath(String relativePath) {
        return getAppDirectory() + relativePath;
    }

    static void ensureDirectoryExists() {
        if(new File(getAppDirectory()).mkdirs()) {
            System.out.println("Application directory does not exist, creating one now.");
        }
    }

    static String readContractsFile() throws IOException {
        ensureDirectoryExists();
        File file = new File(getAppFilePath(AppConstants.CONTRACTS_FILE));
        return readUTF8File(file);
    }

    static void writeFile(String string, File file, Charset encoding) throws IOException {
        FileUtils.writeStringToFile(file, string, encoding);
    }

    static void writeUTF8File(String string, File file) throws IOException {
        writeFile(string, file, StandardCharsets.UTF_8);
    }

}
