package com.lrogerscs.marketlynx.downloader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

/**
 * StockFileDownloader downloads a requested stock's data and places it in a local file.
 *
 * @author Lee Rogers
 */
public class StockFileDownloader {
    /**
     * downloadFile downloads a requested stock's data and places it in the provided local file.
     * @param fileURL URL of data to download.
     * @param localFilePath Local file path.
     */
    public void downloadFile(String fileURL, String localFilePath) {
        try {
            URL url = new URL(fileURL);
            ReadableByteChannel byteChannel = Channels.newChannel(url.openStream());
            FileOutputStream stream = new FileOutputStream(localFilePath);
            FileChannel fileChannel = stream.getChannel();

            fileChannel.transferFrom(byteChannel, 0, Long.MAX_VALUE);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
