package com.phi.generator;

import com.phi.generator.template.FreeMarkerEngine;
import com.phi.generator.model.Form;
import com.phi.generator.parser.SaxParser;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Maven plugin.
 * Converts mmgp files into Angular .ts or react. jsx froms.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class PhiGeneratorMojo extends AbstractMojo {

    @Parameter(property = "generate.outputFormat", defaultValue = "Angular") //or React
    private String outputFormat;

    @Parameter(property = "generate.inputDirectory", defaultValue = "${project.basedir}/src/main/modules/")
    private String inputDirectory;

    @Parameter(property = "generate.outputDirectory", defaultValue = "${project.build.directory}/src/main/webapp/")
    private String outputDirectory;

    @Parameter(property = "generate.dictionaryProperties", defaultValue = "${project.build.directory}/dictionary.properties")
    private String dictionaryProperties;

    private boolean recursiveInput = true;

    private String inputFileExtension = "mmgp";
    private String reportFileExtension = "report";

    private static Properties dictionaryPropertiez;

    private static FreeMarkerEngine engine = new FreeMarkerEngine();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("In " + inputDirectory);
        getLog().info("Out " + outputDirectory);

        getLog().debug("Read files from: " + inputDirectory);

        try {

            dictionaryPropertiez =  new Properties();
            InputStream propertiesIs = null;
            try {
                propertiesIs = new FileInputStream(dictionaryProperties);
                dictionaryPropertiez.load(propertiesIs);
            } catch (Exception e) {
                throw new IllegalArgumentException("Unable to load dictionary.properties from " + dictionaryProperties + " Error: " + e.getMessage(), e);
            }
            finally {
                if (propertiesIs != null) {
                    try {
                        propertiesIs.close();
                    } catch (IOException ioe) {
                        getLog().error("Error closing dictionary.properties", ioe);
                    }
                }
            }


            File inputFile = new File(inputDirectory);
            if (!inputFile.exists()) {
                getLog().info("There is no input folder for the project. Skipping.");
                return;
            }

            List<File> inFiles = getFilesAsArray(FileUtils.iterateFiles(new File(inputDirectory), new String[] { inputFileExtension, reportFileExtension }, recursiveInput));

            for (File file : inFiles) {
                getLog().debug("File getName() " + file.getName());
                getLog().debug("File getAbsolutePath() " + file.getAbsolutePath());
                getLog().debug("File getPath() " + file.getPath());


                String relativePath = inputFile.toURI().relativize(file.toURI()).getPath();

                Form form = SaxParser.parse(file, relativePath);

                String generatedTemplate;
                String generatedCode = null;

                String pathCode = null;

                if ("Angular".equals(outputFormat)) {
                    relativePath = relativePath.replace(".mmgp", ".html");
                    pathCode = relativePath.replace(".html", ".ts");
                    generatedTemplate = engine.generate(form, "angular");
                    generatedCode = engine.generate(form, "angular", true);
                } else if ("React".equals(outputFormat)) {
                    relativePath = relativePath.replace(".mmgp", ".jsx");
                    generatedTemplate = engine.generate(form, "react");
                } else {
                    throw new IllegalArgumentException("Output format can be Angular or React! " + outputFormat);
                }

                File templateFile = new File(outputDirectory + relativePath);
                FileUtils.writeStringToFile(templateFile, generatedTemplate, Charset.defaultCharset().name());

                if (pathCode != null) {
                    File codeFile = new File(outputDirectory + pathCode);
                    FileUtils.writeStringToFile(codeFile, generatedCode, Charset.defaultCharset().name());
                }

            }
        } catch (Exception e) {
            throw new MojoExecutionException("Unable to generate file " + e.getMessage(), e);
        }
    }



    private List<File> getFilesAsArray(Iterator<File> iterator) {
        List<File> files = new ArrayList<>();
        while (iterator.hasNext()) {
            files.add(iterator.next());
        }
        return files;
    }

    private static Pattern pattern = Pattern.compile("^(\\w*)Action[^']*'([^']*)'.*$");

//    Pattern patternIsTemporary = Pattern.compile("^(\\w*)Action\\.temporary\\[.*$"); FIXME check temporary
//    Matcher matcher = pattern.matcher(binding);

    public static String getDomain(String binding) {
        if (binding != null) {
            Matcher matcher = pattern.matcher(binding);
            if (/*!isTemporary &&*/ matcher.matches()) {
                binding = matcher.group(1) + "." + matcher.group(2);
            }

            return (String) dictionaryPropertiez.get(binding);
        } else {
            return null;
        }
    }
}
