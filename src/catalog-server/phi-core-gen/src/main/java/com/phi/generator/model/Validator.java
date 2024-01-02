package com.phi.generator.model;

/**
 * Validator
 * Created by Alex on 10/02/2017.
 */
public class Validator extends BaseComponent {

    private String maximum;

    public Validator(String type, String maximum) {
        super(type, null, null, null);
        this.maximum = maximum;
    }
}
