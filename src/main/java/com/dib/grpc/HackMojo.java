package com.dib.grpc;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javassist.*;

/**
 * Goal which touches a timestamp file.
 */
@Mojo(name = "hack", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class HackMojo extends AbstractMojo {
    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
    private File outputDirectory;

    public void execute() throws MojoExecutionException {
        Translator t = new MyTranslator();
        ClassPool pool = ClassPool.getDefault();
        CtClass cc;

        try {
            getLog().info(outputDirectory.getAbsolutePath());
            pool.insertClassPath(outputDirectory.getAbsolutePath().toString() + "/classes");
        } catch (NotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            cc = pool.get("io.grpc.examples.helloworld.GreeterGrpc");
            cc.setModifiers(1);
            cc.toBytecode();
            cc.writeFile(outputDirectory.getAbsolutePath().toString() + "/classes");

            cc = pool.get("io.grpc.examples.helloworld.GreeterGrpc$GreeterBlockingStub");
            cc.setModifiers(9);
            cc.toBytecode();
            cc.writeFile(outputDirectory.getAbsolutePath().toString() + "/classes");
        } catch (NotFoundException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CannotCompileException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Loader cl = new Loader();
        // try {
        //     cl.addTranslator(pool, t);
        // } catch (NotFoundException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // } catch (CannotCompileException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        // String[] args = null; 

        // try {
        //     cl.run("com.dib.grpc.Main", args);
        // } catch (Throwable e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        
    }
}
