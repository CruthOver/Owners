package com.wiradipa.fieldOwners;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

    private boolean isPhoneNumberEmpty(String phoneNumber){
        return phoneNumber.equals("");
    }

    private boolean isFullNameEmpty(String fullName){
        return fullName.equals("");
    }

    private boolean isPasswordValid(String password){
        return password.length() >= 4;
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
                validRegister();
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

    private void validRegister(){
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

        boolean cancel = false;
        View focusView = null;

        // check valid username
        if (isUsernameEmpty(username)){
            etUsername.setError(getString(R.string.error_field_required));
            focusView = etUsername;
            cancel = true;
        }

        // check valid email
        if (TextUtils.isEmpty(email)){
            etEmail.setError(getString(R.string.error_field_required));
            focusView = etEmail;
            cancel = true;
        }

        if (isPasswordEmpty(password)){
            etPassword.setError(getString(R.string.error_field_required));
            focusView = etPassword;
            cancel = true;
        } else if (!isPasswordEmpty(password)){
            etPassword.setError(getString(R.string.error_invalid_password));
            focusView = etPassword;
            cancel = true;
        }

        if (isConfirmPasswordEmpty(confPassword)){
            etConfirmPassword.setError(getString(R.string.error_field_required));
            focusView = etConfirmPassword;
            cancel = true;
        }

        if (isFullNameEmpty(fullName)){
            etFullName.setError(getString(R.string.error_field_required));
            focusView = etFullName;
            cancel = true;
        }

        if (isPhoneNumberEmpty(phoneNumber)){
            etPhoneNumber.setError(getString(R.string.error_field_required));
            focusView = etPhoneNumber;
            cancel = true;
        }

        requestRegisterOwner();
    }

    private void requestRegisterOwner(){
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
