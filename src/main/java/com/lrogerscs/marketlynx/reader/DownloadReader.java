package com.lrogerscs.marketlynx.reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * DownloadReader reads last download data (name, interval) and returns a List.
 *
 * @author Lee Rogers
 */
public class DownloadReader {
    /**
     * Reads from a local file and returns a List.
     * @param localFilePath Local file path.
     * @return List of lines.
     */
    public List<String> read(String localFilePath) {
        try {
            return Files.readAllLines(Paths.get(localFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList();
    }
}
