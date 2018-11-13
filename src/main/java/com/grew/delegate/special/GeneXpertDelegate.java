/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.delegate.special;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

/**
 *
 * @author bash
 */
public class GeneXpertDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        execution.setVariable("patientTB", true);
        execution.setVariable("patientRR", true);
    }

}
