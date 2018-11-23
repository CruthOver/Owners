package com.wiradipa.fieldOwners.Fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.wiradipa.fieldOwners.Adapter.DataTransaksiLunasAdapter;
import com.wiradipa.fieldOwners.Adapter.RecyclerTouchListener;
import com.wiradipa.fieldOwners.ApiHelper.AppSession;
import com.wiradipa.fieldOwners.ApiHelper.BaseApiService;
import com.wiradipa.fieldOwners.ApiHelper.UtilsApi;
import com.wiradipa.fieldOwners.Adapter.DataTransaksiBookedAdapter;
import com.wiradipa.fieldOwners.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class LunasFragment extends Fragment {

    private Context mContext;
    BaseApiService mApiService;
    AppSession mAppSession;

    private RecyclerView recyclerView;
    DataTransaksiLunasAdapter mAdapter;

    public LunasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.list_item, container, false);

        mContext = getActivity();
        mApiService = UtilsApi.getApiService();
        mAppSession = new AppSession(getActivity());
        recyclerView = (RecyclerView) view.findViewById(R.id.list_item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new DataTransaksiLunasAdapter(mContext);
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    private void listBookedTransaksi(){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Proses");
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.show();

        mApiService.dataTransaksi(mAppSession.getData(AppSession.TOKEN),1)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            progressDialog.dismiss();
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                if (jsonObject.getString("status").equals("Success")){
                                    mAdapter.parsingDataJson(jsonObject.getJSONArray("data"));
                                    mAdapter.notifyDataSetChanged();
                                }else {
                                    String errorMsg = jsonObject.getString("message");
                                    Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                                    mAdapter.notifyDataSetChanged();
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            switch (response.code()){
                                case 404:
                                    popupAllert(getString(R.string.server_not_found));
                                    break;
                                case 500:
                                    popupAllert(getString(R.string.server_error));
                                    break;
                                case 413:
                                    popupAllert(getString(R.string.error_large));
                                    break;
                                default:
                                    popupAllert(getString(R.string.unknown_error));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("debug","OnFailure: JadwalError >" + t.toString());
                        popupAllert("No Internet Connection !!!");
                    }
                });
    }

    public void popupAllert(String alert) {
        new AlertDialog.Builder(mContext)
                .setTitle(R.string.dialog_title_error)
                .setMessage(alert)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.clear();
        listBookedTransaksi();
    }
}
