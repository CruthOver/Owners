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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
        public String mDescription;
        public String mFieldOwnerId;
        public String mGrassTypeId;
        public String mFieldOwnerName;
        public String mGrassTypeName;
        public String mFieldType;
        public String mPitchSize;
        public String mPhotoUrl;
        public String[] mFieldPhotos;
        public String[] mFacilities;
        public String[] mFieldTarifs;
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
//            Picasso.with(mContext).load("http://app.lapangbola.com" + myField.mPhotoUrl).into(((MyFieldViewHolder) viewHolder).mPhotoLapang);
            Glide.with(mContext).load("http://app.lapangbola.com" + myField.mPhotoUrl).into(((MyFieldViewHolder) viewHolder).mPhotoLapang);
        }
    }

    @Override
    public int getItemCount() {
        return listFields.size();
    }

    public String getFieldId(int position){
        return listFields.get(position).mId;
    }

//    public FieldAdapter(@NonNull Context context, @NonNull List<Field> objects) {
//        super(context, 0, objects);
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        if (convertView == null)
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_field, parent, false);
//        Field current = getItem(position);
//        TextView textView = (TextView) convertView.findViewById(R.id.text_field);
//        ImageView img = (ImageView) convertView.findViewById(R.id.icon_field);
//        textView.setText(current.getText());
//        img.setImageResource(current.getImg());
//
//        return convertView;
//    }
}
