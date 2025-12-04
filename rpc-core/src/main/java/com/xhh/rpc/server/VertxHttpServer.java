package com.xhh.rpc.server;

import io.vertx.core.Vertx;

public class VertxHttpServer implements HttpServer{
    @Override
    public void doStart(int port) {
        // 创建 Vertx 实例
        Vertx vertx = Vertx.vertx();

        // 创建 HTTP 服务器
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();

        // 监听端口并处理请求
        server.requestHandler(new HttpServerHandler());

        // 启动 http 服务器并监听指定端口
        server.listen(port)
                .onSuccess(s ->{
                    System.out.println("HTTP server started on port " + server.actualPort());
                })
                .onFailure(failure -> {
                    System.out.println("Failed to start HTTP server: " + failure.getMessage());
                });
    }
}
