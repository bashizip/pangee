    
/* global doc */

function (doc) {

    if (doc.type === "EntityDoc") {
        nameVal = "";
        if (doc.values.nom.value) {
            nameVal = nameVal.concat(" ").concat(doc.values.nom.value);
        }
        if (doc.values.postnom) {
            nameVal = nameVal.concat(" ").concat(doc.values.postnom.value);
        }
        if (doc.values.prenom) {
            nameVal = nameVal.concat(" ").concat(doc.values.prenom.value);
        }
        if (doc.values.dateNaissance) {
            nameVal = nameVal.concat(" né le ").concat(doc.values.dateNaissance.value);
        }
        if (doc.values.paysNaissance) {
            nameVal = nameVal.concat("( ").concat(doc.values.paysNaissance.value).concat(")");
        }
        emit([doc.entityType, doc.values.nom.value, doc.values.prenom.value], {_id:doc._id,entity: doc.values, _name: nameVal});
    }
}

