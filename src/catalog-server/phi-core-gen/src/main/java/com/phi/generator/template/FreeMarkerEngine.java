package com.phi.generator.template;

import com.phi.generator.model.Form;
import freemarker.template.*;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Generates form in react or angular
 * Created by Alex on 19/04/2017.
 */
public class FreeMarkerEngine {

    private Configuration cfg;

    public FreeMarkerEngine() {

        // 1. Configure FreeMarker
        cfg = new Configuration(Configuration.VERSION_2_3_26);

        // Where do we load the templates from:
        cfg.setClassForTemplateLoading(this.getClass(), "/template");

        // Some other recommended settings:
//        cfg.setIncompatibleImprovements(new Version(2, 3, 20));
        cfg.setDefaultEncoding("UTF-8");
//        cfg.setLocale(Locale.US);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    public String generate(Form form, String outputFormat) {
        return generate(form, outputFormat, false);
    }

    public String generate(Form form, String outputFormat, boolean code) {
        Writer stringWriter = new StringWriter();
        try {
            // 2. Proccess template(s)
            // 2.1. Prepare the template input:

//            Map<String, Object> input = new HashMap<>();
//
//            input.put("f", form);

            // 2.2. Get the template
            Template template;
            if (code) {
                template = cfg.getTemplate(outputFormat + "/FormCode.ftl");
            } else {
                template = cfg.getTemplate(outputFormat + "/Form.ftl");
            }

            // 2.3. Generate the output

            // Write output to the console
            // Writer stringWriter = new StringWriter();
//            template.process(input, stringWriter);

//            DefaultObjectWrapper wrppr = new DefaultObjectWrapper();
            template.process(form, stringWriter);

//        // For the sake of example, also write output into a file:
//        Writer fileWriter = new FileWriter(new File("output.html"));
//        try {
//            template.process(input, fileWriter);
//        } finally {
//            fileWriter.close();
//        }

        } catch (IOException ioe) {
            System.err.println("IO ex " + ioe.getMessage());
        } catch (TemplateException te) {
            System.err.println("TemplateException ex " + te.getMessage());
        }
        return stringWriter.toString();
    }
}