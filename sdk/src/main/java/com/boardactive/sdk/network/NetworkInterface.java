package com.boardactive.sdk.network;


import com.boardactive.sdk.models.AdDrop;
import com.boardactive.sdk.models.AdDropBookmarkResponse;
import com.boardactive.sdk.models.AdDropEvent;
import com.boardactive.sdk.models.AdDropLatLng;
import com.boardactive.sdk.models.AdDropRegister;
import com.boardactive.sdk.models.AdDrops;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
// List of imports w/ observables
public interface NetworkInterface {

    @GET("mobile/promotions")
    Observable<List<AdDrops>> getAdDrops();

    @GET("mobile/promotions/{id}")
    Observable<AdDrop> getAdDrop(@Path("id") int id);

    @POST("mobile/promotions/{id}/bookmarks")
    Observable<AdDropBookmarkResponse> createAdDropBookmark(@Path("id") int id);

    @POST("mobile/events")
    Observable<AdDropEvent> sendEvent(@Body AdDropEvent eventObject);

    @POST("web/v1/apps")
    Observable<AdDropRegister> registerApp(@Body AdDropRegister registerObject);

    @DELETE("mobile/promotions/{id}/bookmarks")
    Observable<AdDropBookmarkResponse> removeAdDropBookmark(@Path("id") int id);

    @GET("mobile/promotions/bookmarks")
    Observable<List<AdDrops>> getAdDropsFav();

    @POST("/mobile/mobile_geopoints")
    Observable<AdDropBookmarkResponse> createGeopoint(@Body AdDropLatLng adDropLatLng);

}
