package com.xhh.rpc.protocol;

import cn.hutool.core.util.IdUtil;
import com.xhh.rpc.constant.RpcConstant;
import com.xhh.rpc.model.RpcRequest;
import io.vertx.core.buffer.Buffer;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ProtocolMessageTest {

    @Test
    public void testEncode() throws IOException {
        // 构造消息
        ProtocolMessage<RpcRequest> message = new ProtocolMessage<>();
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
        header.setSerializer((byte) ProtocolMessageSerializerEnum.JDK.getKey());
        header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
        header.setStatus((byte) ProtocolMessageStatusEnum.OK.getValue());
        header.setRequestId(IdUtil.getSnowflakeNextId());
        header.setBodyLength(0);
        RpcRequest request = new RpcRequest();
        request.setServiceName("myService");
        request.setMethodName("myMethod");
        request.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        request.setParameterTypes(new Class[]{String.class});
        request.setParameters(new Object[]{"hello"});
        message.setHeader(header);
        message.setBody(request);

        Buffer encodeBuffer = ProtocolMessageEncoder.encode(message);
        ProtocolMessage<?> protocolMessage = ProtocolMessageDecoder.decode(encodeBuffer);
        Assert.assertNotNull(protocolMessage);
    }
}