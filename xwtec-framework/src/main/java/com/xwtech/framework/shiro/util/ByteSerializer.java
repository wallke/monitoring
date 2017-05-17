package com.xwtech.framework.shiro.util;

import org.apache.shiro.io.SerializationException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.util.Assert;

/**
 * Created by zq on 16/8/18.
 */
public class ByteSerializer {

    private final Converter<Object, byte[]> serializer;
    private final Converter<byte[], Object> deserializer;

    public ByteSerializer() {
        this(new SerializingConverter(), new DeserializingConverter());
    }


    public ByteSerializer(ClassLoader classLoader) {
        this(new SerializingConverter(), new DeserializingConverter(classLoader));
    }



    public ByteSerializer(Converter<Object, byte[]> serializer, Converter<byte[], Object> deserializer) {

        Assert.notNull("Serializer must not be null!");
        Assert.notNull("Deserializer must not be null!");

        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    public Object deserialize(byte[] bytes) {
        if (isEmpty(bytes)) {
            return null;
        }

        try {
            return deserializer.convert(bytes);
        } catch (Exception ex) {
            throw new SerializationException("Cannot deserialize", ex);
        }
    }

    public byte[] serialize(Object object) {
        if (object == null) {
            return EMPTY_ARRAY;
        }
        try {
            return serializer.convert(object);
        } catch (Exception ex) {
            throw new SerializationException("Cannot serialize", ex);
        }
    }

    static final byte[] EMPTY_ARRAY = new byte[0];

    static boolean isEmpty(byte[] data) {
        return (data == null || data.length == 0);
    }

}
