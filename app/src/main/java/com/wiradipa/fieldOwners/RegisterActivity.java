package com.wiradipa.fieldOwners;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

public class RegisterActivity extends AppCompatActivity {
    EditText etUsername, etEmail, etPassword, etConfirmPassword, etPhoneNumber, etFullName;
    Context mContext;
    BaseApiService mApiService;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        mContext =  this;
        mApiService = UtilsApi.getApiService();
        initComponents();

    }

    private boolean isUsernameEmpty(String username){
        return username.equals("");
    }

    private boolean isUsernameValid(String username){
        return username.length() >= 4;
    }

    private boolean isPhoneNumberEmpty(String phoneNumber){
        return phoneNumber.equals("");
    }

    private boolean isPhoneNumberValid(String phoneNumber){
        return phoneNumber.length() >= 10;
    }

    private boolean isFullNameEmpty(String fullName){
        return fullName.equals("");
    }

    private boolean isPasswordValid(String password){
        return password.length() >= 8;
    }

    private boolean isPasswordEmpty(String password){
        return password.equals("");
    }

    private boolean isConfirmPasswordEmpty(String confPassword){
        return confPassword.equals("");
    }

    private void initComponents(){
        etUsername = (EditText) findViewById(R.id.et_username);
        etEmail = (EditText)findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
        etConfirmPassword = (EditText) findViewById(R.id.et_confirm_password);
        etPhoneNumber = (EditText) findViewById(R.id.et_phone_number);
        etFullName = (EditText) findViewById(R.id.et_full_name);

        btnRegister = (Button) findViewById(R.id.register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestRegisterOwner();
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

    private boolean validRegister(){
        etUsername.setError(null);
        etEmail.setError(null);
        etPassword.setError(null);
        etConfirmPassword.setError(null);
        etPhoneNumber.setError(null);
        etFullName.setError(null);

        String username = etUsername.getText().toString();
        String email = etEmail.getText().toString();
        String phoneNumber = etPhoneNumber.getText().toString();
        String fullName = etFullName.getText().toString();
        String password = etPassword.getText().toString();
        String confPassword = etConfirmPassword.getText().toString();

        boolean status = true;

        // check valid username
        if (isUsernameEmpty(username)){
            etUsername.setError(getString(R.string.error_field_required));
            status = false;
        } else if (!isUsernameValid(username)){
            popupAllert(getString(R.string.error_invalid_username));
            status = false;
        }

        // check valid email
        if (TextUtils.isEmpty(email)){
            etEmail.setError(getString(R.string.error_field_required));
            status = false;
        }

        if (isPasswordEmpty(password)){
            etPassword.setError(getString(R.string.error_field_required));
            status = false;
        } else if (!isPasswordValid(password)){
            popupAllert(getString(R.string.error_invalid_password));
            status = false;
        }

        if (isConfirmPasswordEmpty(confPassword)){
            etConfirmPassword.setError(getString(R.string.error_field_required));
            status = false;
        }

        if (isFullNameEmpty(fullName)){
            etFullName.setError(getString(R.string.error_field_required));
            status = false;
        }

        if (isPhoneNumberEmpty(phoneNumber)){
            etPhoneNumber.setError(getString(R.string.error_field_required));
            status = false;
        } else if (!isPhoneNumberValid(phoneNumber)){
            popupAllert(getString(R.string.error_invalid_phone_number));
            status = false;
        }

        return status;
    }

    private void requestRegisterOwner(){
        if (validRegister()){
            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setTitle("Proses");
            progressDialog.setMessage("Tunggu Sebentar");
            progressDialog.show();
            mApiService.registerOwner(etUsername.getText().toString(), etEmail.getText().toString(),
                    etPassword.getText().toString(), etConfirmPassword.getText().toString(), etPhoneNumber.getText().toString(),
                    etFullName.getText().toString()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        progressDialog.dismiss();
                        Log.i("DEBUG ", "onResponse : SUCCESS");
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            if (jsonObject.getString("status").equals("success")) {
                                String noHpResult = jsonObject.getString("phone_number");
                                Intent intent = new Intent(mContext, KodeAktivasiActivity.class);
                                intent.putExtra("resultNoHp", noHpResult);
                                startActivity(intent);
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
                    Log.e("debug", "onFailure : ERROR > " + t.getMessage());
                    popupAllert(getString(R.string.dialog_message_connection_error));
                }
            });
        }
    }
}
