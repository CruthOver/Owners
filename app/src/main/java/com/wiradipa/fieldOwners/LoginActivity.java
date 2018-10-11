package com.wiradipa.fieldOwners;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    TextView tvForgotPassword, tvRegister;

    Context mContext;
    BaseApiService mApiService;
    AppSession mAppSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        mContext = this;
        mApiService = UtilsApi.getApiService();
        mAppSession = new AppSession(mContext);
        initComponents();

        if (mAppSession.isLogin()){
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean isUsernameEmpty(String username){
        return username.equals("");
    }

    private boolean isUsernameValid(String username){
        return username.length() >= 4;
    }

    private boolean isPasswordEmpty(String password){
        return password.equals("");
    }

    private boolean isPasswordValid(String password){
        return password.length() >= 8;
    }

    private void initComponents(){
        etUsername = (EditText) findViewById(R.id.et_username_login);
        etPassword = (EditText) findViewById(R.id.et_password_login);
        btnLogin = (Button) findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestLogin();
            }
        });

        tvRegister = (TextView) findViewById(R.id.register_account);
        SpannableString content = new SpannableString(" di sini");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvRegister.setText(content);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        tvForgotPassword = (TextView) findViewById(R.id.tv_forgot_password);
        SpannableString forgot = new SpannableString(" Lupa Password?");
        forgot.setSpan(new UnderlineSpan(), 0, forgot.length(), 0);
        tvForgotPassword.setText(forgot);
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentForgot = new Intent(mContext, ForgetPasswordActivity.class);
                startActivity(intentForgot);
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

    private boolean checkLogin(){
        boolean status = true;
        etUsername.setError(null);
        etPassword.setError(null);

        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        if (isPasswordEmpty(password)){
            etPassword.setError(getString(R.string.error_field_required));
            status = false;
        } else if (!isPasswordValid(password)){
            popupAllert(getString(R.string.error_invalid_password));
            status = false;
        }

        if (isUsernameEmpty(username)){
            etUsername.setError(getString(R.string.error_field_required));
            status = false;
        } else if (!isUsernameValid(username)){
            popupAllert(getString(R.string.error_invalid_username));
            status = false;
        }

        return status;
    }

    private void requestLogin(){
        if (checkLogin()){
            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setTitle("Proses");
            progressDialog.setMessage("Tunggu Sebentar");
            progressDialog.show();
            mApiService.ownersLogin(etUsername.getText().toString(), etPassword.getText().toString()).enqueue(
                    new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()){
                                progressDialog.dismiss();
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body().string());
                                    if (jsonObject.getString("status").equals("success")){
                                        String username = jsonObject.getString("username");
                                        String name = jsonObject.getString("name");
                                        String email = jsonObject.getString("email");
                                        String token = jsonObject.getString("token");
                                        String ownerId = jsonObject.getString("owner_id");
                                        String photoUrl = jsonObject.getString("photo_url");
                                        String todayRentals = jsonObject.getString("today_rentals");
                                        String monthRentals = jsonObject.getString("month_rentals");
                                        Log.d("TESTING ", todayRentals + " & " + monthRentals);

                                        Intent login = new Intent(mContext, MainActivity.class);
                                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        //Session Login
                                        mAppSession.createSession(token, username, name, email, null, ownerId, photoUrl, todayRentals, monthRentals);
                                        finish();
                                        startActivity(login);
                                    } else {
                                        String errMessage = jsonObject.getString("message");
                                        popupAllert(errMessage);
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
                            Log.e("debug", "onFailure : ERROR > " + t.getMessage());
                            progressDialog.dismiss();
                            popupAllert(getString(R.string.dialog_message_connection_error));
                        }
                    }
            );
        }
    }
}
