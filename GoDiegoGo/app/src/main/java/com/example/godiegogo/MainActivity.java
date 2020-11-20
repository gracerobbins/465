package com.example.godiegogo;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.view.View;
import android.graphics.Color;
import android.content.Intent;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Button button = findViewById(R.id.button_id);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                button.setBackgroundColor(Color.BLUE);

                Intent intent = new Intent(v.getContext(), LoadingPageActivity.class);
                startActivity(intent);
            }
        });

    }
}