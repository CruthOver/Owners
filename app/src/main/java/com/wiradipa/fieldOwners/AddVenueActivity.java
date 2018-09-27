package com.wiradipa.fieldOwners;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.wiradipa.fieldOwners.Adapter.PlaceAutocompleteAdapter;
import com.wiradipa.fieldOwners.ApiHelper.AppSession;
import com.wiradipa.fieldOwners.ApiHelper.BaseApiService;
import com.wiradipa.fieldOwners.ApiHelper.UtilsApi;
import com.wiradipa.fieldOwners.Model.AreasValue;
import com.wiradipa.fieldOwners.Model.FasilitasValue;
import com.wiradipa.fieldOwners.Model.FieldData;
import com.wiradipa.fieldOwners.Model.PlaceInfo;
import com.wiradipa.fieldOwners.Model.ResponseData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

public class AddVenueActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int REQUEST_SELECT_IMAGE = 1;
    private static final int REQUEST_PLACE_PICKER = 0;
//    private static final int REQUEST_GET_IMAGE = 2;
//    private static final int GET_IMAGE_REQUEST = 3;
    private static final int PERMISSION_REQUEST_STORAGE = 77;
    String jsonFacility, jsonAreas, fileImagePath, result, venueId;
    Double mLatitude, mLongitude;

    ImageView mImageView;

    EditText mOpsiHourEditText, mOpsiFaciliesEditText
            , nameVenueEditText, descVenueEditText, titleFieldEditText, titleFieldEditText2;
    ScrollView scrollView;
    Spinner fromHourEditText, untilHourEditText;
    AutoCompleteTextView mAutoCompleteTextView;
    CheckBox checkBoxFacilities, checkBoxArea;
    Button mSubmitBtn, mCancelBtn, mPlacePicker;
    LinearLayout mOpsiHour, mOpsiFacilities;
    LinearLayout checkBoxLinearLayout, cbAreaLinearLayout;
    TextView mResultImage, mResultOtherImage, mResultOtherImage2;
    ImageView mAddImageView, mAddOtherImage, mAddOtherImage2;
    TimePickerDialog timePickerDialog;

    List<FasilitasValue> facilitiesArray;
    List<AreasValue> areasArray;
    List<String> listCheckbox;

    Context mContext;
    BaseApiService mApiService;
    AppSession mAppSession;

    private Uri imageFile;
    private Bitmap bitmap;
    private Bundle bundle;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mPlaceAutoComplete;
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceInfo mPlace;

    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    private int mStartHour, mEndHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_venue);

        mContext = this;
        mApiService = UtilsApi.getApiService();
        mAppSession = new AppSession(mContext);

        facilitiesArray = new ArrayList<>();
        areasArray = new ArrayList<>();
        listCheckbox = new ArrayList<String>();

        initComponents();
        setSpinnerFromHour();
        setSpinnerEndHour();
        setupcheckBoxesFacilities();
        setupCheckboxesAreas();

        bundle = getIntent().getExtras();
        if (bundle != null){
            setTitle("Edit Venue");
            venueId = bundle.getString("venueId");
            editVenue();
        } else {
            setTitle("Tambah Venue");
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getLocationPermission();
        }
    }

    private boolean isNameVenueValid(String nameVenue){
        return nameVenue.equals("");
    }

    private boolean isDecsVenueValid(String descVenue){
        return descVenue.equals("");
    }

    private boolean isAddressVenueValid(String addressVenue){
        return addressVenue.equals("");
    }

    private void initComponents() {
        scrollView = findViewById(R.id.scroll); //parent scrollview in xml, give your scrollview id value

        fromHourEditText = (Spinner) findViewById(R.id.et_from_hour);
        untilHourEditText = (Spinner) findViewById(R.id.et_until_hour);
        mOpsiHourEditText = (EditText) findViewById(R.id.other_opsi_hour);
        mOpsiFaciliesEditText = (EditText) findViewById(R.id.other_opsi_facilites_venue);
        nameVenueEditText = (EditText) findViewById(R.id.venue_name);
        descVenueEditText = (EditText) findViewById(R.id.venue_description);
//        titleFieldEditText = (EditText) findViewById(R.id.et_title_field);
//        titleFieldEditText2 = (EditText) findViewById(R.id.et_title_field2);

        mImageView = (ImageView) findViewById(R.id.view_image);

        mResultImage = (TextView) findViewById(R.id.add_photo_venue);
//        mResultOtherImage = (TextView) findViewById(R.id.add_other_photo_venue);
//        mResultOtherImage2 = (TextView) findViewById(R.id.add_other_photo2_venue);

        mAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoComplete);

//        mOpsiHour = (LinearLayout) findViewById(R.id.add_opsi_hour);
//        mOpsiFacilities = (LinearLayout) findViewById(R.id.add_opsi_facilities_venue);
        checkBoxLinearLayout = (LinearLayout) findViewById(R.id.checkbox_venue);
        cbAreaLinearLayout = (LinearLayout) findViewById(R.id.checkbox_area);

        mAddImageView  = (ImageView) findViewById(R.id.img_picker);
//        mAddOtherImage = (ImageView) findViewById(R.id.img_other_photo);
//        mAddOtherImage2 = (ImageView) findViewById(R.id.img_other_photo2);

        mSubmitBtn = (Button) findViewById(R.id.add_new_venue);
        mCancelBtn = (Button) findViewById(R.id.btnCancel);
        mPlacePicker = (Button) findViewById(R.id.placePicker);

        mPlacePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    //__________start placepicker activity for result
                    startActivityForResult(builder.build(AddVenueActivity.this), REQUEST_PLACE_PICKER);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (venueId == null){
                    addVenue();
                } else {
                    updateVenue();
                }

            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mAddImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intentGallery = new Intent(Intent.ACTION_PICK);
                    intentGallery.setType("image/*");
                    startActivityForResult(Intent.createChooser(intentGallery, "SELECT IMAGE"), REQUEST_SELECT_IMAGE);
                } else {
                    ActivityCompat.requestPermissions(AddVenueActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_STORAGE);
                }
            }
        });

//        mAddOtherImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intentGallery = new Intent(Intent.ACTION_PICK);
//                intentGallery.setType("image/*");
//                if (intentGallery.resolveActivity(getPackageManager()) != null){
//                    startActivityForResult(Intent.createChooser(intentGallery, "SELECT IMAGE"),REQUEST_GET_IMAGE);
//                }
//            }
//        });
//
//        mAddOtherImage2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                intentGallery.setType("image/*");
//                if (intentGallery.resolveActivity(getPackageManager()) != null){
//                    startActivityForResult(Intent.createChooser(intentGallery, "SELECT IMAGE"), GET_IMAGE_REQUEST);
//                }
//            }
//        });
    }

    private void setSpinnerFromHour() {
        ArrayAdapter fromDaySpinner = ArrayAdapter.createFromResource(mContext,
                R.array.startHour, R.layout.spinner_jadwal);

        fromDaySpinner.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        fromHourEditText.setAdapter(fromDaySpinner);
        fromHourEditText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getItemAtPosition(i);
                if (!TextUtils.isEmpty(selection)){
                    if (selection.equals("0")){
                        mStartHour = 0;
                        Log.d("START HOUR ", mStartHour + "");
                    } else if (selection.equals("1")){
                        mStartHour = 1; //monday
                    } else if (selection.equals("2")){
                        mStartHour = 2;
                    } else if (selection.equals("3")){
                        mStartHour = 3;
                    } else if (selection.equals("4")){
                        mStartHour = 4;
                    } else if (selection.equals("5")){
                        mStartHour = 5;
                    } else if (selection.equals("6")){
                        mStartHour = 6;
                    } else if (selection.equals("7")){
                        mStartHour = 7;
                    } else if (selection.equals("8")){
                        mStartHour = 8;
                    } else if (selection.equals("9")){
                        mStartHour = 9;
                    } else if (selection.equals("10")){
                        mStartHour = 10;
                    } else if (selection.equals("11")){
                        mStartHour = 11;
                    } else if (selection.equals("12")){
                        mStartHour = 12;
                    } else if (selection.equals("13")){
                        mStartHour = 13;
                    } else if (selection.equals("14")){
                        mStartHour = 14;
                    } else if (selection.equals("15")){
                        mStartHour = 15;
                    } else if (selection.equals("16")){
                        mStartHour = 16;
                    } else if (selection.equals("17")){
                        mStartHour = 17;
                    } else if (selection.equals("18")){
                        mStartHour = 18;
                    } else if (selection.equals("19")){
                        mStartHour = 19;
                    } else if (selection.equals("20")){
                        mStartHour = 20;
                    } else if (selection.equals("21")){
                        mStartHour = 21;
                    } else if (selection.equals("22")){
                        mStartHour = 22;
                    } else if (selection.equals("23")){
                        mStartHour = 23;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(mContext, "Hari belum dipilih", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSpinnerEndHour(){
        ArrayAdapter fromDaySpinner = ArrayAdapter.createFromResource(mContext,
                R.array.endHour, R.layout.spinner_jadwal);

        fromDaySpinner.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        untilHourEditText.setAdapter(fromDaySpinner);
        untilHourEditText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getItemAtPosition(i);
                if (!TextUtils.isEmpty(selection)) {

                    if (selection.equals("1")) {
                        mEndHour = 1; //monday
                        Log.d("START HOUR ", mEndHour + "");
                    } else if (selection.equals("2")) {
                        mEndHour = 2;
                    } else if (selection.equals("3")) {
                        mEndHour = 3;
                    } else if (selection.equals("4")) {
                        mEndHour = 4;
                    } else if (selection.equals("5")) {
                        mEndHour = 5;
                    } else if (selection.equals("6")) {
                        mEndHour = 6;
                    } else if (selection.equals("7")) {
                        mEndHour = 7;
                    } else if (selection.equals("8")) {
                        mEndHour = 8;
                    } else if (selection.equals("9")) {
                        mEndHour = 9;
                    } else if (selection.equals("10")) {
                        mEndHour = 10;
                    } else if (selection.equals("11")) {
                        mEndHour = 11;
                    } else if (selection.equals("12")) {
                        mEndHour = 12;
                    } else if (selection.equals("13")) {
                        mEndHour = 13;
                    } else if (selection.equals("14")) {
                        mEndHour = 14;
                    } else if (selection.equals("15")) {
                        mEndHour = 15;
                    } else if (selection.equals("16")) {
                        mEndHour = 16;
                    } else if (selection.equals("17")) {
                        mEndHour = 17;
                    } else if (selection.equals("18")) {
                        mEndHour = 18;
                    } else if (selection.equals("19")) {
                        mEndHour = 19;
                    } else if (selection.equals("20")) {
                        mEndHour = 20;
                    } else if (selection.equals("21")) {
                        mEndHour = 21;
                    } else if (selection.equals("221")) {
                        mEndHour = 22;
                    } else if (selection.equals("23")) {
                        mEndHour = 23;
                    } else if (selection.equals("24")) {
                        mEndHour = 24;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(mContext, "Hari belum dipilih", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupcheckBoxesFacilities(){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Proses");
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.show();

        mApiService.listFacilities().enqueue(new Callback<ResponseData>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful()){
                    progressDialog.dismiss();
                    List<FieldData> listFacilities = response.body().getFieldData();
                    for (int i = 0; i<listFacilities.size(); i++){
                        listCheckbox.add(listFacilities.get(i).getNama());
                        checkBoxFacilities = new CheckBox(mContext);
                        checkBoxFacilities.setId(listFacilities.get(i).getId());
                        checkBoxFacilities.setText(listFacilities.get(i).getNama());
                        checkBoxFacilities.setTextColor(Color.WHITE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            checkBoxFacilities.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                        }
                        checkBoxFacilities.setOnClickListener(getOnClickListenerCheckBox(checkBoxFacilities));
//                        LinearLayout listCbVenue = new LinearLayout(mContext);
//                        listCbVenue.setOrientation(LinearLayout.VERTICAL);
//                        listCbVenue.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
//
//                        listCbVenue.addView(checkBoxFacilities);
//                        layoutCheckbox.addView(mFacilitiesCheckBox);
//                        if (layoutCheckbox.getParent() != null){
//                            ((ViewGroup) layoutCheckbox.getParent()).removeView(layoutCheckbox
//                            );
//                        }
                        checkBoxLinearLayout.addView(checkBoxFacilities);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    private void setupCheckboxesAreas(){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Proses");
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.show();

        mApiService.listAreas().enqueue(new Callback<ResponseData>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    List<FieldData> listAreas = response.body().getFieldData();
                    List<String> listCheckbox = new ArrayList<String>();
                    for (int i = 0; i < listAreas.size(); i++) {
                        listCheckbox.add(listAreas.get(i).getNama());
                        checkBoxArea = new CheckBox(mContext);
                        checkBoxArea.setId(listAreas.get(i).getId());
                        checkBoxArea.setText(listAreas.get(i).getNama());
                        checkBoxArea.setTextColor(Color.WHITE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            checkBoxArea.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                        }
                        checkBoxArea.setOnClickListener(getOnClickListenerCheckBoxArea(checkBoxArea));
                        cbAreaLinearLayout.addView(checkBoxArea);
                    }
                }
            }
            @Override
            public void onFailure (Call<ResponseData> call, Throwable t){

            }
        });
    }

    View.OnClickListener getOnClickListenerCheckBox(final CheckBox button){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                for (int i=0; i<=checkBoxFacilities.length(); i++){
//                    View nextChild = checkBoxLinearLayout.getChildAt(i);
//
//                    if (nextChild instanceof  CheckBox){
//                        CheckBox cb = (CheckBox) nextChild;
//                        if (cb.isChecked()){
//                            facilitiesArray.add(cb.getText().toString());
//                        } else {
//                            facilitiesArray.remove(cb.getText().toString());
//                        }
//                    }
//                }
                if (button.isChecked()){
                    facilitiesArray.add(new FasilitasValue(button.getId()));
                } else {
                    facilitiesArray.remove(new FasilitasValue(button.getId()));
                }
//                switch (button.getId()){
//                    case 1:
//                        Toast.makeText(mContext, "Your Click : " + button.getId() + " Text : " + button.getText().toString(), Toast.LENGTH_SHORT).show();
//                }
            }
        };
    }

    View.OnClickListener getOnClickListenerCheckBoxArea(final CheckBox button){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                for (int i=0; i<=checkBoxFacilities.length(); i++){
//                    View nextChild = checkBoxLinearLayout.getChildAt(i);
//
//                    if (nextChild instanceof  CheckBox){
//                        CheckBox cb = (CheckBox) nextChild;
//                        if (cb.isChecked()){
//                            facilitiesArray.add(cb.getText().toString());
//                        } else {
//                            facilitiesArray.remove(cb.getText().toString());
//                        }
//                    }
//                }
                if (button.isChecked()){
                    areasArray.add(new AreasValue(button.getId()));
                } else {
                    areasArray.remove(new AreasValue(button.getId()));
                }
//                switch (button.getId()){
//                    case 1:
//                        Toast.makeText(mContext, "Your Click : " + button.getId() + " Text : " + button.getText().toString(), Toast.LENGTH_SHORT).show();
//                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK && null != data ){
            imageFile = data.getData();
            String[] projection = {MediaStore.Images.Media.DATA};

            if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                Cursor cursor = getContentResolver().query(imageFile, projection, null, null, null);
                if (cursor!=null){
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageFile);
                        cursor.moveToFirst();
                        int index = cursor.getColumnIndex(projection[0]);
                        fileImagePath = cursor.getString(index);

                        File file = new File(fileImagePath);
                        String hasil = file.getName();
                        mResultImage.setText(hasil);
                        mImageView.setImageBitmap(bitmap);

                        cursor.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                if (imageFile !=null){
                    File file = new File(imageFile.getPath());
                    result = file.getName();
                    mResultImage.setText(result);
                }
            }
        } else if (requestCode == REQUEST_PLACE_PICKER && resultCode == RESULT_OK){
            Place place = PlacePicker.getPlace(data, this);
            mPlace = new PlaceInfo();
            mPlace.setLatitude(place.getViewport().getCenter().latitude);
            mPlace.setLongitude(place.getViewport().getCenter().longitude);
            mPlace.setAddress(place.getAddress().toString());
            mAutoCompleteTextView.setText(mPlace.getAddress());

            geoLocate();
        }
//        if (requestCode == REQUEST_GET_IMAGE && resultCode == RESULT_OK){
//            Uri imageUri = data.getData();
//            String[] projection = {MediaStore.Images.Media.DATA};
//
//            if (imageUri != null) {
//                Cursor cursor = mContext.getContentResolver().query(imageUri, projection, null, null, null);
//                if (cursor != null) {
//                    if (cursor.moveToFirst()) {
//                        int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
//                        fileImagePath = cursor.getString(columnIndex);
////                    Toast.makeText(mContext, filePath, Toast.LENGTH_SHORT).show();
//                        cursor.close();
//                    }
//                } else {
//                    fileImagePath = imageUri.getPath();
//                    Log.d("DEBUG PHOTO : ", DatabaseUtils.dumpCursorToString(cursor));
//                }
//            }
//
//            if (fileImagePath!=null){
//                File file = new File(fileImagePath);
//                String result = file.getName();
//                mResultOtherImage.setText(result);
//            }
//        }
//
//        if (requestCode == GET_IMAGE_REQUEST && resultCode == RESULT_OK){
//            Uri imageUri = data.getData();
//            String[] projection = {MediaStore.Images.Media.DATA};
//
//            if (imageUri != null) {
//                Cursor cursor = mContext.getContentResolver().query(imageUri, projection, null, null, null);
//                if (cursor != null) {
//                    if (cursor.moveToFirst()) {
//                        int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
//                        fileImagePath = cursor.getString(columnIndex);
////                    Toast.makeText(mContext, filePath, Toast.LENGTH_SHORT).show();
//                        cursor.close();
//                    }
//                } else {
//                    fileImagePath = imageUri.getPath();
//                    Log.d("DEBUG PHOTO : ", DatabaseUtils.dumpCursorToString(cursor));
//                }
//            }
//
//            if (fileImagePath!=null){
//                File file = new File(fileImagePath);
//                String result = file.getName();
//                mResultOtherImage2.setText(result);
//            }
//        }
    }

//    private String getPathFromUri(Uri contentUri){
//        String filePath = null;
//        String[] projection = {MediaStore.Images.ImageColumns.DATA};
//
//        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
//        cursor.moveToFirst();
//        int columnIndex = cursor.getColumnIndex(projection[0]);
//        filePath = cursor.getString(columnIndex);
////              Toast.makeText(mContext, filePath, Toast.LENGTH_SHORT).show();
////              Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
//        Log.d("DEBUG PHOTO : ", "WHERE HIS PATH?");
//        File file = new File(fileImagePath);
//            String result = file.getName();
//            mResultImage.setText(result);
//            Log.d("NAME : ", fileImagePath);
//
//        cursor.close();
//
//
//        return filePath;
//    }

    private void addVenue() {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Proses");
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.show();

        File image;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            image = new File(fileImagePath);
        } else {
            image = new File(imageFile.getPath());
        }
        Log.d("PAATTTTHHHHHZZZZ", image +"");

        jsonFacility  = new Gson().toJson(facilitiesArray);
        jsonAreas = new Gson().toJson(areasArray);
        Log.d("FASILITAS ", jsonFacility + "");
        Log.d("AREAAAA ", jsonAreas + "");
        RequestBody mName = RequestBody.create(MultipartBody.FORM, nameVenueEditText.getText().toString());
        RequestBody token = RequestBody.create(MultipartBody.FORM, mAppSession.getData(AppSession.TOKEN));
        RequestBody mDesc = RequestBody.create(MultipartBody.FORM, descVenueEditText.getText().toString());
        RequestBody mAddress = RequestBody.create(MultipartBody.FORM, mAutoCompleteTextView.getText().toString());
        RequestBody mLatitude = RequestBody.create(MultipartBody.FORM, String.valueOf(mPlace.getLatitude()));
        RequestBody mLongitude = RequestBody.create(MultipartBody.FORM, String.valueOf(mPlace.getLongitude()));
//        RequestBody startHour = RequestBody.create(MultipartBody.FORM, fromHourEditText.getText().toString());
//        RequestBody endHour = RequestBody.create(MultipartBody.FORM, untilHourEditText.getText().toString());
        RequestBody facilities = RequestBody.create(MultipartBody.FORM, jsonFacility);
        RequestBody areas = RequestBody.create(MultipartBody.FORM, jsonAreas);

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), image);
        MultipartBody.Part partImage = MultipartBody.Part.createFormData("picture", image.getName(), requestBody);

        mApiService.createVenue(token, mName, mDesc, mAddress, mLatitude, mLongitude,
                mStartHour, mEndHour, facilities, areas, partImage,null )
                .enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getString("status").equals("Success")) {
                            String message = jsonObject.getString("message");
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(mContext, VenueActivity.class);
                            finish();
                            startActivity(intent);
                        } else {
                            Toast.makeText(mContext, "Add Venue Failed", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("onFailure", t.getMessage());
            }
        });
    }

    private void editVenue(){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Proses");
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.show();

        mApiService.formEditVenue(venueId, mAppSession.getData(AppSession.TOKEN)).enqueue(
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            progressDialog.dismiss();
                            try {
                                JSONObject data = new JSONObject(response.body().string());
                                Toast.makeText(mContext, "BERHASIL", Toast.LENGTH_SHORT).show();
                                String name = data.getString("name");
                                String desc = data.getString("description");
                                String address = data.getString("address");
                                String latitude = data.getString("latitude");
                                String longitude = data.getString("longitude");
                                String startHour = data.getString("start_hour");
                                String endHour = data.getString("end_hour");
                                String urlPicture = data.getString("picture");

                                nameVenueEditText.setText(name);
                                descVenueEditText.setText(desc);
                                JSONArray jsonArray = data.getJSONArray("facilities");
                                for (int i=0; i<jsonArray.length(); i++){
                                    JSONObject jsonFacilities = jsonArray.getJSONObject(i);
                                    String facilities = jsonFacilities.getString("name");
                                    checkBoxFacilities = new CheckBox(mContext);
                                    checkBoxFacilities.setChecked(listCheckbox.get(i).equalsIgnoreCase(facilities));
                                    Log.d("FACILIETIEESS", facilities);
                                    Log.d("ARRAY", listCheckbox.get(i).equalsIgnoreCase(facilities) + "");
//                                    listCheckbox.get(i).equalsIgnoreCase(facilities);
                                }

                                for (int i=0; i<fromHourEditText.getCount(); i++){
                                    if (fromHourEditText.getItemAtPosition(i).toString().equalsIgnoreCase(startHour)){
                                        fromHourEditText.setSelection(i);
                                    }
                                }
                                for (int i=0; i<untilHourEditText.getCount(); i++){
                                    if (untilHourEditText.getItemAtPosition(i).toString().equalsIgnoreCase(endHour)){
                                        untilHourEditText.setSelection(i);
                                    }
                                }

                                if (latitude.equals("")){
                                    mLatitude = (Double) null;
                                    mLongitude = (Double) null;
                                } else{
                                    mLatitude = Double.parseDouble(latitude);
                                    mLongitude = Double.parseDouble(longitude);
                                }

                                if (mLatitude != null){
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLatitude, mLongitude)));
                                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null);

                                    MarkerOptions markerOptions = new MarkerOptions().
                                            title(name).position(new LatLng(mLatitude, mLongitude));
                                    mMap.addMarker(markerOptions);
                                    mAutoCompleteTextView.setText(address);
                                } else {
                                    Toast.makeText(mContext, "Lokasi tidak ada", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.d("DEBUG EDIT ", t.getMessage());
                    }
                }
        );
    }

    private void updateVenue(){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Proses");
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.show();
    }

    private void getLocationPermission(){
        Log.d("DEBUG : ", "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("DEBUG : ", "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();

            ((WorkArroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .setListener(new WorkArroundMapFragment.OnTouchListener() {
                        @Override
                        public void onTouch() {
                            scrollView.requestDisallowInterceptTouchEvent(true);
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("DEBUG", "onRequestPermissionsResult: called.");
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            Log.d("DEBUG : ", "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d("DEBUG : ", "onRequestPermissionsResult: permission granted");
                    mLocationPermissionGranted = true;
                    //initialize our map
                    initMap();
                }
//                for(int i = 0; i < grantResults.length; i++){
//                    if (grantResults.length > 0
//                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                        // permission was granted. Do the
//                        // contacts-related task you need to do.
//                        if (ContextCompat.checkSelfPermission(this,
//                                Manifest.permission.ACCESS_FINE_LOCATION)
//                                == PackageManager.PERMISSION_GRANTED) {
//                            mLocationPermissionGranted = true;
//                            initMap();
//                        }
//                    } else {
//
//                        // Permission denied, Disable the functionality that depends on this permission.
//                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
//                    }
//                }
            }
            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    private void initMap(){
        Log.d("DEBUG : ", "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d("DEBUG : ", "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(!title.equals("My Location" )){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }

        hideSoftKeyboard();
    }

    private void init(){
        Log.d("DEBUG : ", "init: initializing");

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mAutoCompleteTextView.setOnItemClickListener(mAutoCompleteClickListener);

        mPlaceAutoComplete = new PlaceAutocompleteAdapter(this, mGoogleApiClient,
                LAT_LNG_BOUNDS, null);


        mAutoCompleteTextView.setAdapter(mPlaceAutoComplete);


        mAutoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    //execute our method for searching
                    geoLocate();
                }
                return false;
            }
        });
        hideSoftKeyboard();
//        mGps.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "onClick: clicked gps icon");
//                getDeviceLocation();
//            }
//        });
//
//        hideSoftKeyboard();
    }

    private void geoLocate(){
        Log.d("DEBGU : ", "geoLocate: geolocating");

        String searchString = mAutoCompleteTextView.getText().toString();

        Geocoder geocoder = new Geocoder(mContext);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e("DEBUG : ", "geoLocate: IOException: " + e.getMessage() );
        }

        if(list.size() > 0){
            Address address = list.get(0);

            Log.d("DEBUG : ", "geoLocate: found a location: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
                    address.getAddressLine(0));


        }
    }

    private void getDeviceLocation(){
        Log.d("DEBUG : ", "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d("DEBUG : ", "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My Location");

                        }else{
                            Log.d("DEBUG : ", "onComplete: current location is null");
                            Toast.makeText(mContext, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e("DEBUG : ", "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private AdapterView.OnItemClickListener mAutoCompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideSoftKeyboard();

            final AutocompletePrediction item = mPlaceAutoComplete.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()){
                Log.d("DEBUG : ", "onResult : Place query did not complete successfully: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);

            try {
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                mPlace.setLatitude(place.getViewport().getCenter().latitude);
                mPlace.setLongitude(place.getViewport().getCenter().longitude);
                mPlace.setId(place.getId());
                mPlace.setAddress(place.getAddress().toString());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
            } catch (NullPointerException e){
                e.printStackTrace();
                Log.e("DEBUG ", "onResult: NullPointerException: " + e.getMessage() );
            }


            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlace.getName());

            places.release();
        }
    };

}
