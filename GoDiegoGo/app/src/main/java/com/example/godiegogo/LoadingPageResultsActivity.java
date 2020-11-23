package com.example.godiegogo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.content.Intent;
import java.util.LinkedList;
import java.util.ArrayList;
import com.example.godiegogo.R;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoadingPageResultsActivity extends AppCompatActivity {
    public ArrayList<String> failed_songs;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_page_results);

        Bundle b = this.getIntent().getExtras();
        failed_songs=b.getStringArrayList("failed_songs");
        String transfer_type = b.getString("transfer_type");
        TextView results_header = (TextView)findViewById(R.id.results_header);
        results_header.setText(transfer_type + " Complete! The following songs could not be copied:");

        ListView lv = (ListView) findViewById(R.id.songlist);
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, failed_songs);
        lv.setAdapter(itemsAdapter);

        final Button button = findViewById(R.id.main_menu_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
}
