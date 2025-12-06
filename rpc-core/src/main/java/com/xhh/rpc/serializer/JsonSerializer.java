package com.xhh.rpc.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xhh.rpc.model.RpcRequest;
import com.xhh.rpc.model.RpcResponse;

import java.io.IOException;

/**
 * json 序列化器
 */
public class JsonSerializer implements Serializer{

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public <T> byte[] serialize(T object) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(object);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        T obj = OBJECT_MAPPER.readValue(bytes, clazz);
        if (obj instanceof RpcRequest) {
            return handleReqeust((RpcRequest) obj, clazz);
        }
        if (obj instanceof RpcResponse) {
            return handleResponse((RpcResponse) obj, clazz);
        }
        return obj;
    }

    /**
     * 由于 Object 的原始对象会被擦除，导致反序列化时会被作为 LinkedHashMap 无法转换成原始对象，因此这里做了特殊的处理
     *
     * @param rpcRequest       rpc 请求
     * @param clazz            类型
     * @return
     * @param <T>
     */
    private <T> T handleReqeust(RpcRequest rpcRequest, Class<T> clazz) throws IOException {
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        Object[] parameters = rpcRequest.getParameters();

        // 循环处理每个参数的类型
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            // 如果类型不同，则重新处理一下类型
            if (!parameterType.isAssignableFrom(parameters[i].getClass())) {
                byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(parameters[i]);
                parameters[i] = OBJECT_MAPPER.readValue(bytes, parameterType);
            }
        }
        return clazz.cast(rpcRequest);
    }

    /**
     * 由于 Object 的原始对象会被擦除，导致反序列化时会被作为 LinkedHashMap 无法转换成原始对象，因此这里做了特殊的处理
     *
     * @param rpcResponse       rpc 请求
     * @param clazz            类型
     * @return
     * @param <T>
     */
    private <T> T handleResponse(RpcResponse rpcResponse, Class<T> clazz) throws IOException {
        // 处理响应数据
        byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(rpcResponse.getData());
        rpcResponse.setData(OBJECT_MAPPER.readValue(bytes, rpcResponse.getData().getClass()));
        return clazz.cast(rpcResponse);
    }
}
