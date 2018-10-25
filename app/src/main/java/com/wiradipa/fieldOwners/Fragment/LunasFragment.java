package com.wiradipa.fieldOwners.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wiradipa.fieldOwners.Model.DataTransaksi;
import com.wiradipa.fieldOwners.Adapter.DataTransaksiAdapter;
import com.wiradipa.fieldOwners.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class LunasFragment extends Fragment {


    public LunasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.list_item, container, false);

        ArrayList<DataTransaksi> transaksi = new ArrayList<>();
        transaksi.add(new DataTransaksi("Rajawali","28 Agustus 2018","Ali","Antapani",
                "2 jam","lapangan 1","200.000"));
        transaksi.add(new DataTransaksi("Rajawali","28 Agustus 2018","Ali","Antapani",
                "2 jam","lapangan 1","200.000"));
        transaksi.add(new DataTransaksi("Rajawali","28 Agustus 2018","Ali","Antapani",
                "2 jam","lapangan 1","200.000"));

        ListView listView = view.findViewById(R.id.list_item);

        final DataTransaksiAdapter adapter = new DataTransaksiAdapter(getActivity(), transaksi);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.getItem(position);

            }
        });

        return view;
    }

}
