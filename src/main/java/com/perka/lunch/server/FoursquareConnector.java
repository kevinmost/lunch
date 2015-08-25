package com.perka.lunch.server;

import com.perka.lunch.server.FoursquareService.*;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

import java.util.ArrayList;
import java.util.List;

public class FoursquareConnector {
    private final FoursquareService foursquareService;

    public FoursquareConnector(final String token) {
        this(request -> {
            addVersionAndModeToRequest(request);
            request.addQueryParam("oauth_token", token);
        });
    }

    public FoursquareConnector(final String clientId, final String clientSecret) {
        this(request -> {
            addVersionAndModeToRequest(request);
            request.addQueryParam("client_id", clientId);
            request.addQueryParam("client_secret", clientSecret);
        });
    }

    private FoursquareConnector(RequestInterceptor interceptor) {
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.foursquare.com")
                .setRequestInterceptor(interceptor)
                .build();
        this.foursquareService = restAdapter.create(FoursquareService.class);
    }

    public FoursquareOutgoingResponse search(FoursquareSearchQueryParams params) {
        final FoursquareResponseSearch searchResponse = foursquareService.search(String.format("%s,%s", params.latitude, params.longitude)).response;
        final FoursquareOutgoingResponse out = new FoursquareOutgoingResponse();

        for (FoursquareResponseGroup group : searchResponse.groups) {
            for (FoursquareResponseItem item : group.items) {
                if (shouldVenueBeIncluded(item.venue, params)) {
                    final FoursquareOutgoingVenue outVenue = new FoursquareOutgoingVenue(item.venue, params.croppedWidth, params.croppedHeight);
                    out.venues.add(outVenue);
                }
            }
        }

        return out;
    }

    private static boolean shouldVenueBeIncluded(FoursquareResponseSearchVenue venue, FoursquareSearchQueryParams filters) {
        return venue.hours.isOpen &&
                venue.price.tier >= filters.minTier && venue.price.tier <= filters.maxTier
                ;
    }

    private static String urlFromFoursquareImage(FoursquareResponseHasImage image, int width, int height) {
        return String.format("%s%dx%d%s", image.prefix, width, height, image.suffix);
    }

    private static void addVersionAndModeToRequest(RequestInterceptor.RequestFacade request) {
        request.addQueryParam("m", "foursquare");
        request.addQueryParam("v", "20150825");
    }

    public static class FoursquareOutgoingResponse {
        final List<FoursquareOutgoingVenue> venues = new ArrayList<>();
    }

    public static class FoursquareOutgoingVenue {
        final String name;
        final FoursquareResponseSearchLocation location;
        final String pictureUrlRaw;
        final String pictureUrlCropped;
        final String categoryId;
        final String categoryName;
        final String categoryIconUrl;
        final String price;
        final String openUntil;

        public FoursquareOutgoingVenue(FoursquareResponseSearchVenue venue, int croppedWidth, int croppedHeight) {
            name = venue.name;
            location = venue.location;
            final FoursquareResponseSearchFeaturedPhotosItem photo = venue.featuredPhotos.items.get(0);
            pictureUrlRaw = urlFromFoursquareImage(photo, photo.width, photo.height);
            if (croppedWidth > 0 && croppedHeight > 0) {
                pictureUrlCropped = urlFromFoursquareImage(photo, croppedWidth, croppedHeight);
            } else {
                pictureUrlCropped = null;
            }
            if (venue.categories.isEmpty()) {
                categoryId = null;
                categoryName = null;
                categoryIconUrl = null;
            } else {
                final FoursquareResponseSearchCategory category = venue.categories.get(0);
                categoryId = category.id;
                categoryName = category.name;
                categoryIconUrl = urlFromFoursquareImage(category.icon, 88, 88);
            }
            price = venue.price.message;
            openUntil = venue.hours.status;
        }
    }
}
