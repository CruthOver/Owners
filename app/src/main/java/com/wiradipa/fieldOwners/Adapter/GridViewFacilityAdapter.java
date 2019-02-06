package com.wiradipa.fieldOwners.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.wiradipa.fieldOwners.Model.FasilitasValue;
import com.wiradipa.fieldOwners.Model.FieldData;
import com.wiradipa.fieldOwners.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class GridViewFacilityAdapter extends ArrayAdapter<FasilitasValue> {
    private ArrayList<FasilitasValue>arrayarea = new ArrayList<>();
    ArrayList<FieldData> listAreas = new ArrayList<>();
    public GridViewFacilityAdapter(Context context, List<FasilitasValue> fasilitasValues) {
        super(context, 0,fasilitasValues);
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View gridview = view;
        if(gridview == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridview = inflater.inflate(R.layout.list_checkbox, null);
        }

        CheckBox checkBox = (CheckBox) gridview.findViewById(R.id.checkboxvenue);
        checkBox.setText(listAreas.get(i).getNama());
        checkBox.setChecked(true);

        return gridview;
    }
    public void parsingData(JSONArray dataJsonArray){
        try {
            for (int i=0; i<dataJsonArray.length(); i++){
                JSONObject jsonObject = dataJsonArray.getJSONObject(i);
                FasilitasValue fasilitasValue = new FasilitasValue();
                fasilitasValue.id= jsonObject.getInt("facility_id");
                arrayarea.add(fasilitasValue);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
