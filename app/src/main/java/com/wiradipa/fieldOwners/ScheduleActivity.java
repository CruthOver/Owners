package com.wiradipa.fieldOwners;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wiradipa.fieldOwners.Adapter.JadwalAdapter;
import com.wiradipa.fieldOwners.Adapter.RecyclerTouchListener;
import com.wiradipa.fieldOwners.ApiHelper.AppSession;
import com.wiradipa.fieldOwners.ApiHelper.BaseApiService;
import com.wiradipa.fieldOwners.ApiHelper.UtilsApi;
import com.wiradipa.fieldOwners.Model.Jadwal;
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

public class ScheduleActivity extends AppCompatActivity {

    Context mContext;
    BaseApiService mApiService;
    AppSession mAppSession;

    String id, date;
    String startHour = "Belum ada Data";
    int mIdField;
    Spinner spinnerListField;
    ImageView imgAddRental;
    TextView tanggal;

    RecyclerView recyclerView;
    ArrayList<Jadwal> jadwal;
    ListVenue field;
    private JadwalAdapter jadwalAdapter;
    DatePickerDialog.OnDateSetListener mDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        initToolbar();

        mContext = this;
        mApiService = UtilsApi.getApiService();
        mAppSession = new AppSession(mContext);

        initComponents();

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            id = bundle.getString("idField");
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        jadwalAdapter = new JadwalAdapter(mContext);
        recyclerView.setAdapter(jadwalAdapter);

        jadwal = new ArrayList<>();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView,
                new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (jadwalAdapter.getJadwalId(position) == 1){
                    createPeminjaman(jadwalAdapter.getStartHour(position), jadwalAdapter.getEndHour(position));
//                    jadwalAdapter.clearData();
//                    listJadwal();
                } else{
                    cancelPeminjaman(jadwalAdapter.getStartHour(position), jadwalAdapter.getEndHour(position));
//                    jadwalAdapter.clearData();
//                    listJadwal();
                }
                refreshActivity();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void initComponents(){
        spinnerListField = (Spinner) findViewById(R.id.spinner_id_field);
        recyclerView = (RecyclerView) findViewById(R.id.list_jadwal);
        tanggal = (TextView) findViewById(R.id.tanggal);

        imgAddRental = (ImageView) findViewById(R.id.add_jadwal);
        imgAddRental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddFieldRentalActivity.class);
                startActivity(intent);
            }
        });

        String tempDate = null;
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        String weekDay="";


        //format hari
        if (Calendar.MONDAY == dayOfWeek) {
            weekDay = "Senin";
        } else if (Calendar.TUESDAY == dayOfWeek) {
            weekDay = "Selasa";
        } else if (Calendar.WEDNESDAY == dayOfWeek) {
            weekDay = "Rabu";
        } else if (Calendar.THURSDAY == dayOfWeek) {
            weekDay = "Kamis";
        } else if (Calendar.FRIDAY == dayOfWeek) {
            weekDay = "Jumat";
        } else if (Calendar.SATURDAY == dayOfWeek) {
            weekDay = "Sabtu";
        } else if (Calendar.SUNDAY == dayOfWeek) {
            weekDay = "Minggu";
        }
        //END format hari

        month = month + 1;
        date = weekDay +", "+checkDigit(day)+"-"+checkDigit(month)+ "-" + year;

        tempDate = getIntent().getStringExtra("date");
        if(tempDate!=null){
            date = tempDate;
        }else{
            date = weekDay +", "+checkDigit(day)+"-"+checkDigit(month)+ "-" + year;
        }

        tanggal.setText(date);
        final String mWeekDay = weekDay;
        mDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                date = mWeekDay +", "+checkDigit(dayOfMonth)+"-"+checkDigit(month)+ "-" + year;
                tanggal.setText(date);
                jadwalAdapter.clearData();
                listJadwal();
//                refreshActivity();

            }
        };

        tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(ScheduleActivity.this,
                        R.style.Theme_AppCompat_DayNight_Dialog_MinWidth,
                        mDatePicker,year,month,day);
                dialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.show();
            }
        });
        setSpinnerField();
        spinnerListField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                field = (ListVenue) adapterView.getSelectedItem();
                mIdField = field.getId();
                jadwalAdapter.clearData();
                listJadwal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void setSpinnerField(){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Proses");
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.show();

        mApiService.listFields(mAppSession.getData(AppSession.TOKEN)).enqueue(
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
                                        if (id != null){
                                            if (idField == Integer.parseInt(id)){
                                                listVenue.add(new ListVenue(idField, nameField));
                                                mIdField = idField;
                                            }
                                        } else{
                                            listVenue.add(new ListVenue(idField, nameField));
                                        }
                                    }
                                    final ArrayAdapter<ListVenue> adapter = new ArrayAdapter<ListVenue>(mContext,
                                            R.layout.spinner_jadwal, listVenue);
                                    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                                    spinnerListField.setAdapter(adapter);
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

    public String checkDigit(int number)
    {
        return number<=9?"0"+number:String.valueOf(number);
    }

    private void listJadwal(){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Proses");
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.show();

        mApiService.listSchedule(mAppSession.getData(AppSession.TOKEN), date, mIdField)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            progressDialog.dismiss();
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                if (jsonObject.getString("status").equals("Success")){
                                    jadwalAdapter.ParsingData(jsonObject.getJSONArray("data"));
                                    jadwalAdapter.notifyDataSetChanged();
                                }else {
                                    String errorMsg = jsonObject.getString("message");
                                    Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                                    jadwalAdapter.notifyDataSetChanged();
                                }
                            } catch (JSONException | IOException e) {
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
                        Log.e("debug","OnFailure: JadwalError >" + t.toString());
                        progressDialog.dismiss();
                        popupAllert("No Internet Connection !!!");
                    }
                });
    }

    private void updateJadwal(){
        mApiService.listSchedule(mAppSession.getData(AppSession.TOKEN), date, mIdField)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                if (jsonObject.getString("status").equals("Success")){
                                    jadwalAdapter.updateData(jsonObject.getJSONArray("data"));
                                    jadwalAdapter.notifyDataSetChanged();
                                }else {
                                    String errorMsg = jsonObject.getString("message");
                                    Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                                    jadwalAdapter.notifyDataSetChanged();
                                }
                            } catch (JSONException | IOException e) {
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
                        Log.e("debug","OnFailure: JadwalError >" + t.toString());
                        popupAllert("No Internet Connection !!!");
                    }
                });
    }

    private void createPeminjaman(String startHour, String endHour){
        mApiService.createPeminjaman(mAppSession.getData(AppSession.TOKEN), date,
                startHour, endHour, mIdField, 0)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                if (jsonObject.getString("status").equals("Success")){
//                                    String message = jsonObject.getString("message");
                                    Toast.makeText(mContext, "Pemesanan Berhasil", Toast.LENGTH_SHORT).show();
                                    jadwalAdapter.notifyDataSetChanged();
                                }else {
                                    String errorMsg = jsonObject.getString("message");
                                    Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                                    jadwalAdapter.notifyDataSetChanged();
                                }
                            } catch (JSONException | IOException e) {
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
                        Log.e("debug","OnFailure: JadwalError >" + t.toString());
                        popupAllert("No Internet Connection !!!");
                    }
                });
    }

    public void refreshActivity(){
        finish();
        overridePendingTransition(0, 0);
        Intent intent = new Intent(ScheduleActivity.this, ScheduleActivity.class);
        intent.putExtra("date", date);
        intent.putExtra("idField", id);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void cancelPeminjaman(String startHour, String endHour){
        mApiService.cancelPeminjaman(mAppSession.getData(AppSession.TOKEN), date,
                startHour, endHour, mIdField).enqueue(
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                if (jsonObject.getString("status").equals("Success")){
                                    Toast.makeText(mContext, "Pemesanan Dibatalkan", Toast.LENGTH_SHORT).show();
                                    jadwalAdapter.notifyDataSetChanged();
                                } else {
                                    String errorMsg = jsonObject.getString("message");
                                    Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                                    jadwalAdapter.notifyDataSetChanged();
                                }
                            } catch (JSONException | IOException e) {
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
                        Log.e("debug","OnFailure: JadwalError >" + t.toString());
                        popupAllert("No Internet Connection !!!");
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

    private void initToolbar(){
        Toolbar mToolbar = (Toolbar) findViewById(R.id.app_bar);
        TextView mTvTitle = (TextView) findViewById(R.id.textViewTitle);

        //Init mToolbar
        if (mToolbar != null){
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Load title
        try {
            ActivityInfo activityInfo = getPackageManager().getActivityInfo(
                    getComponentName(), PackageManager.GET_META_DATA);
            String title = activityInfo.loadLabel(getPackageManager())
                    .toString();

            if (mTvTitle != null){
                mTvTitle.setText(title);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        initComponents();
        super.onResume();
    }
}