package com.wiradipa.fieldOwners;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wiradipa.fieldOwners.ApiHelper.AppSession;
import com.wiradipa.fieldOwners.ApiHelper.BaseApiService;
import com.wiradipa.fieldOwners.ApiHelper.UtilsApi;
import com.wiradipa.fieldOwners.Model.PlaceInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailVenueActivity extends AppCompatActivity implements OnMapReadyCallback{

    LinearLayout linearFasilitas, linearFasilitasVenue, linearAreaVenue;
    TextView mDescriptionTextView;
    TextView mLokasiDetail, mNameVenueTextView;
    ScrollView mScrollView;

    String id, description, mLokasi, mName;
    Double mLatitude, mLongitude;

    Context mContext;
    BaseApiService mApiService;
    AppSession mAppSession;

    VenueActivity venueActivity;

    GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_venue);

        venueActivity = new VenueActivity();
        mContext = this;
        mApiService = UtilsApi.getApiService();
        mAppSession = new AppSession(mContext);

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            id = bundle.getString("idVenue");
            Toast.makeText(mContext, id, Toast.LENGTH_SHORT).show();
        }

        linearFasilitas = (LinearLayout) findViewById(R.id.fasilitas_detail_venue);
        linearFasilitasVenue = (LinearLayout) findViewById(R.id.fasilitas_venue_detail);
        linearAreaVenue = (LinearLayout) findViewById(R.id.area_venue_detail) ;
        mDescriptionTextView = (TextView) findViewById(R.id.desc_detail_venue);
        mLokasiDetail = (TextView) findViewById(R.id.lokasi_detail);
        mScrollView = (ScrollView) findViewById(R.id.scrollViewDetailVenue);
        mNameVenueTextView = (TextView) findViewById(R.id.tv_name_venue);

        getDetailVenue();

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
                            mName = jsonObject.getString("name");
                            description = jsonObject.getString("description");
                            mLokasi = jsonObject.getString("address");
                            String latitude = jsonObject.getString("latitude");
                            String longitude = jsonObject.getString("longitude");
                            if (latitude.equals("")){
                                mLatitude = (Double) null;
                                mLongitude = (Double) null;
                            } else{
                                mLatitude = Double.parseDouble(latitude);
                                mLongitude = Double.parseDouble(longitude);
                            }
//                            LatLng location = new LatLng(mLatitude, mLongitude);
//                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                            builder.include(location);
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
                            for (int i=0; i<jsonArray.length(); i++){
                                jsonObject = jsonArray.getJSONObject(i);
                                String facilities = jsonObject.getString("name");
                                TextView textViewFacilities = new TextView(mContext);
                                textViewFacilities.setTextColor(Color.WHITE);
                                textViewFacilities.setTextSize(14f);
                                textViewFacilities.setText("- " + facilities);
                                linearFasilitasVenue.addView(textViewFacilities);
                            }
                            JSONArray arrayArea = jsonObject.getJSONArray("areas");
                            for (int i=0; i<arrayArea.length(); i++){
                                jsonObject = arrayArea.getJSONObject(i);
                                String area = jsonObject.getString("name");
                                TextView textViewFacilities = new TextView(mContext);
                                textViewFacilities.setTextColor(Color.WHITE);
                                textViewFacilities.setTextSize(14f);
                                textViewFacilities.setText("- " + area);
                                linearAreaVenue.addView(textViewFacilities);
                            }
                            mNameVenueTextView.setText(mName);
                            mDescriptionTextView.setText(description);
                        }
                    } catch (JSONException | IOException e) {
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
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
