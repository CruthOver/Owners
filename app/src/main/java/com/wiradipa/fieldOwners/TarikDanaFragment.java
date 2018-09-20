package com.wiradipa.fieldOwners;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.wiradipa.fieldOwners.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class TarikDanaFragment extends android.support.v4.app.Fragment {


    ImageView bca;
    ImageView bni;
    ImageView mandiri;
    ImageView bri;
    Toolbar toolbar;

    public TarikDanaFragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_tarik_dana, container, false);


        bca = (ImageView) view.findViewById(R.id.bca);
        bni = (ImageView) view.findViewById(R.id.bni);
        mandiri = (ImageView) view.findViewById(R.id.mandiri);
        bri = (ImageView) view.findViewById(R.id.bri);

        bca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bca.setSelected(!bca.isPressed());
                bca.setBackgroundResource(R.drawable.highlight);
                bni.setBackgroundResource(R.color.highlight);
                mandiri.setBackgroundResource(R.color.highlight);
                bri.setBackgroundResource(R.color.highlight);
            }
        });

        bni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bni.setSelected(!bca.isPressed());
                bni.setBackgroundResource(R.drawable.highlight);
                bca.setBackgroundResource(R.color.highlight);
                mandiri.setBackgroundResource(R.color.highlight);
                bri.setBackgroundResource(R.color.highlight);
            }
        });

        mandiri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mandiri.setSelected(!bca.isPressed());
                mandiri.setBackgroundResource(R.drawable.highlight);
                bca.setBackgroundResource(R.color.highlight);
                bni.setBackgroundResource(R.color.highlight);
                bri.setBackgroundResource(R.color.highlight);
            }
        });

        bri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bri.setSelected(!bca.isPressed());
                bri.setBackgroundResource(R.drawable.highlight);
                bca.setBackgroundResource(R.color.highlight);
                bni.setBackgroundResource(R.color.highlight);
                mandiri.setBackgroundResource(R.color.highlight);
            }
        });

        return view;
    }

}
