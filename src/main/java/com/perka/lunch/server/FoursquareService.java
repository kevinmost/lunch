package com.perka.lunch.server;

import retrofit.http.GET;
import retrofit.http.Query;

import java.util.List;

public interface FoursquareService {
    @GET("/v2/venues/explore?limit=50&radius=800&section=food&venuePhotos=1&openNow=1")
    FoursquareResponseRoot<FoursquareResponseSearch> search(@Query("ll") String latxLong);

    class FoursquareResponseSearch {
        List<FoursquareResponseGroup> groups;
    }

    class FoursquareResponseGroup {
        List<FoursquareResponseItem> items;
    }

    class FoursquareResponseItem {
        FoursquareResponseSearchVenue venue;
    }

    class FoursquareResponseSearchVenue {
        String id;
        String name;
        FoursquareResponseSearchLocation location;
        FoursquareResponseSearchHours hours;
        FoursquareResponseSearchFeaturedPhotos featuredPhotos;
        List<FoursquareResponseSearchCategory> categories;
        FoursquareResponseSearchPrice price;
    }

    class FoursquareResponseSearchLocation {
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

    class FoursquareResponseSearchHours {
        String status;
        boolean isOpen;
    }

    class FoursquareResponseSearchPrice {
        int tier;
        String message;
    }

    class FoursquareResponseSearchCategory {
        String id;
        String name;
        FoursquareResponseSearchCategoryIcon icon;
    }

    class FoursquareResponseSearchCategoryIcon extends FoursquareResponseHasImage {}

    class FoursquareResponseSearchFeaturedPhotos {
        List<FoursquareResponseSearchFeaturedPhotosItem> items;
    }

    class FoursquareResponseSearchFeaturedPhotosItem extends FoursquareResponseHasImage {
        int width;
        int height;
    }

    abstract class FoursquareResponseHasImage {
        String prefix;
        String suffix;
    }
}
