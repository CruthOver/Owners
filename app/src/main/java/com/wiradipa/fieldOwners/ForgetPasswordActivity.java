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

    private boolean isPhoneNumberEmpty(String phoneNumber){
        return phoneNumber.equals("");
    }

    private boolean isPhoneNumberValid(String phoneNumber){
        return phoneNumber.length() >= 10;
    }

    private boolean checkValid(){
        boolean status = true;
        etEmailForgot.setError(null);

        String phoneNumber = etEmailForgot.getText().toString();

        if (isPhoneNumberEmpty(phoneNumber)){
            etEmailForgot.setError(getString(R.string.error_field_required));
            status = false;
        } else if (!isPhoneNumberValid(phoneNumber)){
            popupAllert(getString(R.string.error_invalid_phone_number));
            status = false;
        }

        return status;
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
        if (checkValid()){
            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setTitle("Proses");
            progressDialog.setMessage("Tunggu Sebentar");
            progressDialog.show();
            mApiService.forgotPasswordOwner(etEmailForgot.getText().toString()).
                    enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        progressDialog.dismiss();
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
                    } else {
                        progressDialog.dismiss();
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
                    progressDialog.dismiss();
                    Log.e("debug", "onFailure : ERROR > " + t.getMessage());
                    popupAllert(getString(R.string.dialog_message_connection_error));
                }
            });
        }
    }
}
