package com.example.boghdady.campusapp.Retrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by boghdady on 23/02/17.
 */

public class Interfaces {

    public interface User_Details{
        @POST("Campus/Get_User_Details.php")
        Call<Responses.GetUserDetailsResponse> getUserData(@Body SentBody.UserOperationBody userOperationBody);
    }
    public interface User_RegisterAPI{
        @POST("Campus/Insert_User.php")
        Call<Responses.SignupResponse> insertUser(@Body SentBody.UserOperationBody userOperationBody);
    }

    public interface User_InserEvent{
        @POST("Campus/Insert_Event.php")
        Call<Responses.DefaultResponse> insertEvent(@Body SentBody.InserEventBody inserEventBody);
    }

    public interface GetPlacesLocation{
        @GET("Campus/Get_All_Locations.php")
        Call<Responses.LocationPlacesResponse> getPlacesLoaction();
    }

    public interface User_Login{
        @POST("Campus/Login.php")
        Call<Responses.SignupResponse> userLogin(@Body SentBody.LoginBody loginBody);
    }

    public interface Update_User{
        @POST("Campus/Update_User.php")
        Call<Responses.DefaultResponse> update_user(@Body SentBody.UpdateUserBody updateUserBody);
    }
}
