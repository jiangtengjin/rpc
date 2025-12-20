package com.xhh.example.provider;

import com.xhh.example.common.service.UserService;
import com.xhh.rpc.bootstrap.ProviderBootstrap;
import com.xhh.rpc.model.ServiceRegisterInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务提供者示例
 */
@Slf4j
public class ProviderExample {

    public static void main(String[] args) {
        // 要注册的服务
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo<UserServiceImpl> serviceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);

        // 启动服务
        ProviderBootstrap.init(serviceRegisterInfoList);
    }

}
