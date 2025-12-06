package com.xhh.rpc.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Hessian 序列化器
 */
public class HessianSerializer implements Serializer{
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        HessianOutput ho = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ho = new HessianOutput(bos);
            ho.writeObject(object);
            return bos.toByteArray();
        } finally {
            if (ho != null) {
                ho.close();
            }
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        HessianInput hi = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
            hi = new HessianInput(bis);
            return (T) hi.readObject(clazz);
        } finally {
            if (hi != null) {
                hi.close();
            }
        }
    }
}
