package com.xhh.rpc.server.tcp;

import com.xhh.rpc.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VertxTcpServer implements HttpServer {

    @Override
    public void doStart(int port) {
        // 创建 Vertx 实例
        Vertx vertx = Vertx.vertx();

        // 创建 TCP 服务器
        NetServer server = vertx.createNetServer();

        // 监听端口并处理请求
        server.connectHandler(new TcpServerHandler());

        // 启动 TCP 服务器并监听指定端口
        server.listen(port)
                .onSuccess(s ->{
                    log.info("TCP server started on port：{}", server.actualPort());
                })
                .onFailure(failure -> {
                    log.info("Failed to start TCP server: {}", failure.getMessage());
                });
    }

    private byte[] handleRequest(byte[] requestData) {
        // 在这里编写处理请求的逻辑，根据 requestData 构造响应数据并返回
        // 这里只是一个示例，实际应用中需要根据具体的请求类型和业务逻辑进行处理
        return "hello, client!".getBytes();
    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}
