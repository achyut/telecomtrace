package pavan.gpslocation.com.gpslocation;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.CellInfoGsm;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LocationManager locationManager = null;
    private LocationListener locationListener = null;
    private TextView tvLocation;
    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int requestCode = 1;
        setContentView(R.layout.activity_main);
        tvLocation = (TextView) findViewById(R.id.tv_location);



        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                requestCode);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                tvLocation.setText("");
                double lat = location.getLatitude();
                double longi = location.getLongitude();
                tvLocation.setText("Lat:" + lat + "Long:" + longi);
                System.out.println("Lat:" + lat + "Long:" + longi);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    5000,
                    10,
                    locationListener);
        } catch (SecurityException e){
            e.printStackTrace();
        }


        btn = (Button) findViewById(R.id.btn_click);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo Info = cm.getActiveNetworkInfo();
                if (Info == null || !Info.isConnectedOrConnecting()) {
                    Log.i("Test", "No connection");
                } else {
                    int netType = Info.getType();
                    int netSubtype = Info.getSubtype();

                    if (netType == ConnectivityManager.TYPE_WIFI) {
                        Log.i("TEST", "Wifi connection");
                        WifiManager wifiManager = (WifiManager) getApplication().getSystemService(Context.WIFI_SERVICE);
                        List<ScanResult> scanResult = wifiManager.getScanResults();
                        for (int i = 0; i < scanResult.size(); i++) {
                            Log.d("scanResult", "Speed of wifi" + scanResult.get(i).level);//The db level of signal
                        }


                        // Need to get wifi strength
                    } else if (netType == ConnectivityManager.TYPE_MOBILE) {
                        Log.i("Gprs / 3g location", "GPRS/3G connection");


                        // Need to get differentiate between 3G/GPRS
                        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM){
                            GsmCellLocation gsmCellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
                            String msg = telephonyManager.getAllCellInfo().toString();
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            Log.d("Message:" , msg);
                        }
                        else{
                            CellInfoGsm cellinfogsm = (CellInfoGsm) telephonyManager.getAllCellInfo().get(0);
                            CellSignalStrengthGsm cellSignalStrengthGsm = cellinfogsm.getCellSignalStrength();
                            int strngth = cellSignalStrengthGsm.getDbm();
                            Log.d("The Signal Strength is:", "" + strngth);
                        }
                    }
                }
            }
        });
    }
}
