/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.dao;

/**
 *
 * @author pbash
 */
public class DatabaseViewNotFound extends Exception {

    public DatabaseViewNotFound(String message) {
        super("View Not found");
    }
    
}
