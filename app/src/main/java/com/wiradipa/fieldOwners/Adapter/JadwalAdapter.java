package com.wiradipa.fieldOwners.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wiradipa.fieldOwners.Model.Jadwal;
import com.wiradipa.fieldOwners.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JadwalAdapter extends RecyclerView.Adapter<JadwalAdapter.ViewHolder> {

//    public static class JadwalClass{
//        public String mRentalDate;
//        public String mStartHour;
//        public String mEndHour;
//        public int mRentalStatus;
//
//        public String getmRentalDate() {
//            return mRentalDate;
//        }
//
//        public String getmStartHour() {
//            return mStartHour;
//        }
//
//        public String getmEndHour() {
//            return mEndHour;
//        }
//
//        public int getmRentalStatus() {
//            return mRentalStatus;
//        }
//
//        public void setmRentalStatus(int mRentalStatus) {
//            this.mRentalStatus = mRentalStatus;
//        }
//    }

    public ArrayList<Jadwal> listJadwal;
    Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView startHour;
        TextView endHour;
        TextView strip;
        ImageView status;
        public ViewHolder(View itemView) {
            super(itemView);
            startHour = (TextView) itemView.findViewById(R.id.startHour);
            endHour = (TextView) itemView.findViewById(R.id.endHour);
            strip = (TextView) itemView.findViewById(R.id.garis);
            status = (ImageView) itemView.findViewById(R.id.status);
        }
    }

    public JadwalAdapter(Context mContext){
        this.mContext = mContext;
        listJadwal = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_jadwal, viewGroup, false);
        return new ViewHolder(view);
    }

    public String checkDigit(String number) {
        return Integer.parseInt(number)<=9?"0"+number+".00":number +".00";
    }

    public void onBindViewHolder(final ViewHolder holder, int i) {
        Jadwal jadwalList = listJadwal.get(i);
        if (jadwalList!=null){
            ((ViewHolder) holder).startHour.setText(checkDigit(jadwalList.startHour));
            ((ViewHolder) holder).endHour.setText(checkDigit(jadwalList.endHours));
            if (jadwalList.status == 0){
                ((ViewHolder) holder).status.setImageResource(R.drawable.booked_btn);
            } else {
                ((ViewHolder) holder).status.setImageResource(R.drawable.tersedia_btn);
            }
        }
    }

    @Override
    public int getItemCount() {
        return listJadwal.size();
    }

    public int getJadwalId(int position){
        return listJadwal.get(position).status;
    }

    public String getStartHour(int position){
        return listJadwal.get(position).startHour;
    }

    public String getEndHour(int position){
        return listJadwal.get(position).endHours;
    }

    public void ParsingData(JSONArray dataJsonArray){
        try {
            for (int i=0; i<dataJsonArray.length(); i++){
                JSONObject jsonObject = dataJsonArray.getJSONObject(i);
                Jadwal jsonClass = new Jadwal();
                jsonClass.date = jsonObject.getString("rental_date");
                jsonClass.startHour = jsonObject.getString("start_hour");
                jsonClass.endHours = jsonObject.getString("end_hour");
                jsonClass.status = jsonObject.getInt("rental_status");
                listJadwal.add(jsonClass);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateData(JSONArray dataJsonArray){
        listJadwal.clear();
        try {
            for (int i=0; i<dataJsonArray.length(); i++){
                JSONObject jsonObject = dataJsonArray.getJSONObject(i);
                Jadwal jsonClass = new Jadwal();
                jsonClass.date = jsonObject.getString("rental_date");
                jsonClass.startHour = jsonObject.getString("start_hour");
                jsonClass.endHours = jsonObject.getString("end_hour");
                jsonClass.status = jsonObject.getInt("rental_status");
                listJadwal.add(jsonClass);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.notifyDataSetChanged();
    }
}
