package com.wiradipa.fieldOwners.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class ListEditTarifAdapter extends RecyclerView.Adapter<ListEditTarifAdapter.ViewHolder> {

    public static class ListEditTarif{
        private String mStartHour;
        private String mEndHour;
        private String mHarga;
        private String mDay;

        public ListEditTarif() {}
    }

    public ArrayList<ListEditTarif> listEditTarifs;
    Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView startHour;
        TextView endHour;
        TextView cost;
        TextView day;
        public ViewHolder(View itemView) {
            super(itemView);
            startHour = (TextView) itemView.findViewById(R.id.startHour);
            endHour = (TextView) itemView.findViewById(R.id.endHour);
            cost = (TextView) itemView.findViewById(R.id.tv_harga);
            day = (TextView) itemView.findViewById(R.id.tv_day);
        }
    }

    public ListEditTarifAdapter(Context mContext) {
        this.mContext = mContext;
        listEditTarifs = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_edit_tarif, viewGroup, false);
        return new ListEditTarifAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ListEditTarif editTarif = listEditTarifs.get(i);
        if (editTarif !=null){
            ((ViewHolder) viewHolder).startHour.setText(editTarif.mStartHour);
            ((ViewHolder) viewHolder).endHour.setText(editTarif.mEndHour);
            ((ViewHolder) viewHolder).cost.setText(editTarif.mHarga);
            ((ViewHolder) viewHolder).day.setText(editTarif.mDay);
        }
    }

    @Override
    public int getItemCount() {
        return listEditTarifs.size();
    }

    public void ParsingData(JSONArray dataJsonArray){
        listEditTarifs.clear();
        try {
            for (int i=0; i<dataJsonArray.length(); i++){
                JSONObject jsonObject = dataJsonArray.getJSONObject(i);
                ListEditTarif jsonClass = new ListEditTarif();


                jsonClass.mDay = jsonObject.getString("wday");
                jsonClass.mStartHour = jsonObject.getString("start_hour");
                jsonClass.mEndHour = jsonObject.getString("end_hour");
                jsonClass.mHarga = jsonObject.getString("tariff");
                listEditTarifs.add(jsonClass);
                Log.d("arrayContent", jsonClass.mDay + " "
                        + jsonClass.mStartHour + " "
                        + jsonClass.mEndHour + " "
                        + jsonClass.mHarga + " ");
            }
            Log.d("arraySize", String.valueOf(dataJsonArray.length()));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void ParsingData(JSONArray dataJsonArray, String day){
        listEditTarifs.clear();
        try {
            for (int i=0; i<dataJsonArray.length(); i++){
                JSONObject jsonObject = dataJsonArray.getJSONObject(i);
                ListEditTarif jsonClass = new ListEditTarif();

                if(jsonObject.getString("wday").equalsIgnoreCase(day)){
                    jsonClass.mDay = jsonObject.getString("wday");
                    jsonClass.mStartHour = jsonObject.getString("start_hour");
                    jsonClass.mEndHour = jsonObject.getString("end_hour");
                    jsonClass.mHarga = jsonObject.getString("tariff");
                    listEditTarifs.add(jsonClass);
                    Log.d("arrayContent", jsonClass.mDay + " "
                            + jsonClass.mStartHour + " "
                            + jsonClass.mEndHour + " "
                            + jsonClass.mHarga + " ");
                }

            }
            Log.d("arraySize", String.valueOf(dataJsonArray.length()));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getDay(int position){
        return listEditTarifs.get(position).mDay;
    }
}
