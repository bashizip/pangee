/*
 * Copyright 2017 Essor RDC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.grew.couchdb;

import java.util.ResourceBundle;

/**
 *
 * @author bashizip
 */
public class DBRefs {

    private static final String DB_URL = "http://localhost:5984";
    private static final String USER_NAME = "admin";
    private static final String PASSWD = "une";

    static ResourceBundle rb = ResourceBundle.getBundle("settings");

    public static DBConfig localDB() {
        return new DBConfig()
                .url(rb.getString("xlocaldb.url"))
                .dbName(rb.getString("xlocaldb.name"))
                .username(rb.getString("xlocaldb.user"))
                .password(rb.getString("xlocaldb.password"));
    }

    public static DBConfig codificationDB() {
        return new DBConfig()
                .url(rb.getString("xcodificationdb.url"))
                .dbName(rb.getString("xcodificationdb.name"))
                .username(rb.getString("xcodificationdb.user"))
                .password(rb.getString("xcodificationdb.password"));
    }

    public static DBConfig proceduresDB() {
        return new DBConfig()
                .url(rb.getString("xproceduredb.url"))
                .dbName(rb.getString("xproceduredb.name"))
                .username(rb.getString("xproceduredb.user"))
                .password(rb.getString("xproceduredb.password"));
    }

    public static DBConfig entitiesDB() {
        return new DBConfig()
                .url(rb.getString("xentitydb.url"))
                .dbName(rb.getString("xentitydb.name"))
                .username(rb.getString("xentitydb.user"))
                .password(rb.getString("xentitydb.password"));

    }

    public static DBConfig schemaDB() {
        return new DBConfig()
                .url(rb.getString("xschemadb.url"))
                .dbName(rb.getString("xschemadb.name"))
                .username(rb.getString("xschemadb.user"))
                .password(rb.getString("xschemadb.password"));

    }

    public static DBConfig rapportsDB() {
        return new DBConfig()
                .url(rb.getString("xreportdb.url"))
                .dbName(rb.getString("xreportdb.name"))
                .username(rb.getString("xreportdb.user"))
                .password(rb.getString("xreportdb.password"));

    }
    
       public static DBConfig filesDB() {
        return new DBConfig()
                .url(rb.getString("xfilesdb.url"))
                .dbName(rb.getString("xfilesdb.name"))
                .username(rb.getString("xfilesdb.user"))
                .password(rb.getString("xfilesdb.password"));

    }

}
