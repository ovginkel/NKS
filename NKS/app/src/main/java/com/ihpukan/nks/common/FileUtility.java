package com.ihpukan.nks.common;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Created by vginkeo on 2017/09/07.
 */

public class FileUtility {

        public static String getFileContents(final File file) throws IOException {
            final InputStream inputStream = new FileInputStream(file);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            final StringBuilder stringBuilder = new StringBuilder();

            boolean done = false;

            while (!done) {
                final String line = reader.readLine();
                done = (line == null);

                if (line != null) {
                    stringBuilder.append(line);
                }
            }

            reader.close();
            inputStream.close();

            return stringBuilder.toString();
        }
}
