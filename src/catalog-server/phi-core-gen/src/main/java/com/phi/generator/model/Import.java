package com.phi.generator.model;

/**
 * Represent a Javascript import
 * Created by Alex on 21/04/2017.
 */
public class Import {

    String form;
    String path;

    Import(String form, String path) {
        this.form = form;
        this.path = path;
    }

    public String getForm() {
        return form;
    }

    public String getPath() {
        return path;
    }

}