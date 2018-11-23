package com.wiradipa.fieldOwners.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wiradipa.fieldOwners.Model.DataTransaksi;
import com.wiradipa.fieldOwners.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class DataTransaksiBookedAdapter extends RecyclerView.Adapter<DataTransaksiBookedAdapter.ViewHolder> {

    public ArrayList<DataTransaksi> listTransaksi;
    Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvNamaLapang;
        TextView tvTanggalLapang;
        TextView tvNamaPenyewa;
        TextView tvNamaLapangPenyewa;
        TextView tvWaktuMain;
        TextView tvNamaLapangMain;
        TextView tvTotalTagihan;
        TextView tvStatusApprove;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNamaLapang = (TextView) itemView.findViewById(R.id.tr_nama_lapangan);
            tvTanggalLapang = (TextView) itemView.findViewById(R.id.tr_tanggal);
            tvNamaPenyewa = (TextView) itemView.findViewById(R.id.tr_nama_penyewa);
            tvNamaLapangPenyewa = (TextView) itemView.findViewById(R.id.tr_lapang_penyewa);
            tvWaktuMain = (TextView) itemView.findViewById(R.id.tr_waktu_main);
            tvNamaLapangMain = (TextView) itemView.findViewById(R.id.tr_lapang_main);
            tvTotalTagihan = (TextView) itemView.findViewById(R.id.tr_total_tagihan);
            tvStatusApprove = (TextView) itemView.findViewById(R.id.tr_status_approve);
        }
    }

    public DataTransaksiBookedAdapter(Context mContext) {
        this.listTransaksi = new ArrayList<>();
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.data_transaksi, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        DataTransaksi dataTransaksi = listTransaksi.get(i);
        if (dataTransaksi != null){
            ((ViewHolder) viewHolder).tvNamaLapang.setText((dataTransaksi.mNamaLapang));
            ((ViewHolder) viewHolder).tvTanggalLapang.setText((dataTransaksi.mTanggalLapang));
            ((ViewHolder) viewHolder).tvNamaPenyewa.setText((dataTransaksi.mNamaPenyewa));
//            ((ViewHolder) viewHolder).tvNamaLapangPenyewa.setText((dataTransaksi.mNamaLapangPenyewa));
            ((ViewHolder) viewHolder).tvWaktuMain.setText((String.valueOf(dataTransaksi.mWaktuMain + " Jam")));
            ((ViewHolder) viewHolder).tvNamaLapangMain.setText((dataTransaksi.mNamaLapangMain));
            ((ViewHolder) viewHolder).tvTotalTagihan.setText((checkDigitMoney(dataTransaksi.mTotalTagihan)));
            if (dataTransaksi.mStatus == 0){
                ((ViewHolder) viewHolder).tvStatusApprove.setText("Belum disetujui");
                ((ViewHolder) viewHolder).tvStatusApprove.setTextColor(mContext.getResources().getColor(R.color.not_approved));
            } else {
                ((ViewHolder) viewHolder).tvStatusApprove.setText("Disetujui");
                ((ViewHolder) viewHolder).tvStatusApprove.setTextColor(mContext.getResources().getColor(R.color.approved));
            }
        }
    }

    @Override
    public int getItemCount() {
        return listTransaksi.size();
    }

    public String checkDigitMoney(int number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format((double) number);
    }

    public void parsingDataJson(JSONArray dataJsonArray){
        try {
            for (int i=0; i<dataJsonArray.length(); i++){
                JSONObject jsonObject = dataJsonArray.getJSONObject(i);
                DataTransaksi jsonTransaksi = new DataTransaksi();
                jsonTransaksi.id = jsonObject.getInt("id");
                jsonTransaksi.mNamaLapang = jsonObject.getString("field_owner_name");
                jsonTransaksi.mTanggalLapang = jsonObject.getString("rental_date");
                jsonTransaksi.mNamaPenyewa = jsonObject.getString("player_name");
//                jsonTransaksi.mNamaLapangPenyewa = jsonObject.getString("rental_status");
                jsonTransaksi.mStartHour = jsonObject.getInt("start_hour");
                jsonTransaksi.mEndHour = jsonObject.getInt("end_hour");
                jsonTransaksi.mWaktuMain = jsonTransaksi.mEndHour - jsonTransaksi.mStartHour;
                jsonTransaksi.mNamaLapangMain = jsonObject.getString("field_name");
                jsonTransaksi.mTotalTagihan = jsonObject.getInt("amount");
                jsonTransaksi.urlBuktiDp = jsonObject.getString("receipt_url");
                jsonTransaksi.mStatus = jsonObject.getInt("down_payment_status");
                jsonTransaksi.mPaymentStatus = jsonObject.getInt("payment_status");

                if (jsonObject.getInt("payment_status") == 0){
                    listTransaksi.add(jsonTransaksi);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getTransaksiId(int position){
        return listTransaksi.get(position).id;
    }

    public String getReceiptUrl(int position){
        return listTransaksi.get(position).urlBuktiDp;
    }

    public  void clear(){
        listTransaksi.clear();
    }
}
