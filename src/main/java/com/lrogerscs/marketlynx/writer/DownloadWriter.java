package com.lrogerscs.marketlynx.writer;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * DownloadWriter writes stock download info (name, interval) to a local file.
 */
public class DownloadWriter {
    /**
     * Writes ticker, interval to a local file.
     * @param ticker Stock ticker.
     * @param interval Stock interval.
     * @param localFilePath Local file path.
     */
    public void write(String ticker, String interval, String localFilePath) {
        try {
            FileOutputStream stream = new FileOutputStream(localFilePath);
            stream.write((ticker + "\n" + interval).getBytes());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
