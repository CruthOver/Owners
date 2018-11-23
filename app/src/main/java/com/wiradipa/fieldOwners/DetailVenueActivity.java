package com.wiradipa.fieldOwners;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wiradipa.fieldOwners.ApiHelper.AppSession;
import com.wiradipa.fieldOwners.ApiHelper.BaseApiService;
import com.wiradipa.fieldOwners.ApiHelper.UtilsApi;
import com.wiradipa.fieldOwners.Fragment.WorkArroundMapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailVenueActivity extends AppCompatActivity implements OnMapReadyCallback{

    LinearLayout linearFasilitas, linearFasilitasVenue, linearAreaVenue;
    TextView mDescriptionTextView, textViewFacilities,
            mLokasiDetail, mNameVenueTextView, mMinTariffTextView;
    ScrollView mScrollView;
    Button mPilihLapang;

    String id, description, mLokasi, mName, idDetail;
    int minTariff;
    Double mLatitude, mLongitude;

    Context mContext;
    BaseApiService mApiService;
    AppSession mAppSession;

    GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_venue);

        mContext = this;
        mApiService = UtilsApi.getApiService();
        mAppSession = new AppSession(mContext);

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            String flag = bundle.getString("FLAG");
            if (flag.equals("addVenue")){
                id = bundle.getString("id");
            } else {
                id = bundle.getString("idVenue");
            }
        }

        linearFasilitas = (LinearLayout) findViewById(R.id.fasilitas_detail_venue);
        linearFasilitasVenue = (LinearLayout) findViewById(R.id.fasilitas_venue_detail);
        linearAreaVenue = (LinearLayout) findViewById(R.id.area_venue_detail) ;
        mDescriptionTextView = (TextView) findViewById(R.id.desc_detail_venue);
        mLokasiDetail = (TextView) findViewById(R.id.lokasi_detail);
        mScrollView = (ScrollView) findViewById(R.id.scrollViewDetailVenue);
        mMinTariffTextView = (TextView) findViewById(R.id.min_tariff);
        mNameVenueTextView = (TextView) findViewById(R.id.tv_name_venue);
        mPilihLapang = (Button) findViewById(R.id.pilih_lapang);

        mPilihLapang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toField;
                toField = new Intent(mContext, FieldActivity.class);
                toField.putExtra("idVenueDetail", idDetail);
                view.getContext().startActivity(toField);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_detail);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        ((WorkArroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_detail))
                .setListener(new WorkArroundMapFragment.OnTouchListener() {
                    @Override
                    public void onTouch() {
                        mScrollView.requestDisallowInterceptTouchEvent(true);
                    }
                });
    }

    private void getDetailVenue(){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Proses");
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.show();

        mApiService.detailVenue(id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getString("status").equals("Success")){
                            idDetail = jsonObject.getString("id");
                            mName = jsonObject.getString("name");
                            description = jsonObject.getString("description");
                            mLokasi = jsonObject.getString("address");
                            minTariff = jsonObject.getInt("min_tariff");
                            String latitude = jsonObject.getString("latitude");
                            String longitude = jsonObject.getString("longitude");
                            if (latitude.equals("")){
                                mLatitude = (Double) null;
                                mLongitude = (Double) null;
                            } else{
                                mLatitude = Double.parseDouble(latitude);
                                mLongitude = Double.parseDouble(longitude);
                            }
                            if (mLatitude != null){
                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLatitude, mLongitude)));
                                mGoogleMap.animateCamera(CameraUpdateFactory.zoomIn());
                                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null);

                                MarkerOptions markerOptions = new MarkerOptions().
                                        title(mName).position(new LatLng(mLatitude, mLongitude));
                                mGoogleMap.addMarker(markerOptions);
                                mLokasiDetail.setText(mLokasi);
                            } else {
                                Toast.makeText(mContext, "Lokasi tidak ada", Toast.LENGTH_SHORT).show();
                                mLokasiDetail.setText("Tidak Ada Lokasi");
                            }

                            JSONArray jsonArray = jsonObject.getJSONArray("facilities");
                            if(linearFasilitasVenue!=null)
                                linearFasilitasVenue.removeAllViews();

                            for (int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonFacilities = jsonArray.getJSONObject(i);
                                String facilities = jsonFacilities.getString("name");
                                textViewFacilities = new TextView(mContext);
                                textViewFacilities.setTextColor(Color.WHITE);
                                textViewFacilities.setTextSize(14f);
                                textViewFacilities.setText("- " + facilities);
                                linearFasilitasVenue.addView(textViewFacilities);
                            }

                            mNameVenueTextView.setText(mName);
                            mDescriptionTextView.setText(description);
                            mMinTariffTextView.setText(checkDigitMoney(minTariff));
                            if (linearAreaVenue!=null)
                                linearAreaVenue.removeAllViews();
                            JSONArray arrayArea = jsonObject.getJSONArray("areas");
                            for (int i=0; i<arrayArea.length(); i++){
                                JSONObject jsonArea = arrayArea.getJSONObject(i);
                                String area = jsonArea.getString("name");
                                TextView textView = new TextView(mContext);
                                textView.setTextColor(Color.WHITE);
                                textView.setTextSize(14f);
                                textView.setText("- " + area);
                                linearAreaVenue.addView(textView);
                            }
                        }
                    } catch (JSONException | IOException e) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int position = item.getItemId();

        switch (position){
            case R.id.action_edit:
                Intent edit = new Intent(mContext, AddVenueActivity.class);
                edit.putExtra("venueId", id);
                startActivity(edit);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDetailVenue();
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
