package com.cartracker.mobile.android.util.json;
/**
 * Created by jw362j on 7/30/2014.
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class JsonBytes extends JsonValue {

    private byte[] value = null;

    public JsonBytes() {
    }

    public JsonBytes(byte[] val) {
        value = val;
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public String toJsonString() {
        if (value == null) {
            return null;
        }
        return Base64.base64Encode(value);
    }

    protected static JsonBytes parseBytes(String s) {
        return new JsonBytes(Base64.base64Decode(s));
    }

    @Override
    protected void read(DataInputStream dis) throws IOException {
        int n = dis.readInt();
        if (n > -1) {
            value = new byte[n];
            dis.readFully(value);
        }
    }

    @Override
    protected void write(DataOutputStream dos) throws IOException {
        dos.writeByte(TYPE_BYTES);
        if (value != null) {
            dos.writeInt(value.length);
            dos.write(value);
        } else {
            dos.writeInt(-1);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}