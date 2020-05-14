package org.cheeryworks.liteql.model.util.json;

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

public abstract class JsonReader {

    public static Map<String, String> readJsonFiles(String path) {
        return readJsonFiles(path, true);
    }

    public static Map<String, String> readJsonFiles(String path, boolean recursive) {
        TreeMap<String, String> jsonFiles = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

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
                            && jarEntry.getName().endsWith(".json")) {
                        String jsonFileRelativePath = jarEntry.getName().substring(
                                path.length() - path.indexOf("!") - 2);


                        if (!recursive && jsonFileRelativePath.indexOf("/") > 0) {
                            continue;
                        }

                        String jsonFileContent = IOUtils.toString(
                                JsonReader.class.getResourceAsStream(
                                        "/" + jarEntry.getName()), StandardCharsets.UTF_8);

                        jsonFiles.put(jsonFileRelativePath, jsonFileContent);
                    }
                }

                jarFile.close();
            } else {
                File typeFilesPath = new File(path);

                if (typeFilesPath.exists()) {
                    Collection<File> typeFiles = FileUtils.listFiles(new File(path), new String[]{"json"}, recursive);

                    for (File typeFile : typeFiles) {
                        String jsonFileRelativePath = typeFile.toURI().toURL().getPath().substring(path.length());
                        String jsonFileContent = IOUtils.toString(typeFile.toURI(), StandardCharsets.UTF_8);

                        jsonFiles.put(jsonFileRelativePath, jsonFileContent);
                    }
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }

        return jsonFiles;
    }

}
