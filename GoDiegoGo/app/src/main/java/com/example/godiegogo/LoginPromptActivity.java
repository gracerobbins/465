package com.example.godiegogo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginPromptActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_prompt);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Bundle b = this.getIntent().getExtras();
        String service_name = b.getString("service");
        TextView login_prompt = (TextView)findViewById(R.id.login_prompt);
        login_prompt.setText("Please enter your credentials for " + service_name + ":");

        final Button button = findViewById(R.id.login_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText usernameTextInput = (EditText)findViewById(R.id.username);
                String username = usernameTextInput.getText().toString();
                EditText passwordTextInput = (EditText)findViewById(R.id.password);
                String password = passwordTextInput.getText().toString();

                //PERFORM API LOGIN STUFF HERE?


                finish();

            }
        });

        final Button cancel = findViewById(R.id.login_cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
}
