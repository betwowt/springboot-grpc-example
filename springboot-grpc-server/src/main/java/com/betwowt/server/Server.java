package com.betwowt.server;

import io.grpc.examples.GreeterGrpc;
import io.grpc.examples.GreeterOuterClass;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class Server {

    private static final Logger log = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        log.debug("测试中文");
        SpringApplication.run(Server.class,args);
    }


    @GRpcService
    public static class GreeterService extends GreeterGrpc.GreeterImplBase {
        @Override
        public void sayHello(GreeterOuterClass.HelloRequest request, StreamObserver<GreeterOuterClass.HelloReply> responseObserver) {
            responseObserver.onNext(GreeterOuterClass.HelloReply.newBuilder().setMessage("hi,i am server").build());
            responseObserver.onCompleted();
        }
    }


}
