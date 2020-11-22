package com.example.godiegogo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginPromptActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_prompt);

        final Button button = findViewById(R.id.login_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                button.setBackgroundColor(Color.BLUE);
                EditText usernameTextInput = (EditText)findViewById(R.id.username);
                String username = usernameTextInput.getText().toString();
                EditText passwordTextInput = (EditText)findViewById(R.id.password);
                String password = passwordTextInput.getText().toString();

                //PERFORM API LOGIN STUFF HERE?


                finish();

            }
        });
    }
}
