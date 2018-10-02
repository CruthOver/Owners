package com.wiradipa.fieldOwners;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class DataTransaksiAdapter extends ArrayAdapter<DataTransaksi> {

    public DataTransaksiAdapter(@NonNull Context context, @NonNull List<DataTransaksi> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.data_transaksi, parent, false);
        DataTransaksi current = getItem(position);
        TextView trNamaLapang = (TextView) convertView.findViewById(R.id.tr_nama_lapangan);
        TextView trTanggal = (TextView) convertView.findViewById(R.id.tr_tanggal);
        TextView trNamaPenyewa = (TextView) convertView.findViewById(R.id.tr_nama_penyewa);
        TextView trLapangPenyewa = (TextView) convertView.findViewById(R.id.tr_lapang_penyewa);
        TextView trWaktuMain = (TextView) convertView.findViewById(R.id.tr_waktu_main);
        TextView trLapangMain = (TextView) convertView.findViewById(R.id.tr_lapang_main);
        TextView trTagihan = (TextView) convertView.findViewById(R.id.tr_total_tagihan);

        trNamaLapang.setText(current.getmNamaLapang());
        trTanggal.setText(current.getmTanggalLapang());
        trNamaPenyewa.setText(current.getmNamaLapangPenyewa());
        trLapangPenyewa.setText(current.getmNamaLapangPenyewa());
        trWaktuMain.setText(current.getmWaktuMain());
        trLapangMain.setText(current.getmNamaLapangMain());
        trTagihan.setText(current.getmTotalTagihan());

        return convertView;
    }
}
