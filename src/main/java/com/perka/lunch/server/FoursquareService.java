package com.perka.lunch.server;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

import java.util.List;

public interface FoursquareService {
    @GET("/v2/venues/search?intent=browse&radius=800&categoryId=4d4b7105d754a06374d81259")
    FoursquareResponseSearch search(@Query("ll") String latxLong);

    @GET("/v2/venues/{venueId}/photos")
    FoursquareResponsePhotos photos(@Path("venueId") String venueId);

    class FoursquareResponsePhotos {
        FoursquareResponsePhotosResponse response;
    }

    class FoursquareResponsePhotosResponse {
        FoursquareResponsePhotosResponsePhotos photos;
    }

    class FoursquareResponsePhotosResponsePhotos {
        List<FoursquareResponsePhotosItem> items;
    }

    class FoursquareResponsePhotosItem {
        String prefix;
        String suffix;
        int width;
        int height;
    }

    class FoursquareResponseSearch {
        FoursquareResponseSearchResponse response = new FoursquareResponseSearchResponse();
    }
    class FoursquareResponseSearchResponse {
        List<FoursquareResponseVenue> venues;
    }
    class FoursquareResponseVenue {
        String id;
        String name;
        FoursquareResponseLocation location;
    }
    class FoursquareResponseLocation {
        String address;
        String crossStreet;
        String lat;
        String lng;
        String distance;
        String postalCode;
        String cc;
        String city;
        String state;
        String country;
    }
}
