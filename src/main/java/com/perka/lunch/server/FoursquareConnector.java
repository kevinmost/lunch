package com.perka.lunch.server;

import com.perka.lunch.server.FoursquareService.*;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

import java.util.ArrayList;
import java.util.List;

public class FoursquareConnector {
    private final FoursquareService foursquareService;

    @SuppressWarnings("unused")
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
        final String latxLong = String.format("%s,%s", params.latitude, params.longitude);
        final FoursquareResponseSearch searchResponse = foursquareService.search(latxLong).response;
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

    private static boolean shouldVenueBeIncluded(FoursquareResponseSearchVenue venue, @SuppressWarnings("UnusedParameters") FoursquareSearchQueryParams filters) {
        final boolean satisfiesPriceFilter;
        //noinspection SimplifiableIfStatement
        if (venue.price == null) {
            satisfiesPriceFilter = true;
        } else {
            satisfiesPriceFilter = filters.minTier <= venue.price.tier && venue.price.tier <= filters.maxTier;
        }
        return venue.hours.isOpen && satisfiesPriceFilter;
    }

    private static String urlFromFoursquarePhoto(FoursquareResponseSearchFeaturedPhotosItem image, int width, int height) {
        return String.format("%s%dx%d%s", image.prefix, width, height, image.suffix);
    }
    private static String urlFromFoursquarePhoto(FoursquareResponseSearchCategoryIcon icon, int dimensions) {
        return String.format("%s%d%s", icon.prefix, dimensions, icon.suffix);
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
            pictureUrlRaw = urlFromFoursquarePhoto(photo, photo.width, photo.height);
            if (croppedWidth > 0 && croppedHeight > 0) {
                pictureUrlCropped = urlFromFoursquarePhoto(photo, croppedWidth, croppedHeight);
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
                categoryIconUrl = urlFromFoursquarePhoto(category.icon, 88);
            }
            if (venue.price == null) {
                price = null;
            } else {
                price = venue.price.message;
            }
            openUntil = venue.hours.status;
        }
    }
}
