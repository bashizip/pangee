
/* global doc */

function (doc) {
    if (doc.type === "EntityDoc") {
        if (doc.values.denominationSociale) {
            emit(doc.values.denominationSociale, 1);
        }
        if (doc.values.rccm) {
            emit(doc.values.rccm, 1);
        }
    }
}