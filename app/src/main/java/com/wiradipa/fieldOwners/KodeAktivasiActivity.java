package com.wiradipa.fieldOwners;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wiradipa.fieldOwners.ApiHelper.BaseApiService;
import com.wiradipa.fieldOwners.ApiHelper.UtilsApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KodeAktivasiActivity extends AppCompatActivity {

    EditText etActivationCode;
    Button mBtnActivationCode;

    Context mContext;
    BaseApiService mApiService;

    String resultNoHp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kode_aktivasi);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        mContext = this;
        mApiService = UtilsApi.getApiService();

        initComponents();

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            resultNoHp = extras.getString("resultNoHp");
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

    private void initComponents(){
        etActivationCode = (EditText) findViewById(R.id.et_activation_code);

        mBtnActivationCode = (Button) findViewById(R.id.next);
        mBtnActivationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activationCode();
            }
        });
    }

    private void activationCode(){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Proses");
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.show();
        mApiService.activationCode(resultNoHp, etActivationCode.getText().toString()).
                enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            progressDialog.dismiss();
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                if (jsonObject.getString("status").equals("success")){
                                    Toast.makeText(mContext, "Berhasil !!!", Toast.LENGTH_SHORT).show();
                                    String name = jsonObject.getString("username");
                                    Intent login = new Intent(mContext, MainActivity.class);
                                    login.putExtra("username", name);
                                    startActivity(login);

                                    finish();
                                } else {
                                    String errorMessage = jsonObject.getString("message");
                                    popupAllert(errorMessage);
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
//                        else {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                            builder.setTitle(R.string.dialog_title_error);
//                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    System.exit(0);
//                                    finish();
//                                }
//                            });
//                            builder.setMessage(R.string.dialog_message_connection_error);
//                            AlertDialog alert1 = builder.create();
//                            alert1.show();
//                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.e("debug", "onFailure : ERROR >" + t.toString());
                        Toast.makeText(mContext, t.toString(), Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle(R.string.dialog_title_error);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.exit(0);
                                finish();
                            }
                        });
                        builder.setMessage(R.string.dialog_message_connection_error);
                        AlertDialog alert1 = builder.create();
                        alert1.show();
                    }
                });
    }
}
