package com.wiradipa.fieldOwners;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wiradipa.fieldOwners.Adapter.FieldAdapter;
import com.wiradipa.fieldOwners.Adapter.RecyclerTouchListener;
import com.wiradipa.fieldOwners.ApiHelper.AppSession;
import com.wiradipa.fieldOwners.ApiHelper.BaseApiService;
import com.wiradipa.fieldOwners.ApiHelper.UtilsApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FieldActivity extends AppCompatActivity {

    ImageView imgAddField;
    TextView mEmptyData;

    Context mContext;
    BaseApiService mApiService;
    AppSession mAppSession;

    RecyclerView recyclerView;
    FieldAdapter mAdapter;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field);

        mContext =  this;
        mApiService = UtilsApi.getApiService();
        mAppSession = new AppSession(mContext);

        mEmptyData = findViewById(R.id.kosong);
        recyclerView = (RecyclerView) findViewById(R.id.list_field);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new FieldAdapter(mContext);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView,
                new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent detailField;
                detailField = new Intent(mContext, DetailFieldActivity.class);
                detailField.putExtra("idField", mAdapter.getFieldId(position));
                detailField.putExtra("FLAG", "FieldActivity");
                view.getContext().startActivity(detailField);
            }

            @Override
            public void onLongClick(View view, int position) {
                Intent detailField;
                detailField = new Intent(mContext, DetailFieldActivity.class);
                detailField.putExtra("idField", mAdapter.getFieldId(position));
                view.getContext().startActivity(detailField);
            }
        }));

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            id = bundle.getString("idVenueDetail");
        }

        imgAddField = (ImageView) findViewById(R.id.add_field);
        imgAddField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAddField = new Intent(mContext, AddFieldActivity.class);
                intentAddField.putExtra("idVenue", id);
                startActivity(intentAddField);
            }
        });
    }

    private void listMyField(){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Proses");
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.show();

        mApiService.listField(mAppSession.getData(AppSession.TOKEN), id)
                .enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getString("status").equals("Success")){
                            mAdapter.parsingData(jsonObject.getJSONArray("data"));
                            if (mAdapter.getItemCount() == 0){
                                mEmptyData.setVisibility(View.VISIBLE);
                            } else {
                                recyclerView.setAdapter(mAdapter);
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            String errMsg = jsonObject.getString("message");
                            popupAllert(errMsg);
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
                Log.e("debug", "OnFailure: ERROR > "+ t.toString());
                progressDialog.dismiss();
                popupAllert("No Internet Connection !!!");
            }
        });
    }

    private void listMyFields(){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Proses");
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.show();

        mApiService.listFields(mAppSession.getData(AppSession.TOKEN))
                .enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getString("status").equals("Success")){
                            mAdapter.parsingData(jsonObject.getJSONArray("data"));
                            if (mAdapter.getItemCount() == 0){
                                mEmptyData.setVisibility(View.VISIBLE);
                            } else {
                                recyclerView.setAdapter(mAdapter);
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            String errMsg = jsonObject.getString("message");
                            popupAllert(errMsg);
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
                Log.e("debug", "OnFailure: ERROR > "+ t.toString());
                progressDialog.dismiss();
                popupAllert("No Internet Connection !!!");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.clear();
        if (id == null){
            listMyFields();
        } else {
            listMyField();
        }
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
}