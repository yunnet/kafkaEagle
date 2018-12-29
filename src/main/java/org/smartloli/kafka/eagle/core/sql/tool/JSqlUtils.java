/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartloli.kafka.eagle.core.sql.tool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartloli.kafka.eagle.core.sql.common.JSqlMapData;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;

/**
 * Define the data structure, query by condition.
 *
 * @author smartloli.
 *         <p>
 *         Created by Mar 29, 2016
 */
public class JSqlUtils {
    public static Gson gson = new Gson();
    private static final Logger LOG = LoggerFactory.getLogger(JSqlUtils.class);

    /**
     * @param tabSchema : Table column,such as {"id":"integer","name":"varchar"}
     * @param tableName : Defining table names for query datasets, such as "user"
     * @param dataSets  : DataSets ,such as
     *                  [{"id":1,"name":"aaa"},{"id":2,"name":"bbb"},{}...]
     * @param sql       : such as "SELECT * FROM TBL"
     *
     * @return String
     *
     * @throws Exception : Throws an exception
     */
    public static String query(JSONObject tabSchema, String tableName, List<JSONArray> dataSets, String sql)
            throws Exception {
        LOG.info("invoke query::tabSchema={}, tableName={}, dataSets={}, sql={}", tabSchema, tableName, dataSets.size(),
                sql);
        File file = createTempJson();
        List<List<String>> list = new LinkedList<List<String>>();
        for (JSONArray dataSet : dataSets) {
            for (Object obj : dataSet) {
                JSONObject object = (JSONObject) obj;
                List<String> tmp = new LinkedList<>();
                for (String key : object.keySet()) {
                    tmp.add(object.getString(key));
                }
                list.add(tmp);
            }
        }
        LOG.info("list:{}", gson.toJson(list));

        List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
        if (list.size() == 0) {
            LOG.warn("got 0 size of data");
            return new Gson().toJson(ret);
        }
        JSqlMapData.loadSchema(tabSchema, tableName, list);

        Class.forName("org.apache.calcite.jdbc.Driver");
        Properties info = new Properties();

        Connection connection = DriverManager.getConnection("jdbc:calcite:model=" + file.getAbsolutePath(), info);
        Statement st = connection.createStatement();
        ResultSet result = st.executeQuery(sql);
        ResultSetMetaData rsmd = result.getMetaData();

        while (result.next()) {
            Map<String, Object> map = new HashMap<String, Object>();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                map.put(rsmd.getColumnName(i), result.getString(rsmd.getColumnName(i)));
            }
            ret.add(map);
        }
        result.close();
        connection.close();
        return new Gson().toJson(ret);
    }

    private static File createTempJson() throws IOException {
        JSONObject object = new JSONObject();
        object.put("version", "1.0");
        object.put("defaultSchema", "db");
        JSONArray array = new JSONArray();
        JSONObject tmp = new JSONObject();
        tmp.put("name", "db");
        tmp.put("type", "custom");
        tmp.put("factory", "org.smartloli.kafka.eagle.core.sql.schema.JSqlSchemaFactory");
        JSONObject tmp2 = new JSONObject();
        tmp.put("operand", tmp2.put("database", "calcite_memory_db"));
        array.add(tmp);
        object.put("schemas", array);
        File f = File.createTempFile("calcitedb", ".json");
        FileWriter out = new FileWriter(f);
        out.write(object.toJSONString());
        out.close();
        f.deleteOnExit();
        return f;
    }

}
