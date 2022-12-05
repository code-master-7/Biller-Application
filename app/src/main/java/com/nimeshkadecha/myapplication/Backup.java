package com.nimeshkadecha.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Objects;
import java.util.Random;

public class Backup extends AppCompatActivity {

    private EditText otpedt;

    private Button verify;

    private ImageView menuclick;

    private String OTP, usertxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

//        TOOL BAR ---------------------------------------------------------------------
        //        Removing Suport bar / top line containing name
        Objects.requireNonNull(getSupportActionBar()).hide();

//        FINDING menu
        menuclick = findViewById(R.id.Menu);

//        Keeping MENUE Invisible
        menuclick.setVisibility(View.INVISIBLE);


//        TOOL BAR /---------------------------------------------------------------------

//        Finding
        otpedt = findViewById(R.id.bOTP);
        verify = findViewById(R.id.bVerify);

//        Getting OTP in INTENT
        Bundle bOTP = getIntent().getExtras();
        OTP = bOTP.getString("bOTP");

//        Calling Backgroung class
        BackgroungTask backgroungTask = new BackgroungTask();
        backgroungTask.execute(OTP);

//        Verify Button
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otptxt = otpedt.getText().toString();

                boolean otpV = OTPValidate(otptxt);
                if (otpV) {
                    if (otptxt.equals(OTP)) {
                        Intent backup = new Intent(Backup.this, Backup_Working.class);
                        Bundle user = getIntent().getExtras();
                        usertxt = user.getString("user");
                        backup.putExtra("user", usertxt);
                        startActivity(backup);
                    } else {
                        Toast.makeText(Backup.this, "Wrong OTP", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Backup.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    Validating OTP
    private boolean OTPValidate(String otpInput) {
        if (otpInput.length() < 6) {
            return false;
        } else {
            return true;
        }
    }

//    Notification Process in Background
    class BackgroungTask extends AsyncTask<String, Void, Void> {
        String b = "BackupOTP";

        @Override
        protected Void doInBackground(String... args) {
            String OTP = args[0];
            //       Start Working with NOTIFICATIOn -----------------------------------------------

            NotificationCompat.Builder builder = new NotificationCompat.Builder(Backup.this, b)
                    .setSmallIcon(R.drawable.message)
                    .setContentTitle("Login OTP for Backup")
                    .setContentText("Your OTP is " + OTP + " Do not share this OTP with others\"")
                    .setPriority(NotificationCompat.DEFAULT_VIBRATE)
                    .setPriority(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                    .setPriority(NotificationCompat.PRIORITY_MAX);

//        Creating chennel and set importance
//        private void createNotificationChannel () {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = getString(R.string.Biller);
                String description = getString(R.string.description);
                int importance = NotificationManager.IMPORTANCE_DEFAULT;

                NotificationChannel channel = new NotificationChannel(b, name, importance);
                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
//    }

            // Create an explicit intent for an Activity in your app
            Intent intent = new Intent(Backup.this, Backup.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(Backup.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builderr = new NotificationCompat.Builder(Backup.this, b)
                    .setSmallIcon(R.drawable.message)
                    .setContentTitle("Otp for reset password")
                    .setContentText("Your OTP is " + OTP + " Do not share this OTP with others")
                    .setPriority(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

//        SHOW NOTIFICATION
            class notify extends Thread {
                void sleep() {
                    try {
                        int time = Integer.parseInt(sleepTime());
                        Thread.sleep(time);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Backup.this);
                        // notificationId is a unique int for each notification that you must define
                        notificationManager.notify(1, builder.build());
                    } catch (Exception e) {
                        Toast.makeText(Backup.this, "e", Toast.LENGTH_SHORT).show();
                    }
                }

                @SuppressLint("DefaultLocale")
                private String sleepTime() {
                    Random rnd = new Random();
                    int otp = rnd.nextInt(9999);
                    return String.format("%04d", otp);
                }
            }
            notify n = new notify();

            n.sleep();

            return null;
        }
    }
}