package com.nimeshkadecha.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class resetPassword extends AppCompatActivity {

    private EditText password, confirmPassword;

    private DBManager DBM;
    private ImageView menuclick;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
//        finding
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.ConfirmPassword);
        DBM = new DBManager(this);

//        WORKING WITH TOOLBAR Starts-------------------------------------------------------------
//        Removing Suport bar / top line containing name
        Objects.requireNonNull(getSupportActionBar()).hide();

//        FINDING menu
        menuclick = findViewById(R.id.Menu);

//        Keeping MENUE Invisible
        menuclick.setVisibility(View.INVISIBLE);
//        WORKING WITH TOOLBAR Ends-------------------------------------------------------------
    }

    //    validating
    private boolean PasswordValidation(EditText password, EditText confirmPassword) {
        String passwordInput = password.getText().toString();
        String confirmPasswordInput = confirmPassword.getText().toString();
        if (passwordInput.length() != confirmPasswordInput.length() || passwordInput.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    //    RESETING PASSWORD
    public void Confirm(View view) {
        boolean VP = PasswordValidation(password, confirmPassword);
        if (VP) {
            if (password.getText().toString().equals(confirmPassword.getText().toString())) {
                Intent gotoMAIN = new Intent(this, MainActivity.class);
                Bundle bundle = getIntent().getExtras();
                String number = bundle.getString("number");
                boolean check;
                check = DBM.resetPassword(number, confirmPassword.getText().toString());

                if (check) {
                    Toast.makeText(this, "Password reset Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(gotoMAIN);
                } else {
                    Toast.makeText(this, "Password NOT reset Successfully", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Password Don't Match", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Invalid Password", Toast.LENGTH_SHORT).show();
        }
    }
}