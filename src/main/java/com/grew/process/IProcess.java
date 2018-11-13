/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.process;

/**
 *
 * @author bashizip
 */

interface IProcess {
    String start(String processName);
    void stop(String processName);
    void nextStep();
}
