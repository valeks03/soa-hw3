package org.example;


import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        Server server = ServerBuilder.forPort(50050)
                .addService(new PostServiceImp()).build();

        server.start();

        System.out.println("Server started, listening on " + server.getPort());

        server.awaitTermination();
    }
}