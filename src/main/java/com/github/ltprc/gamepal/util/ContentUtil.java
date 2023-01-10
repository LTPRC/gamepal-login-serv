package com.github.ltprc.gamepal.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ContentUtil {

    public static JSONObject jsonFile2JSONObject(String filePath) {
        try {
            File jsonFile = new File(filePath);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            reader.close();
            return (JSONObject) JSONObject.parse(sb.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static JSONArray jsonFile2JSONArray(String filePath) {
        try {
            File jsonFile = new File(filePath);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            reader.close();
            return (JSONArray) JSONArray.parse(sb.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
