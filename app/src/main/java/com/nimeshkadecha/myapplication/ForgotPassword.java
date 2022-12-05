package com.nimeshkadecha.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Objects;
import java.util.Random;

public class ForgotPassword extends AppCompatActivity {

    private String OTP = getOTP();

    private EditText number;

    private ImageView menuclick;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
//        WORKING WITH TOOLBAR Starts-------------------------------------------------------------
//        Removing Suport bar / top line containing name
        Objects.requireNonNull(getSupportActionBar()).hide();

//        FINDING menu
        menuclick = findViewById(R.id.Menu);

//        Keeping MENUE Invisible
        menuclick.setVisibility(View.INVISIBLE);
//        WORKING WITH TOOLBAR Ends-------------------------------------------------------------

//        Finding -----
        number = findViewById(R.id.contactnumber);
    }

//    Validation on NUMBER
    private boolean numberValidation(EditText number) {
        String numberInput = number.getText().toString();
        if (numberInput.length() == 10) {
            return true;
        } else {
            return false;
        }
    }

//    Verifying otp and go to OTP_GEN
    public void GetOTP(View view) {
        boolean NV = numberValidation(number);
        if (NV) {
            Intent GETOTP = new Intent(ForgotPassword.this, OTP_Generator.class);
            String CN = number.getText().toString();

            GETOTP.putExtra("number", CN);
            GETOTP.putExtra("OTP", String.valueOf(OTP));
            startActivity(GETOTP);

//       ENDS Working with NOTIFICATIOn -----------------------------------------------
        } else {
            Toast.makeText(this, "Invalid Number", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("DefaultLocale")
//    Generating OTP
    private static String getOTP() {
        Random rnd = new Random();
        int otp = rnd.nextInt(999999);
        return String.format("%06d", otp);
    }
}