package com.dib.grpc;

import java.io.IOException;
import java.util.List;

import javassist.*;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.InnerClassesAttribute;

public class MyTranslator implements Translator {
    private ClassPool pool;

    public void start(ClassPool pool) throws NotFoundException, CannotCompileException {
        this.pool = pool;
    }

    public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
        if ("io.grpc.examples.helloworld.GreeterGrpc".equals(classname)) {
            CtClass cc = pool.get(classname);
            ClassFile cf = cc.getClassFile();
            cc.setModifiers(1);
            // ConstPool cp = new ConstPool("io.grpc.examples.helloworld.GreeterGrpc");
            ConstPool cp = cf.getConstPool();
            List al = cf.getAttributes();
            InnerClassesAttribute inner = (InnerClassesAttribute)al.get(1);
            // InnerClassesAttribute inner = new InnerClassesAttribute(cp);
            String ic = null;
            int i = 0;
            for (; i < inner.tableLength(); i++) {
                ic = inner.innerClass(i);
                if ("io.grpc.examples.helloworld.GreeterGrpc$GreeterBlockingStub".equals(ic)) {
                    break;
                }
            }

            inner.setAccessFlags(i, 9);
            try {
                cc.toBytecode();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                // cc.writeFile(HackMojo.targetclasses);
                cc.writeFile("target/classes/");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if ("io.grpc.examples.helloworld.GreeterGrpc$GreeterBlockingStub".equals(classname)) {
            CtClass cc = pool.get(classname);
            ClassFile cf = cc.getClassFile();
            cc.setModifiers(9);
            // cc.setModifiers(Modifier.PUBLIC | Modifier.);
            // try {
            //     cc.toBytecode();
            // } catch (IOException e) {
            //     // TODO Auto-generated catch block
            //     e.printStackTrace();
            // }
            // ConstPool cp = new ConstPool("io.grpc.examples.helloworld.GreeterGrpc");
            ConstPool cp = cf.getConstPool();
            List al = cf.getAttributes();
            InnerClassesAttribute inner = (InnerClassesAttribute) al.get(2);
            // InnerClassesAttribute inner = new InnerClassesAttribute(cp);
            String ic = null;
            int i = 0;
            for (; i < inner.tableLength(); i++) {
                ic = inner.innerClass(i);
                if ("io.grpc.examples.helloworld.GreeterGrpc$GreeterBlockingStub".equals(ic)) {
                    break;
                }
            }
            inner.setAccessFlags(i, 9);

            try {
                cc.toBytecode();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                // cc.writeFile(HackMojo.targetclasses);
                cc.writeFile("target/classes/");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
    }
}