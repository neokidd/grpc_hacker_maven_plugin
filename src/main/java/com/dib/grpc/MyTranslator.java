package com.dib.grpc;

import java.io.IOException;

import javassist.*;

public class MyTranslator implements Translator {
    private ClassPool pool;

    public void start(ClassPool pool) throws NotFoundException, CannotCompileException {
        this.pool = pool;
    }

    public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
        if ("io.grpc.examples.helloworld.GreeterGrpc$GreeterBlockingStub".equals(classname)) {
            CtClass cc = pool.get(classname);
            cc.setModifiers(9);
            try {
                cc.toBytecode();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                cc.writeFile();
                cc.writeFile("target/classes/");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}