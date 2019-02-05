package com.wiradipa.fieldOwners;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wiradipa.fieldOwners.Adapter.TarifAdapter;
import com.wiradipa.fieldOwners.ApiHelper.AppSession;
import com.wiradipa.fieldOwners.ApiHelper.BaseApiService;
import com.wiradipa.fieldOwners.ApiHelper.UtilsApi;
import com.wiradipa.fieldOwners.Model.CurrencyTextWatcher;
import com.wiradipa.fieldOwners.Model.FieldTariff;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTarifActivity extends AppCompatActivity {

    Spinner mSpinStartDay, mSpinEndDay, mSpinStartHour, mSpinEndHour;
    EditText mFieldCostEditText;
    Button btnAddTarif;
    TextView mEmptyViewTarif;

    ListView mListViewTarif;
    ArrayList<FieldTariff> fieldTarifs;
    TarifAdapter mAdapter;

    Context mContext;
    BaseApiService mApiService;
    AppSession mAppSession;

    int mStartHour, mEndHour, mStartDay, mEndDay;
    String mJsonTarif, mFieldId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tarif);

        mContext = this;
        mApiService = UtilsApi.getApiService();
        mAppSession = new AppSession(mContext);

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            mFieldId = bundle.getString("fieldId");
        }

        initComponents();

        fieldTarifs = new ArrayList<FieldTariff>();
        mAdapter = new TarifAdapter(mContext, fieldTarifs);
        mListViewTarif.setAdapter(mAdapter);
        mListViewTarif.setEmptyView(mEmptyViewTarif);
    }

    private void initComponents(){
        mSpinStartDay = (Spinner) findViewById(R.id.spinner_start_day);
        mSpinEndDay = (Spinner) findViewById(R.id.spinner_end_day);
        mSpinStartHour = (Spinner) findViewById(R.id.spinner_start_hour);
        mSpinEndHour = (Spinner) findViewById(R.id.spinner_end_hour);

        mFieldCostEditText = (EditText) findViewById(R.id.et_cost);
        mEmptyViewTarif = (TextView) findViewById(R.id.empty_tarif_tv);

        mListViewTarif = (ListView) findViewById(R.id.list_tariff);

        btnAddTarif = (Button) findViewById(R.id.addTarif);

        mFieldCostEditText.addTextChangedListener(new CurrencyTextWatcher(mFieldCostEditText));

        setSpinnerStartHour();
        setSpinnerEndHour();
        setSpinnerStartDay();
        setSpinnerUntilDay();

        btnAddTarif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fieldTarifs.add(new FieldTariff(mStartDay, mEndDay, mStartHour, mEndHour,
                        mFieldCostEditText.getText().toString().replaceAll("[Rp,.\\s]", "")));
                setSpinnerStartHour();
                setSpinnerEndHour();
                setSpinnerStartDay();
                setSpinnerUntilDay();
                mFieldCostEditText.setText("");
                mAdapter.notifyDataSetChanged();

                mJsonTarif = new Gson().toJson(fieldTarifs);
                Log.d("JSONTARIF ", mJsonTarif);
            }
        });
    }

    private void setSpinnerStartHour() {
        ArrayAdapter fromDaySpinner = ArrayAdapter.createFromResource(mContext,
                R.array.startHour, R.layout.spinner_jadwal);
        fromDaySpinner.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);

        mSpinStartHour.setAdapter(fromDaySpinner);
        mSpinStartHour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getItemAtPosition(i);
                if (!TextUtils.isEmpty(selection)){
                    if (selection.equals("-- Pilih Jam -- ")){
                        Toast.makeText(mContext, "Pilih Jam Terlebih Dahulu", Toast.LENGTH_SHORT).show();;
                    }
                    if (selection.equals("0")){
                        mStartHour = 0;
                        Log.d("START HOUR ", mStartHour + "");
                    } else if (selection.equals("1")){
                        mStartHour = 1; //monday
                    } else if (selection.equals("2")){
                        mStartHour = 2;
                    } else if (selection.equals("3")){
                        mStartHour = 3;
                    } else if (selection.equals("4")){
                        mStartHour = 4;
                    } else if (selection.equals("5")){
                        mStartHour = 5;
                    } else if (selection.equals("6")){
                        mStartHour = 6;
                    } else if (selection.equals("7")){
                        mStartHour = 7;
                    } else if (selection.equals("8")){
                        mStartHour = 8;
                    } else if (selection.equals("9")){
                        mStartHour = 9;
                    } else if (selection.equals("10")){
                        mStartHour = 10;
                    } else if (selection.equals("11")){
                        mStartHour = 11;
                    } else if (selection.equals("12")){
                        mStartHour = 12;
                    } else if (selection.equals("13")){
                        mStartHour = 13;
                    } else if (selection.equals("14")){
                        mStartHour = 14;
                    } else if (selection.equals("15")){
                        mStartHour = 15;
                    } else if (selection.equals("16")){
                        mStartHour = 16;
                    } else if (selection.equals("17")){
                        mStartHour = 17;
                    } else if (selection.equals("18")){
                        mStartHour = 18;
                    } else if (selection.equals("19")){
                        mStartHour = 19;
                    } else if (selection.equals("20")){
                        mStartHour = 20;
                    } else if (selection.equals("21")){
                        mStartHour = 21;
                    } else if (selection.equals("22")){
                        mStartHour = 22;
                    } else if (selection.equals("23")){
                        mStartHour = 23;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(mContext, "Hari belum dipilih", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSpinnerEndHour() {
        ArrayAdapter fromDaySpinner = ArrayAdapter.createFromResource(mContext,
                R.array.endHour, R.layout.spinner_jadwal);
        fromDaySpinner.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);

        mSpinEndHour.setAdapter(fromDaySpinner);
        mSpinEndHour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getItemAtPosition(i);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.optionDay))) {
                        Toast.makeText(mContext, "Pilih Jam Terlebih Dahulu", Toast.LENGTH_SHORT).show();
                    }
                    if (selection.equals("1")) {
                        mEndHour = 1; //monday
                    } else if (selection.equals("2")) {
                        mEndHour = 2;
                    } else if (selection.equals("3")) {
                        mEndHour = 3;
                    } else if (selection.equals("4")) {
                        mEndHour = 4;
                    } else if (selection.equals("5")) {
                        mEndHour = 5;
                    } else if (selection.equals("6")) {
                        mEndHour = 6;
                    } else if (selection.equals("7")) {
                        mEndHour = 7;
                    } else if (selection.equals("8")) {
                        mEndHour = 8;
                    } else if (selection.equals("9")) {
                        mEndHour = 9;
                    } else if (selection.equals("10")) {
                        mEndHour = 10;
                    } else if (selection.equals("11")) {
                        mEndHour = 11;
                    } else if (selection.equals("12")) {
                        mEndHour = 12;
                    } else if (selection.equals("13")) {
                        mEndHour = 13;
                    } else if (selection.equals("14")) {
                        mEndHour = 14;
                    } else if (selection.equals("15")) {
                        mEndHour = 15;
                    } else if (selection.equals("16")) {
                        mEndHour = 16;
                    } else if (selection.equals("17")) {
                        mEndHour = 17;
                    } else if (selection.equals("18")) {
                        mEndHour = 18;
                    } else if (selection.equals("19")) {
                        mEndHour = 19;
                    } else if (selection.equals("20")) {
                        mEndHour = 20;
                    } else if (selection.equals("21")) {
                        mEndHour = 21;
                    } else if (selection.equals("221")) {
                        mEndHour = 22;
                    } else if (selection.equals("23")) {
                        mEndHour = 23;
                    } else if (selection.equals("24")) {
                        mEndHour = 24;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(mContext, "Hari belum dipilih", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSpinnerStartDay(){
        ArrayAdapter fromDaySpinner = ArrayAdapter.createFromResource(mContext,
                R.array.day, R.layout.spinner_jadwal);
        fromDaySpinner.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);

        mSpinStartDay.setAdapter(fromDaySpinner);
        mSpinStartDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getItemAtPosition(i);
                if (!TextUtils.isEmpty(selection)){
                    if (selection.equals(getString(R.string.optionDay))){
                        Toast.makeText(mContext, "Select Open Day", Toast.LENGTH_SHORT).show();
                    }
                    if (selection.equals(getString(R.string.monday))){
                        mStartDay = 1; //monday
                    } else if(selection.equals(getString(R.string.tuesday))){
                        mStartDay = 2;
                    } else if (selection.equals(getString(R.string.wednesday))){
                        mStartDay = 3;
                    } else if (selection.equals(getString(R.string.thursday))){
                        mStartDay = 4;
                    } else if (selection.equals(getString(R.string.friday))){
                        mStartDay = 5;
                    } else if (selection.equals(getString(R.string.saturday))){
                        mStartDay = 6;
                    } else if (selection.equals(getString(R.string.sunday))){
                        mStartDay = 0;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(mContext, "Hari belum dipilih", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSpinnerUntilDay(){
        ArrayAdapter untilDaySpinner = ArrayAdapter.createFromResource(mContext,
                R.array.day, R.layout.spinner_jadwal);
        untilDaySpinner.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);

        mSpinEndDay.setAdapter(untilDaySpinner);
        mSpinEndDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getItemAtPosition(i);
                if (!TextUtils.isEmpty(selection)){
                    if (selection.equals(getString(R.string.monday))){
                        mEndDay = 1; //monday
                    } else if(selection.equals(getString(R.string.tuesday))){
                        mEndDay = 2;
                    } else if (selection.equals(getString(R.string.wednesday))){
                        mEndDay = 3;
                    } else if (selection.equals(getString(R.string.thursday))){
                        mEndDay = 4;
                    } else if (selection.equals(getString(R.string.friday))){
                        mEndDay = 5;
                    } else if (selection.equals(getString(R.string.saturday))){
                        mEndDay = 6;
                    } else if (selection.equals(getString(R.string.sunday))){
                        mEndDay = 0;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(mContext, "Hari belum dipilih", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addFieldTarif(){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Proses");
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.show();

        Log.d("JSON TARIF", mJsonTarif);

        mApiService.addFieldTarif(mFieldId, mAppSession.getData(AppSession.TOKEN), mJsonTarif).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    progressDialog.dismiss();
                    try {
                        JSONObject data = new JSONObject(response.body().string());
                        if (data.getString("status").equals("Success")){
                            String message = data.getString("message");
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String messageErr = data.getString("message");
                            popupAllert(messageErr);
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    progressDialog.dismiss();
                    switch (response.code()){
                        case 404:
                            popupAllert(getString(R.string.server_not_found));
                            break;
                        case 500:
                            popupAllert(getString(R.string.server_error));
                            break;
                        default:
                            popupAllert(getString(R.string.unknown_error));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("onFailure", t.getMessage());
                popupAllert("No Internet Connection !!!");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_tarif, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int menuID = item.getItemId();

        switch (menuID){
            case R.id.action_save:
                addFieldTarif();
        }

        return super.onOptionsItemSelected(item);
    }

    public void popupAllert(String alert) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_error)
                .setMessage(alert)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss();
                    }
                }).create().show();
    }
}