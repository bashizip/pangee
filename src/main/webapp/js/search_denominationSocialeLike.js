/* global doc */

function(doc) {
    if (doc.type === "EntityDoc") {
        if (doc.values.denominationSociale)
            var stringarray = doc.values.denominationSociale.value.toLowerCase().split(" ");
        for (var idx in stringarray) {
            var key = stringarray[idx];
            if (key.length > 2)
               emit(key.toLowerCase(), { _id: doc._id, entity: doc.values, _name: doc.values.denominationSociale.value });
        }
    }
} 