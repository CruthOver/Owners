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

public class ActivationCodeActivity extends AppCompatActivity {

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

    private boolean isCodeActivationEmpty(String codeActivation){
        return codeActivation.equals("");
    }

    private boolean isCodeActivationValid(String codeActivation){
        return codeActivation.length() >= 5;
    }

    private boolean checkCode(){
        boolean status = true;
        etActivationCode.setError(null);

        String activationCode = etActivationCode.getText().toString();

        if (isCodeActivationEmpty(activationCode)){
            etActivationCode.setError(getString(R.string.error_field_required));
            status = false;
        } else if (!isCodeActivationValid(activationCode)){
            popupAllert(getString(R.string.error_invalid_activation_code));
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
        if (checkCode()){
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
                                Intent login = new Intent(mContext, LoginActivity.class);
                                startActivity(login);
                                finish();
                            } else {
                                String errorMessage = jsonObject.getString("message");
                                popupAllert(errorMessage);
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
                    Log.e("debug", "onFailure : ERROR >" + t.toString());
                    popupAllert(getString(R.string.dialog_message_connection_error));
                }
            });
        }
    }
}