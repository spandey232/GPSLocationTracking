package com.journaldev.gpslocationtracking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginPage extends AppCompatActivity {


    private EditText loc;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        loc=(EditText)findViewById(R.id.location);
    }



    public void click1(View v) {

        Intent i=new Intent(LoginPage.this, MainActivity.class);
        i.putExtra("location",loc.getText().toString());
        startActivity(i);
    }
}
