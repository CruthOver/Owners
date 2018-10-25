package com.wiradipa.fieldOwners.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;
import com.wiradipa.fieldOwners.DetailVenueActivity;
import com.wiradipa.fieldOwners.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VenueAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private class MyVenue{
        public String mId;
        public String mName;
        public String mAddress;
        public String mFasilitas;
        public String mPhotoUrl;
    }

    ArrayList<MyVenue> listVenue;
    Context mContext;

    public class MyVenueViewHolder extends RecyclerView.ViewHolder{
        public TextView mNameVenue;
        public TextView mAddressVenue;
        public TextView mFasilitasVenue;
        public ImageView imageVenue;
        public LinearLayout rootView;
//        public TextView mAddressVenue;

        public MyVenueViewHolder(View itemView) {
            super(itemView);
            rootView = (LinearLayout) itemView.findViewById(R.id.view_venue);
            mNameVenue = (TextView) itemView.findViewById(R.id.nama_venue);
            mAddressVenue = (TextView) itemView.findViewById(R.id.alamat_venue);
            imageVenue = (ImageView) itemView.findViewById(R.id.img_venue);
//            mAddressVenue = (TextView) itemView.findViewById(R.id.alamat_lapang);
        }
    }

    public VenueAdapter(Context mContext){
            this.mContext = mContext;
            listVenue = new ArrayList<>();
    }

    public VenueAdapter (JSONArray dataArray, Context mContext){
        this.mContext = mContext;
        listVenue = new ArrayList<>();
        parsingData(dataArray);
    }

    public void parsingData(JSONArray dataJsonArray){
        try {
            for (int i=0; i<dataJsonArray.length(); i++){
                JSONObject jsonObject = dataJsonArray.getJSONObject(i);
                MyVenue myVenue = new MyVenue();
                myVenue.mId = jsonObject.getString("id");
                myVenue.mName = jsonObject.getString("name");
                myVenue.mAddress = jsonObject.getString("address");
                myVenue.mPhotoUrl = jsonObject.getString("picture_url");
                listVenue.add(myVenue);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_venue, viewGroup, false);
        return new MyVenueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final MyVenue myVenue = listVenue.get(i);

        if (myVenue!=null){
            ((MyVenueViewHolder) viewHolder).mNameVenue.setText(myVenue.mName);
            ((MyVenueViewHolder) viewHolder).mAddressVenue.setText(myVenue.mAddress);
            Glide.with(mContext).load("http://app.lapangbola.com" + myVenue.mPhotoUrl).
                    apply(new RequestOptions().placeholder(R.drawable.ic_image_black_24dp).error(R.drawable.ic_image_black_24dp))
                    .into(((MyVenueViewHolder) viewHolder).imageVenue);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Toast.makeText(mContext, myVenue.mId, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(view.getContext(), DetailVenueActivity.class);
                    intent.putExtra("id_trips", myVenue.mId);
                    view.getContext().startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listVenue.size();
    }

    public String getVenueId(int position){
        return listVenue.get(position).mId;
    }

    public void clear(){
        listVenue.clear();
    }
}
