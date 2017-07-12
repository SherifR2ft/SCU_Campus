package com.example.boghdady.campusapp.StudentScreen;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.example.boghdady.campusapp.Map.SearchMapsActivity;
import com.example.boghdady.campusapp.R;
import com.example.boghdady.campusapp.Registeration.StudentSignupActivity;
import com.example.boghdady.campusapp.Retrofit.Interfaces;
import com.example.boghdady.campusapp.Retrofit.Models;
import com.example.boghdady.campusapp.Retrofit.Responses;
import com.example.boghdady.campusapp.Retrofit.SentBody;
import com.example.boghdady.campusapp.helper.AbstractRunTimePermission;
import com.example.boghdady.campusapp.helper.Constants;
import com.example.boghdady.campusapp.helper.CustomButton;
import com.example.boghdady.campusapp.helper.CustomEditText;
import com.example.boghdady.campusapp.helper.FilePath;
import com.example.boghdady.campusapp.helper.SharedPref;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StudentCreateEvent extends AbstractRunTimePermission {

    FrameLayout backBtn;
    ProgressDialog pDialog;
    String Event_User_Id, Event_Name, Event_Details, Event_Location_Lng, Event_Location_Lat,
            Event_Start_Date, Event_End_Date, Event_Start_Time, Event_End_Time, Event_Link;

    private LatLng mLatlng;
    private Map<String, LatLng> places;
    ArrayAdapter FacultyNamesAdapter;
    Spinner SpnfacultyNames;
    String[] FacultysList;
    CircularImageView profileImage;
    String facultyNames;
    String picturePath;
    TextView plusTxt;
    String ba1 = "";
    SharedPref sharedPref;
    int ACCESS_STORAGE_PERMISSIONS_REQUEST = 1;
    FrameLayout back;
    CustomEditText ed_EventName, ed_EventLink, event_timeTo, event_timeFrom,
            ed_EventDetail, event_date_From, event_date_To;
    String AM_PM = "";
    Calendar fromCalendar, toCalendar;
    CustomButton btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        init();
        GetLocationPlaceDetailsRetrofit();
        // InsertEvent("Eevnt one","فان داى هالا بالا", "214" , "244424" , "2/2/2939", "3432 AM", "http://index-soft.com/Campus/Insert_Event.php");


        back = (FrameLayout) findViewById(R.id.editProfile_backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //------------------------------------------------- Get Time From Calender-----------------------------------------------------//

        event_timeFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(StudentCreateEvent.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        if (selectedHour < 12) {
                            AM_PM = "AM";
                        } else if (selectedHour >= 12) {
                            AM_PM = "PM";
                            selectedHour = selectedHour - 12;
                        }
                        event_timeFrom.setText(selectedHour + ":" + selectedMinute + " " + AM_PM);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle(getString(R.string.select_time));
                mTimePicker.show();
            }
        });

        event_timeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(StudentCreateEvent.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        if (selectedHour < 12) {
                            AM_PM = "AM";
                        } else if (selectedHour >= 12) {
                            AM_PM = "PM";
                            selectedHour = selectedHour - 12;
                        }
                        event_timeTo.setText(selectedHour + ":" + selectedMinute + " " + AM_PM);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle(getString(R.string.select_time));
                mTimePicker.show();
            }
        });


//------------------------------------------------- Get Date From Calender-----------------------------------------------------//
        fromCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                fromCalendar.set(Calendar.YEAR, year);
                fromCalendar.set(Calendar.MONTH, monthOfYear);
                fromCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                updateLabelFrom();
            }
        };

        event_date_From.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(StudentCreateEvent.this, date, fromCalendar
                        .get(Calendar.YEAR), fromCalendar.get(Calendar.MONTH),
                        fromCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        //----------------------------------------------------------------------------------------------//

        toCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                toCalendar.set(Calendar.YEAR, year);
                toCalendar.set(Calendar.MONTH, monthOfYear);
                toCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelto();
            }

        };
        event_date_To.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(StudentCreateEvent.this, date1, toCalendar
                        .get(Calendar.YEAR), toCalendar.get(Calendar.MONTH),
                        toCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InsertEvent();
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


//--------------------------------------------------------------------------------------------------------------------------------

    private void updateLabelFrom() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);


        event_date_From.setText(sdf.format(fromCalendar.getTime()));

    }

    private void updateLabelto() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        event_date_To.setText(sdf.format(toCalendar.getTime()));
    }


//---------------------------------------------------------------------------------------------------------------------------------------------------------------

    void clearData() {
         ed_EventName.setText("");
         ed_EventDetail.setText("");
         event_date_From.setText("");
         event_date_To.setText("");
         event_timeFrom.setText("");
         event_timeTo.setText("");
         ed_EventLink.setText("");
    }

//---------------------------------------------------------------------------------------------------------------------------------------------------------------
    private void init() {
        SpnfacultyNames = (Spinner) findViewById(R.id.spinner_location);
        profileImage = (CircularImageView) findViewById(R.id.event_upload_photo);
        plusTxt = (TextView) findViewById(R.id.signup_plus);
        sharedPref = SharedPref.getInstance(getApplicationContext());

        ed_EventName = (CustomEditText) findViewById(R.id.ed_EventName);
        ed_EventLink = (CustomEditText) findViewById(R.id.ed_EventLink);
        event_timeTo = (CustomEditText) findViewById(R.id.event_timeTo);
        event_timeFrom = (CustomEditText) findViewById(R.id.event_timeFrom);
        ed_EventDetail = (CustomEditText) findViewById(R.id.ed_EventDetail);

        event_date_From = (CustomEditText) findViewById(R.id.event_date_From);
        event_date_To = (CustomEditText) findViewById(R.id.event_date_To);
        btn_save = (CustomButton) findViewById(R.id.btn_save);
    }


//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private void InsertEvent() {


        Event_User_Id = sharedPref.getString("student_id");
        Event_Name = ed_EventName.getText().toString();
        Event_Details = ed_EventDetail.getText().toString();
        Event_Start_Date = event_date_From.getText().toString();
        Event_End_Date = event_date_To.getText().toString();
        Event_Start_Time = event_timeFrom.getText().toString();
        Event_End_Time = event_timeTo.getText().toString();
        Event_Link = ed_EventLink.getText().toString();
        Event_Location_Lng = mLatlng.longitude + "";
        Event_Location_Lat = mLatlng.latitude + "";
        boolean validated = true;

        if (Event_Name.equalsIgnoreCase("")) {
            validated = false;
            Toast.makeText(StudentCreateEvent.this, R.string.please_enter_right_event_name, Toast.LENGTH_SHORT).show();
        }
        if (Event_Details.equalsIgnoreCase("")) {
            validated = false;
            Toast.makeText(StudentCreateEvent.this, R.string.please_enter_right_event_details, Toast.LENGTH_SHORT).show();
        }

        if (Event_Start_Date.equalsIgnoreCase("")) {
            validated = false;
            Toast.makeText(StudentCreateEvent.this, R.string.please_enter_right_event_date, Toast.LENGTH_SHORT).show();
        }
        if (Event_End_Date.equalsIgnoreCase("")) {
            validated = false;
            Toast.makeText(StudentCreateEvent.this, R.string.please_enter_right_event_date, Toast.LENGTH_SHORT).show();
        }
        if (Event_Start_Time.equalsIgnoreCase("")) {
            validated = false;
            Toast.makeText(StudentCreateEvent.this, R.string.please_enter_right_event_time, Toast.LENGTH_SHORT).show();
        }
        if (Event_End_Time.equalsIgnoreCase("")) {
            validated = false;
            Toast.makeText(StudentCreateEvent.this, R.string.please_enter_right_event_time, Toast.LENGTH_SHORT).show();
        }
        if (validated) {

            pDialog = new ProgressDialog(StudentCreateEvent.this);
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
            Interfaces.User_InserEvent inserEvent = retrofit.create(Interfaces.User_InserEvent.class);
            Call<Responses.DefaultResponse> call = inserEvent.insertEvent(new SentBody.InserEventBody(Event_User_Id, Event_Name, Event_Details,
                    Event_Location_Lng, Event_Location_Lat, Event_Start_Date, Event_End_Date, Event_Start_Time,
                    Event_End_Time, Event_Link,ba1));

            call.enqueue(new Callback<Responses.DefaultResponse>() {
                @Override
                public void onResponse(Call<Responses.DefaultResponse> call, Response<Responses.DefaultResponse> response) {


                    if (response.body().getSuccess() == 1) {
                        Toast.makeText(StudentCreateEvent.this, "Event Added Successfully", Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                         clearData();
                    } else if (response.body().getSuccess() == 0) {
                        Toast.makeText(getApplication(), getString(R.string.wrong), Toast.LENGTH_LONG).show();
                        pDialog.dismiss();
                    }

                }


                @Override
                public void onFailure(Call<Responses.DefaultResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), getString(R.string.check_your_internet), Toast.LENGTH_LONG).show();
                    pDialog.dismiss();
                }
            });

        }

    }


    //---------------------------------------------------------------------------------------------------------------------------------------------------
    public void getImage() {
        final Dialog dialog = new Dialog(StudentCreateEvent.this);
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
            picturePath = FilePath.getPath(StudentCreateEvent.this, selectedImage.toString());

            File sd = Environment.getExternalStorageDirectory();
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap sent = BitmapFactory.decodeFile(picturePath, bmOptions);
            sent = Bitmap.createScaledBitmap(sent, 200, 200, true);


//            Bitmap bm = BitmapFactory.decodeFile(picturePath);
//            Bitmap sent = Bitmap.createScaledBitmap(bm, 500, 500, true);

            plusTxt.setVisibility(View.INVISIBLE);
            // Set the Image in ImageView after decoding the String
            profileImage.setImageBitmap(sent);
            uploading(sent);
        } else if (resultCode == RESULT_OK && data != null && requestCode == 2) {
            Bundle bu = data.getExtras();
            Bitmap bit = (Bitmap) bu.get("data");
            Bitmap sent = Bitmap.createScaledBitmap(bit, 500, 500, true);
            plusTxt.setVisibility(View.INVISIBLE);


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

    void GetLocationPlaceDetailsRetrofit() {

        pDialog = new ProgressDialog(StudentCreateEvent.this);
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

        Interfaces.GetPlacesLocation getPlaceLocation = retrofit.create(Interfaces.GetPlacesLocation.class);
        Call<Responses.LocationPlacesResponse> call = getPlaceLocation.getPlacesLoaction();
        call.enqueue(new Callback<Responses.LocationPlacesResponse>() {
            @Override
            public void onResponse(Call<Responses.LocationPlacesResponse> call, Response<Responses.LocationPlacesResponse> response) {

                try {
                    if (response.body().getSuccess() == 1) {


                        List<Models.LocationModel> placeLocation = response.body().getLocation();

                        places = new HashMap<String, LatLng>();
                        FacultysList = new String[placeLocation.size()];

                        for (int i = 0; i < placeLocation.size(); i++) {
                            places.put(placeLocation.get(i).getFaculty_Name(), new LatLng(placeLocation.get(i).getLatitude(), placeLocation.get(i).getLongitude()));
                            FacultysList[i] = placeLocation.get(i).getFaculty_Name();
                        }
                        FillFucaltySpinner();

                    }
                    if (response.body().getSuccess() == 0) {
                        Toast.makeText(StudentCreateEvent.this, R.string.wrong, Toast.LENGTH_LONG).show();
                        pDialog.dismiss();
                    }
                    pDialog.dismiss();
                } catch (Exception e) {
                    e.getMessage();
                    pDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Responses.LocationPlacesResponse> call, Throwable t) {
                try {
                    Toast.makeText(StudentCreateEvent.this, R.string.check_your_internet, Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                } catch (Exception e) {
                    pDialog.dismiss();
                }
            }
        });
    }


    //----------------------------------------------------------------------------------------------------------------------------------------------------
    void FillFucaltySpinner() {

        FacultyNamesAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, FacultysList);
        FacultyNamesAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        SpnfacultyNames.setAdapter(FacultyNamesAdapter);

        SpnfacultyNames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //  Department_Spinner = Department[position];
                mLatlng = getIdFromPlaces(((TextView) view).getText());


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    //-------------------------------------------------------------------------------------
    // method to get Lat and lon from using faculty name
    private LatLng getIdFromPlaces(CharSequence text) {

        return places.get(text.toString());
    }


}
