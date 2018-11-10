package com.wiradipa.fieldOwners;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.wiradipa.fieldOwners.ApiHelper.AppSession;
import com.wiradipa.fieldOwners.ApiHelper.BaseApiService;
import com.wiradipa.fieldOwners.ApiHelper.UtilsApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

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
            , tvTime, tvClubPlayer, tvDownPayment, tvReceiveable, tvAmount;
    Button btnLunas, btnBatal, btnApprove, btnProofPayment;
    CircleImageView civPlayer;

    String mIdTransaksi, mNamaLapang, mNamaVenue, mStartHour, mEndHour,
            mTanggal, mPayment, mPenyewa, mUrlBukti;
    int id, mUangMuka, mPiutang, mTagihan;

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
            mUrlBukti = extras.getString("receiptUrl");
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
        tvDownPayment = (TextView) findViewById(R.id.detail_down_payment);
        tvReceiveable = (TextView) findViewById(R.id.detail_receiveable);
        tvAmount = (TextView) findViewById(R.id.detail_amount);

        civPlayer = (CircleImageView) findViewById(R.id.photo_player);

        btnLunas = (Button) findViewById(R.id.btnLunas);
        btnBatal = (Button) findViewById(R.id.btnBatal);
//        btnApprove = (Button) findViewById(R.id.btnApproveDP);
        btnProofPayment = (Button) findViewById(R.id.proofOfPayment);

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

//        btnApprove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                approveDownPayment();
//            }
//        });

        btnProofPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogReceiptUrl(mUrlBukti);
            }
        });
    }

    public String checkDigit(String number) {
        return Integer.parseInt(number)<=9?"0"+number+".00":number +".00";
    }

    public String checkDigitMoney(int number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format((double) number);
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
                            mPayment = data.getString("payment_status");
                            mTagihan = data.getInt("amount");
//                            mUangMuka = data.getInt("down_payment");
//                            mPiutang = data.getInt("receivable");

                            tvPlayerName.setText(mPenyewa);
                            tvFieldName.setText(mNamaLapang);
                            tvVenueName.setText(mNamaVenue);
                            tvDate.setText(mTanggal);
                            tvIdTransaction.setText(mIdTransaksi);
                            tvTime.setText(checkDigit(mStartHour) + " - " + checkDigit(mEndHour));
//                            tvDownPayment.setText(checkDigitMoney(mUangMuka));
//                            tvReceiveable.setText(checkDigitMoney(mPiutang));
                            tvAmount.setText(checkDigitMoney(mTagihan));
                        } else {
                            String errMsg = data.getString("message");
                            popupAllert(errMsg);
                        }
                    } catch (IOException | JSONException e) {
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

    private void approveDownPayment(){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Proses");
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.show();

        mApiService.approveDownPayment(id, mAppSession.getData(AppSession.TOKEN))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    progressDialog.dismiss();
                    try {
                        JSONObject data = new JSONObject(response.body().string());
                        String message = data.getString("message");
                        if (data.getString("status").equals("Success")){
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            popupAllert(message);
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

    public void dialogReceiptUrl(String url){
        String titleText = "Tambah Tarif";
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_alert_dialog, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageReceipt);
        TextView msgNotAvailable = (TextView) view.findViewById(R.id.img_not_available);
        if (url.equals("") || url == null || url.equals("/receipts/original/missing.png")){
            msgNotAvailable.setVisibility(View.VISIBLE);
            msgNotAvailable.setText(R.string.image_not_available);
            imageView.setImageResource(R.drawable.ic_image_black_24dp);
        } else {
            msgNotAvailable.setVisibility(View.GONE);
            Glide.with(mContext).load("http://app.lapangbola.com" + url).apply(new RequestOptions().placeholder(R.drawable
                    .ic_image_black_24dp).error(R.drawable.ic_image_black_24dp)).into(imageView);
        }
        dialogBuilder.setView(view);
        dialogBuilder.setTitle(titleText);

        dialogBuilder.setPositiveButton("Setuju", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                approveDownPayment();
            }
        });
        dialogBuilder.setNeutralButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(mContext, "Dalam Tahap Pengembangan", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });

        dialogBuilder.setNegativeButton("Tolak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        final AlertDialog dialog = dialogBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.not_approved));
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.approved));
                dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#000000"));

            }
        });
        dialog.show();
    }
}
