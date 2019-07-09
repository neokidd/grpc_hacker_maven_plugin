package com.dib.grpc;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.project.MavenProject;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javassist.*;

import com.dib.grpc.Main;

/**
 * Goal which touches a timestamp file.
 */
@Mojo(name = "hack", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.COMPILE, requiresDependencyCollection = ResolutionScope.COMPILE)
public class HackMojo extends AbstractMojo {
   public static String targetclasses = "";
   
    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${project.build.directory}",  property = "outputDir", required = true)
    private File outputDirectory;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${plugin}", readonly = true) // Maven 3 only
    private PluginDescriptor plugin;
    
    public void execute() throws MojoExecutionException {
        Translator t = new MyTranslator();
        ClassPool pool = ClassPool.getDefault();
        pool.appendSystemPath();
        List<String> cpls = null;
        
        try {
            cpls = project.getCompileClasspathElements();
        } catch (DependencyResolutionRequiredException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        
        try {
            pool.insertClassPath(outputDirectory.getAbsolutePath().toString() + "/classes");
            targetclasses = outputDirectory.getAbsolutePath().toString() + "/classes";
            // pool.insertClassPath("/Users/oliverluan/.m2/repository/com/dib/maven/plugins/hack-modifier-maven-plugin/1.0-SNAPSHOT/hack-modifier-maven-plugin-1.0-SNAPSHOT.jar");
            pool.insertClassPath(plugin.getPluginArtifact().getFile().getPath());
            // pool.insertClassPath("/Users/oliverluan/.m2/repository/io/grpc/grpc-netty-shaded/1.17.0/grpc-netty-shaded-1.17.0.jar");
            // pool.insertClassPath("/Users/oliverluan/.m2/repository/io/grpc/grpc-stub/1.17.0/grpc-stub-1.17.0.jar");
            // pool.insertClassPath("/Users/oliverluan/.m2/repository/io/grpc/grpc-protobuf/1.17.0/grpc-protobuf-1.17.0.jar");

            for (String cp : cpls) {
                pool.insertClassPath(cp);
            }
        } catch (NotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        Loader cl = new Loader();
        try {
            cl.addTranslator(pool, t);
        } catch (NotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CannotCompileException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            cl.run("com.dib.grpc.Main", null);
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // ClassPool pool = ClassPool.getDefault();
        // CtClass cc;
        // CtClass cc_inner;

        // getLog().info("running zero");

        // try {
        //     getLog().info(outputDirectory.getAbsolutePath());
        //     pool.insertClassPath(outputDirectory.getAbsolutePath().toString() + "/classes");
        // } catch (NotFoundException e1) {
        //     // TODO Auto-generated catch block
        //     e1.printStackTrace();
        // }

        // try {
        //     getLog().info("running first");
            
        //     // cc = pool.get("io.grpc.examples.helloworld.GreeterGrpc");
        //     // cc.setModifiers(1);
        //     // cc.toBytecode();
            
        //     getLog().info("running second");
        //     cc_inner = pool.get("io.grpc.examples.helloworld.GreeterGrpc$GreeterBlockingStub");
        //     int tmp = cc_inner.getModifiers();
        //     cc_inner.setModifiers(9);
        //     tmp = cc_inner.getModifiers();
        //     // cc_inner.toBytecode();
        //     cc_inner.writeFile(outputDirectory.getAbsolutePath().toString() + "/classes");
        //     // cc.writeFile(outputDirectory.getAbsolutePath().toString() + "/classes");
        //     getLog().info("running third " + tmp);
            
        //     // CtClass cc_inner2 = pool.get("io.grpc.examples.helloworld.GreeterGrpc$GreeterBlockingStub");
        //     // tmp = cc_inner2.getModifiers();
        //     // getLog().info("running fourth" + tmp);

        // } catch (NotFoundException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        //     getLog().info(e);
        // } catch (IOException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        //     getLog().info(e);
        // } catch (CannotCompileException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        //     getLog().info(e);
        // }

        return;
        
    }
}
