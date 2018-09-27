package com.wiradipa.fieldOwners;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wiradipa.fieldOwners.Adapter.ViewPagerAdapter;
import com.wiradipa.fieldOwners.ApiHelper.AppSession;


/**
 * A simple {@link Fragment} subclass.
 */
public class BerandaFragment extends Fragment {

    private AppBarLayout appBarLayout;

    TextView mPemesananMinggu, mPemesananBulan;

    AppSession mAppSession;

    public BerandaFragment() {
        // Required empty public constructor
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_beranda, container, false);

        mAppSession = new AppSession(getActivity().getApplicationContext());

        appBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar_id);

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

        mPemesananMinggu = (TextView) view.findViewById(R.id.week_rentals);
        mPemesananBulan = (TextView) view.findViewById(R.id.month_rentals);

        mPemesananMinggu.setText(mAppSession.getData(AppSession.ORDERED_WEEK) + " Pemesanan");
        mPemesananBulan.setText(mAppSession.getData(AppSession.ORDERED_MONTH) + " Pemesanan");

        Log.d("PEMESANAN", mAppSession.getData(AppSession.ORDERED_WEEK) + " & " + mAppSession.getData(AppSession.ORDERED_MONTH));
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