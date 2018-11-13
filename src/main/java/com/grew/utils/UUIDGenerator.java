/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.utils;

import java.util.UUID;

/**
 *
 * @author bashizip
 */
public class UUIDGenerator {

    public static String nextID() {
        return UUID.randomUUID().toString();
    }

    public static String nextUserID() {
        return UUID.randomUUID().toString();
    }
}
