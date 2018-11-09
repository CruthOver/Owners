package com.wiradipa.fieldOwners;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailTransaksiActivity extends AppCompatActivity {

    private Context mContext;
    BaseApiService mApiService;
    AppSession mAppSession;

    TextView tvPlayerName, tvFieldName, tvVenueName, tvDate, tvIdTransaction
            , tvTime, tvClubPlayer;
    Button btnLunas, btnBatal;
    CircleImageView civPlayer;

    String mIdTransaksi, mNamaLapang, mNamaVenue, mTagihan, mStartHour, mEndHour,
            mTanggal, mPayment, mPenyewa;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaksi);

        mContext = this;
        mApiService = UtilsApi.getApiService();
        mAppSession = new AppSession(mContext);

        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            id = extras.getInt("idTransaction");
            Toast.makeText(mContext, id + "", Toast.LENGTH_SHORT).show();
        }
        initComponents();
        detailTransaction();
    }

    private void initComponents(){
        tvPlayerName = (TextView) findViewById(R.id.player_name);
        tvFieldName = (TextView) findViewById(R.id.detail_tr_namaLapang);
        tvVenueName = (TextView) findViewById(R.id.detail_tr_namaVenue);
        tvIdTransaction = (TextView) findViewById(R.id.detail_tr_idTransaction);
        tvDate = (TextView) findViewById(R.id.detail_tr_tanggalMain);
        tvTime = (TextView) findViewById(R.id.detail_tr_time);
        tvClubPlayer = (TextView) findViewById(R.id.club_player);

        civPlayer = (CircleImageView) findViewById(R.id.photo_player);

        btnLunas = (Button) findViewById(R.id.btnLunas);
        btnBatal = (Button) findViewById(R.id.btnBatal);

        btnLunas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });

        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public String checkDigit(String number) {
        return Integer.parseInt(number)<=9?"0"+number+".00":number +".00";
    }

    private void detailTransaction(){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Proses");
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.show();

        mApiService.detailTransaksi(id, mAppSession.getData(AppSession.TOKEN), 1)
                .enqueue(new Callback<ResponseBody>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            progressDialog.dismiss();
                            try {
                                JSONObject data = new JSONObject(response.body().string());
                                if (data.getString("status").equals("Success")){
                                    mIdTransaksi = data.getString("id_transaction");
                                    mPenyewa = data.getString("player_name");
                                    mTanggal = data.getString("rental_date");
                                    mStartHour = data.getString("start_hour");
                                    mEndHour = data.getString("end_hour");
                                    mNamaLapang = data.getString("field_name");
                                    mNamaVenue = data.getString("field_owner_name");
                                    mTagihan = data.getString("amount");
                                    mPayment = data.getString("payment_status");

                                    tvPlayerName.setText(mPenyewa);
                                    tvFieldName.setText(mNamaLapang);
                                    tvVenueName.setText(mNamaVenue);
                                    tvDate.setText(mTanggal);
                                    tvIdTransaction.setText(mIdTransaksi);
                                    tvTime.setText(checkDigit(mStartHour) + " - " + checkDigit(mEndHour));
                                }
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }
}
