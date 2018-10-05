package com.wiradipa.fieldOwners;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.wiradipa.fieldOwners.Adapter.ListEditTarifAdapter;
import com.wiradipa.fieldOwners.Adapter.RecyclerTouchListener;
import com.wiradipa.fieldOwners.ApiHelper.AppSession;
import com.wiradipa.fieldOwners.ApiHelper.BaseApiService;
import com.wiradipa.fieldOwners.ApiHelper.UtilsApi;
import com.wiradipa.fieldOwners.Model.Jadwal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListEditTarifActivity extends AppCompatActivity {

    Context mContext;
    BaseApiService mApiService;
    AppSession mAppSession;

    ListEditTarifAdapter adapter;
    RecyclerView recyclerView;

    String id;
    int mStartDay;
    Spinner spinnerDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_edit_tarif);

        mContext = this;
        mApiService = UtilsApi.getApiService();
        mAppSession = new AppSession(mContext);

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            id = bundle.getString("fieldId");
        }

        spinnerDay = (Spinner) findViewById(R.id.edit_tarif_hari);
        adapter = new ListEditTarifAdapter(mContext);
        recyclerView = (RecyclerView) findViewById(R.id.list_edit_tarif);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);

        setSpinnerDay();
        getDataTarif();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(mContext, "YEEEEAAAAHHH", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void setSpinnerDay(){
        ArrayAdapter fromDaySpinner = ArrayAdapter.createFromResource(mContext,
                R.array.day, R.layout.spinner_jadwal);

        fromDaySpinner.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        spinnerDay.setAdapter(fromDaySpinner);
        spinnerDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getItemAtPosition(i);
                if (!TextUtils.isEmpty(selection)){
                    if (selection.equals(getString(R.string.optionDay))){
                        Toast.makeText(mContext, "Silahkan Pilih Hari", Toast.LENGTH_SHORT).show();
                    }
                    if (selection.equals(getString(R.string.monday))){
                        mStartDay = 1; //monday
                        getDataTarif("Senin");
                    } else if(selection.equals(getString(R.string.tuesday))){
                        mStartDay = 2; //tuesday
                        getDataTarif("Selasa");
                    } else if (selection.equals(getString(R.string.wednesday))){
                        mStartDay = 3; //wednesday
                        getDataTarif("Rabu");
                    } else if (selection.equals(getString(R.string.thursday))){
                        mStartDay = 4; //thursday
                        getDataTarif("Kamis");
                    } else if (selection.equals(getString(R.string.friday))){
                        mStartDay = 5; //friday
                        getDataTarif("Jumat");
                    } else if (selection.equals(getString(R.string.saturday))){
                        mStartDay = 6; //saturday
                        getDataTarif("Sabtu");
                    } else if (selection.equals(getString(R.string.sunday))){
                        mStartDay = 0; //sunday
                        getDataTarif("Minggu");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(mContext, "Hari belum dipilih", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDataTarif(){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Proses");
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.show();

        mApiService.detailField(id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    progressDialog.dismiss();
                    try {
                        JSONObject data = new JSONObject(response.body().string());
                        if (data.getString("status").equals("Success")){
                            adapter.ParsingData(data.getJSONArray("field_tariffs"));
                            adapter.notifyDataSetChanged();
                        }else {
                            String errorMsg = data.getString("message");
                            Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("debug","OnFailure: JadwalError >" + t.toString());
            }
        });
    }

    private void getDataTarif(final String day){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Proses");
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.show();

        mApiService.detailField(id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    progressDialog.dismiss();
                    try {
                        JSONObject data = new JSONObject(response.body().string());
                        if (data.getString("status").equals("Success")){
                            adapter.ParsingData(data.getJSONArray("field_tariffs"), day);
                            adapter.notifyDataSetChanged();
                        }else {
                            String errorMsg = data.getString("message");
                            Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("debug","OnFailure: JadwalError >" + t.toString());
            }
        });
    }
}
