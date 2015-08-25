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

        public FoursquareResponseSearchVenue() {
        }

        public FoursquareResponseSearchVenue(FoursquareResponseSearchVenue copy) {
            this.id = copy.id;
            this.name = copy.name;
            this.location = copy.location;
        }
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
        boolean isOpen;
    }

    class FoursquareResponseSearchFeaturedPhotos {
        List<FoursquareResponseSearchFeaturedPhotosItem> items;
    }

    class FoursquareResponseSearchFeaturedPhotosItem {
        String prefix;
        String suffix;
        int width;
        int height;
    }
}
