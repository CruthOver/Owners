package com.wiradipa.fieldOwners;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wiradipa.fieldOwners.Adapter.VenueAdapter;
import com.wiradipa.fieldOwners.ApiHelper.AppSession;
import com.wiradipa.fieldOwners.ApiHelper.BaseApiService;
import com.wiradipa.fieldOwners.ApiHelper.UtilsApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailFieldActivity extends AppCompatActivity {

    TextView mDetailHarga, mSizeField, mDescField, mTypeField, mGrassType;
    private ImageView iv;
    Button mBtnSewa;

    String id;

    FieldActivity fieldActivity;

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

        fieldActivity = new FieldActivity();

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            id = bundle.getString("idField");
        }

        initComponents();
        detailField();
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
                intent = new Intent(mContext, JadwalActivity.class);
                intent.putExtra("idField", id);
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
//                            Toast.makeText(mContext, "Berhasil", Toast.LENGTH_SHORT).show();
                            String url = jsonObject.getString("picture_url");
                            String sizeField = jsonObject.getString("pitch_size");
                            String typeGrass = jsonObject.getString("grass_type_name");
                            String typeField = jsonObject.getString("field_type_name");
                            String descField = jsonObject.getString("description");
                            String costField = "";

                            JSONArray jsonArray = jsonObject.getJSONArray("field_tariffs");
                            for (int i=0; i<jsonArray.length(); i++){
                                jsonObject = jsonArray.getJSONObject(i);
                                costField = jsonObject.getString("tariff");
                            }

                            Picasso.with(mContext).load("http://app.lapangbola.com" + url).into(iv);
                            mSizeField.setText(sizeField);
                            mGrassType.setText(typeGrass);
                            mTypeField.setText(typeField);
                            mDetailHarga.setText(costField + ".000");
                            mDescField.setText(descField);
                        }
                    } catch (IOException | JSONException e) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.action_edit:
                Toast.makeText(this, "Belum bisa Edit", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
