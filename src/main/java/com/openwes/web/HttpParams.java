package com.openwes.web;

import com.openwes.core.utils.StrConv;
import io.vertx.core.MultiMap;
import java.util.Map;

/**
 *
 * @author Deadpool {@literal (locngo@fortna.com)}
 * @since Jun 25, 2019
 * @version 1.0.0
 *
 */
public class HttpParams {

    //FROM MULTIMAP
    public final static boolean getBoolean(MultiMap multimap, String key) {
        return StrConv.stringToBoolean(multimap.get(key));
    }

    public final static short getShort(MultiMap multimap, String key) {
        return StrConv.stringToShort(multimap.get(key));
    }

    public final static short getShortOrErr(MultiMap multimap, String key) {
        return StrConv.stringToShortOrErr(multimap.get(key));
    }

    public final static int getInt(MultiMap multimap, String key) {
        return StrConv.stringToInt(multimap.get(key));
    }

    public final static int getIntOrErr(MultiMap multimap, String key) {
        return StrConv.stringToIntOrErr(multimap.get(key));
    }

    public final static long getLong(MultiMap multimap, String key) {
        return StrConv.stringToLong(multimap.get(key));
    }

    public final static long getLongOrErr(MultiMap multimap, String key) {
        return StrConv.stringToLongOrErr(multimap.get(key));
    }

    public final static float getFloat(MultiMap multimap, String key) {
        return StrConv.stringToFloat(multimap.get(key));
    }

    public final static float getFloatOrError(MultiMap multimap, String key) {
        return StrConv.stringToFloatOrErr(multimap.get(key));
    }

    public final static double getDouble(MultiMap multimap, String key) {
        return StrConv.stringToDouble(multimap.get(key));
    }

    public final static double getDoubleOrErr(MultiMap multimap, String key) {
        return StrConv.stringToDoubleOrErr(multimap.get(key));
    }

    //FROM MAP
    public final static boolean getBoolean(Map<String, String> map, String key) {
        return StrConv.stringToBoolean(map.get(key));
    }

    public final static short getShort(Map<String, String> map, String key) {
        return StrConv.stringToShort(map.get(key));
    }

    public final static short getShortOrErr(Map<String, String> map, String key) {
        return StrConv.stringToShortOrErr(map.get(key));
    }

    public final static int getInt(Map<String, String> map, String key) {
        return StrConv.stringToInt(map.get(key));
    }

    public final static int getIntOrErr(Map<String, String> map, String key) {
        return StrConv.stringToIntOrErr(map.get(key));
    }

    public final static long getLong(Map<String, String> map, String key) {
        return StrConv.stringToLong(map.get(key));
    }

    public final static long getLongOrErr(Map<String, String> map, String key) {
        return StrConv.stringToLongOrErr(map.get(key));
    }

    public final static float getFloat(Map<String, String> map, String key) {
        return StrConv.stringToFloat(map.get(key));
    }

    public final static float getFloatOrError(Map<String, String> map, String key) {
        return StrConv.stringToFloatOrErr(map.get(key));
    }

    public final static double getDouble(Map<String, String> map, String key) {
        return StrConv.stringToDouble(map.get(key));
    }

    public final static double getDoubleOrErr(Map<String, String> map, String key) {
        return StrConv.stringToDoubleOrErr(map.get(key));
    }
    //End
}
