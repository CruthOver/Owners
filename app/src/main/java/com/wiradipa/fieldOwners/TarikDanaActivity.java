package com.wiradipa.fieldOwners;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toolbar;

public class TarikDanaActivity extends AppCompatActivity {

    ImageView bca;
    ImageView bni;
    ImageView mandiri;
    ImageView bri;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarik_dana);

        bca = (ImageView) findViewById(R.id.bca);
        bni = (ImageView) findViewById(R.id.bni);
        mandiri = (ImageView) findViewById(R.id.mandiri);
        bri = (ImageView) findViewById(R.id.bri);

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
    }
}
