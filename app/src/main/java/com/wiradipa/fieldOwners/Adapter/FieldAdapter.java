package com.wiradipa.fieldOwners.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;
import com.wiradipa.fieldOwners.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FieldAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private class MyField{
        public String mId;
        public String mName;
        public String mAddress;
        public String mFieldType;
        public String mPhotoUrl;
    }

    ArrayList<MyField> listFields;
    Context mContext;

    public class MyFieldViewHolder extends RecyclerView.ViewHolder{
        public TextView mNameField;
        public TextView mAddressField;
        public TextView mTypeFieldName;
        public ImageView mPhotoLapang;
        public LinearLayout rootView;

        public MyFieldViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = (LinearLayout) itemView.findViewById(R.id.view_field);
            mNameField = (TextView) itemView.findViewById(R.id.nama_lapang);
            mAddressField = (TextView) itemView.findViewById(R.id.alamat_lapang);
            mTypeFieldName = (TextView) itemView.findViewById(R.id.jenis_lapang);
            mPhotoLapang = (ImageView) itemView.findViewById(R.id.img_lapang);
        }
    }

    public FieldAdapter(Context mContext){
        this.mContext = mContext;
        listFields = new ArrayList<>();
    }

    public FieldAdapter (JSONArray dataArray, Context mContext){
        this.mContext = mContext;
        listFields = new ArrayList<>();
        parsingData(dataArray);
    }

    public void parsingData(JSONArray dataJsonArray){
        try {
            for (int i=0; i<dataJsonArray.length(); i++){
                JSONObject jsonObject = dataJsonArray.getJSONObject(i);
                MyField myField = new MyField();
                myField.mId = jsonObject.getString("id");
                myField.mName = jsonObject.getString("name");
                myField.mAddress = jsonObject.getString("field_owner_name");
                myField.mFieldType = jsonObject.getString("field_type_name");
                myField.mPhotoUrl = jsonObject.getString("picture_url");
                listFields.add(myField);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_field, viewGroup, false);
        return new MyFieldViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final MyField myField = listFields.get(i);

        if (myField!=null){
            ((MyFieldViewHolder) viewHolder).mNameField.setText(myField.mName);
            ((MyFieldViewHolder) viewHolder).mAddressField.setText(myField.mAddress);
            ((MyFieldViewHolder) viewHolder).mTypeFieldName.setText(myField.mFieldType);
            Glide.with(mContext).load("http://app.lapangbola.com" + myField.mPhotoUrl)
                    .apply(new RequestOptions().placeholder(R.drawable.ic_image_black_24dp).
                    error(R.drawable.ic_image_black_24dp)).into(((MyFieldViewHolder) viewHolder).mPhotoLapang);
        }
    }

    @Override
    public int getItemCount() {
        return listFields.size();
    }

    public String getFieldId(int position){
        return listFields.get(position).mId;
    }

    public void clear(){
        listFields.clear();
    }
}
