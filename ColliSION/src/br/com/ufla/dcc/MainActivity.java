package br.com.ufla.dcc;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import br.com.ufla.dcc.bluetooth.Bluetooth;
import br.com.ufla.dcc.gps.GPSTracker;

public class MainActivity extends Activity {

	Thread locationSender;
	Thread screenInformationUpdater;

	TextView myLatitude;
	TextView myLongitude;
	TextView otherLatitude;
	TextView otherLongitude;
	Button start;
	Button stop;

	GPSTracker gps;
	Bluetooth bluetooth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		myLatitude = (TextView) findViewById(R.id.myLatitude);
		myLongitude = (TextView) findViewById(R.id.myLongitude);
		otherLatitude = (TextView) findViewById(R.id.otherLatitude);
		otherLongitude = (TextView) findViewById(R.id.otherLongitude);
		start = (Button) findViewById(R.id.start);
		stop = (Button) findViewById(R.id.stop);

		gps = new GPSTracker(this);
		bluetooth = new Bluetooth("linvor");

		start.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (bluetooth.openBT()) {
					updateScreenInformation();
					sendLocation();
				}

			}

		});

		stop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (bluetooth.closeBT()) {
					locationSender.interrupt();
					screenInformationUpdater.interrupt();
				}
			}

		});

	}

	private void updateScreenInformation() {
		final Handler handler = new Handler();

		screenInformationUpdater = new Thread(new Runnable() {
			public void run() {
				Boolean go = true;
				while (go) {
					try {
						Thread.sleep(1000);

						String[] arrayData = bluetooth.getData().split("F");

						String currentOtherData = new String();

						if (arrayData.length > 2)
							currentOtherData = arrayData[arrayData.length - 2];

						arrayData = currentOtherData.split("Y");

						if (arrayData.length > 1) {
							final String myLat = new String(
									arrayData[0].replace("X", ""));
							final String myLong = new String(arrayData[1]);
							handler.post(new Runnable() {
								public void run() {
									setViewInfo(myLat, myLong);
								}
							});
						}

					} catch (InterruptedException e) {
						go = false;
						e.printStackTrace();
					}
				}
			}
		});

		screenInformationUpdater.start();
	}

	private void sendLocation() {
		locationSender = new Thread(new Runnable() {
			public void run() {
				Boolean go = true;
				while (go) {
					try {
						Thread.sleep(1000);
						if (gps.canGetLocation()) {
							Location location = gps.getLocation();
							bluetooth.sendData("X" + location.getLatitude()
									+ "Y" + location.getLongitude() + "F");
						}
					} catch (InterruptedException e) {
						go = false;
						e.printStackTrace();
					}
				}
			}
		});

		locationSender.start();

	}

	private void setViewInfo(String otherLat, String otherLong) {
		if (gps.canGetLocation()) {
			Location location = gps.getLocation();
			myLatitude.setText(String.valueOf(location.getLatitude()));
			myLongitude.setText(String.valueOf(location.getLongitude()));
			otherLatitude.setText(otherLat);
			otherLongitude.setText(otherLong);
		}
	}

//	public double getDistancia(double latitude, double longitude, double latitudePto, double longitudePto){  
//		  
//	      
//	    double ptolat = Math.toRadians(latitude);   
//	    double ptolon = Math.toRadians(longitude);  
//	    double pto2lat = Math.toRadians(latitudePto);  
//	    double pto2lon = Math.toRadians(longitudePto);  
//	  
//	  
//	  
//	    double dlon, dlat, a, distancia;  
//	  
//	        dlon = pto2lon - ptolon;  
//	        dlat = pto2lat  - ptolat;  
//	  
//	        a = Math.pow(Math.sin(dlat/2),2) + Math.cos(latitude) * Math.cos(latitudePto) * Math.pow(Math.sin(dlon/2),2);  
//	        distancia = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));  
//	  
//	      return distancia * 6378140;   
//	 }  

}
