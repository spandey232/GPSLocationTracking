package com.journaldev.gpslocationtracking;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {


    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;
    int count=0;
    String loc="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String[] str = {""};

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

        final Handler handler=new Handler();
        final Runnable[] runnable = new Runnable[1];
        Button btn1 = (Button) findViewById(R.id.btn1);
        Button btn2 = (Button) findViewById(R.id.btn2);




        final String location=getIntent().getStringExtra("location");

        Toast.makeText(getApplicationContext()," "+location, Toast.LENGTH_SHORT).show();



        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*--------------------------------------------------------*/

                runnable[0] =new Runnable() {
                    @Override
                    public void run() {


                        locationTrack = new LocationTrack(MainActivity.this);


                        if (locationTrack.canGetLocation()) {


                            double longitude = locationTrack.getLongitude();
                            double latitude = locationTrack.getLatitude();
                            double altitude=locationTrack.getAltitude();

                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
                            String strDate = mdformat.format(calendar.getTime());
                            String s="Location"+"\t\t"+"Time"+ "\t\t"+"Longitude"+"\t\t"+"Latitude"+"\t\t"+"Altitude\n";
                            if(count==0){ loc=location; count++;}
                            else loc="   "+"\t";
                            str[0]+=loc+"\t\t"+strDate + "\t"+Double.toString(longitude)  +"\t" +Double.toString(latitude)+"\t"+Double.toString(altitude)+"\n";

                            Toast.makeText(getApplicationContext(),strDate + "\t"+Double.toString(longitude)  +"\t" +Double.toString(latitude)+"\t"+Double.toString(altitude), Toast.LENGTH_SHORT).show();

                    /*-------------------------------------------------------------*/
                            String fileContents ="\n"+s+str[0];
                            FileOutputStream outputStream;
                            File rootDir=new File(getExternalFilesDir(null),"GPS File" );

                            rootDir.mkdirs();

                            //

                            try {
                                FileOutputStream fileOutputStream = new FileOutputStream(new File(rootDir, "myfile"));
                                // outputStream = openFileOutput(new File(rootDir, "myfile"),MODE_PRIVATE);
                                fileOutputStream.write(fileContents.getBytes());
                                fileOutputStream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                    /*-------------------------------------------------------------*/
                        } else {

                            locationTrack.showSettingsAlert();
                        }
                        handler.postDelayed(this,100);

                    }
                };
                runnable[0].run();

                /*-------------------------------------------------*/
            }
        });


        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                handler.removeCallbacks(runnable[0]);

            }
        });


    }


    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTrack.stopListener();
    }


}