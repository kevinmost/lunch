package com.perka.lunch.server;

import org.jetbrains.annotations.NotNull;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

import java.util.ArrayList;
import java.util.List;
import com.perka.lunch.server.FoursquareService.*;

public class FoursquareConnector {
    private final FoursquareService foursquareService;

    public FoursquareConnector(final String token) {
        final RequestInterceptor addTokenInterceptor = (RequestInterceptor.RequestFacade request) -> {
            request.addQueryParam("oauth_token", token);
            request.addQueryParam("m", "foursquare");
            request.addQueryParam("v", "20150825");
        };
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.foursquare.com")
                .setRequestInterceptor(addTokenInterceptor)
                .build();
        this.foursquareService = restAdapter.create(FoursquareService.class);
    }

    public FoursquareOutgoing search(@NotNull String lat, @NotNull String lon, int width, int height) {
        final FoursquareOutgoing returnValue = new FoursquareOutgoing();

        final FoursquareResponseSearch searchResponse = foursquareService.search(String.format("%s,%s", lat, lon));
        final List<FoursquareResponseVenue> venues = searchResponse.response.venues;

        for (FoursquareResponseVenue venue : venues) {
            final FoursquareResponsePhotos photos = foursquareService.photos(venue.id);
            final FoursquareResponsePhotosItem featuredPhoto = photos.response.photos.items.get(0);
            final FoursquareOutgoingVenue outgoingVenue = new FoursquareOutgoingVenue();
            outgoingVenue.id = venue.id;
            outgoingVenue.location = venue.location;
            outgoingVenue.name = venue.name;
            outgoingVenue.pictureUrlRaw = getPictureUrl(featuredPhoto, featuredPhoto.width, featuredPhoto.height);
            if (width > 0 && height > 0) {
                outgoingVenue.pictureUrlCropped = getPictureUrl(featuredPhoto, width, height);
            } else {
                outgoingVenue.pictureUrlCropped = outgoingVenue.pictureUrlRaw;
            }
            returnValue.venues.add(outgoingVenue);
        }
        return returnValue;
    }

    private static String getPictureUrl(FoursquareResponsePhotosItem photo, int width, int height) {
        return String.format("%s%dx%d%s", photo.prefix, width, height, photo.suffix);
    }

    public static class FoursquareOutgoing {
        final List<FoursquareOutgoingVenue> venues = new ArrayList<>();
    }

    public static class FoursquareOutgoingVenue extends FoursquareResponseVenue {
        String pictureUrlRaw;
        String pictureUrlCropped;
    }
}
