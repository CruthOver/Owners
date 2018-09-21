package com.wiradipa.fieldOwners;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wiradipa.fieldOwners.Adapter.TarifAdapter;
import com.wiradipa.fieldOwners.ApiHelper.AppSession;
import com.wiradipa.fieldOwners.ApiHelper.BaseApiService;
import com.wiradipa.fieldOwners.ApiHelper.UtilsApi;
import com.wiradipa.fieldOwners.Model.FieldData;
import com.wiradipa.fieldOwners.Model.FieldTariff;
import com.wiradipa.fieldOwners.Model.ListVenue;
import com.wiradipa.fieldOwners.Model.ResponseData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddFieldActivity extends AppCompatActivity {

    private static final int REQUEST_SELECT_IMAGE = 1;
    private static final int REQUEST_GET_IMAGE = 2;
    private static final int GET_IMAGE_REQUEST = 3;

    EditText mFieldNameEditText, mDescriptionFieldEditText
            , mFieldSizeEditText, mFieldCostEditText, mFieldCostEditText2, mTitleFieldEditText
            , mTitleFieldEditText2;
    Spinner mFromDaySpinner, mUntilDaySpinner, mFromDay2Spinner, mUntilDay2Spinner, mFieldTypeSpinner, mGrassTypeSpinner,
            mFromHourSpinner, mUntilHourSpinner, mFromHourSpinner2, mUntilHourSpinner2, spinnerVenue;
    CheckBox mFacilitiesCheckBox;
    TextView mResultPhoto, mResultOtherPhoto, mResultOtherPhoto2, addOtherTarif;
    Button mBtnSubmit, mBtnCancel;
    ImageView mAddImageView, mAddOtherImageView, mAddOtherImageView2;
    String fileImagePath;

    TimePickerDialog timePickerDialog;

    ArrayList<FieldTariff> fieldTarifs;
    ListVenue venue;
    ListView listViewTarif;
    TarifAdapter mAdapter;

    private Uri imageFile;
    private Bitmap bitmap;

    Context mContext;
    BaseApiService mApiService;
    AppSession mAppSession;

    private int mStartDay, mEndDay, mStartDay2, mEndDay2, mFieldType, mGrassType
            , mStartHour, mEndHour, mStartHour2, mEndHour2, mIdVenue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_field);

        mContext = this;
        mApiService = UtilsApi.getApiService();
        mAppSession = new AppSession(mContext);

        fieldTarifs = new ArrayList<FieldTariff>();
        listViewTarif = (ListView) findViewById(R.id.list_tariff);
        //fieldTarifs.add(new FieldTariff(01, 02, 1, 2 , "200.000"));
        mAdapter = new TarifAdapter(mContext, fieldTarifs);
        listViewTarif.setAdapter(mAdapter);

        initComponents();
    }

    private boolean isNameFieldValid(String nameField){
        return nameField.equals("");
    }

    private boolean isDecsVenueValid(String descField){
        return descField.equals("");
    }

    private void initComponents(){
        mFieldNameEditText = (EditText) findViewById(R.id.et_field_name);
        mDescriptionFieldEditText = (EditText) findViewById(R.id.et_field_description);
        mFieldSizeEditText = (EditText) findViewById(R.id.et_size_field);
//        mFieldCostEditText = (EditText) findViewById(R.id.et_cost_field);

//        mFieldCostEditText2 = (EditText) findViewById(R.id.tv_cost_field2);
//        mTitleFieldEditText = (EditText) findViewById(R.id.et_title_field);
//        mTitleFieldEditText2 = (EditText) findViewById(R.id.et_title_field2);
//        mFromHourSpinner2 = (Spinner) findViewById(R.id.et_from_hour2);
//        mUntilHourSpinner2 = (Spinner) findViewById(R.id.et_until_hour2);
//        mUntilHourSpinner = (Spinner) findViewById(R.id.et_until_hour);
//        mFromHourSpinner = (Spinner) findViewById(R.id.et_from_hour);

//        mFacilitiesCheckBox = (CheckBox) findViewById(R.id.checkbox_facilities_field);

        spinnerVenue = (Spinner) findViewById(R.id.spinner_id_venue);

//        mFromDaySpinner = (Spinner) findViewById(R.id.spinner_from_day);
//        mUntilDaySpinner = (Spinner) findViewById(R.id.spinner_until_day);
//        mFromDay2Spinner = (Spinner) findViewById(R.id.spinner_from_day2);
//        mUntilDay2Spinner = (Spinner) findViewById(R.id.spinner_until_day2);
        mFieldTypeSpinner = (Spinner) findViewById(R.id.spinner_type_field);
        mGrassTypeSpinner = (Spinner) findViewById(R.id.spinner_type_floor);

        mResultPhoto = (TextView) findViewById(R.id.tv_photo1);
//        mResultOtherPhoto =(TextView) findViewById(R.id.other_photo);
//        mResultOtherPhoto2 = (TextView) findViewById(R.id.other_photo2);
        addOtherTarif = (TextView) findViewById(R.id.add_other_opsi);

        mBtnSubmit = (Button) findViewById(R.id.add_new_field);
        mBtnCancel = (Button) findViewById(R.id.btn_cancel);

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addField();
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mAddImageView = (ImageView) findViewById(R.id.add_photo);

        mAddImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intentGallery = new Intent(Intent.ACTION_PICK);
                    intentGallery.setType("image/*");
                    if (intentGallery.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(Intent.createChooser(intentGallery, "SELECT IMAGE"), REQUEST_SELECT_IMAGE);
                    } else {
                        Toast.makeText(mContext, "You Don't Have Pick Image", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ActivityCompat.requestPermissions(AddFieldActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_SELECT_IMAGE);
                }
            }
        });

        addOtherTarif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogTarif();
            }
        });

//        mAddOtherImageView = (ImageView) findViewById(R.id.img_other_photo);
//        mAddOtherImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intentGallery = new Intent(Intent.ACTION_PICK);
//                intentGallery.setType("image/*");
//                if (intentGallery.resolveActivity(getPackageManager()) != null){
//                    startActivityForResult(Intent.createChooser(intentGallery, "SELECT IMAGE"),REQUEST_GET_IMAGE);
//                }
//            }
//        });

//        mAddOtherImageView2 = (ImageView) findViewById(R.id.img_other_photo2);
//        mAddOtherImageView2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intentGallery = new Intent(Intent.ACTION_PICK);
//                intentGallery.setType("image/*");
//                if (intentGallery.resolveActivity(getPackageManager()) != null){
//                    startActivityForResult(Intent.createChooser(intentGallery, "SELECT IMAGE"), GET_IMAGE_REQUEST);
//                }
//            }
//        });
        mFieldTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedName = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(mContext, i + " : " + selectedName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mGrassTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedName = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(mContext, i + " : " + selectedName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerVenue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                venue = (ListVenue) adapterView.getSelectedItem();
                mIdVenue = venue.getId();
                Toast.makeText(mContext, "Venue ID: "+ venue.getId()+",  Venue Name : "+ venue.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        addOtherTarif.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ArrayList<String> layoutTarif = new ArrayList();
//                LinearLayout linearLayout = new LinearLayout(mContext);
//                linearLayout.setOrientation(LinearLayout.VERTICAL);
//                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
//
//                for (int i=0; i<layoutTarif.size(); i++){
//                    view = LayoutInflater.from(mContext).inflate(R.layout.list_tariff, linearLayout);
//                    mUntilHourSpinner = (Spinner) view.findViewById(R.id.et_until_hour);
//                    mFromHourSpinner = (Spinner) view.findViewById(R.id.et_from_hour);
//                    mFromDaySpinner = (Spinner) view.findViewById(R.id.spinner_from_day);
//                    mUntilDaySpinner = (Spinner) view.findViewById(R.id.spinner_until_day);
//                    mFieldCostEditText = (EditText) view.findViewById(R.id.et_cost_field);
//
//                    linearLayout.addView(view);
//                    Toast.makeText(mContext, "Layout Ditambahkan", Toast.LENGTH_SHORT).show();
//                }
//                Toast.makeText(mContext, "Layout Gagal Ditambahkan", Toast.LENGTH_SHORT).show();
//
//            }
//        });

//        setupSpinnerFromDay();
//        setupSpinnerUntilDay();
//        setupSpinnerFromDay2();
//        setupSpinnerUntilDay2();
        setupSpinnerTypeGrass();
        setupSpinnerTypeField();
//        setSpinnerFromHour();
//        setSpinnerEndHour();
        setSpinnerVenue();
//        setSpinnerFromHour2();
//        setSpinnerEndHour2();
    }

    private void setSpinnerFromHour() {

        ArrayAdapter fromDaySpinner = ArrayAdapter.createFromResource(mContext,
                R.array.startHour, R.layout.spinner_jadwal);

        fromDaySpinner.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mFromHourSpinner.setAdapter(fromDaySpinner);
        mFromHourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getItemAtPosition(i);
                if (!TextUtils.isEmpty(selection)){
                    if (selection.equals("-- Pilih Jam -- ")){
                        Toast.makeText(mContext, "Pilih Jam Terlebih Dahulu", Toast.LENGTH_SHORT).show();;
                    }
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
//        mFromHourEditText.setInputType(InputType.TYPE_NULL);
//        mFromHourEditText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final Calendar calendar = Calendar.getInstance();
//                int hour = calendar.get(Calendar.HOUR);
//                int minute = calendar.get(Calendar.MINUTE);
//                //time picker dialog
//                timePickerDialog = new TimePickerDialog(mContext, android.R.style.Theme_Holo_Dialog, new TimePickerDialog.OnTimeSetListener() {
//                    @SuppressLint("DefaultLocale")
//                    @Override
//                    public void onTimeSet(TimePicker timePicker, int sHour, int sMinute) {
//                        mFromHourEditText.setText(String.format("%02d:%02d", sHour, sMinute));
//                    }
//                }, hour, minute, true);
//                timePickerDialog.setIcon(R.drawable.akun_icon);
//                timePickerDialog.setTitle("Please Select Time");
//                timePickerDialog.show();
//            }
//        });
    }

    private void setSpinnerEndHour() {

        ArrayAdapter fromDaySpinner = ArrayAdapter.createFromResource(mContext,
                R.array.endHour, R.layout.spinner_jadwal);

        fromDaySpinner.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mUntilHourSpinner.setAdapter(fromDaySpinner);
        mUntilHourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getItemAtPosition(i);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.optionDay))) {
                        Toast.makeText(mContext, "Pilih Jam Terlebih Dahulu", Toast.LENGTH_SHORT).show();
                    }

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

//    private void setSpinnerFromHour2() {
//
//        ArrayAdapter fromDaySpinner = ArrayAdapter.createFromResource(mContext,
//                R.array.startHour, R.layout.spinner_jadwal);
//
//        fromDaySpinner.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
//
//        mFromHourSpinner2.setAdapter(fromDaySpinner);
//        mFromHourSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String selection = (String) adapterView.getItemAtPosition(i);
//                if (!TextUtils.isEmpty(selection)){
//                    if (selection.equals("-- Pilih Jam --")){
//                        Toast.makeText(mContext, "Pilih Jam Terlebih Dahulu", Toast.LENGTH_SHORT).show();;
//                    }
//                    if (selection.equals("0")){
//                        mStartHour2 = 0;
//                        Log.d("START HOUR ", mStartHour2 + "");
//                    } else if (selection.equals("1")){
//                        mStartHour2 = 1; //monday
//                    } else if(selection.equals("2")){
//                        mStartHour2 = 2;
//                    } else if (selection.equals("3")){
//                        mStartHour2 = 3;
//                    } else if (selection.equals("4")){
//                        mStartHour2 = 4;
//                    } else if (selection.equals("5")){
//                        mStartHour2 = 5;
//                    } else if (selection.equals("6")){
//                        mStartHour2 = 6;
//                    } else if (selection.equals("7")){
//                        mStartHour2 = 7;
//                    } else if (selection.equals("8")){
//                        mStartHour2 = 8;
//                    } else if (selection.equals("9")){
//                        mStartHour2 = 9;
//                    } else if (selection.equals("10")){
//                        mStartHour2 = 10;
//                    } else if (selection.equals("11")){
//                        mStartHour2 = 11;
//                    } else if (selection.equals("12")){
//                        mStartHour2 = 12;
//                    } else if (selection.equals("13")){
//                        mStartHour2 = 13;
//                    } else if (selection.equals("14")){
//                        mStartHour2 = 14;
//                    } else if (selection.equals("15")){
//                        mStartHour2 = 15;
//                    } else if (selection.equals("16")){
//                        mStartHour2 = 16;
//                    } else if (selection.equals("17")){
//                        mStartHour2 = 17;
//                    } else if (selection.equals("18")){
//                        mStartHour2 = 18;
//                    } else if (selection.equals("19")){
//                        mStartHour2 = 19;
//                    } else if (selection.equals("20")){
//                        mStartHour2 = 20;
//                    } else if (selection.equals("21")){
//                        mStartHour2 = 21;
//                    } else if (selection.equals("221")){
//                        mStartHour2 = 22;
//                    } else if (selection.equals("23")){
//                        mStartHour2 = 23;
//                    }
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                Toast.makeText(mContext, "Hari belum dipilih", Toast.LENGTH_SHORT).show();
//            }
//        });
////        mFromHourEditText.setInputType(InputType.TYPE_NULL);
////        mFromHourEditText.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                final Calendar calendar = Calendar.getInstance();
////                int hour = calendar.get(Calendar.HOUR);
////                int minute = calendar.get(Calendar.MINUTE);
////                //time picker dialog
////                timePickerDialog = new TimePickerDialog(mContext, android.R.style.Theme_Holo_Dialog, new TimePickerDialog.OnTimeSetListener() {
////                    @SuppressLint("DefaultLocale")
////                    @Override
////                    public void onTimeSet(TimePicker timePicker, int sHour, int sMinute) {
////                        mFromHourEditText.setText(String.format("%02d:%02d", sHour, sMinute));
////                    }
////                }, hour, minute, true);
////                timePickerDialog.setIcon(R.drawable.akun_icon);
////                timePickerDialog.setTitle("Please Select Time");
////                timePickerDialog.show();
////            }
////        });
//    }
//
//    private void setSpinnerEndHour2() {
//
//        ArrayAdapter fromDaySpinner = ArrayAdapter.createFromResource(mContext,
//                R.array.endHour, R.layout.spinner_jadwal);
//
//        fromDaySpinner.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
//
//        mUntilHourSpinner2.setAdapter(fromDaySpinner);
//        mUntilHourSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String selection = (String) adapterView.getItemAtPosition(i);
//                if (!TextUtils.isEmpty(selection)) {
//                    if (selection.equals("-- Pilih Jam --")) {
//                        Toast.makeText(mContext, "Pilih Jam Terlebih Dahulu", Toast.LENGTH_SHORT).show();
//                    }
//                    if (selection.equals("1")) {
//                        mEndHour2 = 1; //monday
//                    } else if (selection.equals("2")) {
//                        mEndHour2 = 2;
//                    } else if (selection.equals("3")) {
//                        mEndHour2 = 3;
//                    } else if (selection.equals("4")) {
//                        mEndHour2 = 4;
//                    } else if (selection.equals("5")) {
//                        mEndHour2 = 5;
//                    } else if (selection.equals("6")) {
//                        mEndHour2 = 6;
//                    } else if (selection.equals("7")) {
//                        mEndHour2 = 7;
//                    } else if (selection.equals("8")) {
//                        mEndHour2 = 8;
//                    } else if (selection.equals("9")) {
//                        mEndHour2 = 9;
//                    } else if (selection.equals("10")) {
//                        mEndHour2 = 10;
//                    } else if (selection.equals("11")) {
//                        mEndHour2 = 11;
//                    } else if (selection.equals("12")) {
//                        mEndHour2 = 12;
//                    } else if (selection.equals("13")) {
//                        mEndHour2 = 13;
//                    } else if (selection.equals("14")) {
//                        mEndHour2 = 14;
//                    } else if (selection.equals("15")) {
//                        mEndHour2 = 15;
//                    } else if (selection.equals("16")) {
//                        mEndHour2 = 16;
//                    } else if (selection.equals("17")) {
//                        mEndHour2 = 17;
//                    } else if (selection.equals("18")) {
//                        mEndHour2 = 18;
//                    } else if (selection.equals("19")) {
//                        mEndHour2 = 19;
//                    } else if (selection.equals("20")) {
//                        mEndHour2 = 20;
//                    } else if (selection.equals("21")) {
//                        mEndHour2 = 21;
//                    } else if (selection.equals("221")) {
//                        mEndHour2 = 22;
//                    } else if (selection.equals("23")) {
//                        mEndHour2 = 23;
//                    } else if (selection.equals("24")) {
//                        mEndHour2 = 24;
//                    }
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                Toast.makeText(mContext, "Hari belum dipilih", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void setupSpinnerFromDay(){
        ArrayAdapter fromDaySpinner = ArrayAdapter.createFromResource(mContext,
                R.array.day, R.layout.spinner_jadwal);

        fromDaySpinner.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mFromDaySpinner.setAdapter(fromDaySpinner);
        mFromDaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getItemAtPosition(i);
                if (!TextUtils.isEmpty(selection)){
                    if (selection.equals(getString(R.string.optionDay))){
                        Toast.makeText(mContext, "Select Open Day", Toast.LENGTH_SHORT).show();
                    }
                    if (selection.equals(getString(R.string.monday))){
                        mStartDay = 1; //monday
                    } else if(selection.equals(getString(R.string.tuesday))){
                        mStartDay = 2;
                    } else if (selection.equals(getString(R.string.wednesday))){
                        mStartDay = 3;
                    } else if (selection.equals(getString(R.string.thursday))){
                        mStartDay = 4;
                    } else if (selection.equals(getString(R.string.friday))){
                        mStartDay = 5;
                    } else if (selection.equals(getString(R.string.saturday))){
                        mStartDay = 6;
                    } else if (selection.equals(getString(R.string.sunday))){
                        mStartDay = 0;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(mContext, "Hari belum dipilih", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpinnerUntilDay(){
        ArrayAdapter untilDaySpinner = ArrayAdapter.createFromResource(mContext,
                R.array.day, R.layout.spinner_jadwal);

        untilDaySpinner.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mUntilDaySpinner.setAdapter(untilDaySpinner);
        mUntilDaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getItemAtPosition(i);
                if (!TextUtils.isEmpty(selection)){
                    if (selection.equals(getString(R.string.monday))){
                        mEndDay = 1; //monday
                    } else if(selection.equals(getString(R.string.tuesday))){
                        mEndDay = 2;
                    } else if (selection.equals(getString(R.string.wednesday))){
                        mEndDay = 3;
                    } else if (selection.equals(getString(R.string.thursday))){
                        mEndDay = 4;
                    } else if (selection.equals(getString(R.string.friday))){
                        mEndDay = 5;
                    } else if (selection.equals(getString(R.string.saturday))){
                        mEndDay = 6;
                    } else if (selection.equals(getString(R.string.sunday))){
                        mEndDay = 0;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(mContext, "Hari belum dipilih", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSpinnerVenue(){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Proses");
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.show();

        mApiService.listVenue(mAppSession.getData(AppSession.TOKEN)).enqueue(
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            progressDialog.dismiss();
                            try {
                                int idVenue;
                                String nameVenue = "";
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                if (jsonObject.getString("status").equals("Success")){
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    ArrayList<ListVenue> listVenue = new ArrayList<>();
                                    for (int i=0; i<jsonArray.length(); i++){
                                        jsonObject = jsonArray.getJSONObject(i);
                                        idVenue = jsonObject.getInt("id");
                                        nameVenue = jsonObject.getString("name");
                                        listVenue.add(new ListVenue(idVenue, nameVenue));
                                        Log.d("NAME VENUE", idVenue + nameVenue);
                                    }
                                    final ArrayAdapter<ListVenue> adapter = new ArrayAdapter<ListVenue>(mContext, R.layout.spinner_jadwal, listVenue);
                                    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                                    spinnerVenue.setAdapter(adapter);
                                }

                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                }
        );
    }

//    private void setupSpinnerFromDay2(){
//        ArrayAdapter fromDay2Spinner = ArrayAdapter.createFromResource(mContext,
//                R.array.day, R.layout.spinner_jadwal);
//
//        fromDay2Spinner.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
//
//        mFromDay2Spinner.setAdapter(fromDay2Spinner);
//        mFromDay2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String selection = (String) adapterView.getItemAtPosition(i);
//                if (!TextUtils.isEmpty(selection)){
//                    if (selection.equals(getString(R.string.monday))){
//                        mStartDay2 = 1; //monday
//                    } else if(selection.equals(getString(R.string.tuesday))){
//                        mStartDay2 = 2;
//                    } else if (selection.equals(getString(R.string.wednesday))){
//                        mStartDay2 = 3;
//                    } else if (selection.equals(getString(R.string.thursday))){
//                        mStartDay2 = 4;
//                    } else if (selection.equals(getString(R.string.friday))){
//                        mStartDay2 = 5;
//                    } else if (selection.equals(getString(R.string.saturday))){
//                        mStartDay2 = 6;
//                    } else if (selection.equals(getString(R.string.sunday))){
//                        mStartDay2 = 0;
//                    }
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                Toast.makeText(mContext, "Hari belum dipilih", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void setupSpinnerUntilDay2(){
//        ArrayAdapter untilDay2Spinner = ArrayAdapter.createFromResource(mContext,
//                R.array.day, R.layout.spinner_jadwal);
//
//        untilDay2Spinner.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
//
//        mUntilDay2Spinner.setAdapter(untilDay2Spinner);
//        mUntilDay2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String selection = (String) adapterView.getItemAtPosition(i);
//                if (!TextUtils.isEmpty(selection)){
//                    if (selection.equals(getString(R.string.monday))){
//                        mEndDay2 = 1; //monday
//                    } else if(selection.equals(getString(R.string.tuesday))){
//                        mEndDay2 = 2;
//                    } else if (selection.equals(getString(R.string.wednesday))){
//                        mEndDay2 = 3;
//                    } else if (selection.equals(getString(R.string.thursday))){
//                        mEndDay = 4;
//                    } else if (selection.equals(getString(R.string.friday))){
//                        mEndDay2 = 5;
//                    } else if (selection.equals(getString(R.string.saturday))){
//                        mEndDay2 = 6;
//                    } else if (selection.equals(getString(R.string.sunday))){
//                        mEndDay2 = 0;
//                    }
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                Toast.makeText(mContext, "Hari belum dipilih", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void setupSpinnerTypeGrass(){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Proses");
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.show();
        mApiService.typesGrass().enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful()){
                    progressDialog.dismiss();
                    List<FieldData> fieldTypeItems = response.body().getFieldData();
                    List<String> listSpinner = new ArrayList<String>();
                    for (int i=0; i < fieldTypeItems.size(); i++){
                        listSpinner.add(fieldTypeItems.get(i).getNama());
                        mGrassType = fieldTypeItems.get(i).getId();
                        Log.d("DEBUGGING : ", mGrassType + " : " + listSpinner);
                    }

                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_jadwal, listSpinner);
                    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    mGrassTypeSpinner.setAdapter(adapter);

                } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle(R.string.dialog_title_error);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.exit(0);
                                finish();
                            }
                        });
                        builder.setMessage("Server Error !!");
                        AlertDialog alert1 = builder.create();
                        alert1.show();
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {

            }
        });
    }

    private void setupSpinnerTypeField(){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Proses");
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.show();

        mApiService.typeField().enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful()){
                    progressDialog.dismiss();
                    List<FieldData> fieldTypeItems = response.body().getFieldData();
                    List<String> listSpinner = new ArrayList<String>();
                    listSpinner.add(" Pilih Jenis Lapangan ");
                    for (int i=0; i < fieldTypeItems.size(); i++){
                        listSpinner.add(fieldTypeItems.get(i).getNama());
                        mFieldType = fieldTypeItems.get(i).getId();
                        Log.d("DEBUGGING : ", mFieldType + " : " + listSpinner);
                    }

                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_jadwal, listSpinner);
                    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    mFieldTypeSpinner.setAdapter(adapter);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(R.string.dialog_title_error);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                            finish();
                        }
                    });
                    builder.setMessage("Server Error !!");
                    AlertDialog alert1 = builder.create();
                    alert1.show();
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {

            }
        });
    }

    private void dialogTarif(){
        final FieldTariff fieldTariff = new FieldTariff();

        String titleText = "Tambah Tarif";
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.list_tariff, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);

        // Initialize a new foreground color span instance
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.WHITE);

        // Initialize a new spannable string builder instance
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);

        // Apply the text color span
        ssBuilder.setSpan(
                foregroundColorSpan,
                0,
                titleText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );


        dialogBuilder.setTitle(ssBuilder);

        mFromDaySpinner = (Spinner) dialogView.findViewById(R.id.spinner_from_day);
        mUntilDaySpinner = (Spinner) dialogView.findViewById(R.id.spinner_until_day);
        mUntilHourSpinner = (Spinner) dialogView.findViewById(R.id.et_until_hour);
        mFromHourSpinner = (Spinner) dialogView.findViewById(R.id.et_from_hour);
        mFieldCostEditText = (EditText) dialogView.findViewById(R.id.et_cost_field);

        setupSpinnerFromDay();
        setupSpinnerUntilDay();
        setSpinnerFromHour();
        setSpinnerEndHour();

        dialogBuilder.setPositiveButton("Tambah", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fieldTariff.setStartDay(mStartDay);
                fieldTariff.setEndDay(mEndDay);
                fieldTariff.setStartHour(mStartHour);
                fieldTariff.setEndHour(mEndHour);
                fieldTariff.setTariff(mFieldCostEditText.getText().toString());
                fieldTarifs.add(fieldTariff);

                mAdapter.notifyDataSetChanged();
            }
        });
        dialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000046")));
        dialog.show();
    }

    private void addField(){
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

        fieldTarifs.add(new FieldTariff(mStartDay, mEndDay, mStartHour,
                mEndHour, mFieldCostEditText.getText().toString()));

        String jsonTarif = new Gson().toJson(fieldTarifs);
        Log.d("GSOOONNNTARIIIF ", jsonTarif);
        Log.d("FieldSPINNER ", spinnerVenue.getId()+ " => " + mIdVenue);
        Log.d("FieldSPINNERTYPE ", mFieldTypeSpinner.getSelectedItemPosition() + " ");

        RequestBody mName = RequestBody.create(MultipartBody.FORM, mFieldNameEditText.getText().toString());
        RequestBody token = RequestBody.create(MultipartBody.FORM, mAppSession.getData(AppSession.TOKEN));
        RequestBody mDesc = RequestBody.create(MultipartBody.FORM, mDescriptionFieldEditText.getText().toString());
//        RequestBody typeField = RequestBody.create(MultipartBody.FORM, mFieldTypeSpinner.getSelectedItemPosition());
        RequestBody sizeField = RequestBody.create(MultipartBody.FORM, mFieldSizeEditText.getText().toString());
        RequestBody tariff = RequestBody.create(MultipartBody.FORM, jsonTarif);

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), image);
        MultipartBody.Part partImage = MultipartBody.Part.createFormData("picture", image.getName(), requestBody);

        mApiService.createField(token, mName, mDesc, mIdVenue,
                mGrassTypeSpinner.getSelectedItemPosition(),
                mFieldTypeSpinner.getSelectedItemPosition(), sizeField,
                tariff, partImage, null).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getString("status").equals("Success")){
                            Toast.makeText(mContext, "BERHASIL", Toast.LENGTH_SHORT).show();
                            String message = jsonObject.getString("message");
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(mContext, FieldActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(mContext, "Add Field Failed", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK){
            imageFile = data.getData();
            String[] projection = {MediaStore.Images.Media.DATA};

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                Cursor cursor = getContentResolver().query(imageFile, projection, null, null, null);
                if (cursor!=null){
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageFile);
                        cursor.moveToFirst();
                        int index = cursor.getColumnIndex(projection[0]);
                        fileImagePath = cursor.getString(index);

                        File file = new File(fileImagePath);
                        String hasil = file.getName();
                        mResultPhoto.setText(hasil);

                        cursor.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                if (imageFile!=null){
                    File file = new File(imageFile.getPath());
                    String result = file.getName();
                    mResultPhoto.setText(result);
//                Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("DEBUG PHOTO : ", "WHERE HIS PATH?");
                }
            }
        }

//        if (requestCode == REQUEST_GET_IMAGE && resultCode == RESULT_OK){
//            Uri imageUri = data.getData();
//            String otherPathUri = getPathFromUri(imageUri);
//
//            if (otherPathUri!=null){
//                File file = new File(otherPathUri);
//                String result = file.getName();
//                mResultOtherPhoto.setText(result);
//            }
//        }
//
//        if (requestCode == GET_IMAGE_REQUEST && resultCode == RESULT_OK){
//            Uri imageUri = data.getData();
//            String otherPathUri = getPathFromUri(imageUri);
//
//            if (otherPathUri!=null){
//                File file = new File(otherPathUri);
//                String result = file.getName();
//                mResultOtherPhoto2.setText(result);
//            }
//        }
    }
}
