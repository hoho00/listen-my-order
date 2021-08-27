package com.example.listen_my_order.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.listen_my_order.R;
import com.example.listen_my_order.adapter.ImportMenuAdapter;

import java.util.ArrayList;

import euphony.lib.receiver.AcousticSensor;
import euphony.lib.receiver.EuRxManager;

public class ImportMenuActivity extends AppCompatActivity {

    private boolean mode = true;
    private Button setImportButton;
    private TextView storeNameView;
    private RecyclerView menuListView;
    private ImportMenuAdapter importMenuAdapter;
    private ArrayList<MenuData> menuList = new ArrayList<>();

    private EuRxManager mRxManager = new EuRxManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_menu);

        setImportButton = (Button) findViewById(R.id.btn_set_import);
        storeNameView = (TextView) findViewById(R.id.tv_store_name);
        menuListView = (RecyclerView) findViewById(R.id.rv_menu_list);
        menuListView.setLayoutManager(new LinearLayoutManager(this));
        importMenuAdapter = new ImportMenuAdapter(menuList);
        menuListView.setAdapter(importMenuAdapter);

        setImportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode) {
                    mRxManager.finish();
                    setImportButton.setText(R.string.btn_start_listen);
                    mode = false;
                }
                else {
                    mRxManager.listen();
                    setImportButton.setText(R.string.btn_stop_listen);
                    mode = true;
                }
            }
        });

        mRxManager.setAcousticSensor(new AcousticSensor() {
            @Override
            public void notify(String letters) {
//                storeNameView.setText(letters); //Todo: letters에서 storeName만 분리해 저장

                for(String menu : letters.split("\n")) {
                    String[] menuInfo = menu.split(" ");
                    MenuData menuData = new MenuData(menuInfo[1], menuInfo[2], Float.parseFloat(menuInfo[3]));
                    menuList.add(menuData);
                }
                importMenuAdapter.notifyDataSetChanged();

                Toast.makeText(getApplicationContext(), "Finish importing!", Toast.LENGTH_SHORT).show();

                mRxManager.finish();
                setImportButton.setText(R.string.btn_start_listen);
                mode = false;
            }
        });
        mRxManager.listen();
    }
}