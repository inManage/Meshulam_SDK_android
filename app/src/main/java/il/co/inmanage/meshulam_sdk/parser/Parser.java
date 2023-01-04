package il.co.inmanage.meshulam_sdk.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import il.co.inmanage.meshulam_sdk.interfaces.Parseable;
import il.co.inmanage.meshulam_sdk.server_responses.BaseResponse;
import il.co.inmanage.meshulam_sdk.server_responses.SortResponse;
import il.co.inmanage.meshulam_sdk.utils.LoggingHelper;
import il.co.inmanage.meshulam_sdk.utils.NumberUtils;


@SuppressWarnings("unchecked")

public class Parser {

    public static <T> T jsonParse(JSONArray jsonArray, int key, T ret) {
        if (jsonArray != null && jsonArray.length() > key) {
            try {
                Object obj = jsonArray.get(key);
                ret = getObjectData(obj, ret);
            } catch (Exception e) {
                onParseFailure(e, key + "");
            }
        }
        return ret;
    }

    /*public static HashMap<String, Boolean> createStringBooleanHashMap(JSONObject jsonObject) {
        LoggingHelper.entering();
        HashMap<String, Boolean> hashMap = new HashMap();
        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            hashMap.put(key, Parser.jsonParse(jsonObject, key, false));
        }
        return hashMap;
    }*/

    private static <T> T getObjectData(Object obj, T ret) throws JSONException {
        String objSimpleName = obj.getClass().getSimpleName();
        String retSimpleName = ret.getClass().getSimpleName();
        if (objSimpleName == null || objSimpleName.isEmpty()) {
            return ret;
        }
        if (retSimpleName.equals(objSimpleName)) {
            ret = (T) obj;
        } else if (ret instanceof JSONObject && obj instanceof JSONArray) {
            ret = (T) convertJsonArrayToJsonObject((JSONArray) obj);
        } else if (ret instanceof Boolean) {
            String data = obj.toString();
            ret = (T) Boolean.valueOf(data.equals("1"));
        } else if (ret instanceof String && NumberUtils.stringIsNumber(obj.toString())) {
            ret = (T) obj.toString();
        } else if (ret instanceof JSONArray && obj instanceof JSONObject) {
            ret = (T) convertJsonObjectToJsonArray((JSONObject) obj);
        } else {
            ret = convertStringToNumber(ret, obj);
        }
        return ret;
    }

    public static <T> T jsonParse(JSONObject jsonObject, String key, T ret) {
        if (jsonObject != null && jsonObject.has(key)) {
            try {
                Object obj = jsonObject.get(key);
                if (!obj.equals("")) {
                    ret = getObjectData(obj, ret);
                }
            } catch (Exception e) {
                onParseFailure(e, key);
            }
        }
        return ret;
    }

    public static <T> T jsonParseT(JSONObject jsonObject, String key, T ret) {
        if (jsonObject != null && jsonObject.has(key)) {
            try {
                Object obj = jsonObject.get(key);
                ret = getObjectData(obj, ret);
            } catch (Exception e) {
                onParseFailure(e, key);
            }
        }
        return ret;
    }


    public static String jsonParse(JSONObject jsonObject, String key, String ret) {
        if (jsonObject != null && jsonObject.has(key)) {
            try {
                return jsonObject.getString(key).equals("null") ? "" : jsonObject.getString(key);
            } catch (Exception e) {
                try {
                    return getObjectData(getObjectData(jsonObject.get(key), ret), ret);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return ret;
    }

//    public static int jsonParse(JSONObject jsonObject, String key, int ret) {
//        if (jsonObject != null && jsonObject.has(key)) {
//            try {
//                return jsonObject.getInt(key);
//            } catch (Exception e) {
//                try {
//                    return getObjectData(getObjectData(jsonObject.get(key), ret), ret);
//                } catch (JSONException e1) {
//                    e1.printStackTrace();
//                }
//            }
//        }
//        return ret;
//    }

    private static void onParseFailure(Exception e, String key) {
        LoggingHelper.e("exception type:" + e.getClass().getSimpleName() + " message:" + e.getMessage() + " was thrown by " + key);
    }

    private static <T> T convertStringToNumber(T ret, Object obj) {
        if (NumberUtils.stringIsNumber(obj.toString())) {
            if (ret instanceof Integer) {
                ret = (T) NumberUtils.getIntegerFromString(obj + "");
            } else if (ret instanceof Double) {
                ret = (T) (Double) NumberUtils.getDoubleFromString(obj + "");
            } else if (ret instanceof Long) {
                ret = (T) (Long) NumberUtils.getLongFromString(obj + "");
            } else if (ret instanceof Float) {
                ret = (T) NumberUtils.getFloatFromString(obj + "");
            }
        }
        return ret;
    }

    private static JSONArray convertJsonObjectToJsonArray(JSONObject jsonObject) throws JSONException {
        Iterator<String> iterator = jsonObject.keys();
        JSONArray jsonArray = new JSONArray();
        while (iterator.hasNext()) {
            String key2 = iterator.next();
            jsonArray.put(jsonObject.get(key2));
        }
        return jsonArray;
    }

    private static JSONObject convertJsonArrayToJsonObject(JSONArray array) throws JSONException {
        JSONObject ret = new JSONObject();
        for (int i = 0; i < array.length(); i++) {
            ret.put(i + "", array.get(i));
        }
        return ret;
    }

    public static <T> List<T> createList(JSONObject jsonObject, T defaultValue) {
        List<T> list = new ArrayList<>();
        if (jsonObject != null) {
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                T value = Parser.jsonParse(jsonObject, key, defaultValue);
                if (value != defaultValue) {
                    list.add(value);
                }
            }
        }
        return list;
    }

    public static HashMap<String, String> getHashMapFromJson(JSONObject jsonObject) {
        LoggingHelper.entering();
        HashMap<String, String> hashMap = new HashMap<>();
        if (jsonObject != null) {
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                hashMap.put(key, Parser.jsonParse(jsonObject, key, Parser.createTempString()));
            }
        }
        return hashMap;
    }

    public static JSONArray createTempJsonArray() {
        return new JSONArray();
    }

    public static JSONObject createTempJsonObject() {
        return new JSONObject();
    }

    public static String createTempString() {
        return "";
    }

    /*public static BaseResponse getBaseResponseFromJson(JSONObject json, Parseable parseable, String key) {
        BaseResponse response;
        JSONObject jsonObject = Parser.jsonParse(json, key, new JSONObject());
        response = parseable.createResponse(jsonObject);
        return response;
    }*/

    public static List<? extends BaseResponse> createList(JSONObject json, Parseable parseable) {
        List<BaseResponse> list = new ArrayList<>();
        if (json != null) {
            Iterator<String> iterator = json.keys();
            while (iterator.hasNext()) {
                list.add(parseable.createResponse(Parser.jsonParse(json, iterator.next(), new JSONObject())));
            }
        }
        return list;
    }

    public static List<? extends BaseResponse> createList(JSONArray jsonArray, Parseable parseable) {
        List<BaseResponse> list = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(parseable.createResponse(Parser.jsonParse(jsonArray, i, new JSONObject())));
            }
        }
        return list;
    }

    public static List<String> createList(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(Parser.jsonParse(jsonArray, i, ""));
            }
        }
        return list;
    }

    public static List<? extends BaseResponse> createList(JSONObject json, SortResponse sortResponse) {
        List<SortResponse> list = (List<SortResponse>) createList(json, (Parseable) sortResponse);
        Collections.sort(list);
        return list;
    }

    public static List<? extends BaseResponse> createList(JSONArray jsonArray, SortResponse sortResponse) {
        List<SortResponse> list = (List<SortResponse>) createList(jsonArray, (Parseable) sortResponse);
        Collections.sort(list);
        return list;
    }

    public static HashMap<String, List<String>> createHashMap(JSONObject json) {
        HashMap<String, List<String>> hashMap = new HashMap<>();
        if (json != null) {
            Iterator<String> iterator = json.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                JSONArray jsonObject = Parser.jsonParse(json, key, new JSONArray());
                hashMap.put(key, createList(jsonObject));
            }
        }
        return hashMap;
    }

    public static HashMap<String, ? extends BaseResponse> createHashMap(JSONObject json, Parseable parser) {
        HashMap<String, BaseResponse> hashMap = new HashMap<>();
        if (json != null) {
            Iterator<String> iterator = json.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                hashMap.put(key, parser.createResponse(Parser.jsonParse(json, key, new JSONObject())));
            }
        }
        return hashMap;
    }

    public static HashMap<String, ? extends BaseResponse> createHashMap(JSONArray jsonArray, Parseable parser) {
        HashMap<String, BaseResponse> hashMap = new HashMap<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.optJSONObject(i);
            Iterator<String> iterator = json.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                JSONObject jsonObject = Parser.jsonParse(json, key, new JSONObject());
                hashMap.put(key, parser.createResponse(jsonObject));
            }
        }
        return hashMap;
    }

    public static JSONObject createJsonObject(String json) {
        try {
            return new JSONObject(json);
        } catch (Exception e) {
            return createTempJsonObject();
        }
    }

    public static List<String> createStringList(JSONObject jsonObject, String json) {
        try {
            JSONArray itemArray = jsonObject.getJSONArray(json);
            return Parser.createList(itemArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static HashMap<String, ? extends BaseResponse> createHashMapWithSpecificKey(JSONObject json, BaseResponse response, String specificKey) {
        HashMap<String, BaseResponse> hashMap = new HashMap<String, BaseResponse>();
        Iterator<String> iterator = json.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            JSONObject jsonObject = Parser.jsonParse(json, key, new JSONObject());
            String mapKey = Parser.jsonParse(jsonObject, specificKey, Parser.createTempString());
            hashMap.put(mapKey, response.createResponse(jsonObject));
        }
        return hashMap;
    }
}
