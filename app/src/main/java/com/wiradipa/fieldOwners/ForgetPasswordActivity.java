package com.wiradipa.fieldOwners;

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

public class ForgetPasswordActivity extends AppCompatActivity {

    EditText etEmailForgot;
    Button btnForgot;

    Context mContext;
    BaseApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mContext = this;
        mApiService = UtilsApi.getApiService();

        etEmailForgot = (EditText) findViewById(R.id.email_forgot);
        btnForgot = (Button) findViewById(R.id.btn_forgot);

        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassword();
            }
        });
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

    private void forgotPassword(){
        mApiService.forgotPasswordOwner(etEmailForgot.getText().toString()).
                enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                if (jsonObject.getString("status").equals("success")){
                                    String message = jsonObject.getString("message");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                    builder.setTitle("Pesan!");
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(mContext, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                    builder.setMessage("Password baru anda telah kami kirim melalui SMS");
                                    AlertDialog alert1 = builder.create();
                                    alert1.show();

                                } else {
                                    String errMsg = jsonObject.getString("message");
                                    popupAllert(errMsg);
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

                        Log.e("debug", "onFailure : ERROR > " + t.getMessage());
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
