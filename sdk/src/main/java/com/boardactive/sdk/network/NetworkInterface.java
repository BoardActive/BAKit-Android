package com.boardactive.sdk.network;


import java.util.List;

import com.boardactive.sdk.models.AdDrop;
import com.boardactive.sdk.models.AdDropBookmarkResponse;
import com.boardactive.sdk.models.AdDropLatLng;
import com.boardactive.sdk.models.AdDropResponse;
import com.boardactive.sdk.models.AdDrops;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NetworkInterface {

    @GET("mobile/promotions")
    Observable<List<AdDrops>> getAdDrops();

    @GET("mobile/promotions/{id}")
    Observable<AdDrop> getAdDrop(@Path("id") int id);

    @POST("mobile/promotions/{id}/bookmarks")
    Observable<AdDropBookmarkResponse> createAdDropBookmark(@Path("id") int id);

    @DELETE("mobile/promotions/{id}/bookmarks")
    Observable<AdDropBookmarkResponse> removeAdDropBookmark(@Path("id") int id);

    @GET("mobile/promotions/bookmarks")
    Observable<List<AdDrops>> getAdDropsFav();

    @POST("/mobile/mobile_geopoints")
    Observable<AdDropBookmarkResponse> createGeopoint(@Body AdDropLatLng adDropLatLng);

}
