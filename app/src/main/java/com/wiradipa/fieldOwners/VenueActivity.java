package com.wiradipa.fieldOwners;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.wiradipa.fieldOwners.Adapter.RecyclerTouchListener;
import com.wiradipa.fieldOwners.Adapter.VenueAdapter;
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

public class VenueActivity extends AppCompatActivity {

    TextView mEmptyTextView;
    RecyclerView recyclerView;
    private VenueAdapter mAdapter;

    Context mContext;
    BaseApiService mApiService;
    AppSession mAppSession;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue);
        initToolbar();

        mContext = this;
        mApiService = UtilsApi.getApiService();
        mAppSession = new AppSession(mContext);

        recyclerView = findViewById(R.id.list_venue);
        mEmptyTextView = findViewById(R.id.kosong);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new VenueAdapter(mContext);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView,
                new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent detailVenue;
                detailVenue = new Intent(mContext, DetailVenueActivity.class);
                detailVenue.putExtra("idVenue", mAdapter.getVenueId(position));
                detailVenue.putExtra("FLAG", "VenueActivity");
                view.getContext().startActivity(detailVenue);
            }

            @Override
            public void onLongClick(View view, int position) {
                Intent detailVenue;
                detailVenue = new Intent(mContext, DetailVenueActivity.class);
                detailVenue.putExtra("idVenue", mAdapter.getVenueId(position));
                detailVenue.putExtra("FLAG", "VenueActivity");
                startActivity(detailVenue);
            }
        }));

        ImageView add = findViewById(R.id.tambah_venue);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VenueActivity.this, AddVenueActivity.class);
                startActivity(intent);
            }
        });
    }

    private void listVenue(){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Proses");
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.show();

        mApiService.listVenue(mAppSession.getData(AppSession.TOKEN)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getString("status").equals("Success")){
                            mAdapter.parsingData(jsonObject.getJSONArray("data"));
                            if (mAdapter.getItemCount() == 0){
                                mEmptyTextView.setVisibility(View.VISIBLE);
                            } else {
                                recyclerView.setAdapter(mAdapter);
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            progressDialog.dismiss();
                            String errMsg = jsonObject.getString("message");
                            Toast.makeText(mContext, errMsg, Toast.LENGTH_SHORT).show();
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
        listVenue();
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

        }
    }
}