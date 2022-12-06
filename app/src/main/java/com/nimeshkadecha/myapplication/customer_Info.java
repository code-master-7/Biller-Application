package com.nimeshkadecha.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class customer_Info extends AppCompatActivity {

    private EditText nameedt, dateedt, billidedt, contactedt, todateedt;

    private Button searchbtn, showbtn, pdf;

    private ImageView menuclick;

    private DBManager DB = new DBManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_info);

//        Getting User from INTENT
        Bundle seller = getIntent().getExtras();
        String sellertxt = seller.getString("seller");

//------------------------------------------------------------------Working on toolbar
        //        Removing Suport bar / top line containing name
        Objects.requireNonNull(getSupportActionBar()).hide();

//        FINDING menu
        menuclick = findViewById(R.id.Menu);

//        Keeping MENUE Invisible
        menuclick.setVisibility(View.INVISIBLE);
//---------------------------------------------------------------END working on toolbar


//        Finding everything---------------------------
        nameedt = findViewById(R.id.name);
        dateedt = findViewById(R.id.date);
        billidedt = findViewById(R.id.billID);
        contactedt = findViewById(R.id.contact);
        todateedt = findViewById(R.id.rangeDatetEDT);

//        Calculating And Formating DATE
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

//        Adding todays date On click of edit text
        dateedt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = dateedt.getText().toString();
                if (date.isEmpty()) {
                    dateedt.setText(formattedDate);
                }
            }
        });
// adding todays date on click in it
        todateedt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = todateedt.getText().toString();
                if (date.isEmpty()) {
                    todateedt.setText(formattedDate);
                }
            }
        });
//      Working with button

        searchbtn = findViewById(R.id.searchbtn);

//        Searcing -------------------------
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nametxt, datetxt, billIDtxt, contactTXT, ToDate;

                nametxt = nameedt.getText().toString();
                datetxt = dateedt.getText().toString();
                billIDtxt = billidedt.getText().toString();
                contactTXT = contactedt.getText().toString();
                ToDate = todateedt.getText().toString();


                if (nametxt.isEmpty() && datetxt.isEmpty() && billIDtxt.isEmpty() && contactTXT.isEmpty() && ToDate.isEmpty()) {
                    Toast.makeText(customer_Info.this, "Fill at least one information to search", Toast.LENGTH_SHORT).show();
                } else if (datetxt.isEmpty() && !ToDate.isEmpty()) {
                    Toast.makeText(customer_Info.this, "Enter Starting Date", Toast.LENGTH_SHORT).show();
                } else {
                    Cursor res;
                    res = DB.cusInfo(sellertxt);
                    if (!nametxt.isEmpty()) {
//                        Toast.makeText(customer_Info.this, "Search by name", Toast.LENGTH_SHORT).show();
                        res = DB.CustomerNameBill(nametxt, sellertxt);
                    } else if (!contactTXT.isEmpty()) {
//                        Toast.makeText(customer_Info.this, "Search by contact number", Toast.LENGTH_SHORT).show();
//                        CustomerNameBill
                        res = DB.Customernumberbill(contactTXT, sellertxt);
                    } else if (!datetxt.isEmpty()) {
//                        Toast.makeText(customer_Info.this, "Search by Date", Toast.LENGTH_SHORT).show();
                        if (!ToDate.isEmpty()) {
                            res = DB.rangeSearch(datetxt, ToDate, sellertxt);
                        } else {
                            res = DB.CustomerDateBill(datetxt, sellertxt);
                        }
                    } else if (!billIDtxt.isEmpty()) {
                        Integer billID;
                        billID = Integer.parseInt(billIDtxt);
//                        Toast.makeText(customer_Info.this, "Search by BILL ID", Toast.LENGTH_SHORT).show();
                        res = DB.CustomerBillID(billID, sellertxt);
                    } else {
                        res = DB.cusInfo(sellertxt);
                        Toast.makeText(customer_Info.this, "Error", Toast.LENGTH_SHORT).show();

                    }


                    if (res.getCount() == 0) {
                        Toast.makeText(customer_Info.this, "No Entry Exist", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int total = 0;
                    StringBuffer buffer = new StringBuffer();
                    while (res.moveToNext()) {
//                    DATE | name | number | Total |
                        buffer.append("Bill ID = " + res.getString(8) + "\n");
                        buffer.append("Customer Name = " + res.getString(5) + "\n");
                        buffer.append("Customer Number = " + res.getString(6) + "\n");
                        buffer.append("Date = " + res.getString(7) + "\n");
                        buffer.append("Product Name = " + res.getString(1) + "\n");
                        buffer.append("Price = " + res.getString(2) + "\n");
                        buffer.append("Quantity = " + res.getString(3) + "\n");
                        buffer.append("Sub Total = " + res.getString(4) + "\n\n");
                        total += Integer.parseInt(res.getString(4));
                    }

                    buffer.append("Total = " + total);

                    AlertDialog.Builder builder = new AlertDialog.Builder(customer_Info.this);
                    builder.setCancelable(true);
                    builder.setTitle("Bills");
                    builder.setMessage(buffer.toString());
                    builder.show();
                }
            }
        });

//        PDF WORKING

        pdf = findViewById(R.id.pdfC);
        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createPDF();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            @SuppressLint("DefaultLocale")
            private String getrandom() {
                Random rnd = new Random();
                int otp = rnd.nextInt(999999999);
                return String.format("%09d", otp);
            }

            //            Creating PDF
            private void createPDF() throws FileNotFoundException {
                String nametxt, datetxt, billIDtxt, contactTXT, ToDate;

                nametxt = nameedt.getText().toString();
                datetxt = dateedt.getText().toString();
                billIDtxt = billidedt.getText().toString();
                contactTXT = contactedt.getText().toString();
                ToDate = todateedt.getText().toString();

                if (nametxt.isEmpty() && datetxt.isEmpty() && billIDtxt.isEmpty() && contactTXT.isEmpty()) {
                    Toast.makeText(customer_Info.this, "Fill at least one information to search", Toast.LENGTH_SHORT).show();
                } else {


                    String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
//                File name
//                    String id = String.valueOf(billId);
//                Create file object
                    String s = getrandom();
                    File file = new File(pdfPath, "Renewed BILL" + s + ".pdf");
                    OutputStream outputStream = new FileOutputStream(file);

                    PdfWriter writer = new PdfWriter(file);
                    PdfDocument pdfDocument = new PdfDocument(writer);
                    Document document = new Document(pdfDocument);

                    float cWidth[] = {120, 220, 120, 100};
                    Table table1 = new Table(cWidth);

//        Table 1 do this
//        Want users||||| NAME EMail GST ADDRESS NUMBER
//                           0  1     3   5       4

                    Cursor selerDATA = DB.GetUser(sellertxt);
                    if (selerDATA.getCount() == 0) {
                        Toast.makeText(customer_Info.this, "No Entry Exist", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        selerDATA.moveToFirst();
                        do {
                            table1.addCell(new Cell().add(new Paragraph("Seller Name").setFontSize(14)).setBorder(Border.NO_BORDER));
                            table1.addCell(new Cell().add(new Paragraph(selerDATA.getString(0) + "").setFontSize(14)).setBorder(Border.NO_BORDER));
//                            table1.addCell(new Cell().add(new Paragraph("Seller Name").setFontSize(14)));
//                            table1.addCell(new Cell().add(new Paragraph(selerDATA.getString(0)+"").setFontSize(14)));

                            table1.addCell(new Cell().add(new Paragraph("Seller Email").setFontSize(14)).setBorder(Border.NO_BORDER));
                            table1.addCell(new Cell().add(new Paragraph(selerDATA.getString(1) + "").setFontSize(14)).setBorder(Border.NO_BORDER));
//                            table1.addCell(new Cell().add(new Paragraph("Seller Email").setFontSize(14)));
//                            table1.addCell(new Cell().add(new Paragraph(selerDATA.getString(1)+"").setFontSize(14)));

                            table1.addCell(new Cell().add(new Paragraph("Seller Number").setFontSize(14)).setBorder(Border.NO_BORDER));
                            table1.addCell(new Cell().add(new Paragraph(selerDATA.getString(4) + "").setFontSize(14)).setBorder(Border.NO_BORDER));
//                            table1.addCell(new Cell().add(new Paragraph("Seller Number").setFontSize(14)));
//                            table1.addCell(new Cell().add(new Paragraph(selerDATA.getString(4)+"").setFontSize(14)));

                            table1.addCell(new Cell().add(new Paragraph("Seller GST").setFontSize(14)).setBorder(Border.NO_BORDER));
                            table1.addCell(new Cell().add(new Paragraph(selerDATA.getString(3) + "").setFontSize(14)).setBorder(Border.NO_BORDER));
//                            table1.addCell(new Cell().add(new Paragraph("Seller GST").setFontSize(14)));
//                            table1.addCell(new Cell().add(new Paragraph(selerDATA.getString(3)+"").setFontSize(14)));

                            table1.addCell(new Cell().add(new Paragraph("Address").setFontSize(14)).setBorder(Border.NO_BORDER));
                            table1.addCell(new Cell().add(new Paragraph(selerDATA.getString(5) + "").setFontSize(14)).setBorder(Border.NO_BORDER));
//                            table1.addCell(new Cell().add(new Paragraph("Address").setFontSize(14)));
//                            table1.addCell(new Cell().add(new Paragraph(selerDATA.getString(5)+"").setFontSize(14)));
                        } while (selerDATA.moveToNext());
                    }


                    //        Table 2 do this    FROM BILLID
//        Want display ||||||  customerName=5 customerNumber=6 date=7

                    float cWidth3[] = {142, 142, 142, 142};
                    Table table3 = new Table(cWidth3);

                    float cWidth5[] = {142, 142, 142, 142};
                    Table table5 = new Table(cWidth5);

                    float cWidth2[] = {270, 100, 100, 100};
                    Table table2 = new Table(cWidth2);


                    Cursor customerDetail;
                    Cursor list;
                    customerDetail = DB.cusInfo(sellertxt);
                    int checker = 0;

                    if (!nametxt.isEmpty()) {
                        checker = 1;
                        customerDetail = DB.CustomerNameBill(nametxt, sellertxt);
                        list = DB.CustomerNameBill(nametxt, sellertxt);
                    } else if (!contactTXT.isEmpty()) {
                        checker = 2;
                        customerDetail = DB.Customernumberbill(contactTXT, sellertxt);
                        list = DB.Customernumberbill(contactTXT, sellertxt);
                    } else if (!datetxt.isEmpty()) {
                        if (!ToDate.isEmpty()) {
                            checker = 4;
                            customerDetail = DB.rangeSearch(datetxt, ToDate, sellertxt);
                            list = DB.rangeSearch(datetxt, ToDate, sellertxt);
                        } else {
                            checker = 3;
                            customerDetail = DB.CustomerDateBill(datetxt, sellertxt);
                            list = DB.CustomerDateBill(datetxt, sellertxt);
                        }
                    } else if (!billIDtxt.isEmpty()) {
                        checker = 5;
                        Integer billID;
                        billID = Integer.parseInt(billIDtxt);
                        customerDetail = DB.CustomerBillID(billID, sellertxt);
                        list = DB.CustomerBillID(billID, sellertxt);
                    } else {
                        customerDetail = DB.cusInfo(sellertxt);
                        list = DB.cusInfo(sellertxt);
                        Toast.makeText(customer_Info.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                    if (customerDetail.getCount() == 0) {
                        Toast.makeText(customer_Info.this, "No Entry Exist", Toast.LENGTH_SHORT).show();
                    }
                    else {
//                        ------------------------------------------------------------------------------
                        if (checker == 3 || checker == 4) {
                            customerDetail.moveToFirst();
                            do {
                                table5.addCell(new Cell().add(new Paragraph("Customer Name").setFontSize(14)).setBorder(Border.NO_BORDER));
                                table5.addCell(new Cell().add(new Paragraph(customerDetail.getString(5) + "").setFontSize(14)).setBorder(Border.NO_BORDER));
                                table5.addCell(new Cell().add(new Paragraph("Customer Number").setFontSize(14)).setBorder(Border.NO_BORDER));
                                table5.addCell(new Cell().add(new Paragraph(customerDetail.getString(6) + "").setFontSize(14)).setBorder(Border.NO_BORDER));
//                            table3.addCell(new Cell().add(new Paragraph("Customer Name").setFontSize(14)));
//                            table3.addCell(new Cell().add(new Paragraph(customerDetail.getString(5)+"").setFontSize(14)));
//
//                            table3.addCell(new Cell().add(new Paragraph("Customer Number").setFontSize(14)));
//                            table3.addCell(new Cell().add(new Paragraph(customerDetail.getString(6)+"").setFontSize(14)));
//                                if (!ToDate.isEmpty() && !datetxt.isEmpty()) {
//                                    table5.addCell(new Cell().add(new Paragraph("From Date").setFontSize(14)).setBorder(Border.NO_BORDER));
//                                    table5.addCell(new Cell().add(new Paragraph(customerDetail.getString(7) + "").setFontSize(14)).setBorder(Border.NO_BORDER));
//                                    table5.addCell(new Cell().add(new Paragraph("To Date").setFontSize(14)).setBorder(Border.NO_BORDER));
//                                    table5.addCell(new Cell().add(new Paragraph(ToDate + "").setFontSize(14)).setBorder(Border.NO_BORDER));
//
//                                 table3.addCell(new Cell().add(new Paragraph("From Date").setFontSize(14)));
//                                table3.addCell(new Cell().add(new Paragraph(customerDetail.getString(7)+"").setFontSize(14)));
//                                table3.addCell(new Cell().add(new Paragraph("To Date").setFontSize(14)));
//                                table3.addCell(new Cell().add(new Paragraph(ToDate+"").setFontSize(14)));
//                                } else {
                                    table5.addCell(new Cell().add(new Paragraph("Date").setFontSize(14)).setBorder(Border.NO_BORDER));
                                    table5.addCell(new Cell().add(new Paragraph(customerDetail.getString(7) + "").setFontSize(14)).setBorder(Border.NO_BORDER));
                                    table5.addCell(new Cell().setBorder(Border.NO_BORDER));
                                    table5.addCell(new Cell().setBorder(Border.NO_BORDER));
//                                table3.addCell(new Cell().add(new Paragraph("Date").setFontSize(14)));
//                                table3.addCell(new Cell().add(new Paragraph(customerDetail.getString(7)+"").setFontSize(14)));
//                                }

//                            table3.addCell((new Cell().add(new Paragraph("Bill ID: ").setFontSize(14))));
//                            table3.addCell((new Cell().add(new Paragraph(customerDetail.getString(8)).setFontSize(14))));
                                table5.addCell(new Cell().add(new Paragraph("Bill ID").setFontSize(14)).setBorder(Border.NO_BORDER));
                                table5.addCell(new Cell().add(new Paragraph(customerDetail.getString(8) + "").setFontSize(14)).setBorder(Border.NO_BORDER));
                                table5.addCell(new Cell().setBorder(Border.NO_BORDER));
                                table5.addCell(new Cell().setBorder(Border.NO_BORDER));

//Just Printing headings -----------------------------------------------------------------------
                                table5.addCell(new Cell().add(new Paragraph("Product Name")));
                                table5.addCell(new Cell().add(new Paragraph("Product Price")));
                                table5.addCell(new Cell().add(new Paragraph("Product Quantity")));
                                table5.addCell(new Cell().add(new Paragraph("Sub Total")));

//                                customerDetail.moveToFirst();

//                                this index help to privent repit table printing
                                int index = 0;
                                int total = 0;
                                if (list.getCount() == 0) {
                                    Toast.makeText(customer_Info.this, "No Entry Exist", Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
                                    list.moveToFirst();
                                    do {
                                        if (customerDetail.getString(8).equals(list.getString(8))) {
                                            table5.addCell(new Cell().add(new Paragraph(list.getString(1) + "")));
                                            table5.addCell(new Cell().add(new Paragraph(list.getString(2) + "")));
                                            table5.addCell(new Cell().add(new Paragraph(list.getString(3) + "")));
                                            table5.addCell(new Cell().add(new Paragraph(list.getString(4) + "")));
                                            total += list.getInt(4);
                                            index++;
                                        }
                                    } while (list.moveToNext());
                                }

                                int ix = customerDetail.getPosition() + index;
                                customerDetail.moveToPosition(ix);

                                table5.addCell(new Cell(1, 3).add(new Paragraph("Total")));
                                table5.addCell(new Cell().add(new Paragraph(total + "")));
                                table5.addCell(new Cell(1, 4).setBorder(Border.NO_BORDER));

                            } while (customerDetail.moveToNext());
// -----------------------------------------------------------------------------------------------------------------------
                        }
                        else if(checker == 1 || checker == 2){
                            customerDetail.moveToFirst();
                            do {
                                table5.addCell(new Cell().add(new Paragraph("Customer Name").setFontSize(14)).setBorder(Border.NO_BORDER));
                                table5.addCell(new Cell().add(new Paragraph(customerDetail.getString(5) + "").setFontSize(14)).setBorder(Border.NO_BORDER));
                                table5.addCell(new Cell().add(new Paragraph("Customer Number").setFontSize(14)).setBorder(Border.NO_BORDER));
                                table5.addCell(new Cell().add(new Paragraph(customerDetail.getString(6) + "").setFontSize(14)).setBorder(Border.NO_BORDER));
//                            table3.addCell(new Cell().add(new Paragraph("Customer Name").setFontSize(14)));
//                            table3.addCell(new Cell().add(new Paragraph(customerDetail.getString(5)+"").setFontSize(14)));
//
//                            table3.addCell(new Cell().add(new Paragraph("Customer Number").setFontSize(14)));
//                            table3.addCell(new Cell().add(new Paragraph(customerDetail.getString(6)+"").setFontSize(14)));
//                                if (!ToDate.isEmpty() && !datetxt.isEmpty()) {
//                                    table5.addCell(new Cell().add(new Paragraph("From Date").setFontSize(14)).setBorder(Border.NO_BORDER));
//                                    table5.addCell(new Cell().add(new Paragraph(customerDetail.getString(7) + "").setFontSize(14)).setBorder(Border.NO_BORDER));
//                                    table5.addCell(new Cell().add(new Paragraph("To Date").setFontSize(14)).setBorder(Border.NO_BORDER));
//                                    table5.addCell(new Cell().add(new Paragraph(ToDate + "").setFontSize(14)).setBorder(Border.NO_BORDER));
//
//                                 table3.addCell(new Cell().add(new Paragraph("From Date").setFontSize(14)));
//                                table3.addCell(new Cell().add(new Paragraph(customerDetail.getString(7)+"").setFontSize(14)));
//                                table3.addCell(new Cell().add(new Paragraph("To Date").setFontSize(14)));
//                                table3.addCell(new Cell().add(new Paragraph(ToDate+"").setFontSize(14)));
//                                } else {
                                    table5.addCell(new Cell().add(new Paragraph("Date").setFontSize(14)).setBorder(Border.NO_BORDER));
                                    table5.addCell(new Cell().add(new Paragraph(customerDetail.getString(7) + "").setFontSize(14)).setBorder(Border.NO_BORDER));
                                    table5.addCell(new Cell().setBorder(Border.NO_BORDER));
                                    table5.addCell(new Cell().setBorder(Border.NO_BORDER));
//                                table3.addCell(new Cell().add(new Paragraph("Date").setFontSize(14)));
//                                table3.addCell(new Cell().add(new Paragraph(customerDetail.getString(7)+"").setFontSize(14)));
//                                }

//                            table3.addCell((new Cell().add(new Paragraph("Bill ID: ").setFontSize(14))));
//                            table3.addCell((new Cell().add(new Paragraph(customerDetail.getString(8)).setFontSize(14))));
                                table5.addCell(new Cell().add(new Paragraph("Bill ID").setFontSize(14)).setBorder(Border.NO_BORDER));
                                table5.addCell(new Cell().add(new Paragraph(customerDetail.getString(8) + "").setFontSize(14)).setBorder(Border.NO_BORDER));
                                table5.addCell(new Cell().setBorder(Border.NO_BORDER));
                                table5.addCell(new Cell().setBorder(Border.NO_BORDER));

//Just Printing headings -----------------------------------------------------------------------
                                table5.addCell(new Cell().add(new Paragraph("Product Name")));
                                table5.addCell(new Cell().add(new Paragraph("Product Price")));
                                table5.addCell(new Cell().add(new Paragraph("Product Quantity")));
                                table5.addCell(new Cell().add(new Paragraph("Sub Total")));

//                                customerDetail.moveToFirst();

//                                this index help to privent repit table printing
                                int index = 0;
                                int total = 0;
                                if (list.getCount() == 0) {
                                    Toast.makeText(customer_Info.this, "No Entry Exist", Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
                                    list.moveToFirst();

                                    do {
                                        if (customerDetail.getString(8).equals(list.getString(8))) {
                                            table5.addCell(new Cell().add(new Paragraph(list.getString(1) + "")));
                                            table5.addCell(new Cell().add(new Paragraph(list.getString(2) + "")));
                                            table5.addCell(new Cell().add(new Paragraph(list.getString(3) + "")));
                                            table5.addCell(new Cell().add(new Paragraph(list.getString(4) + "")));
                                            total += list.getInt(4);
                                            index++;
                                        }
                                    } while (list.moveToNext());
                                }

                                int ix = customerDetail.getPosition() + index - 1;
                                customerDetail.moveToPosition(ix);

                                table5.addCell(new Cell(1, 3).add(new Paragraph("Total")));
                                table5.addCell(new Cell().add(new Paragraph(total + "")));
                                table5.addCell(new Cell(1, 4).setBorder(Border.NO_BORDER));

                            } while (customerDetail.moveToNext());
                        }
//                        _----------------------------------------------------------------------------
                        else {
                            customerDetail.moveToFirst();

                            table3.addCell(new Cell().add(new Paragraph("Customer Name").setFontSize(14)).setBorder(Border.NO_BORDER));
                            table3.addCell(new Cell().add(new Paragraph(customerDetail.getString(5) + "").setFontSize(14)).setBorder(Border.NO_BORDER));
                            table3.addCell(new Cell().add(new Paragraph("Customer Number").setFontSize(14)).setBorder(Border.NO_BORDER));
                            table3.addCell(new Cell().add(new Paragraph(customerDetail.getString(6) + "").setFontSize(14)).setBorder(Border.NO_BORDER));
//                            table3.addCell(new Cell().add(new Paragraph("Customer Name").setFontSize(14)));
//                            table3.addCell(new Cell().add(new Paragraph(customerDetail.getString(5)+"").setFontSize(14)));
//
//                            table3.addCell(new Cell().add(new Paragraph("Customer Number").setFontSize(14)));
//                            table3.addCell(new Cell().add(new Paragraph(customerDetail.getString(6)+"").setFontSize(14)));

                            if (!ToDate.isEmpty() && !datetxt.isEmpty()) {
                                table3.addCell(new Cell().add(new Paragraph("From Date").setFontSize(14)).setBorder(Border.NO_BORDER));
                                table3.addCell(new Cell().add(new Paragraph(customerDetail.getString(7) + "").setFontSize(14)).setBorder(Border.NO_BORDER));
                                table3.addCell(new Cell().add(new Paragraph("To Date").setFontSize(14)).setBorder(Border.NO_BORDER));
                                table3.addCell(new Cell().add(new Paragraph(ToDate + "").setFontSize(14)).setBorder(Border.NO_BORDER));
//
//                                 table3.addCell(new Cell().add(new Paragraph("From Date").setFontSize(14)));
//                                table3.addCell(new Cell().add(new Paragraph(customerDetail.getString(7)+"").setFontSize(14)));
//                                table3.addCell(new Cell().add(new Paragraph("To Date").setFontSize(14)));
//                                table3.addCell(new Cell().add(new Paragraph(ToDate+"").setFontSize(14)));

                            } else {
                                table3.addCell(new Cell().add(new Paragraph("Date").setFontSize(14)).setBorder(Border.NO_BORDER));
                                table3.addCell(new Cell().add(new Paragraph(customerDetail.getString(7) + "").setFontSize(14)).setBorder(Border.NO_BORDER));
//                                table3.addCell(new Cell().add(new Paragraph("Date").setFontSize(14)));
//                                table3.addCell(new Cell().add(new Paragraph(customerDetail.getString(7)+"").setFontSize(14)));
                            }

//                            table3.addCell((new Cell().add(new Paragraph("Bill ID: ").setFontSize(14))));
//                            table3.addCell((new Cell().add(new Paragraph(customerDetail.getString(8)).setFontSize(14))));
                            table3.addCell(new Cell().add(new Paragraph("Bill ID").setFontSize(14)).setBorder(Border.NO_BORDER));
                            table3.addCell(new Cell().add(new Paragraph(customerDetail.getString(8) + "").setFontSize(14)).setBorder(Border.NO_BORDER));
////Just Printing headings -----------------------------------------------------------------------
                            table2.addCell(new Cell().add(new Paragraph("Product Name")));
                            table2.addCell(new Cell().add(new Paragraph("Product Price")));
                            table2.addCell(new Cell().add(new Paragraph("Product Quantity")));
                            table2.addCell(new Cell().add(new Paragraph("Sub Total")));

                            customerDetail.moveToFirst();

                            int total = 0;

                            if (list.getCount() == 0) {
                                Toast.makeText(customer_Info.this, "No Entry Exist", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                list.moveToFirst();
                                do {
                                    table2.addCell(new Cell().add(new Paragraph(list.getString(1) + "")));
                                    table2.addCell(new Cell().add(new Paragraph(list.getString(2) + "")));
                                    table2.addCell(new Cell().add(new Paragraph(list.getString(3) + "")));
                                    table2.addCell(new Cell().add(new Paragraph(list.getString(4) + "")));
                                    total += list.getInt(4);
                                } while (list.moveToNext());
                            }

                            table2.addCell(new Cell(1, 3).add(new Paragraph("Total")));
                            table2.addCell(new Cell().add(new Paragraph(total + "")));

                        }
//                        ---------------------------Working------------------------------------------
//                Displaying data
                        document.add(table1);
                        document.add(new Paragraph("\n"));
                        if (checker <=4) {
                            document.add(table5);
                        } else {
                            document.add(table3);
                            document.add(new Paragraph("\n"));
                            document.add(table2);
                        }
                        document.close();
                        Toast.makeText(customer_Info.this, "PDF Created", Toast.LENGTH_SHORT).show();

//                Opening PDf ---------------------------------
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                            if (file.exists()) {
                                Uri uri = FileProvider.getUriForFile(customer_Info.this, getApplicationContext().getPackageName() + ".provider", file);
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(uri, "application/pdf");
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                startActivity(intent);
                            } else {
                                Toast.makeText(customer_Info.this, "File can't be created", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                }
            }
        });

        showbtn = findViewById(R.id.showallData);

        showbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Cursor res = DB.cusInfo(sellertxt);
                if (res.getCount() == 0) {
                    Toast.makeText(customer_Info.this, "No Entry Exist", Toast.LENGTH_SHORT).show();
                    return;
                }

                StringBuffer buffer = new StringBuffer();
                while (res.moveToNext()) {
//                    DATE | name | number | Total |
                    buffer.append("Bill ID = " + res.getString(0) + "\n");
                    buffer.append("Customer Name = " + res.getString(1) + "\n");
                    buffer.append("Customer Number = " + res.getString(2) + "\n");
                    buffer.append("Date = " + res.getString(3) + "\n");
                    buffer.append("Total = " + res.getString(4) + "\n\n");
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(customer_Info.this);
                builder.setCancelable(true);
                builder.setTitle("Bills");
                builder.setMessage(buffer.toString());
                builder.show();

            }
        });

    }
}