package net.vinnen.sensorjackeba.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import net.vinnen.sensorjackeba.ListAdapter;
import net.vinnen.sensorjackeba.MainActivity;
import net.vinnen.sensorjackeba.R;

import java.io.File;

/**
 * This class displays all downloaded files that can be played in the Main Activity
 */
public class SelectFile extends AppCompatActivity {

    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        File dataDir = new File(Environment.getExternalStorageDirectory(), "trackerJacketData");

        listview = (ListView) findViewById(R.id.listview);
        listview.setAdapter(new ListAdapter(this, dataDir.list()));
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filename = (String) listview.getAdapter().getItem(position);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("PlayFile", filename);
                navigateUpToFromChild(SelectFile.this, intent);
            }
        });
    }

}
