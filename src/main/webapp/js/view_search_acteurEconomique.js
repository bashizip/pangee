
/* global doc */

function (doc) {
    if (doc.type === "EntityDoc") {
        if (doc.values.rccm) {
            if (doc.values.denominationSociale) {
                emit([doc.entityType,doc.values.denominationSociale.value, doc.values.rccm.value], 
                {_id: doc._id, entity: doc.values, _name: doc.values.denominationSociale.value});
            }
        }
    }
}