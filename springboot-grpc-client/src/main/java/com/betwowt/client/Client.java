package com.betwowt.client;


import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.examples.GreeterGrpc;
import io.grpc.examples.GreeterOuterClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class Client {

    public static final Logger log = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        log.debug("中文测试");
        ConfigurableApplicationContext context = new SpringApplicationBuilder(Client.class)
                .properties(Collections.singletonMap("server.port",8081))
                .run(args);

        DiscoveryClient discoveryClient = context.getBean(DiscoveryClient.class);

        List<ServiceInstance> instances = discoveryClient.getInstances("grpc-server-example");

        if (CollectionUtils.isEmpty(instances)){
            log.error("grpc server not start");
            context.close();
            return;
        }

        ServiceInstance serviceInstance = instances.get(0);

        final ManagedChannel channel = ManagedChannelBuilder.forAddress(serviceInstance.getHost(),serviceInstance.getPort())
                .usePlaintext()
                .build();

        GreeterGrpc.GreeterFutureStub futureStub = GreeterGrpc.newFutureStub(channel);

        ListenableFuture<GreeterOuterClass.HelloReply> res = futureStub.sayHello(GreeterOuterClass.HelloRequest.newBuilder().setName("betwowt").build());

        String message = res.get().getMessage();
        log.info("server response:"+message);

        channel.shutdown();
        context.close();
    }



}
