package com.wiradipa.fieldOwners;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FragmentStatusPemesanan1 extends Fragment {

    public FragmentStatusPemesanan1(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status_pesanan1, container, false);

        ImageView venue = (ImageView) view.findViewById(R.id.venue);
        venue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), VenueActivity.class);
                startActivity(intent);
            }
        });

        ImageView field = (ImageView) view.findViewById(R.id.field);
        field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FieldActivity.class);
                startActivity(intent);
            }
        });

//        ImageView jadwal = (ImageView) view.findViewById(R.id.jadwal);
//        jadwal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), JadwalActivity.class);
//                startActivity(intent);
//            }
//        });

        return view;
    }
}
