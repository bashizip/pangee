package com.grew.formserver;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.ejb.Stateless;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import com.grew.couchdb.CoushDB;
import com.grew.couchdb.DBConfig;
import com.grew.couchdb.DBRefs;
import com.grew.formserver.Utility.HtmlTag;
import com.grew.formserver.Utility.Property;
import static com.grew.formserver.Utility.alpacaProperties;
import static com.grew.formserver.Utility.communTypes;
import static com.grew.formserver.Utility.metadatas;
import static com.grew.formserver.Utility.notShowFields;
import static com.grew.formserver.Utility.*;

@Stateless
public class SchemaCompiler {

    private LinkedTreeMap<String, Object> parameters;
    private LinkedTreeMap<String, Object> readonlyPropertyParents = new LinkedTreeMap<>();
    private LinkedTreeMap<String, Object> formOptions;

  
    enum Datasources {
        FORMS, CODES, DICTIONARIES, FIELDS, PARAMETERS
    };

    public SchemaCompiler() {
            parameters = getObject(null, "parameters", Datasources.PARAMETERS);
    }

    private LinkedTreeMap<String, Object> getObject(String viewKeyName, String key, Datasources datasources) {

        DBConfig dBConfig = null;
        String viewName = "by_" + viewKeyName;
        String designDocId = "_design/";

        switch (datasources) {
            case CODES:
                dBConfig = DBRefs.codificationDB();
                designDocId = "_design/" + "CodeDoc";
                break;
            case FORMS:
                dBConfig = DBRefs.schemaDB();
                designDocId = "_design/" + "JsonSchemaDoc";
                break;
            case DICTIONARIES:
                dBConfig = DBRefs.schemaDB();
                designDocId = "_design/" + "DictionaryDoc";
                break;
            case FIELDS:
                dBConfig = DBRefs.schemaDB();
                designDocId = "_design/" + "FieldDoc";
                break;
            case PARAMETERS:
                dBConfig = DBRefs.schemaDB();
                designDocId = "_design/" + "";
                break;
        }

        CoushDB coushDB = new CoushDB(dBConfig);
        LinkedTreeMap<String, Object> res = null;
        ViewQuery query = null;

        if (viewKeyName == null) {
            query = new ViewQuery().
                    viewName("_all_docs").
                    designDocId(designDocId).
                    key(key).includeDocs(true);
        } else {
            query = new ViewQuery().
                    designDocId(designDocId).
                    viewName(viewName).
                    key(key).includeDocs(true);
        }

        ViewResult vr = coushDB.getDB().queryView(query);

        String result = "";

        if (!vr.isEmpty()) {
            result = vr.getRows().get(0).getDoc();
        }

        // System.out.println(result);
        res = new Gson().fromJson(result, new TypeToken<LinkedTreeMap>() {
        }.getType());

        return res;

    }

    private List<LinkedTreeMap<String, Object>> getAllDictionaries() {
        List<LinkedTreeMap<String, Object>> res = new LinkedList<>();
        
        DBConfig dBConfig = DBRefs.schemaDB();
        
        String viewName = "all_dictionaries";
        String designDocId = "_design/DictionaryDoc";

        ViewQuery query = null;

        query = new ViewQuery().
                designDocId(designDocId).
                viewName(viewName).
                includeDocs(true);

        CoushDB coushDB = new CoushDB(dBConfig);

        ViewResult vr = coushDB.getDB().queryView(query);

        for (ViewResult.Row row : vr) {
            LinkedTreeMap el = new Gson().fromJson(row.getDoc(), new TypeToken<LinkedTreeMap>() {
            }.getType());

            res.add(el);
        }
        return res;
    }


    public String compile(String formKey) {

        LinkedTreeMap<String, Object> form = getObject(Property.formKey, formKey, Datasources.FORMS);
        String formSchema = null;
        if (form != null) {
            formSchema = generateFormSchema(form).toString();
            // System.out.println(formSchema);
        } else {
            System.err.println("Form not found : " + formKey);
        }
        return formSchema;
    }

    private JsonObject generateFormSchema(LinkedTreeMap<String, Object> form) {
        JsonObject schema = new JsonObject();//Json-schema
        JsonObject options = new JsonObject();//AlpacaJS

        schema.addProperty(Property.$schema, draft04);
        schema.addProperty(Property.title, form.get(Property.title).toString());
        schema.addProperty(Property.type, Type.object);
        Boolean searchForm = (Boolean) form.get(Property.searchForm);
        String dictionary = (String) form.get(Type.dictionary);
        if (searchForm != null && searchForm && dictionary != null) {
            schema.addProperty(Property.searchForm, searchForm);
            schema.addProperty(Property.entityType, dictionary);
        }
        formOptions = (LinkedTreeMap<String, Object>) form.get(Property.options);
        List<String> formFields = (ArrayList) form.get(Property.fields);
        if (formFields != null && !formFields.isEmpty()) {

            JsonObject schemaProperties = new JsonObject();//Properties in json-schema section
            JsonObject optionFields = new JsonObject();//Fields in alpacaJS section
            formFields.forEach((field) -> {
                createSchemaProperty(field, schemaProperties, true, false, field);
                createSchemaProperty(field, optionFields, false, false, field);
            });
            schema.add(Property.properties, schemaProperties);
            options.add(Property.fields, optionFields);

            JsonObject definitions = new JsonObject();//Definitions -> dictionaries
            getAllDictionaries().forEach((dic) -> {
                definitions.add(dic.get(Property.name).toString(), createDictionary(dic.get(Property.name).toString(), true, true));
            });
            schema.add(Property.definitions, definitions);
        }
        JsonParser parser = new JsonParser();
        setParameters(schema, (LinkedTreeMap<String, Object>) parameters.get(Property.schema));
        setParameters(options, (LinkedTreeMap<String, Object>) parameters.get(Property.form));

        //Final compilation
        JsonObject main = new JsonObject();
        main.add(Property.schema, schema);
        main.add(Property.options, options);
        if (searchForm == null || !searchForm) {
            main.add(Property.settings, parser.parse(parameters.get(Property.settings).toString()));
        }
        return main;
    }

    private void createSchemaProperty(String fieldName, JsonObject parent, boolean isProperty, boolean creatingDic, String cumulKey) {
        LinkedTreeMap<String, Object> hmap = getObject(Property.name, fieldName, Datasources.FIELDS);
        if (hmap != null) {
            boolean multiValued = hmap.get(Property.multiValued) != null ? Boolean.parseBoolean(hmap.get(Property.multiValued).toString()) : false;
            String dataType = hmap.get(Property.dataType) != null ? hmap.get(Property.dataType).toString() : null;
            String title = hmap.get(Property.title) != null ? hmap.get(Property.title).toString() : null;
            if (isProperty) {
                JsonObject schemaProperty = createProperty(hmap, creatingDic, cumulKey);
                if (schemaProperty.size() > 0) {
                    if (multiValued) {
                        JsonObject propertyItems = new JsonObject();
                        if (schemaProperty.get(Property.type).getAsString().equals(Type.object)) {
                            JsonElement properties = schemaProperty.get(Property.properties);
                            properties.getAsJsonObject().remove(Property.readonly);
                            schemaProperty.remove(Property.properties);
                            if (title != null) {
                                propertyItems.addProperty(Property.title, title);
                            }
                            if (schemaProperty.get(Property.composeName) != null) {
                                propertyItems.add(Property.composeName, schemaProperty.remove(Property.composeName));
                            }
                            if (schemaProperty.get(Property.required) != null) {
                                propertyItems.add(Property.required, schemaProperty.get(Property.required));
                            }
                            propertyItems.addProperty(Property.type, Type.object);
                            propertyItems.add(Property.properties, properties);
                        } else {
                            List<String> keystoBeRemoved = new ArrayList<>();
                            schemaProperty.entrySet().forEach((me) -> {
                                if (!Utility.arraySchemaProperties.contains(me.getKey())) {
                                    propertyItems.add(me.getKey(), me.getValue());
                                    if (!me.getKey().equals(Property.readonly) && !me.getKey().equals(Property.required)) {
                                        keystoBeRemoved.add(me.getKey());
                                    }
                                }
                            });
                            keystoBeRemoved.forEach((key) -> {
                                schemaProperty.remove(key);
                            });
                            if (schemaProperty.get(Property.stringDefault) != null) {
                                schemaProperty.remove(Property.stringDefault);
                            }
                        }
                        schemaProperty.addProperty(Property.type, Type.array);
                        schemaProperty.add(Property.items, propertyItems);
                    }
                    if (schemaProperty.get(Property.type).getAsString().equals(Type.object)
                            && schemaProperty.get(Property.properties) != null) {
                        schemaProperty.get(Property.properties).getAsJsonObject().remove(Property.readonly);
                    }
                    parent.add(fieldName, schemaProperty);
                }
            } else {
                JsonObject optionField = createOptionField(hmap, cumulKey);
                JsonObject main = new JsonObject();
                LinkedTreeMap<String, Object> fieldOptions = (LinkedTreeMap<String, Object>) parameters.get(Property.fields);
                if (multiValued) {
                    if (hmap.get(Property.description) != null) {
                        main.addProperty(AlpacaProperty.helper, hmap.get(Property.description).toString());
                    }
                    if (dataType != null && dataType.equals(Type.object)) {
                        optionField.addProperty(AlpacaProperty.collapsible, true);
                        optionField.addProperty(AlpacaProperty.lazyLoading, true);
                    }
                    main.add(Property.items, optionField);
                    main.addProperty(Property.type, Type.array);
                    setParameters(main, (LinkedTreeMap<String, Object>) fieldOptions.get(Type.array));
                    parent.add(fieldName, main);
                } else {
                    if (hmap.get(Property.dataType).toString().equals(Type.object)) {
                        setParameters(optionField, (LinkedTreeMap<String, Object>) fieldOptions.get(Type.object));
                    }
                    parent.add(fieldName, optionField);
                }
            }
        } else {
            System.err.println("Field not found: " + fieldName);
        }
    }

    private JsonObject createProperty(LinkedTreeMap<String, Object> hmap, boolean creatingDic, String cumulKey) {
        if (!creatingDic) {
            hmap = setFormOptionsToField(hmap, cumulKey);
            if (!cumulKey.contains(".") && hmap.get(Property._id) != null) {
                readonlyPropertyParents.put(cumulKey, hmap.get(Property.readonly));
            }
        }
        List<String> exeptFields = getSpecifiedFields(hmap, Property.except);
        List<String> onlyFields = getSpecifiedFields(hmap, Property.only);
        JsonObject prop = new JsonObject();
        if (hmap != null) {
            String codeInline = hmap.get(Property.codeInline) != null ? hmap.get(Property.codeInline).toString() : null;
            LinkedTreeMap<String, Object> dependantParent = hmap.get(Property.parent) != null ? (LinkedTreeMap<String, Object>) hmap.get(Property.parent) : null;
            hmap.entrySet().forEach((me) -> {
                if (!metadatas.contains(me.getKey()) && !alpacaProperties.contains(me.getKey()) && !notShowFields.contains(me.getKey())) {
                    String key = replacePropertyNameSchema(me.getKey());
                    if (me.getValue().getClass() == LinkedTreeMap.class) {
                        LinkedTreeMap<String, Object> meHashMap = (LinkedTreeMap<String, Object>) me.getValue();
                        List<String> exeptChildFields = getSpecifiedFields(meHashMap, Property.except);
                        List<String> onlyChildFields = getSpecifiedFields(meHashMap, Property.only);
                        String childCumulKey = cumulKey;
                        if (!key.equals(Property.properties)) {
                            childCumulKey = cumulKey + "." + key;
                        }
                        if (!creatingDic) {
                            meHashMap = setFormOptionsToField(meHashMap, childCumulKey);
                        }
                        JsonObject childProp = createProperty(meHashMap, creatingDic, childCumulKey);
                        if (childProp.size() > 0) {
                            prop.add(key, childProp);
                        }
                    } else if (me.getValue().getClass() == String.class) {
                        if (key.equals(Type.dictionary)) {
                            if (!exeptFields.isEmpty()) {
                                LinkedTreeMap<String, Object> dic = getObject(Property.name, me.getValue().toString(), Datasources.DICTIONARIES);
                                List<String> restExceptFields = new ArrayList<>();
                                ((List<String>) dic.get(Property.fields)).stream().filter((field) -> (!exeptFields.contains(field))).forEachOrdered((field) -> {
                                    restExceptFields.add(field);
                                });
                                createSpecifiedField(prop, restExceptFields, true, cumulKey, me.getValue().toString());
                            } else if (!onlyFields.isEmpty()) {
                                createSpecifiedField(prop, onlyFields, true, cumulKey, me.getValue().toString());
                            } else if (isFieldDictionaryOverloaded(cumulKey)) {
                                LinkedTreeMap<String, Object> dic = getObject(Property.name, me.getValue().toString(), Datasources.DICTIONARIES);
                                List<String> dicFields = (List<String>) dic.get(Property.fields);
                                createSpecifiedField(prop, dicFields, true, cumulKey, me.getValue().toString());
                            } else {
                                prop.addProperty(Property.$ref, "#/" + Property.definitions + "/" + me.getValue().toString());
                            }
                            prop.addProperty(Type.dictionary, me.getValue().toString());
                        } else if (key.equals(Property.type) && me.getValue().toString().equals(Type.dictionary)) {
                            prop.addProperty(Property.type, Type.object);
                            if (cumulKey.contains(".")) {
                                renderFieldReadonlyFromParent(prop, cumulKey);
                            }
                        } else if (key.equals(Property.code)) {
                            if (dependantParent == null && codeInline == null) {
                                prop.add(Property.stringEnum, addCodeItems(Property.value, me.getValue().toString()));
                            } else if (dependantParent != null && codeInline == null) {
                                prop.add(Property.stringEnum, new JsonArray());
                                prop.addProperty(Property.dependantParent, dependantParent.get(Property.field).toString());
                                prop.add(Property.dependantData, getDependantData(Property.value, me.getValue().toString(), dependantParent.get(Property.code).toString()));
                            } else if (dependantParent == null && codeInline != null) {
                                prop.add(Property.stringEnum, addCodeItems(Property.value, codeInline));
                                prop.add(Property.dependantData, getDependantData(Property.value, me.getValue().toString(), codeInline));
                            } else if (dependantParent != null && codeInline != null) {
                                prop.add(Property.stringEnum, new JsonArray());
                                prop.addProperty(Property.dependantParent, dependantParent.get(Property.field).toString());
                                JsonObject main = new JsonObject();
                                Map dependantData = new Gson().fromJson(getDependantData(Property.value, codeInline, dependantParent.get(Property.code).toString()), HashMap.class);
                                dependantData.putAll(new Gson().fromJson(getDependantData(Property.value, me.getValue().toString(), codeInline), HashMap.class));
                                prop.add(Property.dependantData, new JsonParser().parse(new Gson().toJson(dependantData)));
                            }
                        } else {
                            prop.addProperty(key, getCustomTypeSchema(key, me.getValue().toString()));
                            if (cumulKey.contains(".") && key.equals(Property.type)) {
                                renderFieldReadonlyFromParent(prop, cumulKey);
                            }
                        }
                    } else if (me.getValue().getClass() == Boolean.class) {
                        if (!key.equals(Property.multiValued)) {
                            prop.addProperty(key, (Boolean) me.getValue());
                        }
                    } else if (me.getValue().getClass() == Double.class) {
                        if (key.equals(Property.defaultOnStart)) {
                            int maxToCreate = ((Double) me.getValue()).intValue();
                            setArrayDefault(prop, maxToCreate);
                        } else {
                            prop.addProperty(key, ((Double) me.getValue()).intValue());
                        }
                    } else if (me.getValue().getClass() == ArrayList.class) {
                        if (!key.equals(Property.except) && !key.equals(Property.only)) {
                            JsonArray array = new JsonArray();
                            ((ArrayList<String>) me.getValue()).forEach((val) -> {
                                array.add(val);
                            });
                            prop.add(key, array);
                        }
                    } else {
                        //FIXME
                    }
                }
            });
        }
        return prop;
    }

    private JsonObject createDictionary(String dicName, boolean isProperty, boolean creatingDic) {
        LinkedTreeMap<String, Object> dic = getObject(Property.name, dicName, Datasources.DICTIONARIES);
        JsonObject dicProp = new JsonObject();
        JsonObject properties = new JsonObject();
        if (isProperty) {
            dicProp.addProperty(Property.type, Type.object);
            String searchCriteria = Property.recherche + dicName.substring(0, 1).toUpperCase() + dicName.substring(1) + Property._fk;
            dicProp.addProperty(Property.searchCriteriaFK, searchCriteria);
            properties.add(Property._id, null);
        } else {
            JsonObject _id = new JsonObject();
            _id.addProperty(Property.type, HtmlTag.hidden);
            properties.add(Property._id, _id);
        }
        dic.entrySet().forEach((me) -> {
            if (me.getKey().equals(Property.fields)) {
                ((ArrayList<String>) me.getValue()).forEach((val) -> {
                    createSchemaProperty(val, properties, isProperty, creatingDic, val);
                });
            } else if (me.getValue().getClass() == String.class) {
                if (!metadatas.contains(me.getKey()) && isProperty) {
                    dicProp.addProperty(me.getKey(), me.getValue().toString());
                }
            } else {
                //FIXME
            }
        });
        if (isProperty) {
            dicProp.add(Property.properties, properties);
            return dicProp;
        } else {
            return properties;
        }
    }

    private JsonObject createOptionField(LinkedTreeMap<String, Object> hmap, String cumulKey) {
        hmap = setFormOptionsToField(hmap, cumulKey);
        boolean multiValued = hmap.get(Property.multiValued) != null ? Boolean.parseBoolean(hmap.get(Property.multiValued).toString()) : false;
        String codeInline = hmap.get(Property.codeInline) != null ? hmap.get(Property.codeInline).toString() : null;
        String stringCase = hmap.get(AlpacaProperty.stringCase) != null ? hmap.get(AlpacaProperty.stringCase).toString() : null;
        LinkedTreeMap<String, Object> dependantParent = hmap.get(Property.parent) != null ? (LinkedTreeMap<String, Object>) hmap.get(Property.parent) : null;
        List<String> exeptFields = getSpecifiedFields(hmap, Property.except);
        List<String> onlyFields = getSpecifiedFields(hmap, Property.only);
        JsonObject field = new JsonObject();
        hmap.entrySet().forEach((me) -> {
            if (!notShowFields.contains(me.getKey()) && !jsonSchemaProperties.contains(me.getKey())) {
                String key = replacePropertyNameOption(me.getKey());
                if (me.getValue().getClass() == LinkedTreeMap.class) {
                    LinkedTreeMap<String, Object> meHashMap = (LinkedTreeMap<String, Object>) me.getValue();
                    String childCumulKey = cumulKey;
                    if (!key.equals(Property.fields)) {
                        childCumulKey = cumulKey + "." + key;
                    }
                    meHashMap = setFormOptionsToField(meHashMap, childCumulKey);
                    field.add(key, createOptionField(meHashMap, childCumulKey));
                } else if (me.getValue().getClass() == String.class) {
                    if ((alpacaProperties.contains(me.getKey()) || customTypes.contains(me.getValue().toString())
                            || (key.equals(Property.type) && communTypes.contains(me.getValue().toString()))
                            || key.equals(Property.code) || key.equals(AlpacaProperty.helper))
                            && (stringCase == null || !me.getKey().equals(Property.dataType))) {
                        String value = getCustomTypeOption(key, me.getValue().toString());
                        if (key.equals(Property.code)) {
                            if (dependantParent == null) {
                                field.add(Property.optionLabels, addCodeItems(HtmlTag.text, codeInline == null ? me.getValue().toString() : codeInline));
                            }
                            field.addProperty(Property.showMessages, false);
                        } else if (key.equals(AlpacaProperty.helper)) {
                            if (!multiValued) {
                                field.addProperty(key, value);
                            }
                        } else {
                            field.addProperty(key, value);
                        }
                        LinkedTreeMap<String, Object> fieldOptions = (LinkedTreeMap<String, Object>) parameters.get(Property.fields);
                        this.setCustomAlpacaOptions(field, key, value, fieldOptions);

                        if (optionViews.contains(value)) {
                            field.addProperty(AlpacaProperty.view, value + "-" + AlpacaProperty.view);
                            if (value.equals(Type.amount)) {
                                JsonArray optionCurrencies = new JsonArray();
                                ((List<String>) parameters.get(Property.currencies)).forEach((currency) -> {
                                    optionCurrencies.add(currency);
                                });
                                field.add(Property.currencies, optionCurrencies);
                            }
                        }
                        if (key.equals(Property.type) && (me.getValue().toString().equals(Type.integer) || me.getValue().toString().equals(Type.number))) {
                            field.addProperty(AlpacaProperty.numericEntry, true);
                        }
                    } else if (key.equals(Type.dictionary)) {
                        if (!exeptFields.isEmpty()) {
                            LinkedTreeMap<String, Object> dic = getObject(Property.name, me.getValue().toString(), Datasources.DICTIONARIES);
                            List<String> restExceptFields = new ArrayList<>();
                            ((List<String>) dic.get(Property.fields)).stream().filter((f) -> (!exeptFields.contains(f))).forEachOrdered((f) -> {
                                restExceptFields.add(f);
                            });
                            createSpecifiedField(field, restExceptFields, false, cumulKey, me.getValue().toString());
                        } else if (!onlyFields.isEmpty()) {
                            createSpecifiedField(field, onlyFields, false, cumulKey, me.getValue().toString());
                        } else {
                            field.add(Property.fields, createDictionary(me.getValue().toString(), false, false));
                        }
                        LinkedTreeMap<String, Object> fieldOptions = (LinkedTreeMap<String, Object>) parameters.get(Property.fields);
                        setParameters(field, (LinkedTreeMap<String, Object>) fieldOptions.get(Type.object));
                    } else if (me.getKey().equals(Property.dataType) && me.getValue().toString().equals(Type.object) && !multiValued) {
                        setParameters(field, (LinkedTreeMap<String, Object>) ((LinkedTreeMap<String, Object>) parameters.get(Property.fields)).get(Type.object));
                    } else {
//                            System.err.println(me.getKey() + " -> " + me.getValue());
                    }
                } else if (me.getValue().getClass() == Double.class) {
                    field.addProperty(key, ((Double) me.getValue()).intValue());
                } else {
//                    System.out.println(me.getKey() + " ### " + me.getValue().getClass());
                }
            }
        });
        return field;
    }

    private String replacePropertyNameSchema(String prop) {
        return prop.replace(Property.dataType, Property.type)
                .replace(Property.regexp, Property.pattern);
    }

    private String replacePropertyNameOption(String prop) {
        return prop.replace(AlpacaProperty.stringCase, Property.type)
                .replace(Property.properties, Property.fields)
                .replace(Property.dataType, Property.type)
                .replace(Property.cssClass, AlpacaProperty.fieldClass)
                .replace(AlpacaProperty.mask, Property.maskString)
                .replace(Property.description, AlpacaProperty.helper);
    }

    private String getCustomTypeSchema(String key, String value) {
        if (key.equals(Property.type) && customTypes.contains(value)) {
            return Type.string;
        } else {
            return value;
        }
    }

    private String getCustomTypeOption(String key, String value) {
        if (key.equals(Property.type) && value.equals(Type.capitalized)) {
            return AlpacaProperty.personalName;
        } else if (key.equals(Property.type) && value.equals(Type.string)) {
            return HtmlTag.text;
        } else if (key.equals(Property.type) && value.equals(Type.integer)) {
            return HtmlTag.integer;
        } else if (key.equals(Property.type) && value.equals(Type.stringFloat)) {
            return HtmlTag.number;
        } else if (key.equals(Property.type) && value.equals(Type.code)) {
            return HtmlTag.select;
        } else if (key.equals(Property.type) && value.equals(Type.report)) {
            return AlpacaProperty.print;
        } else if (key.equals(Property.type) && value.equals(Type.document)) {
            return AlpacaProperty.doc;
        } else {
            return value;
        }
    }

    private void setCustomAlpacaOptions(JsonObject field, String key, String value, LinkedTreeMap<String, Object> fieldOptions) {
        if (key.equals(Property.type) && value.equals(HtmlTag.text)) {
            setParameters(field, (LinkedTreeMap<String, Object>) fieldOptions.get(HtmlTag.text));
        } else if (key.equals(Property.type) && value.equals(HtmlTag.select)) {
            setParameters(field, (LinkedTreeMap<String, Object>) fieldOptions.get(HtmlTag.select));
        } else if (key.equals(Property.type) && value.equals(Type.textarea)) {
            setParameters(field, (LinkedTreeMap<String, Object>) fieldOptions.get(Type.textarea));
        } else if (key.equals(Property.type) && value.equals(Type.date)) {
            setParameters(field, (LinkedTreeMap<String, Object>) fieldOptions.get(Type.date));
        }
    }

    private JsonArray addCodeItems(String property, String fieldName) {
        JsonArray array = new JsonArray();
        LinkedTreeMap<String, Object> code = getObject(Property.name, fieldName, Datasources.CODES);
        if (code != null) {
            List<LinkedTreeMap<String, String>> codeItems = (List<LinkedTreeMap<String, String>>) code.get(Property.items);
            codeItems.forEach((codeItem) -> {
                array.add(codeItem.get(property));
            });
        } else {
            System.err.println("Code not found: " + fieldName);
        }
        return array;
    }

    private JsonObject getDependantData(String property, String fieldName, String dependantParent) {
        JsonObject main = new JsonObject();
        LinkedTreeMap<String, Object> code = getObject(Property.name, fieldName, Datasources.CODES);
        if (code != null) {
            if (dependantParent != null) {
                LinkedTreeMap<String, Object> parentCode = getObject(Property.name, dependantParent, Datasources.CODES);
                if (parentCode != null) {
                    List<LinkedTreeMap<String, String>> parentCodeItems = (List<LinkedTreeMap<String, String>>) parentCode.get(Property.items);
                    parentCodeItems.forEach((parentCodeItem) -> {
                        String parentKey = parentCodeItem.get(property);
                        JsonArray parentArray = new JsonArray();
                        List<LinkedTreeMap<String, Object>> codeItems = (List<LinkedTreeMap<String, Object>>) code.get(Property.items);
                        getCodeItemsByKey(codeItems, parentKey).forEach((item) -> {
                            JsonObject itemObject = new JsonObject();
                            itemObject.addProperty(Property.value, item.get(Property.value).toString());
                            itemObject.addProperty(HtmlTag.text, item.get(HtmlTag.text).toString());
                            parentArray.add(itemObject);
                        });
                        main.add(parentKey, parentArray);
                    });
                    return main;
                } else {
                    System.err.println("Code not found: " + dependantParent);
                }
            }
        } else {
            System.err.println("Code not found: " + fieldName);
        }
        return main;
    }

    private List<String> getSpecifiedFields(LinkedTreeMap<String, Object> hmap, String property) {
        List<String> specifiedFields = new ArrayList<>();
        if (hmap != null) {
            hmap.entrySet().forEach((Map.Entry<String, Object> me) -> {
                if (me.getValue().getClass() == ArrayList.class && me.getKey().equals(property)) {
                    ((List<String>) me.getValue()).forEach((item) -> {
                        specifiedFields.add(item);
                    });
                }
            });
        }
        return specifiedFields;
    }

    private void createSpecifiedField(JsonObject prop, List<String> fields, boolean isProperty, String cumulKey, String dicName) {
        JsonObject properties = new JsonObject();
        if (isProperty) {
            prop.addProperty(Property.type, Type.object);
            String searchCriteria = Property.recherche + dicName.substring(0, 1).toUpperCase() + dicName.substring(1) + Property._fk;
            prop.addProperty(Property.searchCriteriaFK, searchCriteria);
            LinkedTreeMap<String, Object> dic = getObject(Property.name, dicName, Datasources.DICTIONARIES);
            prop.addProperty(Property.composeName, dic.get(Property.composeName).toString());
            prop.addProperty(Property.key, dic.get(Property.key).toString());
            properties.add(Property._id, null);
        } else {
            JsonObject _id = new JsonObject();
            _id.addProperty(Property.type, HtmlTag.hidden);
            properties.add(Property._id, _id);
        }

        fields.forEach((field) -> {
            createSchemaProperty(field, properties, isProperty, false, cumulKey + "." + field);
        });
        prop.add(isProperty ? Property.properties : Property.fields, properties);
    }

    private void setParameters(JsonObject parent, LinkedTreeMap<String, Object> parameters) {
        parameters.entrySet().forEach((par) -> {
            if (par.getValue().getClass() == String.class) {
                parent.addProperty(par.getKey(), par.getValue().toString());
            } else if (par.getValue().getClass() == Boolean.class) {
                parent.addProperty(par.getKey(), (Boolean) par.getValue());
            } else if (par.getValue().getClass() == Double.class) {
                parent.addProperty(par.getKey(), ((Double) par.getValue()).intValue());
            } else if (par.getValue().getClass() == ArrayList.class) {
                JsonArray array = new JsonArray();
                ((ArrayList<LinkedTreeMap<String, Object>>) par.getValue()).forEach((val) -> {
                    JsonObject item = new JsonObject();
                    setParameters(item, val);
                    array.add(item);
                });
                parent.add(par.getKey(), array);
            } else if (par.getValue().getClass() == LinkedTreeMap.class) {
                JsonObject object = new JsonObject();
                setParameters(object, (LinkedTreeMap<String, Object>) par.getValue());
                parent.add(par.getKey(), object);
            } else {
                //FIXME
            }
        });
    }

    private List<LinkedTreeMap<String, Object>> getCodeItemsByKey(List<LinkedTreeMap<String, Object>> codeItems, String parentCodeItem) {
        List<LinkedTreeMap<String, Object>> items = new ArrayList<>();
        codeItems.forEach((codeItem) -> {
            List<String> depends = (ArrayList) codeItem.get(Property.depends);
            if (depends.contains(parentCodeItem)) {
                items.add(codeItem);
            }
        });
        return items;
    }

    private LinkedTreeMap<String, Object> setFormOptionsToField(LinkedTreeMap<String, Object> hmap, String cumulKey) {
        if (formOptions != null) {
            for (Entry<String, Object> entry : formOptions.entrySet()) {
                if (entry.getKey().equals(cumulKey)) {
                    if (entry.getValue() != null) {
                        overloadFieldOptions(hmap, (LinkedTreeMap<String, Object>) entry.getValue());
                    } else {
                        return null;
                    }
                }
            }
        }
        return hmap;
    }

    private void overloadFieldOptions(LinkedTreeMap<String, Object> hmap, LinkedTreeMap<String, Object> newOptions) {
        newOptions.entrySet().forEach((me) -> {
            if (me.getValue() != null) {
                hmap.put(me.getKey(), me.getValue());
            } else {
                List<String> keystoBeRemoved = new ArrayList<>();
                hmap.entrySet().forEach((e) -> {
                    if (e.getKey().equals(me.getKey()) && !metadatas.contains(me.getKey()) && !alpacaProperties.contains(me.getKey())) {
                        keystoBeRemoved.add(me.getKey());
                    }
                });
                keystoBeRemoved.forEach((key) -> {
                    hmap.remove(key);
                });
            }
        });
    }

    private boolean isFieldDictionaryOverloaded(String cumulKey) {
        return formOptions != null ? formOptions.entrySet().stream().anyMatch((entry) -> (entry.getKey().contains(cumulKey))) : false;
    }

    private void setArrayDefault(JsonObject prop, int size) {
        JsonArray defaultArray = new JsonArray();
        for (int i = 0; i < size; i++) {
            defaultArray.add(new JsonObject());
        }
        prop.add(Property.stringDefault, defaultArray);
    }

    private void renderFieldReadonlyFromParent(JsonObject prop, String cumulKey) {
        String parentKey = cumulKey.substring(0, cumulKey.indexOf("."));
        if (readonlyPropertyParents.containsKey(parentKey)) {
            Object isParentReadonly = readonlyPropertyParents.get(parentKey);
            if (isParentReadonly != null) {
                prop.addProperty(Property.readonly, Boolean.parseBoolean(isParentReadonly.toString()));
            }
        }
    }

    public static void main(String[] args) {
        SchemaCompiler compiler = new SchemaCompiler();
       String schema = compiler.compile("im:archivage_FK");
        System.out.println(schema);

    }
}
