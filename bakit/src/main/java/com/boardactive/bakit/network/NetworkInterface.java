package com.boardactive.bakit.network;


import com.boardactive.bakit.models.Event;
import com.boardactive.bakit.models.Location;
import com.boardactive.bakit.models.Response;
import com.boardactive.bakit.models.User;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

// List of imports w/ observables
public interface NetworkInterface {

    @PUT("me")
    Observable<User> putMe();

    @POST("me")
    Observable<User> postMe(@Body User user);

    @POST("locations")
    Observable<Response> postLocation(@Body Location location);

    @POST("events")
    Observable<Response> postEvent(@Body Event event);

}
