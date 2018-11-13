
function (doc) {
    if (doc.type === "EntityDoc") {
        var nameVal = "";


        if (doc.values.numeroVoie) {
            nameVal = nameVal.concat(" ").concat(doc.values.numeroVoie.value);
        }

        if (doc.values.typeVoie) {
            nameVal = nameVal.concat(" ").concat(doc.values.typeVoie.value);
        }

        if (doc.values.nomVoie) {
            nameVal = nameVal.concat(" ").concat(doc.values.nomVoie.value);
        }

        if (doc.values.commune) {
            nameVal = nameVal.concat(" ").concat(doc.values.commune.value);
        }

        if (doc.values.ville) {
            nameVal = nameVal.concat(" ").concat(doc.values.ville.value);
        }

        if (doc.values.province) {
            nameVal = nameVal.concat(" ").concat(doc.values.province.value);
        }

        emit([doc.entityType, doc.values.nomVoie.value, doc.values.ville.value], {_id: doc._id, entity: doc.values, _name: nameVal});

    }
}