package com.wiradipa.fieldOwners;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.wiradipa.fieldOwners.Adapter.FieldAdapter;
import com.wiradipa.fieldOwners.Adapter.RecyclerTouchListener;
import com.wiradipa.fieldOwners.ApiHelper.AppSession;
import com.wiradipa.fieldOwners.ApiHelper.BaseApiService;
import com.wiradipa.fieldOwners.ApiHelper.UtilsApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field);

        mContext =  this;
        mApiService = UtilsApi.getApiService();
        mAppSession = new AppSession(mContext);

        recyclerView = (RecyclerView) findViewById(R.id.list_field);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new FieldAdapter(mContext);

        mEmptyData = findViewById(R.id.kosong);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent detailField;
                detailField = new Intent(mContext, DetailFieldActivity.class);
                detailField.putExtra("idField", mAdapter.getFieldId(position));
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

//        if (mAdapter.getItemCount() == 0){
//            mEmptyData.setVisibility(View.VISIBLE);
//        } else {
//            recyclerView.setAdapter(mAdapter);
//        }

        listMyFields();

        imgAddField = (ImageView) findViewById(R.id.add_field);
        imgAddField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAddField = new Intent(mContext, AddFieldActivity.class);
                startActivity(intentAddField);
            }
        });
    }

    private void listMyFields(){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Proses");
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.show();

        mApiService.listField(mAppSession.getData(AppSession.TOKEN))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            progressDialog.dismiss();
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                if (jsonObject.getString("status").equals("Success")){
                                    mAdapter.parsingData(jsonObject.getJSONArray("data"));
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    String errMsg = jsonObject.getString("message");
                                    Toast.makeText(mContext, errMsg, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("debug", "OnFailure: ERROR > "+ t.toString());
                        progressDialog.dismiss();
                        Toast.makeText(mContext, t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
