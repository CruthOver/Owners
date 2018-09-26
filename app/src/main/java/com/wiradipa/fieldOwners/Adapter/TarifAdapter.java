package com.wiradipa.fieldOwners.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.wiradipa.fieldOwners.Model.FieldTariff;
import com.wiradipa.fieldOwners.R;

import java.util.ArrayList;
import java.util.List;

public class TarifAdapter extends BaseAdapter{

    ArrayList<FieldTariff> tariffs = new ArrayList<FieldTariff>();
    Context mContext;
    FieldTariff fieldTariff;

    String startDay, endDay, startHours, endHours;

    public TarifAdapter(Context mContext, ArrayList<FieldTariff> tariffs) {
        this.tariffs = tariffs;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return tariffs.size();
    }

    @Override
    public Object getItem(int i) {
        return tariffs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(mContext).inflate(R.layout.listview_tarif, viewGroup, false);

        fieldTariff = new FieldTariff();

        //        tvEndHour.setText(String.valueOf(tariffs.get(i).getEndHour()));

        if (tariffs.get(i).getStartDay()==1){
            startDay = "Senin"; //monday
        } else if (tariffs.get(i).getStartDay()==2){
            startDay = "Selasa";
        } else if (tariffs.get(i).getStartDay()==3){
            startDay = "Rabu";
        } else if (tariffs.get(i).getStartDay()==4){
            startDay = "Kamis";
        } else if (tariffs.get(i).getStartDay()==5){
            startDay = "Jumat";
        } else if (tariffs.get(i).getStartDay()==6){
            startDay = "Sabtu";
        } else if (tariffs.get(i).getStartDay()==0){
            startDay = "Minggu";
        }

        if (tariffs.get(i).getEndDay()==1){
            endDay= "Senin"; //monday
        } else if (tariffs.get(i).getEndDay()==2){
            endDay= "Selasa";
        } else if (tariffs.get(i).getEndDay()==3){
            endDay= "Rabu";
        } else if (tariffs.get(i).getEndDay()==4){
            endDay= "Kamis";
        } else if (tariffs.get(i).getEndDay()==5){
            endDay= "Jumat";
        } else if (tariffs.get(i).getEndDay()==6){
            endDay= "Sabtu";
        } else if (tariffs.get(i).getEndDay()==0){
            endDay= "Minggu";
        }

        startHours = String.valueOf(tariffs.get(i).getStartHour());
        endHours = String.valueOf(tariffs.get(i).getEndHour());

        TextView tvFromDay = (TextView) view.findViewById(R.id.tv_from_day);
        tvFromDay.setText(startDay);
        TextView tvEndDay = (TextView) view.findViewById(R.id.tv_until_day);
        tvEndDay.setText(endDay);
        TextView tvFromHour = (TextView) view.findViewById(R.id.tv_from_hour);
        tvFromHour.setText(startHours);
        TextView tvEndHour = (TextView) view.findViewById(R.id.tv_until_hour);
        tvEndHour.setText(endHours);
        TextView etCost = (EditText) view.findViewById(R.id.et_cost_field);
        etCost.setText(tariffs.get(i).getTariff());
        ImageView removeList = (ImageView) view.findViewById(R.id.removeList);
        removeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tariffs.remove(i);
                notifyDataSetChanged();
                Log.d("COUNTTT", getCount() + "");
            }
        });
        Log.d("TARIFFFF", tariffs.get(i).getTariff());

        return view;
    }

    public void updateItems(ArrayList<FieldTariff> newList) {
        tariffs.clear();
        tariffs.addAll(newList);
    }
}
