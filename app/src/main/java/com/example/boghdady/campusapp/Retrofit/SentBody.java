package com.example.boghdady.campusapp.Retrofit;

/**
 * Created by boghdady on 23/02/17.
 */

public class SentBody {


//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public static class UserOperationBody{
        String User_ID,User_Name,User_Email,User_Password,User_Image, User_Phone,
        User_Faculty , Study_Year , Department , User_Section , Checking;

        // constructor for student signup
        public UserOperationBody(String user_Name, String user_Email, String user_Password, String user_Image, String user_Phone , String user_Faculty ,String study_Year , String department , String user_Section , String checking) {
            User_Name = user_Name;
            User_Email = user_Email;
            User_Password = user_Password;
            User_Image = user_Image;
            User_Phone = user_Phone;
            User_Faculty = user_Faculty;
            Study_Year = study_Year;
            Department = department;
            User_Section = user_Section;
            Checking = checking;
        }

        // constructor for doctor signup
        public UserOperationBody(String user_Name, String user_Email, String user_Password, String user_Image, String user_Phone , String user_Faculty , String checking) {
            User_Name = user_Name;
            User_Email = user_Email;
            User_Password = user_Password;
            User_Image = user_Image;
            User_Phone = user_Phone;
            User_Faculty = user_Faculty;
            Checking = checking;
        }

        // constructor for login
        public UserOperationBody(String user_Email, String user_Password) {
            User_Email = user_Email;
            User_Password = user_Password;
        }

    }

//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public static class InserEventBody {

    String Event_User_Id, Event_Name, Event_Details, Event_Location_Lng, Event_Location_Lat,
            Event_Start_Date,Event_End_Date, Event_Start_Time,Event_End_Time, Event_Link,Event_Image;

    public InserEventBody(String event_User_Id, String event_Name, String event_Details,
                          String event_Location_Lng, String event_Location_Lat, String event_Start_Date, String event_End_Date,
                          String event_Start_Time, String event_End_Time, String event_Link,String event_Image) {
        Event_User_Id = event_User_Id;
        Event_Name = event_Name;
        Event_Details = event_Details;
        Event_Location_Lng = event_Location_Lng;
        Event_Location_Lat = event_Location_Lat;
        Event_Start_Date = event_Start_Date;
        Event_End_Date = event_End_Date;
        Event_Start_Time = event_Start_Time;
        Event_End_Time = event_End_Time;
        Event_Link = event_Link;
        Event_Image=event_Image;
    }
}


//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public static class UpdateUserBody {

        String User_Id , User_Image , User_Phone,User_Password;

    public UpdateUserBody(String user_Id, String user_Image, String user_Phone, String user_Password) {
        User_Id = user_Id;
        User_Image = user_Image;
        User_Phone = user_Phone;
        User_Password = user_Password;
    }
}
//----------------------------------------------------------------------------------------------------------------
public static class LoginBody {

    String User_Email, User_Password;

    public LoginBody(String email, String password) {
        User_Email = email;
        User_Password = password;
}

//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public static class InsertLocationID{
        int ID ;

        public InsertLocationID(int ID) {
            this.ID = ID;
        }
    }
}
}
