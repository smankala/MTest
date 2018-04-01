package com.santhosh.mtest.network;


import com.santhosh.mtest.LoginResponse;
import com.santhosh.mtest.LoginUser;
import com.santhosh.mtest.User;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface LoginService {

    @POST("users")
    Single<User> createUser(@Body User user);

    @POST("auth")
    Single<LoginResponse> login(@Body LoginUser user);

    @PATCH("users/{id}")
    Single<User> update( @Header("Authorization") String token, @Path("id") int userId,@Body User user);

    @GET("users/{id}")
    Single<User> getUser(@Header("Authorization") String token,@Path("id") int userId);
}
