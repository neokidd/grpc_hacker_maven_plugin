package com.dib.grpc;

import javassist.*;

import java.util.logging.*;

public class Main {
    public static void main(String[] args) throws Throwable {
        // ClassPool pool = ClassPool.getDefault();
        // Loader cl = new Loader(pool);

        // CtClass ct =
        // pool.get("io.grpc.examples.helloworld.GreeterGrpc$GreeterBlockingStub");
        // ct.setModifiers(9);

        // Class c =
        // cl.loadClass("io.grpc.examples.helloworld.GreeterGrpc$GreeterBlockingStub");
        // Object stub = c.newInstance();
        // Logger.getLogger("main").info("stub: " + stub);
        Class.forName("io.grpc.examples.helloworld.GreeterGrpc$GreeterBlockingStub");
        Logger.getLogger("main").info("stub: ");
    }
}