package com.example.boghdady.campusapp.Retrofit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by boghdady on 23/02/17.
 */

public class Responses {

    public static class GetUserDetailsResponse{
        ArrayList<Models.UserModel> Users;
        int success;

        public ArrayList<Models.UserModel> getUsers() {
            return Users;
        }

        public void setUsers(ArrayList<Models.UserModel> users) {
            Users = users;
        }

        public int getSuccess() {
            return success;
        }

        public void setSuccess(int success) {
            this.success = success;
        }
    }
//----------------------------------------------------------------------------------------------------------

    public static class DefaultResponse{
        int success;
        String message;

        public int getSuccess() {
            return success;
        }

        public void setSuccess(int success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

//---------------------------------------------------------------------------------------------------------

public static class LoginResponse{
    int success;
    String message;
    String checking;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getChecking() {
        return checking;
    }

    public void setChecking(String checking) {
        this.checking = checking;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
//---------------------------------------------------------------------------------------------------------------
    public  static class  SignupResponse{



    Models.UserModel user;

    public Models.UserModel getUser() {
        return user;
    }

    public void setUser(Models.UserModel user) {
        this.user = user;
    }

    int success;
    String message;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


//----------------------------------------------------------------------------------------------------------------------------------------


    public static class LocationPlacesResponse{

        List<Models.LocationModel> Location ;
        int success ;

        public List<Models.LocationModel> getLocation() {
            return Location;
        }

        public void setLocation(List<Models.LocationModel> location) {
            Location = location;
        }

        public int getSuccess() {
            return success;
        }


        public void setSuccess(int success) {
            this.success = success;
        }
    }
}
