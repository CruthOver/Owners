package com.wiradipa.fieldOwners;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.wiradipa.fieldOwners.ApiHelper.AppSession;
import com.wiradipa.fieldOwners.ApiHelper.BaseApiService;
import com.wiradipa.fieldOwners.ApiHelper.UtilsApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailFieldActivity extends AppCompatActivity {

    TextView mDetailHarga, mSizeField, mDescField, mTypeField, mGrassType;
    private ImageView iv;
    Button mBtnSewa, mBtnEditTarif;
    static String id;

    Context mContext;
    BaseApiService mApiService;
    AppSession mAppSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_field);

        mContext = this;
        mApiService = UtilsApi.getApiService();
        mAppSession = new AppSession(mContext);

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            String flag = bundle.getString("FLAG");
            if (flag.equals("addField")){
                id = bundle.getString("id");
            } else {
                id = bundle.getString("idField");
            }
        }
        initComponents();
    }

    private void initComponents() {
        mDetailHarga = (TextView) findViewById(R.id.detail_harga);
        mSizeField = (TextView) findViewById(R.id.et_size_field_detail);
        mDescField = (TextView) findViewById(R.id.descr_field);
        mTypeField = (TextView) findViewById(R.id.jenis_lapangan);
        mGrassType = (TextView) findViewById(R.id.jenis_lantai);
        iv = (ImageView) findViewById(R.id.image_view_detail_lapang);

        mBtnSewa = (Button) findViewById(R.id.sewa_lapangan_bayar);
        mBtnSewa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(mContext, ScheduleActivity.class);
                intent.putExtra("idField", id);
                startActivity(intent);
            }
        });

        mBtnEditTarif = (Button) findViewById(R.id.edit_tarif);
        mBtnEditTarif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(mContext, ListEditTarifActivity.class);
                intent.putExtra("fieldId", id);
                startActivity(intent);
            }
        });
    }

    private void detailField(){
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
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getString("status").equals("Success")){
                            String url = jsonObject.getString("picture_url");
                            String sizeField = jsonObject.getString("pitch_size");
                            String typeGrass = jsonObject.getString("grass_type_name");
                            String typeField = jsonObject.getString("field_type_name");
                            String descField = jsonObject.getString("description");
                            int costField = jsonObject.getInt("min_tariff");

                            Glide.with(mContext).load("http://app.lapangbola.com" + url)
                                    .apply(new RequestOptions().placeholder(R.drawable.ic_image_black_24dp)
                                            .error(R.drawable.ic_image_black_24dp)).into(iv);
                            mSizeField.setText(sizeField);
                            mGrassType.setText(typeGrass);
                            mTypeField.setText(typeField);
                            mDetailHarga.setText(checkDigitMoney(costField));
                            mDescField.setText(descField);
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

    @Override
    protected void onResume() {
        super.onResume();
        detailField();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuID = item.getItemId();

        switch (menuID){
            case R.id.action_edit:
                Intent edit = new Intent(mContext, AddFieldActivity.class);
                edit.putExtra("fieldID", id);
                edit.putExtra("FLAG", "edit");
                startActivity(edit);
        }

        return super.onOptionsItemSelected(item);
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

    public String checkDigitMoney(int number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format((double) number);
    }
}