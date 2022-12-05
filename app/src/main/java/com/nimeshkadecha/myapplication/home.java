package com.nimeshkadecha.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class home extends AppCompatActivity {

    private ImageView menu, backBtn;

    private Button product;

    private TextView customerInfo, editInfo, backup;

    private View navagationDrawer;

    private DBManager DB = new DBManager(this);

    private EditText name, number, date;

//    Generate OTP
    @SuppressLint("DefaultLocale")
    private static String getOTP() {
        Random rnd = new Random();
        int otp = rnd.nextInt(999999);
        return String.format("%06d", otp);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final int[] billIDd = {DB.getbillid()};
        billIDd[0]++;

//        Finding edit texts
        name = findViewById(R.id.name);
        number = findViewById(R.id.contact);
        date = findViewById(R.id.date);
        backup = findViewById(R.id.backup);
        backup.setVisibility(View.INVISIBLE);

//        Generating and formating date --------------------------
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        date.setText(formattedDate);

//        FINDING BUTTONS
        product = findViewById(R.id.products);

//        Adding seller email from INTENT
        Bundle bundle = getIntent().getExtras();
        String email = bundle.getString("Email");

//        Working with TOOLBAR STARTS --------------------------------------------------------------

//        Removing Suport bar / top line containing name
        Objects.requireNonNull(getSupportActionBar()).hide();

//        Hiding navigationgrawer
        navagationDrawer = findViewById(R.id.navigation);
        navagationDrawer.setVisibility(View.INVISIBLE);

//        FINDING menu
        menu = findViewById(R.id.Menu);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navagationDrawer.setVisibility(View.VISIBLE);
                product.setVisibility(View.INVISIBLE);

            }
        });

//        FINDING Backbtn
        backBtn = findViewById(R.id.btnBack);

//        WORKING IN NAVAGATION DRAWER starts  -----------------------------------------------------

//        hiding navagation on back btn click------------
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navagationDrawer.setVisibility(View.INVISIBLE);
                product.setVisibility(View.VISIBLE);
            }
        });
//        Working with TOOLBAR Ends --------------------------------------------------------------

//        finding customer info btn and edit info btn
        customerInfo = findViewById(R.id.customerinfo);
        editInfo = findViewById(R.id.editInfo);

        customerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(home.this, "Customer Info Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(home.this, customer_Info.class);
                intent.putExtra("seller", email);
                startActivity(intent);
            }
        });

        editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(home.this, "Edit info btn CLICKED", Toast.LENGTH_SHORT).show();

//                This intent go to editinformation.class
                Intent intent = new Intent(home.this, editInformation.class);
                intent.putExtra("Email", email);
                startActivity(intent);

            }
        });

//        Working on Backup ---------------------------------------------------------------
        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String OTP = getOTP();
                Intent backup = new Intent(home.this, Backup.class);
                backup.putExtra("user", email);
                backup.putExtra("bOTP", OTP);
                startActivity(backup);
            }
        });

//        WORKING IN NAVAGATION DRAWER Ends  -----------------------------------------------------

//        Products enter intent add customer and go to next INTENT for adding product this will add customer information
        product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                billIDd[0]++;
                Intent intent = new Intent(home.this, Additems.class);

                String nametxt, numbertxt, datetxt;
                nametxt = name.getText().toString();
                numbertxt = number.getText().toString();
                datetxt = date.getText().toString();

                if (nametxt.isEmpty() || numbertxt.isEmpty() || datetxt.isEmpty()) {
                    if (nametxt.isEmpty() && numbertxt.isEmpty() && datetxt.isEmpty()) {
                        Toast.makeText(home.this, "Fill up above detail", Toast.LENGTH_SHORT).show();
                    } else if (nametxt.isEmpty()) {
                        Toast.makeText(home.this, "Enter Customer Name", Toast.LENGTH_SHORT).show();
                    } else if (numbertxt.isEmpty()) {
                        Toast.makeText(home.this, "Enter Customer Number", Toast.LENGTH_SHORT).show();
                    } else if (datetxt.isEmpty()) {
                        Toast.makeText(home.this, "Enter Date", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(home.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (numbertxt.length() != 10) {
                        Toast.makeText(home.this, "Invalid Number", Toast.LENGTH_SHORT).show();
                    } else {
                        datetxt = date.getText().toString();

                        intent.putExtra("cName", nametxt);
                        intent.putExtra("cNumber", numbertxt);
                        intent.putExtra("date", datetxt);
                        intent.putExtra("billId", billIDd[0]);
                        intent.putExtra("seller", email);
                        startActivity(intent);
                    }
                }
            }
        });
    }
}