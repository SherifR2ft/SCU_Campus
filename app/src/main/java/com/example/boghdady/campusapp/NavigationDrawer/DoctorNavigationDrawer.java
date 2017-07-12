package com.example.boghdady.campusapp.NavigationDrawer;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.boghdady.campusapp.DoctorScreen.DoctorCreateEvent;
import com.example.boghdady.campusapp.DoctorScreen.DoctorProfile;
import com.example.boghdady.campusapp.Notification_list;
import com.example.boghdady.campusapp.R;
import com.example.boghdady.campusapp.StudentScreen.StudentCreateEvent;
import com.example.boghdady.campusapp.helper.Constants;
import com.example.boghdady.campusapp.helper.CustomTextView;
import com.example.boghdady.campusapp.helper.SharedPref;
import com.mikhaellopez.circularimageview.CircularImageView;

public class DoctorNavigationDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    CircularImageView profileImage;
    CustomTextView userName;
    SharedPref sharedPref = SharedPref.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_navigation_drawer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("SCU Campus ");
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.doctor_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.doctor_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);

        profileImage = (CircularImageView)view.findViewById(R.id.user_imageView);
        userName = (CustomTextView)view.findViewById(R.id.txtProfileName);

        SetDoctorImageAndName();

    }

//-----------------------------------------------------------------------------------------------------------------------
    // method to get student image and student name form shared preferance

    void SetDoctorImageAndName ()
    {
        String userImg = sharedPref.getString("doctor_image");
        if (!userImg.equalsIgnoreCase("")) {

            Glide.with(DoctorNavigationDrawer.this)
                    .load(Constants.IMAGES_URL + userImg)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .crossFade()
                    .dontAnimate()
                    .into(profileImage);
            userName.setText(sharedPref.getString("doctor_name"));
        } else {
            profileImage.setImageDrawable(getResources().getDrawable(R.drawable.profile_place_holder));
        }
    }
//---------------------------------------------------------------------------------------------------------------------

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.doctor_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//-----------------------------------------------------------------------------------------------------------------------
@Override
public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.navigation_drawer, menu);
    return true;
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.notification_settings) {
            Intent intent=new Intent(this,Notification_list.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
//--------------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();


        if(id == R.id.doctor_study_table) {

        }
        else if(id == R.id.doctor_creat_events) {
            Intent i = new Intent(DoctorNavigationDrawer.this , DoctorCreateEvent.class);
            startActivity(i);

        }
        else if(id == R.id.doctor_events) {

        }
        else if(id == R.id.doctor_settings) {
            Intent i = new Intent(DoctorNavigationDrawer.this , DoctorProfile.class);
            startActivity(i);
        }
        else if(id == R.id.doctor_share) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.doctor_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
