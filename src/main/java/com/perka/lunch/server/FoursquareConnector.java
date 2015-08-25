package com.perka.lunch.server;

import com.perka.lunch.server.FoursquareService.*;
import org.jetbrains.annotations.NotNull;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

import java.util.ArrayList;
import java.util.List;

public class FoursquareConnector {
    private final FoursquareService foursquareService;

    public FoursquareConnector(final String token) {
        this(request -> {
            request.addQueryParam("oauth_token", token);
            request.addQueryParam("m", "foursquare");
            request.addQueryParam("v", "20150825");
        });
    }

    public FoursquareConnector(final String clientId, final String clientSecret) {
        this(request -> {
            request.addQueryParam("client_id", clientId);
            request.addQueryParam("client_secret", clientSecret);
            request.addQueryParam("m", "foursquare");
            request.addQueryParam("v", "20150825");
        });
    }

    private FoursquareConnector(RequestInterceptor interceptor) {
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.foursquare.com")
                .setRequestInterceptor(interceptor)
                .build();
        this.foursquareService = restAdapter.create(FoursquareService.class);
    }

    public FoursquareOutgoingResponse search(@NotNull String lat, @NotNull String lon, int width, int height) {
        final FoursquareResponseSearch searchResponse = foursquareService.search(String.format("%s,%s", lat, lon)).response;
        final FoursquareOutgoingResponse out = new FoursquareOutgoingResponse();

        for (FoursquareResponseGroup group : searchResponse.groups) {
            for (FoursquareResponseItem item : group.items) {
                final FoursquareOutgoingVenue outVenue = new FoursquareOutgoingVenue(item.venue, width, height);
                out.venues.add(outVenue);
            }
        }

        return out;
    }

    private static String urlFromVenuePhoto(FoursquareResponseSearchFeaturedPhotosItem photo, int width, int height) {
        return String.format("%s%dx%d%s", photo.prefix, width, height, photo.suffix);
    }

    public static class FoursquareOutgoingResponse {
        final List<FoursquareOutgoingVenue> venues = new ArrayList<>();
    }

    public static class FoursquareOutgoingVenue {
        final String id;
        final String name;
        final FoursquareResponseSearchLocation location;
        final String pictureUrlRaw;
        final String pictureUrlCropped;

        public FoursquareOutgoingVenue(FoursquareResponseSearchVenue venue, int croppedWidth, int croppedHeight) {
            id = venue.id;
            name = venue.name;
            location = venue.location;
            final FoursquareResponseSearchFeaturedPhotosItem photo = venue.featuredPhotos.items.get(0);
            pictureUrlRaw = urlFromVenuePhoto(photo, photo.width, photo.height);
            if (croppedWidth > 0 && croppedHeight > 0) {
                pictureUrlCropped = urlFromVenuePhoto(photo, croppedWidth, croppedHeight);
            } else {
                pictureUrlCropped = null;
            }
        }
    }
}
