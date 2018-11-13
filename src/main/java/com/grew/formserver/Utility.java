package com.grew.formserver;

import java.util.Arrays;
import java.util.List;

public class Utility {

    public static String draft04 = "http://json-schema.org/draft-04/schema#";
    public static List<String> metadatas = Arrays.asList(Property._id, Property.type, Property.name, Property._rev);
    public static List<String> customTypes = Arrays.asList(Type.code, Type.date, Type.email, Type.url, Type.amount, Type.bool, Type.textarea, Type.stringFloat, Type.phone, Type.pdf, Type.document, Type.report);
    public static List<String> optionViews = Arrays.asList(Type.amount, Type.bool, Type.pdf, AlpacaProperty.doc, AlpacaProperty.print);
    public static List<String> alpacaProperties = Arrays.asList(AlpacaProperty.stringCase, AlpacaProperty.mask, Property.cssClass, Property.css, Property.order);
    public static List<String> communProperties = Arrays.asList(Property.type);
    public static List<String> notShowFields = Arrays.asList(Property.parent);
    public static List<String> jsonSchemaProperties = Arrays.asList(Property.stringDefault, Property.maxItems, Property.minItems, Property.uniqueItems);
    public static List<String> communTypes = Arrays.asList(Type.integer, Type.string, Type.number);
    public static List<String> arraySchemaProperties = Arrays.asList(Property.maxItems, Property.minItems, Property.uniqueItems, Property.stringDefault, Property.visibility);

    public static class Property {

        public static String _id = "_id";
        public static String _rev = "_rev";
        public static String type = "type";
        public static String dataType = "dataType";
        public static String regexp = "regexp";
        public static String pattern = "pattern";
        public static String name = "name";
        public static String code = "code";
        public static String stringEnum = "enum";
        public static String options = "options";
        public static String schema = "schema";
        public static String properties = "properties";
        public static String $schema = "$schema";
        public static String definitions = "definitions";
        public static String title = "title";
        public static String maskString = "maskString";
        public static String $ref = "$ref";
        public static String fields = "fields";
        public static String items = "items";
        public static String formKey = "formKey";
        public static String multiValued = "multiValued";
        public static String maxItems = "maxItems";
        public static String minItems = "minItems";
        public static String uniqueItems = "uniqueItems";
        public static String description = "description";
        public static String format = "format";
        public static String value = "value";
        public static String optionLabels = "optionLabels";
        public static String dependantParent = "dependantParent";
        public static String parent = "parent";
        public static String codeInline = "codeInline";
        public static String readonly = "readonly";
        public static String field = "field";
        public static String dependantData = "dependantData";
        public static String depends = "depends";
        public static String except = "except";
        public static String only = "only";
        public static String recherche = "recherche";
        public static String _fk = "_FK";
        public static String css = "css";
        public static String order = "order";
        public static String cssClass = "cssClass";
        public static String currencies = "currencies";
        public static String form = "form";
        public static String stringDefault = "default";
        public static String defaultOnStart = "defaultOnStart";
        public static String composeName = "composeName";
        public static String required = "required";
        public static String key = "key";
        public static String searchCriteriaFK = "searchCriteriaFK";
        public static String settings = "settings";
        public static String searchForm = "searchForm";
        public static String entityType = "entityType";
        public static String showMessages = "showMessages";
        public static String visibility = "visibility";
    }

    public static class Type {

        public static String string = "string";
        public static String integer = "integer";
        public static String number = "number";
        public static String stringFloat = "float";
        public static String code = "code";
        public static String date = "date";
        public static String array = "array";
        public static String object = "object";
        public static String dictionary = "dictionary";
        public static String email = "email";
        public static String url = "url";
        public static String capitalized = "capitalized";
        public static String amount = "amount";
        public static String bool = "boolean";
        public static String textarea = "textarea";
        public static String phone = "phone";
        public static String pdf = "pdf";
        public static String document = "document";
        public static String report = "report";

    }

    public static class AlpacaProperty {

        public static String stringCase = "case";
        public static String helper = "helper";
        public static String print = "print";
        public static String doc = "doc";
        public static String view = "view";
        public static String uppercase = "uppercase";
        public static String fieldClass = "fieldClass";
        public static String personalName = "personalname";
        public static String mask = "mask";
        public static String numericEntry = "numericEntry";
        public static String collapsible = "collapsible";
        public static String collapsed = "collapsed";
        public static String animate = "animate";
        public static String lazyLoading = "lazyLoading";
        public static String toolbarSticky = "toolbarSticky";
        public static String hideInitValidationError = "hideInitValidationError";
        public static String disallowOnlyEmptySpaces = "disallowOnlyEmptySpaces";
        public static String removeDefaultNone = "removeDefaultNone";
        public static String sort = "sort";
        public static String picker = "picker";
        public static String manualEntry = "manualEntry";
        public static String noneLabel = "noneLabel";
        public static String focus = "focus";

    }

    public static class HtmlTag {

        public static String select = "select";
        public static String text = "text";
        public static String hidden = "hidden";
        public static String integer = "integer";
        public static String number = "number";

    }
}
