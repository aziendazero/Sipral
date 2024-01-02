package com.phi.label;

import java.io.*;
import java.util.*;

/**
 * Fix duplicate labels
 *
 * Find duplicate labels and remove from messages_**.properties of module that doesen't contain form with that label
 *
 * @author Alex
 */
public class FixLabels {

    private static String inPath = System.getProperty("user.dir")+File.separator+File.separator + "../../ssa/webapp/src/main/modules/";

    class LblDescriptor {
        public File module;
        public String lblContent;

        public LblDescriptor(File module, String lblContent) {
            this.module = module;
            this.lblContent = lblContent;
        }
    }

    class ModuleProps {
/*        public Properties en;
        public Properties it;
        public Properties de;*/

        public Properties properties;

        public String path;

        public String label;

        public List<String> keysToBeRemoved = new ArrayList<String>();
    }

    public void fixaaaaa() {


        HashMap<String, LblDescriptor> en = new HashMap<String, LblDescriptor>();
        HashMap<String, LblDescriptor> it = new HashMap<String, LblDescriptor>();
        HashMap<String, LblDescriptor> de = new HashMap<String, LblDescriptor>();

        HashMap<String, ModuleProps> modulesProperties = new HashMap<String, ModuleProps>();

        File rootFolder = new File( inPath );

        if (!rootFolder.exists()) {
            return;
        }

        File[] subFolders = rootFolder.listFiles();

        for (File module : subFolders) {

            System.out.println("MODULE: " + module.getName());

            File[] moduleSubFolders = module.listFiles();

            for (File moduleSf : moduleSubFolders) {

                if (moduleSf.isDirectory() && moduleSf.getName().equals("LABELS")) {

                    File[] labelz = moduleSf.listFiles();

/*                    ModuleProps moduleProps = new ModuleProps();
                    moduleProps.path = moduleSf.getPath();

                    modulesProperties.put(module.getName(), moduleProps);*/

                    for (File label : labelz) {

                        /*if (label.getName().equals("messages_en.properties")) {
                            moduleProps.en = loadPropertyz(label.getPath());
                        } else if (label.getName().equals("messages_it.properties")) {
                            moduleProps.it = loadPropertyz(label.getPath());
                        } else if (label.getName().equals("messages_de.properties")) {
                            moduleProps.de = loadPropertyz(label.getPath());
                        }*/

                        if (label.getName().startsWith("messages_")) {

                            ModuleProps moduleProps = new ModuleProps();
                            moduleProps.path = moduleSf.getPath();
                            moduleProps.label = label.getName();

                            modulesProperties.put(module.getName() + label.getName(), moduleProps);

                            moduleProps.properties = loadPropertyz(label.getPath());

                            System.out.println("Finding duplicates for language " + label.getName());

                            //findDuplicatez(moduleProps, modulesProperties, module, rootFolder, label);

                            if (label.getName().equals("messages_en.properties")) {
                                findDuplicatez(en, moduleProps, modulesProperties, module, rootFolder, label);
                            } else if (label.getName().equals("messages_it.properties")) {
                                findDuplicatez(it, moduleProps, modulesProperties, module, rootFolder, label);
                            } else if (label.getName().equals("messages_de.properties")) {
                                findDuplicatez(de, moduleProps, modulesProperties, module, rootFolder, label);
                            }
                        }

                    }


/*                    for (Object keyz : moduleProps.it.keySet()) {

                        if (!it.containsKey(keyz)) {

                            //New label
                            it.put(keyz.toString(), new LblDescriptor(module, moduleProps.it.get(keyz).toString()));

                        } else {

                            //Duplicate label: find containing forms
                            String widgetKey = keyz.toString();

                            if (keyz.toString().startsWith("alt_")) {
                                widgetKey = keyz.toString().substring(4, keyz.toString().length());
                            }

                            //Find in previous module
                            List<String> formWithLabelInPrevMod = new ArrayList<String>();

                            findLabelInForm(formWithLabelInPrevMod, it.get(keyz).module, rootFolder, widgetKey);

                            boolean isInPreviousMod = formWithLabelInPrevMod.size() > 0;


                            //Find in current module
                            List<String> formWithLabelInCurrMod = new ArrayList<String>();

                            findLabelInForm(formWithLabelInCurrMod, module, rootFolder, widgetKey);

                            boolean isInCurrentMod = formWithLabelInCurrMod.size() > 0;



                            if (isInPreviousMod) {
                                if (isInCurrentMod) {
                                    System.out.println("--------> ERROR: " + keyz + " present in two modules: " + it.get(keyz).module.getName() + " and " + module.getName());
                                } else {
                                    System.out.println("REMOVE " + keyz + " from module: " + module.getName());

                                    moduleProps.keysToBeRemoved.add(keyz.toString());
                                }
                            } else {
                                ModuleProps previousModuleProps = modulesProperties.get(it.get(keyz).module.getName());
                                if (isInCurrentMod) {
                                    System.out.println("REMOVE " + keyz + " from module: " + it.get(keyz).module.getName());

                                    previousModuleProps.keysToBeRemoved.add(keyz.toString());

                                } else {
                                    System.out.println("REMOVE " + keyz + " from module: " + it.get(keyz).module.getName() + " and " + module.getName());

                                    previousModuleProps.keysToBeRemoved.add(keyz.toString());

                                    moduleProps.keysToBeRemoved.add(keyz.toString());
                                }
                            }
                        }
                    }*/
                }
            }
        }

        //Remove keys

        for (Object key : modulesProperties.keySet()) {

            ModuleProps moduleProps = modulesProperties.get(key);

            for (String keyToBeRemoved : moduleProps.keysToBeRemoved) {

                /*moduleProps.en.remove(keyToBeRemoved);
                moduleProps.it.remove(keyToBeRemoved);
                moduleProps.de.remove(keyToBeRemoved);*/

                moduleProps.properties.remove(keyToBeRemoved);

            }

            //Save properties

            /*savePropertyz( moduleProps.en, moduleProps.path + "/messages_en.properties");
            savePropertyz( moduleProps.it, moduleProps.path + "/messages_it.properties");
            savePropertyz( moduleProps.de, moduleProps.path + "/messages_de.properties");*/

            savePropertyz( moduleProps.properties, moduleProps.path + "/" + moduleProps.label);

        }


    }

    private void findDuplicatez (HashMap<String, LblDescriptor> keysInModules, ModuleProps moduleProps, HashMap<String, ModuleProps> modulesProperties, File module, File rootFolder, File label) {

        for (Object keyz : moduleProps.properties.keySet()) {

            if (!keysInModules.containsKey(keyz)) {

                //New label
                keysInModules.put(keyz.toString(), new LblDescriptor(module, moduleProps.properties.get(keyz).toString()));

            } else {

                //Duplicate label: find containing forms
                String widgetKey = keyz.toString();

                if (keyz.toString().startsWith("alt_")) {
                    widgetKey = keyz.toString().substring(4, keyz.toString().length());
                }

                //Find in previous module
                List<String> formWithLabelInPrevMod = new ArrayList<String>();

                findLabelInForm(formWithLabelInPrevMod, keysInModules.get(keyz).module, rootFolder, widgetKey);

                boolean isInPreviousMod = formWithLabelInPrevMod.size() > 0;


                //Find in current module
                List<String> formWithLabelInCurrMod = new ArrayList<String>();

                findLabelInForm(formWithLabelInCurrMod, module, rootFolder, widgetKey);

                boolean isInCurrentMod = formWithLabelInCurrMod.size() > 0;



                if (isInPreviousMod) {
                    if (isInCurrentMod) {
                        System.out.println("--------> ERROR: " + keyz + " present in two modules: " + keysInModules.get(keyz).module.getName() + " and " + module.getName());
                    } else {
                        System.out.println("REMOVE " + keyz + " from module: " + module.getName());

                        moduleProps.keysToBeRemoved.add(keyz.toString());
                    }
                } else {
                    ModuleProps previousModuleProps = modulesProperties.get(keysInModules.get(keyz).module.getName() + label.getName());
                    if (isInCurrentMod) {
                        System.out.println("REMOVE " + keyz + " from module: " + keysInModules.get(keyz).module.getName());

                        previousModuleProps.keysToBeRemoved.add(keyz.toString());

                    } else {
                        System.out.println("REMOVE " + keyz + " from module: " + keysInModules.get(keyz).module.getName() + " and " + module.getName());

                        previousModuleProps.keysToBeRemoved.add(keyz.toString());

                        moduleProps.keysToBeRemoved.add(keyz.toString());
                    }
                }
            }
        }
    }

    private void findLabelInForm(List<String> formContenentiLabel, File module, File rootFolder, String lblKey) {

        List<File> formz = new ArrayList<File>();

        File[] moduleSubFolderz = module.listFiles();

        for (File mofFolder : moduleSubFolderz) {

            if (mofFolder.isDirectory()) {
                findLabelInForm(formContenentiLabel, mofFolder, rootFolder, lblKey);
            } else if ( mofFolder.getName().endsWith(".mmgp") ) {
                formz.add(mofFolder);
            }
        }

        for (File form : formz) {
            List<String> formLines = parseFile(form.getPath());

            for (String line : formLines) {
                if (line.contains(lblKey)) {
                    String relativePath = rootFolder.toURI().relativize(form.toURI()).getPath();
                    formContenentiLabel.add(relativePath);
                }
            }
        }
    }

    private Properties loadPropertyz(String labelPath) {
        Properties lblPropz = null;
        FileInputStream lblIS = null;
        try {

            lblIS = new FileInputStream(labelPath);

            lblPropz = new SortedProperties();
            lblPropz.load(lblIS);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                lblIS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lblPropz;
    }

    public void savePropertyz(Properties props, String path) {
        try {

            File f = new File(path);
            OutputStream out = new FileOutputStream( f );
            props.store(out, null);
        }
        catch (Exception e ) {
            e.printStackTrace();
        }
    }

    public static void main (String args[]) throws IOException {

        FixLabels lblFixer = new FixLabels();

        lblFixer.fixaaaaa();

    }

    private static List<String> parseFile(String f) {

        List<String> ret = new ArrayList<String>();

        BufferedReader br = null;
        try {
            String line;
            br = new BufferedReader(new FileReader(f));
            while ((line = br.readLine()) != null) {
                ret.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return ret;
    }

}