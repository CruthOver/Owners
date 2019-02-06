package com.wiradipa.fieldOwners.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.wiradipa.fieldOwners.Model.AreasValue;
import com.wiradipa.fieldOwners.Model.FieldData;
import com.wiradipa.fieldOwners.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class GridViewAreaAdapter extends ArrayAdapter<AreasValue> {
    private LayoutInflater inflater;
    private ArrayList<AreasValue> arrayarea = new ArrayList<>();
    ArrayList<FieldData> listAreas = new ArrayList<>();

    CheckBox checkBox;
    public GridViewAreaAdapter(@NonNull Context context, List<AreasValue> arrayarea) {
        super(context,0, arrayarea);

    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View gridview = view;
        if(gridview == null){
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridview = inflater.inflate(R.layout.list_checkbox, null);
        }

        checkBox = (CheckBox) gridview.findViewById(R.id.checkboxvenue);
        checkBox.setId(listAreas.get(i).getId());
        checkBox.setText(listAreas.get(i).getNama());
        checkBox.setChecked(true);

        return gridview;
        }

    public void parsingData(JSONArray dataJsonArray){
        try {
            for (int i=0; i<dataJsonArray.length(); i++){
                JSONObject jsonObject = dataJsonArray.getJSONObject(i);
                AreasValue jsonarray = new AreasValue();
                jsonarray.id = jsonObject.getInt("area_id");
                arrayarea.add(jsonarray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    }



