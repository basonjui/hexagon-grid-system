package com.masterisehomes.geometryapi.hexagon;

import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Type;

import lombok.Getter;
import lombok.ToString;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;



@ToString
public class HexagonDto {
    @Getter
    private double latitude, longitude, circumradius;
    @Getter
    private Coordinates centroid;
    @Getter
    private Hexagon hexagon;
    final static private Gson gson = new Gson();

    public HexagonDto(HashMap<String, Object> lambdaEvent) {
        String lambdaBody = lambdaEvent.get("body").toString(); // lambdaEvent.get("body") returns an Object
    }

    public HexagonDto(Hexagon hexagon) {
        this.hexagon = hexagon;
        this.centroid = this.hexagon.getCentroid();
        this.circumradius = this.hexagon.getCircumradius();
    }

    public static void main(String[] args) {
        String json = "{\"headers\":{\"accept\":\"*/*\",\"accept-encoding\":\"gzip,deflate,br\",\"cache-control\":\"no-cache\",\"content-length\":\"94\",\"content-type\":\"application/json\",\"host\":\"w69ofkpum1.execute-api.ap-southeast-1.amazonaws.com\",\"postman-token\":\"b82131ff-fbd3-4f82-b161-9928f5841126\",\"user-agent\":\"PostmanRuntime/7.29.0\",\"x-amzn-trace-id\":\"Root\\u003d1-62c40256-4142296c49732d04792267e9\",\"x-forwarded-for\":\"58.186.187.212\",\"x-forwarded-port\":\"443\",\"x-forwarded-proto\":\"https\"},\"isBase64Encoded\":false,\"rawPath\":\"/test\",\"routeKey\":\"POST/test\",\"requestContext\":{\"accountId\":\"166716236007\",\"apiId\":\"w69ofkpum1\",\"domainName\":\"w69ofkpum1.execute-api.ap-southeast-1.amazonaws.com\",\"domainPrefix\":\"w69ofkpum1\",\"http\":{\"method\":\"POST\",\"path\":\"/test\",\"protocol\":\"HTTP/1.1\",\"sourceIp\":\"58.186.187.212\",\"userAgent\":\"PostmanRuntime/7.29.0\"},\"requestId\":\"UyVNng7WSQ0EJXg\\u003d\",\"routeKey\":\"POST/test\",\"stage\":\"$default\",\"time\":\"05/Jul/2022:09:20:22+0000\",\"timeEpoch\":1.657012822985E12},\"body\":\"{\\n\\\"latitude\\\":10.779922217270345,\\n\\\"longitude\\\":106.69901892529006,\\n\\\"radius\\\":100\\n}\",\"version\":\"2.0\",\"rawQueryString\":\"\"}";
        JsonObject jsonObj = gson.fromJson(json, JsonObject.class);

        // Get String in "body" key, get UNESCAPED Json String, and parse as JsonObject
        String escapedString = jsonObj.get("body").getAsString(); 
        JsonObject escapedStringObj = gson.fromJson(escapedString, JsonObject.class);
        
        System.out.println(escapedStringObj.get("latitude").getAsDouble());

        Type stringObjectmap = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> map = gson.fromJson(json, stringObjectmap);

        System.out.println(jsonObj);
    }
}
