package com.perka.lunch.server;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Spark;

public class LunchServer {
    private static final Gson GSON = new Gson();

    private static final String TOKEN = System.getenv("TOKEN_FOURSQUARE");
    private static final String CLIENT_ID = System.getenv("CLIENT_ID");
    private static final String CLIENT_SECRET = System.getenv("CLIENT_SECRET");

    private static final FoursquareConnector FOURSQUARE_CONNECTOR = new FoursquareConnector(CLIENT_ID, CLIENT_SECRET);

    public static void main(String[] args) {
        setSparkListenPort();
        initRouteSearch();
    }

    private static void setSparkListenPort() {
        final int portNumber = Integer.parseInt(System.getenv("PORT"));
        Spark.port(portNumber);
    }

    private static void initRouteSearch() {
        Spark.get("/search", (Request request, Response response) -> {
            final String latitude = request.queryParams("latitude");
            final String longitude = request.queryParams("longitude");
            final String width = request.queryParams("width");
            final String height = request.queryParams("height");

            return FOURSQUARE_CONNECTOR.search(
                    latitude == null ? "40.7529081" : latitude,
                    longitude == null ? "-73.9739277" : longitude,
                    Strings.isNullOrEmpty(width) ? -1 : Integer.parseInt(width),
                    Strings.isNullOrEmpty(height) ? -1 : Integer.parseInt(height)
            );
        }, GSON::toJson);
    }
}
