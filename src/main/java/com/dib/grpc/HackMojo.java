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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.*;

// import com.dib.grpc.Main;

/**
 * Goal which touches a timestamp file.
 */
@Mojo(name = "hack", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.COMPILE, requiresDependencyCollection = ResolutionScope.COMPILE)
public class HackMojo extends AbstractMojo {
    public static String outputClassesDir = "";
    public static String outputGeneratedSourceDir = "";
    public static String outputGeneratedTestSourceDir = "";

    public static Map<String, String> service2package = new HashMap();

    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
    private File outputDirectory;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${plugin}", readonly = true) // Maven 3 only
    private PluginDescriptor plugin;

    public void execute() throws MojoExecutionException {

        outputClassesDir = outputDirectory.getAbsolutePath().toString() + "/classes";
        outputGeneratedSourceDir = outputDirectory.getAbsolutePath().toString() + "/generated-sources";
        outputGeneratedTestSourceDir = outputDirectory.getAbsolutePath().toString() + "/generated-test-sources";

        File dir = new File(outputGeneratedSourceDir);
        List<File> files = (List<File>) FileUtils.listFiles(dir, new WildcardFileFilter("*Grpc.java"),
                TrueFileFilter.INSTANCE);
        for (File file : files) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String serviceName = file.getName().substring(0, file.getName().length()-9);
                String line = reader.readLine();
                String packageName = line.substring(8, line.length()-1).trim();
                service2package.put(serviceName, packageName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Translator t = new MyTranslator();
        Loader cl = new Loader();
        ClassPool pool = ClassPool.getDefault();
        
        try {
            List<String> cpls = project.getCompileClasspathElements();
            for (String cp : cpls) {
                pool.insertClassPath(cp);
            }
            pool.insertClassPath(outputDirectory.getAbsolutePath().toString() + "/classes");
            pool.insertClassPath(plugin.getPluginArtifact().getFile().getPath());
            pool.appendSystemPath();

            cl.addTranslator(pool, t);

        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (DependencyResolutionRequiredException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }

        List<String> argList = new ArrayList<>();
        for (String key : service2package.keySet()) {
            String grpcClassName = service2package.get(key) + "." + key + "Grpc";
            String stubClassName = grpcClassName + "$" + key + "BlockingStub";
            argList.add(grpcClassName);
            argList.add(stubClassName);
        }

        String[] args = new String[argList.size()];
        try {
            cl.run("com.dib.grpc.Main", argList.toArray(args));
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return;
        
    }
}
