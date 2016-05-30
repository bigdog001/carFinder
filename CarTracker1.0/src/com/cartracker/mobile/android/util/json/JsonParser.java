package com.cartracker.mobile.android.util.json;
/**
 * Created by jw362j on 7/30/2014.
 */



import com.cartracker.mobile.android.util.SystemUtil;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class JsonParser {

    /**
     * Parse a String to a JsonValue.
     *
     * @param s
     *            a String
     * @return a JsonValue
     */
    public static JsonValue parse(String s) {
        return JsonValue.parseValue(s);
    }

    public static void serialize(JsonValue obj, DataOutputStream dos) throws IOException {
        JsonValue.writeObject(obj, dos);
    }

    /**
     * 序列化对象
     *
     * @param obj
     *            被序列化的对象
     * @param gzip
     *            进行gzip压缩
     * @return
     * @throws IOException
     */
    public static byte[] serialize(JsonValue obj, boolean gzip) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = null;
        if (gzip) {
            GZIPOutputStream gos = new GZIPOutputStream(baos);
            dos = new DataOutputStream(gos);
        } else {
            dos = new DataOutputStream(baos);
        }
        serialize(obj, dos);
        dos.close();
        return baos.toByteArray();
    }

    public static JsonValue deserialize(DataInputStream dis) throws IOException {
        JsonValue obj = JsonValue.readObject(dis);
        try {
            while (dis.read() != -1)
                ;
        } catch (IOException e) {
        }
        return obj;
    }

    /**
     * 反序列化对象
     *
     * @param bytes
     *            数据
     * @param gzip
     *            bytes是否被gzip压缩过
     * @return 对象
     * @throws IOException
     */
    public static JsonValue deserialize(byte[] bytes, boolean gzip) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = null;
        if (gzip) {
            GZIPInputStream gis = new GZIPInputStream(bais);
            dis = new DataInputStream(gis);
        } else {
            dis = new DataInputStream(bais);
        }
        JsonValue obj = deserialize(dis);
        dis.close();
        return obj;
    }

    /**
     * 从数据流中读取对象
     *
     * @param is
     *            输入流
     * @param gzip
     *            是否使用gzip压缩
     * @param contentLength
     *            已知的数据长度，未知则传入<=0的数
     * @return 对象
     * @throws IOException
     */
    public static JsonValue deserialize(InputStream is, boolean gzip, int contentLength) throws IOException {
        byte[] buf = null;
        try {
            buf = SystemUtil.toByteArray(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonValue obj = deserialize(buf, gzip);
        return obj;
    }

}