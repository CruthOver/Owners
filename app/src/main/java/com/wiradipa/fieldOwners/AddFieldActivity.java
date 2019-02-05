package com.wiradipa.fieldOwners;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wiradipa.fieldOwners.Adapter.TarifAdapter;
import com.wiradipa.fieldOwners.ApiHelper.AppSession;
import com.wiradipa.fieldOwners.ApiHelper.BaseApiService;
import com.wiradipa.fieldOwners.ApiHelper.UtilsApi;
import com.wiradipa.fieldOwners.Model.CurrencyTextWatcher;
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
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFieldActivity extends AppCompatActivity {

    private static final int REQUEST_SELECT_IMAGE = 1;
    private int mStartDay, mEndDay, mFieldType, mGrassType
            , mStartHour, mEndHour, mIdVenue;
    private String jsonTarif, id, fieldId;
    private RelativeLayout tarifRelativeLayout;
    private Uri imageFile;

    EditText mFieldNameEditText, mDescriptionFieldEditText
            , mFieldSizeEditText, mFieldCostEditText;
    Spinner mFromDaySpinner, mUntilDaySpinner, mFieldTypeSpinner, mGrassTypeSpinner,
            mFromHourSpinner, mUntilHourSpinner, spinnerVenue;
    TextView mResultPhoto, mEmptyViewTarif, mTextViewVenue;
    Button mBtnSubmit, mBtnCancel, addOtherTarif;
    ImageView mAddImageView;
    String fileImagePath = "";
    String imagePath = "";
    TimePickerDialog timePickerDialog;

    ArrayList<FieldTariff> fieldTarifs;
    ListVenue venue;
    NonScrollListView listViewTarif;
    TarifAdapter mAdapter;

    Context mContext;
    BaseApiService mApiService;
    AppSession mAppSession;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_field);

        mContext = this;
        mApiService = UtilsApi.getApiService();
        mAppSession = new AppSession(mContext);

        initComponents();

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            id = bundle.getString("idVenue");
            try{
                fieldId = bundle.getString("fieldID");
                String flag  = (String) bundle.get("FLAG");
                if(flag.equals("edit")){
                    detailField(fieldId);
                    tarifRelativeLayout.setVisibility(View.GONE);
                    addOtherTarif.setVisibility(View.GONE);
                    getSupportActionBar().setTitle(R.string.edit_lapangan);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        fieldTarifs = new ArrayList<FieldTariff>();
        listViewTarif = (NonScrollListView) findViewById(R.id.list_tariff);
        listViewTarif.setNestedScrollingEnabled(false);
        mAdapter = new TarifAdapter(mContext, fieldTarifs);
        listViewTarif.setAdapter(mAdapter);
    }

    private boolean emptyCheck(){
        boolean status = true;
        mFieldNameEditText.setError(null);
        mDescriptionFieldEditText.setError(null);
        mFieldSizeEditText.setError(null);

        if(isStringEmpty(mFieldNameEditText.getText().toString())){
            mFieldNameEditText.setError(getString(R.string.error_field_required));
            status = false;
        }
        if(isStringEmpty(mDescriptionFieldEditText.getText().toString())){
            mDescriptionFieldEditText.setError(getString(R.string.error_field_required));
            status = false;
        }
        if(isStringEmpty(mFieldSizeEditText.getText().toString())){
            mFieldSizeEditText.setError(getString(R.string.error_field_required));
            status = false;
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M || android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (isStringEmpty(fileImagePath)){
                status = false;
                popupAllert("Gambar belum dipilih");
            }
        } else {
            if(isStringEmpty(imagePath)){
                status = false;
                popupAllert("Gambar belum dipilih");
            }
        }
        return status;
    }

    private boolean isStringEmpty(String x){
        return x.equals("");
    }

    public void popupAllert(String alert) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_error)
                .setMessage(alert)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private void detailField(String requestID){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Proses");
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.show();

        mApiService.detailField(requestID).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getString("status").equals("Success")){
//                            Toast.makeText(mContext, "Berhasil", Toast.LENGTH_SHORT).show();
                            String nameField = jsonObject.getString("name");
                            String url = jsonObject.getString("picture_url");
                            String venueName = jsonObject.getString("field_owner_name");
                            String sizeField = jsonObject.getString("pitch_size");
                            String typeGrass = jsonObject.getString("grass_type_name");
                            String typeField = jsonObject.getString("field_type_name");
                            String descField = jsonObject.getString("description");
                            String costField = "";
                            String venueID = jsonObject.getString("field_owner_id");

                            int grassID = Integer.parseInt(jsonObject.getString("grass_type_id"));
                            int fieldID = 0;
                            try {
                                fieldID = Integer.parseInt(jsonObject.getString("field_type_id"));
                            } catch (NumberFormatException e){
                                e.printStackTrace();
                                fieldID = 1;
                            }

                            JSONArray jsonArray = jsonObject.getJSONArray("field_tariffs");
                            for (int i=0; i<jsonArray.length(); i++){
                                jsonObject = jsonArray.getJSONObject(i);
                                costField = jsonObject.getString("tariff");
                            }

                            if (String.valueOf(grassID) == null){
                                mGrassTypeSpinner.setSelection(1, false);
                            } else {
                                for (int i=0; i<mGrassTypeSpinner.getCount(); i++){
                                    if(mGrassTypeSpinner.getItemAtPosition(i).toString().equalsIgnoreCase(typeGrass)){
                                        mGrassTypeSpinner.setSelection(i, false);
                                    }
                                }
                            }

                            for (int i=0; i<spinnerVenue.getCount(); i++){
                                if(spinnerVenue.getItemAtPosition(i).toString().equalsIgnoreCase(venueName)){
                                    spinnerVenue.setSelection(i, false);
                                }
                            }

                            for (int i=0; i<mFieldTypeSpinner.getCount(); i++){
                                if(mFieldTypeSpinner.getItemAtPosition(i).toString().equalsIgnoreCase(typeField)){
                                    mFieldTypeSpinner.setSelection(i, false);
                                }
                            }
                            id = venueID;

                            mResultPhoto.setText(url);
                            mFieldNameEditText.setText(nameField);
                            mFieldSizeEditText.setText(sizeField);
                            mDescriptionFieldEditText.setText(descField);
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    switch (response.code()){
                        case 404:
                            popupAllert(getString(R.string.server_not_found));
                            break;
                        case 500:
                            popupAllert(getString(R.string.server_error));
                            break;
                        case 413:
                            popupAllert(getString(R.string.error_large));
                            break;
                        default:
                            popupAllert(getString(R.string.unknown_error));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("debug", "OnFailure: ERROR > "+ t.toString());
                progressDialog.dismiss();
                popupAllert("No Internet Connection !!!");
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void initComponents(){
        tarifRelativeLayout = findViewById(R.id.tarif_RL);
        mFieldNameEditText = (EditText) findViewById(R.id.et_field_name);
        mDescriptionFieldEditText = (EditText) findViewById(R.id.et_field_description);
        mFieldSizeEditText = (EditText) findViewById(R.id.et_size_field);

        spinnerVenue = (Spinner) findViewById(R.id.spinner_id_venue);
        mFieldTypeSpinner = (Spinner) findViewById(R.id.spinner_type_field);
        mGrassTypeSpinner = (Spinner) findViewById(R.id.spinner_type_floor);

        mResultPhoto = (TextView) findViewById(R.id.tv_photo1);
        mEmptyViewTarif = (TextView) findViewById(R.id.empty_tarif_tv);
        mTextViewVenue = (TextView) findViewById(R.id.textVenue);

        addOtherTarif = (Button) findViewById(R.id.add_other_opsi);
        mBtnSubmit = (Button) findViewById(R.id.add_new_field);
        if (fieldId == null){
            mBtnSubmit.setText("SIMPAN");
        } else {
            mBtnSubmit.setText("UPDATE");
        }
        mBtnCancel = (Button) findViewById(R.id.btn_cancel);

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fieldId == null){
                    addField(jsonTarif);
                } else {
                    updateField();
                }
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
//                        Toast.makeText(mContext, "You Don't Have Pick Image", Toast.LENGTH_SHORT).show();
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

        mFieldTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ListVenue field = (ListVenue) adapterView.getSelectedItem();
                mFieldType = field.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Log.d("FIELD TYPE", mFieldType + " ");
        mGrassTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ListVenue grass = (ListVenue) adapterView.getSelectedItem();
                mGrassType = grass.getId();
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
//                Toast.makeText(mContext, "Venue ID: "+ venue.getId()+",  Venue Name : "+ venue.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setupSpinnerTypeGrass();
        setupSpinnerTypeField();
        setSpinnerVenue();
        Log.d("FIELD TYPE1", mFieldType + " ");
    }

    private void setSpinnerFromHour() {
        ArrayAdapter fromDaySpinner = ArrayAdapter.createFromResource(mContext,
                R.array.startHour, R.layout.spinner_jadwal);
        fromDaySpinner.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);

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
        fromDaySpinner.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);

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

    private void setupSpinnerFromDay(){
        ArrayAdapter fromDaySpinner = ArrayAdapter.createFromResource(mContext,
                R.array.day, R.layout.spinner_jadwal);
        fromDaySpinner.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);

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
        untilDaySpinner.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);

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
                                        if (id !=null){
                                            if (idVenue == Integer.parseInt(id)){
                                                listVenue.add(new ListVenue(idVenue,nameVenue));
                                                mIdVenue = idVenue;
                                            }
                                        } else{
                                            listVenue.add(new ListVenue(idVenue, nameVenue));
                                        }
                                    }
                                    final ArrayAdapter<ListVenue> adapter = new ArrayAdapter<ListVenue>(mContext,
                                            R.layout.spinner_jadwal, listVenue);
                                    adapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
                                    spinnerVenue.setAdapter(adapter);
                                }

                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            progressDialog.dismiss();
                            popupAllert("Gagal Mengambil Data Tipe Lapangan");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.d("onFailure", t.getMessage());
                        popupAllert("No Internet Connection !!!");
                    }
                }
        );
    }

    private void setupSpinnerTypeGrass(){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Proses");
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.show();
        mApiService.typesGrass().enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful()){
                    int typeID;
                    String typeName;
                    progressDialog.dismiss();
                    List<FieldData> fieldTypeItems = response.body().getFieldData();
                    List<ListVenue> listSpinner = new ArrayList<>();
                    for (int i=0; i < fieldTypeItems.size(); i++){
                        typeID = fieldTypeItems.get(i).getId();
                        typeName = fieldTypeItems.get(i).getNama();
                        listSpinner.add(new ListVenue(typeID, typeName));
                        Log.d("DEBUGGING : ", typeID + " : " + listSpinner);
                    }
                    final ArrayAdapter<ListVenue> adapter = new ArrayAdapter<>(mContext, R.layout.spinner_jadwal, listSpinner);
                    adapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
                    mGrassTypeSpinner.setAdapter(adapter);
                } else {
                    progressDialog.dismiss();
                    popupAllert("Gagal Mengambil Data Tipe Rumput");
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("onFailure", t.getMessage());
                popupAllert("No Internet Connection !!!");
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
                    int typeID;
                    String typeName;
                    List<FieldData> fieldTypeItems = response.body().getFieldData();
                    List<ListVenue> listSpinner = new ArrayList<>();
                    for (int i=0; i < fieldTypeItems.size(); i++){
                        typeID = fieldTypeItems.get(i).getId();
                        typeName = fieldTypeItems.get(i).getNama();
                        listSpinner.add(new ListVenue(typeID, typeName));
                        mFieldType = fieldTypeItems.get(i).getId();
                        mFieldType = typeID;
                        Log.d("DEBUGGING : ", mFieldType + " : " + listSpinner);
                    }
                    final ArrayAdapter<ListVenue> adapter = new ArrayAdapter<>(mContext, R.layout.spinner_jadwal, listSpinner);
                    adapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
                    mFieldTypeSpinner.setAdapter(adapter);
                } else {
                    progressDialog.dismiss();
                    popupAllert("Gagal Mengambil Data Tipe Lapangan");
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("onFailure", t.getMessage());
                popupAllert("No Internet Connection !!!");
            }
        });
    }

    private void dialogTarif(){
        String titleText = "Tambah Tarif";
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_list_tariff, null);
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

        mFieldCostEditText.addTextChangedListener(new CurrencyTextWatcher(mFieldCostEditText));

        setupSpinnerFromDay();
        setupSpinnerUntilDay();
        setSpinnerFromHour();
        setSpinnerEndHour();

        dialogBuilder.setPositiveButton("Tambah", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fieldTarifs.add(new FieldTariff(mStartDay, mEndDay, mStartHour, mEndHour,
                        mFieldCostEditText.getText().toString().replaceAll("[Rp,.\\s]", "")));
                mAdapter.notifyDataSetChanged();
                jsonTarif= new Gson().toJson(fieldTarifs);
                Log.d("JSONTARIF ", jsonTarif);
                listViewTarif.setEmptyView(mEmptyViewTarif);
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

    private void addField(String stringJsonTarif){
        if(emptyCheck()){
            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setTitle("Proses");
            progressDialog.setMessage("Tunggu Sebentar");
            progressDialog.show();

            File image;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M || android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
                image = new File(fileImagePath);
            } else {
                image = new File(imagePath);
            }
            RequestBody mName = RequestBody.create(MultipartBody.FORM, mFieldNameEditText.getText().toString());
            RequestBody token = RequestBody.create(MultipartBody.FORM, mAppSession.getData(AppSession.TOKEN));
            RequestBody mDesc = RequestBody.create(MultipartBody.FORM, mDescriptionFieldEditText.getText().toString());
            RequestBody sizeField = RequestBody.create(MultipartBody.FORM, mFieldSizeEditText.getText().toString());
            RequestBody tariff = RequestBody.create(MultipartBody.FORM, stringJsonTarif);
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), image);
            final MultipartBody.Part partImage = MultipartBody.Part.createFormData("picture", image.getName(), requestBody);

            mApiService.createField(token, mName, mDesc, mIdVenue, mGrassType, mFieldType,
                    sizeField, tariff, partImage, null).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            if (jsonObject.getString("status").equals("Success")){
//                                String message = jsonObject.getString("message");
                                Toast.makeText(mContext, "Field Berhasil Disimpan !!", Toast.LENGTH_SHORT).show();
                                String field = jsonObject.getString("id");
                                Intent intent = new Intent(mContext, DetailFieldActivity.class);
                                intent.putExtra("id", field);
                                intent.putExtra("FLAG", "addField");
                                startActivity(intent);
                                finish();
                            } else {
                                progressDialog.dismiss();
                                String messageErr = jsonObject.getString("message");
                                popupAllert(messageErr);
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        progressDialog.dismiss();
                        switch (response.code()){
                            case 404:
                                popupAllert(getString(R.string.server_not_found));
                                break;
                            case 500:
                                popupAllert(getString(R.string.server_error));
                                break;
                            case 413:
                                popupAllert(getString(R.string.error_large));
                                break;
                            default:
                                popupAllert(getString(R.string.unknown_error));
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.d("onFailure", t.getMessage());
                    popupAllert("No Internet Connection !!!");
                }
            });
        }
    }

    private void updateField(){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Proses");
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.show();

        File image = null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M || android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            if (fileImagePath == null || fileImagePath.equals("")){
                image = null;
            } else {
                image = new File(fileImagePath);
            }
        } else {
            if (imageFile == null){
                image = null;
            } else {
                image = new File(imageFile.getPath());
            }
        }

        RequestBody mName = RequestBody.create(MultipartBody.FORM, mFieldNameEditText.getText().toString());
        RequestBody token = RequestBody.create(MultipartBody.FORM, mAppSession.getData(AppSession.TOKEN));
        RequestBody mDesc = RequestBody.create(MultipartBody.FORM, mDescriptionFieldEditText.getText().toString());
        RequestBody sizeField = RequestBody.create(MultipartBody.FORM, mFieldSizeEditText.getText().toString());

        MultipartBody.Part partImage = null;
        if (image != null){
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), image);
            partImage = MultipartBody.Part.createFormData("picture", image.getName(), requestBody);
        } else {
            RequestBody file = RequestBody.create(MediaType.parse(""), "");
            partImage = MultipartBody.Part.createFormData("picture", null, file);
        }

        mApiService.updateField(fieldId, token, mName, mDesc, mIdVenue, mGrassType,
                mFieldType, sizeField,null, partImage, null).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getString("status").equals("Success")){
//                            String message = jsonObject.getString("message");
                            Toast.makeText(mContext, "Field Berhasil Diperbarui!!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            progressDialog.dismiss();
                            String messageErr = jsonObject.getString("message");
                            popupAllert(messageErr);
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    progressDialog.dismiss();
                    switch (response.code()){
                        case 404:
                            popupAllert(getString(R.string.server_not_found));
                            break;
                        case 500:
                            popupAllert(getString(R.string.server_error));
                            break;
                        case 413:
                            popupAllert(getString(R.string.error_large));
                            break;
                        default:
                            popupAllert(getString(R.string.unknown_error));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("onFailure", t.getMessage());
                popupAllert("No Internet Connection !!!");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK && null != data){
            imageFile = data.getData();
            String[] projection = {MediaStore.Images.Media.DATA};

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M || android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
                Cursor cursor = getContentResolver().query(imageFile, projection, null, null, null);
                if (cursor!=null){
//                    try {
//                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageFile);
//                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
//                        bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    cursor.moveToFirst();
                    int index = cursor.getColumnIndex(projection[0]);
                    fileImagePath = cursor.getString(index);

                    File file = new File(fileImagePath);
                    String hasil = file.getName();
                    mResultPhoto.setText(hasil);
                    cursor.close();
                } else {
                    fileImagePath = imageFile.getPath();
                }
            } else {
                if (imageFile!=null){
                    imagePath = imageFile.getPath();
                    File file = new File(imagePath);
                    String result = file.getName();
                    mResultPhoto.setText(result);
                } else {
                    imageFile = Uri.EMPTY;
                }
            }
        }
    }
}
