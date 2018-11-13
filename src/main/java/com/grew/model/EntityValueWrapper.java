/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.model;

/**
 *
 * @author pbash
 */
public class EntityValueWrapper {

    private Object value;
    private String fragment;

    public EntityValueWrapper(Object value, String fragment) {
        this.value = value;
        this.fragment = fragment;
    }

    public EntityValueWrapper() {
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getFragment() {
        return fragment;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

}
