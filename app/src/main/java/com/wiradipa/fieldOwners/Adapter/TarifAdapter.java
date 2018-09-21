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
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(mContext).inflate(R.layout.listview_tarif, viewGroup, false);

        fieldTariff = new FieldTariff();

        TextView tvFromDay = (TextView) view.findViewById(R.id.tv_from_day);
        tvFromDay.setText(tariffs.get(i).getStartDay());
        TextView tvEndDay = (TextView) view.findViewById(R.id.tv_until_day);
        tvEndDay.setText(tariffs.get(i).getEndDay());
        TextView tvFromHour = (TextView) view.findViewById(R.id.tv_from_hour);
        tvFromHour.setText(tariffs.get(i).getStartHour());
        TextView tvEndHour = (TextView) view.findViewById(R.id.tv_until_hour);
        tvEndHour.setText(tariffs.get(i).getEndHour());
        TextView etCost = (EditText) view.findViewById(R.id.et_cost_field);
        etCost.setText(tariffs.get(i).getTariff());
        Log.d("TARIFFFF", tariffs.get(i).getTariff());

        return view;
    }
}
