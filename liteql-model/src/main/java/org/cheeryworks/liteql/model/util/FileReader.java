package org.cheeryworks.liteql.model.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class FileReader {

    public static Map<String, String> readJsonFilesRecursively(String path) {
        return readFilesRecursively(path, "json");
    }

    public static Map<String, String> readFilesRecursively(String path, String suffix) {
        return readFiles(path, suffix, true);
    }

    public static Map<String, String> readFiles(String path, String suffix, boolean recursively) {
        TreeMap<String, String> files = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        try {
            if (!path.endsWith("/")) {
                path += "/";
            }

            if (path.contains(".jar")) {
                JarFile jarFile = new JarFile(path.substring(5, path.indexOf("!")));
                Enumeration<JarEntry> typeEntries = jarFile.entries();
                while (typeEntries.hasMoreElements()) {
                    JarEntry jarEntry = typeEntries.nextElement();
                    if (jarEntry.getName().startsWith(path.substring(path.indexOf("!") + 2))
                            && jarEntry.getName().endsWith("." + suffix)) {
                        String fileRelativePath = jarEntry.getName().substring(
                                path.length() - path.indexOf("!") - 2);


                        if (!recursively && fileRelativePath.indexOf("/") > 0) {
                            continue;
                        }

                        String fileContent = IOUtils.toString(
                                FileReader.class.getResourceAsStream(
                                        "/" + jarEntry.getName()), StandardCharsets.UTF_8);

                        files.put(fileRelativePath, fileContent);
                    }
                }

                jarFile.close();
            } else {
                File typeFilesPath = new File(path);

                if (typeFilesPath.exists()) {
                    Collection<File> typeFiles = FileUtils.listFiles(new File(path), new String[]{suffix}, recursively);

                    for (File typeFile : typeFiles) {
                        String fileRelativePath = typeFile.toURI().toURL().getPath().substring(path.length());
                        String fileContent = IOUtils.toString(typeFile.toURI(), StandardCharsets.UTF_8);

                        files.put(fileRelativePath, fileContent);
                    }
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }

        return files;
    }

}
