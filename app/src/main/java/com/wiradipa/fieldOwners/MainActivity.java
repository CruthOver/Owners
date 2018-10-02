package com.wiradipa.fieldOwners;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView mEmailTv, mUsernameTv;
    Button  mLogout;

    AppSession mAppSession;
    Context mContext;
    BaseApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        mEmailTv = (TextView) headerView.findViewById(R.id.email);
        mUsernameTv= (TextView) headerView.findViewById(R.id.username);
        mLogout = (Button) findViewById(R.id.logout);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, new BerandaFragment());
        fragmentTransaction.commit();

        mContext = this;
        mApiService = UtilsApi.getApiService();
        mAppSession = new AppSession(mContext);

        mEmailTv.setText(mAppSession.getData(AppSession.EMAIL));
        mUsernameTv.setText(mAppSession.getData(AppSession.USERNAME));
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialogBuilder = new AlertDialog.Builder(mContext).
                        setTitle("Logout").setMessage("Anda Yakin Ingin Logout ?").
                        setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                signOutOwner();
                                mAppSession.clearSession();
                                Intent intent = new Intent(mContext, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                finish();
                                startActivity(intent);
                            }
                        }).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        Fragment fragment = null;

        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            fragment = new BerandaFragment();
            getSupportActionBar().setTitle("BERANDA");
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            fragment = new TarikDanaFragment();
            getSupportActionBar().setTitle("TARIK DANA");
        }
        else if (id == R.id.nav_transaksi) {
            fragment = new TransaksiFragment();
            getSupportActionBar().setTitle("Transaksi");
        }
//        else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        if (fragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOutOwner(){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Proses");
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.show();
        mApiService.signOutOwner(mAppSession.getData(AppSession.TOKEN)).enqueue(
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            progressDialog.dismiss();
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                if (jsonObject.getString("status").equals("success")){
                                    Toast.makeText(mContext, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                } else{
                                    String errorMessage = jsonObject.getString("message");
                                    Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle(R.string.dialog_title_error);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    System.exit(0);
                                    finish();
                                }
                            });
                            builder.setMessage("Server Error !!");
                            AlertDialog alert1 = builder.create();
                            alert1.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("debug", "onFailure : ERROR > " + t.getMessage());
                        Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
