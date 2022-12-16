package com.nimeshkadecha.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class Additems extends AppCompatActivity {

    //    toolbar and navagation drawer starts;
    private ImageView menu, backBtn;
    private View navagationDrawer;
    private TextView customerInfo, editInfo;
    private Button Add;
//    toolbar and navagation drawer ends;

    //    for insert operation and outher stuf
    Button show;
    EditText productName, price, quantity;
    DBManager DB = new DBManager(this);

    String cNametxt, cNumbertxt, datetext, sellertxt;
    int billIdtxt;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additems);

//        Finding buttons
        menu = findViewById(R.id.Menu);
        menu.setVisibility(View.INVISIBLE);
        Add = findViewById(R.id.button3);
        show = findViewById(R.id.show);

//        Working with TOOLBAR STARTS --------------------------------------------------------------

//        Removing Suport bar / top line containing name
        Objects.requireNonNull(getSupportActionBar()).hide();

//        Hiding navigationgrawer
        navagationDrawer = findViewById(R.id.navigation);
        navagationDrawer.setVisibility(View.INVISIBLE);

//        FINDING menu
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navagationDrawer.setVisibility(View.VISIBLE);
                Add.setVisibility(View.INVISIBLE);
                show.setVisibility(View.INVISIBLE);

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
                Add.setVisibility(View.VISIBLE);
                show.setVisibility(View.VISIBLE);
            }
        });
//        Working with TOOLBAR Ends --------------------------------------------------------------
//        finding customer info btn and edit info btn
        customerInfo = findViewById(R.id.customerinfo);
        editInfo = findViewById(R.id.editInfo);

        customerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Additems.this, "Customer Info Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(home.this, "Edit info btn CLICKED", Toast.LENGTH_SHORT).show();

//                This intent go to editinformation.class
                Intent intent = new Intent(Additems.this, editInformation.class);
                Bundle bundle = getIntent().getExtras();
                String email = bundle.getString("Email");

                intent.putExtra("Email", email);
                startActivity(intent);

            }
        });

//        WORKING IN NAVAGATION DRAWER Ends  -----------------------------------------------------

//  INSERT OPERATION IN DISPLAY TABLE ------------------------------------------------------------------
//        INSERTING DATA

//        Getting INTENT
        Bundle name = getIntent().getExtras();
        cNametxt = name.getString("cName");

        Bundle num = getIntent().getExtras();
        cNumbertxt = num.getString("cNumber");

        Bundle dat = getIntent().getExtras();
        datetext = dat.getString("date");

        Bundle bID = getIntent().getExtras();
        billIdtxt = bID.getInt("billId");

        Bundle seller = getIntent().getExtras();
        sellertxt = seller.getString("seller");

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productName = findViewById(R.id.productname);
                price = findViewById(R.id.price);
                quantity = findViewById(R.id.quantity);

                String productName_ST, price_ST, quantity_ST;
                productName_ST = productName.getText().toString();
                price_ST = price.getText().toString();
                quantity_ST = quantity.getText().toString();
                if (productName_ST.isEmpty() || price_ST.isEmpty() || quantity_ST.isEmpty()) {
                    if (productName_ST.isEmpty() && price_ST.isEmpty() && quantity_ST.isEmpty()) {
                        Toast.makeText(Additems.this, "Entry details", Toast.LENGTH_SHORT).show();
                    } else if (productName_ST.isEmpty()) {
                        Toast.makeText(Additems.this, "Product filed is Empty", Toast.LENGTH_SHORT).show();
                    } else if (price_ST.isEmpty()) {
                        Toast.makeText(Additems.this, "Price filed is Empty", Toast.LENGTH_SHORT).show();
                    } else if (quantity_ST.isEmpty()) {
                        Toast.makeText(Additems.this, "Quantity filed is Empty", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Additems.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    if (cNametxt.isEmpty() || cNumbertxt.isEmpty() || datetext.isEmpty()) {
                        Toast.makeText(Additems.this, "Emptity intent", Toast.LENGTH_SHORT).show();
                    } else {

                        boolean check = DB.Insert_List(productName_ST, price_ST, quantity_ST, cNametxt, cNumbertxt, datetext, billIdtxt, sellertxt, 0);
                        if (check) {
                            Toast.makeText(Additems.this, "Inserted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Additems.this, "Not Inserted", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

//        ON click Shsow

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(Additems.this, ShowList.class);

                intent2.putExtra("seller", sellertxt);

                intent2.putExtra("cName", cNametxt);

                intent2.putExtra("cNumber", cNumbertxt);

                intent2.putExtra("date", datetext);

                intent2.putExtra("billId", billIdtxt);

                startActivity(intent2);
                finish();
            }
        });
    }
}