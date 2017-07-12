package com.example.boghdady.campusapp.DoctorScreen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.boghdady.campusapp.R;
import com.example.boghdady.campusapp.StudentScreen.StudentProfile;
import com.example.boghdady.campusapp.helper.Constants;
import com.example.boghdady.campusapp.helper.CustomButton;
import com.example.boghdady.campusapp.helper.CustomEditText;
import com.example.boghdady.campusapp.helper.CustomTextView;
import com.example.boghdady.campusapp.helper.SharedPref;
import com.mikhaellopez.circularimageview.CircularImageView;

public class DoctorProfile extends AppCompatActivity {

    CircularImageView doctorImage;
    CustomTextView edit_profile_photo;
    CustomEditText doctor_name,doctor_email,doctor_password,
            doctor_phone_number, doctor_faculty ;
    CustomButton edit_profile_saveBtn;
    SharedPref sharedPref = SharedPref.getInstance(this);
    FrameLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        init();
        SetDoctorDetails();

        back = (FrameLayout) findViewById(R.id.editProfile_backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


//-------------------------------------------------------------------------------------------------------------------------------
    private void init()
    {
        doctorImage =(CircularImageView)findViewById(R.id.profile_photo);
        edit_profile_photo = (CustomTextView)findViewById(R.id.edit_profile_photo);
        doctor_name = (CustomEditText)findViewById(R.id.doctor_name);
        doctor_email = (CustomEditText)findViewById(R.id.doctor_email);
        doctor_password = (CustomEditText)findViewById(R.id.doctor_password);
        doctor_phone_number = (CustomEditText)findViewById(R.id.doctor_phone_number);
        doctor_faculty = (CustomEditText)findViewById(R.id.doctor_faculty);

        edit_profile_saveBtn =(CustomButton)findViewById(R.id.edit_profile_saveBtn);
    }

//------------------------------------------------------------------------------------------------------------------

    // method to get student image and student name form shared preferance

    void SetDoctorDetails ()
    {
        String userImg = sharedPref.getString("doctor_image");
        if (!userImg.equalsIgnoreCase("")) {

            Glide.with(DoctorProfile.this)
                    .load(Constants.IMAGES_URL + userImg)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .crossFade()
                    .dontAnimate()
                    .into(doctorImage);
            doctor_name.setText(sharedPref.getString("doctor_name"));
            doctor_email.setText(sharedPref.getString("doctor_email"));
            doctor_password.setText(sharedPref.getString("doctor_password"));
            doctor_phone_number.setText(sharedPref.getString("doctor_phone"));
            doctor_faculty.setText(sharedPref.getString("doctor_faculty"));


        } else {
            doctorImage.setImageDrawable(getResources().getDrawable(R.drawable.profile_place_holder));
        }
    }

//-----------------------------------------------------------------------------------------------------------------------------
}
