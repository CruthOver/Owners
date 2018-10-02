package com.wiradipa.fieldOwners;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wiradipa.fieldOwners.Adapter.JadwalAdapter;
import com.wiradipa.fieldOwners.Adapter.RecyclerTouchListener;
import com.wiradipa.fieldOwners.ApiHelper.AppSession;
import com.wiradipa.fieldOwners.ApiHelper.BaseApiService;
import com.wiradipa.fieldOwners.ApiHelper.UtilsApi;
import com.wiradipa.fieldOwners.Model.Jadwal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JadwalActivity extends AppCompatActivity {

    Context mContext;
    BaseApiService mApiService;
    AppSession mAppSession;

    String id, date;
    String startHour = "Belum ada Data";

    private RecyclerView listView;
    ArrayList<Jadwal> jadwal;
    private JadwalAdapter jadwalAdapter;
    Jadwal mJadwal;
    DatePickerDialog.OnDateSetListener mDatePicker;
    final Calendar myCalendar = Calendar.getInstance(Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);


        mContext = this;
        mApiService = UtilsApi.getApiService();
        mAppSession = new AppSession(mContext);
        listView = (RecyclerView) findViewById(R.id.list_jadwal);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, OrientationHelper.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());
        jadwalAdapter = new JadwalAdapter(mContext);
        listView.setAdapter(jadwalAdapter);

        jadwal = new ArrayList<>();
        mJadwal = new Jadwal();

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            id = bundle.getString("idField");
        }

        final TextView tanggal = (TextView) findViewById(R.id.tanggal);

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

        
        tanggal.setText(date);

        mDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                date = year + "-" + checkDigit(month)+ "-" + checkDigit(dayOfMonth);
                tanggal.setText(date);
                listJadwal();
                refreshActivity();
            }
        };

        tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(JadwalActivity.this,
                        R.style.Theme_AppCompat_DayNight_Dialog_MinWidth,
                        mDatePicker,year,month,day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.show();
            }
        });

        listJadwal();

        listView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), listView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (jadwalAdapter.getJadwalId(position) == 1){
                    createPeminjaman(jadwalAdapter.getStartHour(position), jadwalAdapter.getEndHour(position));
                } else{
                    cancelPeminjaman(jadwalAdapter.getStartHour(position), jadwalAdapter.getEndHour(position));
                }
                refreshActivity();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
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

        mApiService.listSchedule(mAppSession.getData(AppSession.TOKEN), date, id)
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
                        }
                        }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("debug","OnFailure: JadwalError >" + t.toString());
                        progressDialog.dismiss();
                        Toast.makeText(mContext, t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateJadwal(){

        mApiService.listSchedule(mAppSession.getData(AppSession.TOKEN), date, id)
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
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("debug","OnFailure: JadwalError >" + t.toString());
                        Toast.makeText(mContext, t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createPeminjaman(String startHour, String endHour){
        mApiService.createPeminjaman(mAppSession.getData(AppSession.TOKEN), date,
                startHour, endHour, id, 0)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                if (jsonObject.getString("status").equals("Success")){
                                    String message = jsonObject.getString("message");
                                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                                    jadwalAdapter.notifyDataSetChanged();
                                }else {
                                    String errorMsg = jsonObject.getString("message");
                                    Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                                    jadwalAdapter.notifyDataSetChanged();
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("debug","OnFailure: JadwalError >" + t.toString());
                        Toast.makeText(mContext, t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void refreshActivity(){
        finish();
        overridePendingTransition(0, 0);
        Intent intent = new Intent(JadwalActivity.this, JadwalActivity.class);
        intent.putExtra("date", date);
        intent.putExtra("idField", id);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void cancelPeminjaman(String startHour, String endHour){
        mApiService.cancelPeminjaman(mAppSession.getData(AppSession.TOKEN), date,
                startHour, endHour, id).enqueue(
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                if (jsonObject.getString("status").equals("Success")){
                                    Toast.makeText(mContext, "Berhasil", Toast.LENGTH_SHORT).show();
                                    jadwalAdapter.notifyDataSetChanged();
                                } else {
                                    String errorMsg = jsonObject.getString("message");
                                    Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                                    jadwalAdapter.notifyDataSetChanged();
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
}
