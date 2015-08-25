package com.perka.lunch.server;

import com.google.gson.annotations.SerializedName;
import spark.QueryParamsMap;

import java.lang.reflect.Field;

public class FoursquareSearchQueryParams {
    public String latitude = "40.7529081";
    public String longitude = "-73.9739277";

    @SerializedName("width")
    public int croppedWidth = -1;

    @SerializedName("height")
    public int croppedHeight = -1;

    public int minTier = Integer.MIN_VALUE;
    public int maxTier = Integer.MAX_VALUE;

    public FoursquareSearchQueryParams(QueryParamsMap map) {
        for (Field field : getClass().getFields()) {
            final String lookupName;
            final SerializedName serializedName = field.getAnnotation(SerializedName.class);
            if (serializedName == null) {
                lookupName = field.getName();
            } else {
                lookupName = serializedName.value();
            }
            final String lookupValue = map.value(lookupName);
            if (lookupValue != null) {
                try {
                    // TODO: Do all the other parts of this gross chain
                    if (field.getType().isAssignableFrom(Integer.TYPE)) {
                        field.setInt(this, Integer.parseInt(lookupValue));
                    } else {
                        field.set(this, lookupValue);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
