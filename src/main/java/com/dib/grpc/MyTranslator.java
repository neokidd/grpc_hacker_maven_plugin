package com.dib.grpc;

import java.io.IOException;
import java.util.List;

import javassist.*;
import javassist.bytecode.ClassFile;
import javassist.bytecode.InnerClassesAttribute;

public class MyTranslator implements Translator {

    public void start(ClassPool pool) throws NotFoundException, CannotCompileException {
    }

    public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {

        for (String key : HackMojo.service2package.keySet()) {
            String grpcClassName = HackMojo.service2package.get(key) + "." + key + "Grpc";
            String stubClassName = grpcClassName + "$" + key +  "BlockingStub";
            if (grpcClassName.equals(classname)) { 
                CtClass cc = pool.get(classname);
                ClassFile cf = cc.getClassFile();
                cc.setModifiers(1);
                List al = cf.getAttributes();
                InnerClassesAttribute inner = null;
                for (Object a : al) {
                    if (a instanceof InnerClassesAttribute) {
                        inner = (InnerClassesAttribute) a;
                    }
                }
                String ic = null;
                int i = 0;
                for (; i < inner.tableLength(); i++) {
                    ic = inner.innerClass(i);
                    if (stubClassName.equals(ic)) {
                        break;
                    }
                }

                inner.setAccessFlags(i, 9);

                try {
                    cc.toBytecode();
                    cc.writeFile(HackMojo.outputClassesDir);
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }

            if (stubClassName.equals(classname)) {
                CtClass cc = pool.get(classname);
                ClassFile cf = cc.getClassFile();
                cc.setModifiers(9);

                List al = cf.getAttributes();
                InnerClassesAttribute inner = null;
                for (Object a : al) {
                    if (a instanceof InnerClassesAttribute) {
                        inner = (InnerClassesAttribute) a;
                    }
                }
                String ic = null;
                int i = 0;
                for (; i < inner.tableLength(); i++) {
                    ic = inner.innerClass(i);
                    if (stubClassName.equals(ic)) {
                        break;
                    }
                }

                inner.setAccessFlags(i, 9);

                try {
                    cc.toBytecode();
                    cc.writeFile(HackMojo.outputClassesDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}