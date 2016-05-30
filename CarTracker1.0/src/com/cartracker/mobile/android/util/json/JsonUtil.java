package com.cartracker.mobile.android.util.json;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by jw362j on 7/30/2014.
 */
public class JsonUtil {
    public static byte[] toByteArray(InputStream input) throws Exception {
        if (input == null) {
            return null;
        }
        ByteArrayOutputStream output = null;
        byte[] result = null;
        try {
            output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 100];
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }
            result = output.toByteArray();
        } finally {
            closeQuietly(input);
            closeQuietly(output);
        }
        return result;
    }

    /**
     * close InputStream
     */
    public static void closeQuietly(InputStream is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * close InputStream
     */
    public static void closeQuietly(OutputStream os) {
        try {
            if (os != null) {
                os.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
