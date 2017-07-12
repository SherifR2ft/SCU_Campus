package com.example.boghdady.campusapp.StudentScreen;

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
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.boghdady.campusapp.NavigationDrawer.DoctorNavigationDrawer;
import com.example.boghdady.campusapp.NavigationDrawer.StudentNavigationDrawer;
import com.example.boghdady.campusapp.R;
import com.example.boghdady.campusapp.Retrofit.Interfaces;
import com.example.boghdady.campusapp.Retrofit.Models;
import com.example.boghdady.campusapp.Retrofit.Responses;
import com.example.boghdady.campusapp.Retrofit.SentBody;
import com.example.boghdady.campusapp.helper.AbstractRunTimePermission;
import com.example.boghdady.campusapp.helper.Constants;
import com.example.boghdady.campusapp.helper.CustomButton;
import com.example.boghdady.campusapp.helper.CustomEditText;
import com.example.boghdady.campusapp.helper.CustomTextView;
import com.example.boghdady.campusapp.helper.FilePath;
import com.example.boghdady.campusapp.helper.SharedPref;
import com.example.boghdady.campusapp.login;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StudentProfile extends AbstractRunTimePermission {

    CircularImageView studentImage;
    CustomTextView edit_profile_photo;
    CustomEditText student_name,student_email,student_password,
            student_phone_number, student_faculty , student_year ,
            student_department , student_section;
    CustomButton edit_profile_saveBtn;
    String  User_Id,userImg , User_Phone , User_Password;
    ProgressDialog pDialog;
    CircularImageView profileImage;
    String picturePath;
    String ba1 = "";
    SharedPref sharedPref;
    int ACCESS_STORAGE_PERMISSIONS_REQUEST = 1;
    FrameLayout back;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile2);

        init();
        SetStudentDetails();

        back = (FrameLayout) findViewById(R.id.editProfile_backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        // get user image
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    requestAppPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_DOCUMENTS}, R.string.perrmission, ACCESS_STORAGE_PERMISSIONS_REQUEST);
                } else {
                    getImage();
                }
            }
        });

    }

    @Override
    public void onPermissionGranted(int requestCode) {
        getImage();
    }


//-------------------------------------------------------------------------------------------------------------------------------
    private void init()
    {
        studentImage =(CircularImageView)findViewById(R.id.profile_photo);
        edit_profile_photo = (CustomTextView)findViewById(R.id.edit_profile_photo);
        student_name = (CustomEditText)findViewById(R.id.student_name);
        student_email = (CustomEditText)findViewById(R.id.student_email);
        student_password = (CustomEditText)findViewById(R.id.student_password);
        student_phone_number = (CustomEditText)findViewById(R.id.student_phone_number);
        student_faculty = (CustomEditText)findViewById(R.id.student_faculty);
        student_year = (CustomEditText)findViewById(R.id.student_year);
        student_department = (CustomEditText)findViewById(R.id.student_department);
        student_section = (CustomEditText)findViewById(R.id.student_section);
        edit_profile_saveBtn =(CustomButton)findViewById(R.id.edit_profile_saveBtn);

    }
//--------------------------------------------------------------------------------------------------------------------------------

// method to get student image and student name form shared preferance

    void SetStudentDetails ()
    {
         userImg = sharedPref.getString("student_image");
        if (!userImg.equalsIgnoreCase("")) {

            Glide.with(StudentProfile.this)
                    .load(Constants.IMAGES_URL + userImg)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .crossFade()
                    .dontAnimate()
                    .into(studentImage);
            student_name.setText(sharedPref.getString("student_name"));
            student_email.setText(sharedPref.getString("student_email"));
            student_password.setText(sharedPref.getString("student_password"));
            student_phone_number.setText(sharedPref.getString("student_phone"));
            student_faculty.setText(sharedPref.getString("student_faculty"));
            student_year.setText(sharedPref.getString("student_study_year"));
            student_department.setText(sharedPref.getString("student_department"));
            student_section.setText(sharedPref.getString("student_section"));

        } else {
            studentImage.setImageDrawable(getResources().getDrawable(R.drawable.profile_place_holder));
        }
    }

//----------------------------------------------------------------------------------------------------------------------------


//    private void UpdateStudentDetails()
//    {
//        edit_profile_saveBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                User_Id = sharedPref.getString("student_id");
//                User_Phone = student_phone_number.getText().toString();
//                User_Password = student_password.getText().toString();
//
//                    pDialog = new ProgressDialog(StudentProfile.this);
//                    pDialog.setMessage(getString(R.string.please_wait));
//                    pDialog.setCancelable(false);
//                    pDialog.show();
//
//                    Gson gson = new GsonBuilder()
//                            .setLenient()
//                            .create();
//                    Retrofit retrofit = new Retrofit.Builder()
//                            .baseUrl(Constants.BASE_URL)
//                            .addConverterFactory(GsonConverterFactory.create(gson))
//                            .build();
//                    Interfaces.Update_User Update_User_Interface=retrofit.create(Interfaces.Update_User.class);
//                    Call<Responses.DefaultResponse> call=Update_User_Interface.update_user(new SentBody.UpdateUserBody(User_Id,,User_Phone,User_Password));
//                    call.enqueue(new Callback<Responses.DefaultResponse>() {
//                        @Override
//                        public void onResponse(Call<Responses.DefaultResponse> call, Response<Responses.DefaultResponse> response) {
//                            try {
//                                if (response.body().getSuccess()== 1){
//
//                                }
//                                if (response.body().getSuccess()==0){
//                                    Toast.makeText(StudentProfile.this, " "+response.body().getMessage(), Toast.LENGTH_LONG).show();
//                                    pDialog.dismiss();
//                                }
//                                pDialog.dismiss();
//                            }catch (Exception e){
//                                e.getMessage();
//                                pDialog.dismiss();
//                            }
//                        }
//                        @Override
//                        public void onFailure(Call<Responses.DefaultResponse> call, Throwable t) {
//                            try {
//                                Toast.makeText(StudentProfile.this, R.string.check_your_internet, Toast.LENGTH_SHORT).show();
//                                pDialog.dismiss();
//                            }catch (Exception e){
//                                pDialog.dismiss();
//                            }
//                        }
//                    });
//
//            }
//        });
//    }


    //---------------------------------------------------------------------------------------------------------------------------------------------------
    public void getImage() {
        final Dialog dialog = new Dialog(StudentProfile.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.setContentView(R.layout.dialog);
        LinearLayout cameraLayout = (LinearLayout) dialog.findViewById(R.id.linearLayout);
        LinearLayout galleryLaout = (LinearLayout) dialog.findViewById(R.id.gal_layout);
        TextView cancel = (TextView) dialog.findViewById(R.id.dialog_cancel_txt);
        cameraLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 2);
                dialog.dismiss();
            }
        });
        galleryLaout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), 1);
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(true);
        dialog.show();
    }
//---------------------------------------------------------------------------------------------------------------------------------------------------

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && data != null && requestCode == 1) {
            Uri selectedImage = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            picturePath = FilePath.getPath(StudentProfile.this, selectedImage.toString());

            File sd = Environment.getExternalStorageDirectory();
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap sent = BitmapFactory.decodeFile(picturePath, bmOptions);
            sent = Bitmap.createScaledBitmap(sent, 200, 200, true);


//            Bitmap bm = BitmapFactory.decodeFile(picturePath);
//            Bitmap sent = Bitmap.createScaledBitmap(bm, 500, 500, true);

            edit_profile_photo.setVisibility(View.INVISIBLE);
            // Set the Image in ImageView after decoding the String
            profileImage.setImageBitmap(sent);
            uploading(sent);
        } else if (resultCode == RESULT_OK && data != null && requestCode == 2) {
            Bundle bu = data.getExtras();
            Bitmap bit = (Bitmap) bu.get("data");
            Bitmap sent = Bitmap.createScaledBitmap(bit, 500, 500, true);
            edit_profile_photo.setVisibility(View.INVISIBLE);


            profileImage.setImageBitmap(sent);
            uploading(sent);
        }
    }
//--------------------------------------------------------------------------------------------------------------------------------------------------

    public void uploading(Bitmap bitmap) {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ba1 = Base64.encodeToString(ba, 0);
        sharedPref.putString("profile_img_base64", ba1);
    }

//--------------------------------------------------------------------------------------------------------------------------------------------------


}
