package com.wiradipa.fieldOwners;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wiradipa.fieldOwners.ApiHelper.AppSession;
import com.wiradipa.fieldOwners.ApiHelper.BaseApiService;
import com.wiradipa.fieldOwners.ApiHelper.UtilsApi;
import com.wiradipa.fieldOwners.Model.ListVenue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFieldRentalActivity extends AppCompatActivity {

    Spinner spinnerStartHour, spinnerDuration, spinnerField
            , spinnerVenue,spinnerDay;
    EditText etNameRenter, etPhoneNumberRenter;
    TextView tvDate, tvKalender;
    Button btnAddRentals, btnCancel;

    Context mContext;
    BaseApiService mApiService;
    AppSession mAppSession;

    int mIdVenue, mIdField, mStartHour, mEndHour;
    String date,mDay;

    ListVenue venue;
    DatePickerDialog.OnDateSetListener mDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_field_rental);

        mContext = this;
        mApiService = UtilsApi.getApiService();
        mAppSession = new AppSession(mContext);

        initComponents();
    }

    private boolean emptyCheck(){
        boolean status = true;
        etNameRenter.setError(null);
        etPhoneNumberRenter.setError(null);

        if(isStringEmpty(etNameRenter.getText().toString())){
            etNameRenter.setError(getString(R.string.error_field_required));
            status = false;
        }
        if(isStringEmpty(etPhoneNumberRenter.getText().toString())){
            etPhoneNumberRenter.setError(getString(R.string.error_field_required));
            status = false;
        }

        if (spinnerStartHour.getSelectedItemPosition() == 0 || spinnerDuration.getSelectedItemPosition() == 0){
            popupAllert("Jam Mulai belum dipilih");
        }

        if (spinnerStartHour.getSelectedItemPosition() == 0 || spinnerDuration.getSelectedItemPosition() == 0){
            popupAllert("Waktu Durasi Main belum dipilih");
        }



        return status;
    }

    private boolean isStringEmpty(String x){
        return x.equals("");
    }

    private void initComponents(){
        spinnerStartHour = (Spinner) findViewById(R.id.spinner_start_hour);
        spinnerDuration = (Spinner) findViewById(R.id.spinner_duration);
        spinnerField = (Spinner) findViewById(R.id.spinner_field);
        spinnerVenue = (Spinner) findViewById(R.id.spinner_venue);
        spinnerDay = (Spinner) findViewById(R.id.spinner_day_booking);

        etNameRenter = (EditText) findViewById(R.id.name_renter);
        etPhoneNumberRenter = (EditText) findViewById(R.id.phoneNumberRenter);

        tvDate = (TextView) findViewById(R.id.tv_date);
        tvKalender = (TextView) findViewById(R.id.kalendar);

        btnAddRentals = (Button) findViewById(R.id.add_new_rental);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        btnAddRentals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFieldRental();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        spinnerVenue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                venue = (ListVenue) adapterView.getSelectedItem();
                mIdVenue = venue.getId();
                setSpinnerField();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                venue = (ListVenue) adapterView.getSelectedItem();
                mIdField = venue.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setSpinnerVenue();
        setSpinnerDate();
        setSpinnerStartHour();
        setSpinnerDuration();
        setSpinnerKalender();
        setSpinnerDay();
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
                                        listVenue.add(new ListVenue(idVenue,nameVenue));
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

    private void setSpinnerField(){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Proses");
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.show();

        mApiService.listField(mAppSession.getData(AppSession.TOKEN), String.valueOf(mIdVenue)).enqueue(
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            progressDialog.dismiss();
                            try {
                                int idField;
                                String nameField = "";
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                if (jsonObject.getString("status").equals("Success")){
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    ArrayList<ListVenue> listVenue = new ArrayList<>();
                                    for (int i=0; i<jsonArray.length(); i++){
                                        jsonObject = jsonArray.getJSONObject(i);
                                        idField = jsonObject.getInt("id");
                                        nameField = jsonObject.getString("name");
                                        listVenue.add(new ListVenue(idField, nameField));
                                        mIdField = idField;
                                    }
                                    final ArrayAdapter<ListVenue> adapter = new ArrayAdapter<ListVenue>(mContext,
                                            R.layout.spinner_jadwal, listVenue);
                                    adapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1
                                    );
                                    spinnerField.setAdapter(adapter);
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
                        Toast.makeText(mContext, "BLALBLABLAB", Toast.LENGTH_SHORT).show();
                    }
                }
        );
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

    private void setSpinnerDate(){
        String tempDate = null;

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        month = month + 1;
        date = year + "-" + checkDigit(month) + "-" + checkDigit(day) ;

        tempDate = getIntent().getStringExtra("date");
        if(tempDate!=null){
            date = tempDate;
        }else{
            date = year + "-" + checkDigit(month) + "-" + checkDigit(day) ;
        }

        tvDate.setText(date);
        mDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                date = year + "-" + checkDigit(month)+ "-" + checkDigit(dayOfMonth);
                tvDate.setText(date);
            }
        };

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(mContext,
                        R.style.Theme_AppCompat_DayNight_Dialog_MinWidth,
                        mDatePicker,year,month,day);
                dialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.show();
            }
        });
    }
    private void setSpinnerKalender(){
        String tempDate = null;

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        month = month + 1;
        date = year + "-" + checkDigit(month) + "-" + checkDigit(day) ;

        tempDate = getIntent().getStringExtra("date");
        if(tempDate!=null){
            date = tempDate;
        }else{
            date = year + "-" + checkDigit(month) + "-" + checkDigit(day) ;
        }
        mDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                date = year + "-" + checkDigit(month)+ "-" + checkDigit(dayOfMonth);
                tvKalender.setText(date);
            }
        };

        tvKalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(mContext,
                        R.style.Theme_AppCompat_DayNight_Dialog_MinWidth,
                        mDatePicker,year,month,day);
                dialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.show();
            }
        });
    }

    public String checkDigit(int number)
    {
        return number<=9?"0"+number:String.valueOf(number);
    }

    private void setSpinnerStartHour() {
        ArrayAdapter fromDaySpinner = ArrayAdapter.createFromResource(mContext,
                R.array.startHour, R.layout.spinner_jadwal);
        fromDaySpinner.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);

        spinnerStartHour.setAdapter(fromDaySpinner);
        spinnerStartHour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getItemAtPosition(i);
                if (!TextUtils.isEmpty(selection)){
                    if (selection.equals("0")){
                        mStartHour = 0;
                    } else if (selection.equals("1")){
                        mStartHour = 1;
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
                Toast.makeText(mContext, "Jam Main belum dipilih", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSpinnerDuration() {
        ArrayAdapter fromDaySpinner = ArrayAdapter.createFromResource(mContext,
                R.array.endHour, R.layout.spinner_jadwal);
        fromDaySpinner.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);

        spinnerDuration.setAdapter(fromDaySpinner);
        spinnerDuration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getItemAtPosition(i);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals("1")) {
                        mEndHour = 1;
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
                    } else if (selection.equals("22")) {
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
                Toast.makeText(mContext, "Waktu Durasi belum dipilih", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSpinnerDay() {
        ArrayAdapter fromDaySpinner = ArrayAdapter.createFromResource(mContext,
                R.array.day, R.layout.spinner_jadwal);
        fromDaySpinner.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);

        spinnerDay.setAdapter(fromDaySpinner);
        spinnerDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getItemAtPosition(i);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(R.string.optionDay)) {
                        mDay = "--- pilih hari ---";
                    } else if (selection.equals(R.string.monday)) {
                        mDay = "senin";
                    } else if (selection.equals(R.string.tuesday)) {
                        mDay = "selasa";
                    } else if (selection.equals(R.string.wednesday)) {
                        mDay = "rabu";
                    } else if (selection.equals(R.string.thursday)) {
                        mDay = "kamis";
                    } else if (selection.equals(R.string.friday)) {
                        mDay = "jumat";
                    } else if (selection.equals(R.string.saturday)) {
                        mDay = "sabtu";
                    } else if (selection.equals(R.string.sunday)) {
                        mDay = "minggu";
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(mContext, "Jam Main belum dipilih", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void addFieldRental(){
        if (emptyCheck()){
            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setTitle("Proses");
            progressDialog.setMessage("Tunggu Sebentar");
            progressDialog.show();

            Log.d("ID FIELD", mIdField + "");
            Log.d("ID FIELD", mIdVenue + "");
            Log.d("ID FIELD", mStartHour + "");
            Log.d("ID FIELD", mEndHour + "");

            mApiService.addFieldRentalManually(mAppSession.getData(AppSession.TOKEN), date, mStartHour, mEndHour,
                    etNameRenter.getText().toString(), etPhoneNumberRenter.getText().toString(), mIdVenue, mIdField)
                    .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            progressDialog.dismiss();
                            try {
                                JSONObject data = new JSONObject(response.body().string());
                                if (data.getString("status").equals("Success")){
                                    Toast.makeText(mContext, "Data Penyewaan Berhasil Disimpan !!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    String messageErr = data.getString("message");
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
                }
            );
        }
    }
}
