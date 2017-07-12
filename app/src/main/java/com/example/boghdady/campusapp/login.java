package com.example.boghdady.campusapp;

import android.*;
import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.boghdady.campusapp.NavigationDrawer.DoctorNavigationDrawer;
import com.example.boghdady.campusapp.NavigationDrawer.StudentNavigationDrawer;
import com.example.boghdady.campusapp.Registeration.StudentSignupActivity;
import com.example.boghdady.campusapp.Retrofit.Interfaces;
import com.example.boghdady.campusapp.Retrofit.Models;
import com.example.boghdady.campusapp.Retrofit.Responses;
import com.example.boghdady.campusapp.Retrofit.SentBody;
import com.example.boghdady.campusapp.helper.Constants;
import com.example.boghdady.campusapp.helper.CustomButton;
import com.example.boghdady.campusapp.helper.CustomEditText;
import com.example.boghdady.campusapp.helper.FilePath;
import com.example.boghdady.campusapp.helper.SharedPref;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class login extends AppCompatActivity
{

    CustomEditText ed_email , ed_password;
    String Email , Password;
    CustomButton btn_login;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        User_Login();

    }


    private void init() {
        ed_email=(CustomEditText)findViewById(R.id.ed_email);
        ed_password=(CustomEditText)findViewById(R.id.ed_password);
        btn_login=(CustomButton)findViewById(R.id.btn_login);
    }

//---------------------------------------------------------------------------------------------------------------------
void User_Login()
{
    btn_login.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Email = ed_email.getText().toString();
            Password = ed_password.getText().toString();

            boolean validated = true;

            if (Email.equalsIgnoreCase("")) {
                validated = false;
                Toast.makeText(login.this, R.string.please_enter_email, Toast.LENGTH_SHORT).show();
            }
            if (Password.equalsIgnoreCase("")) {
                validated = false;
                Toast.makeText(login.this, R.string.please_enter_password, Toast.LENGTH_SHORT).show();
            }

            if (validated) {

                pDialog = new ProgressDialog(login.this);
                pDialog.setMessage(getString(R.string.please_wait));
                pDialog.setCancelable(false);
                pDialog.show();

                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Constants.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
                Interfaces.User_Login User_Login_Interface=retrofit.create(Interfaces.User_Login.class);
                Call<Responses.SignupResponse> call=User_Login_Interface.userLogin(new SentBody.LoginBody(Email,Password));
                call.enqueue(new Callback<Responses.SignupResponse>() {
                    @Override
                    public void onResponse(Call<Responses.SignupResponse> call, Response<Responses.SignupResponse> response) {
                        try {
                            if (response.body().getSuccess()== 1){
                                SharedPref sharedPref=SharedPref.getInstance(getApplicationContext());
                                Models.UserModel userModel = response.body().getUser();

                                if(userModel.getChecking()==0)
                                {
                                    sharedPref.putString("student_id",userModel.getUser_ID());
                                    sharedPref.putString("student_name",userModel.getUser_Name());
                                    sharedPref.putString("student_image",userModel.getUser_Image());
                                    sharedPref.putString("student_email",userModel.getUser_Email());
                                    sharedPref.putString("student_phone",userModel.getUser_Phone());
                                    sharedPref.putString("student_faculty",userModel.getUser_Faculty());
                                    sharedPref.putString("student_password",userModel.getUser_Password());
                                    sharedPref.putString("student_section",userModel.getUser_Section());
                                    sharedPref.putString("student_department",userModel.getDepartment());
                                    sharedPref.putString("student_study_year",userModel.getStudy_Year());
                                    sharedPref.putInt("checking",userModel.getChecking());

                                    Intent intent=new Intent(login.this,StudentNavigationDrawer.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {

                                    sharedPref.putString("doctor_id",userModel.getUser_ID());
                                    sharedPref.putString("doctor_name",userModel.getUser_Name());
                                    sharedPref.putString("doctor_image",userModel.getUser_Image());
                                    sharedPref.putString("doctor_email",userModel.getUser_Email());
                                    sharedPref.putString("doctor_phone",userModel.getUser_Phone());
                                    sharedPref.putString("doctor_faculty",userModel.getUser_Faculty());
                                    sharedPref.putString("doctor_password",userModel.getUser_Password());
                                    sharedPref.putInt("checking",userModel.getChecking());

                                    Intent intent=new Intent(login.this,DoctorNavigationDrawer.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }

                            }
                            if (response.body().getSuccess()==0){
                                Toast.makeText(login.this, " "+response.body().getMessage(), Toast.LENGTH_LONG).show();
                                pDialog.dismiss();
                            }
                            pDialog.dismiss();
                        }catch (Exception e){
                            e.getMessage();
                            pDialog.dismiss();
                        }
                    }
                    @Override
                    public void onFailure(Call<Responses.SignupResponse> call, Throwable t) {
                        try {
                            Toast.makeText(login.this, R.string.check_your_internet, Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                        }catch (Exception e){
                            pDialog.dismiss();
                        }
                    }
                });
            }
        }
    });


}

//----------------------------------------------------------------------------------------------------------------------------------------------------





}