/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/* global doc */

function (doc) {
    if (doc.type === "EntityDoc") {
        if (doc.values.rccm) {
            emit(doc.values.rccm.value, doc);
        }
    }
}