package com.example.cnsgeofence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationStatusCodes;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements OnClickListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, LocationListener,
        OnAddGeofencesResultListener, OnMapClickListener {
	GoogleMap gMap;
	LocationClient locationClient;
	Marker marker;
	LatLng workingLatLng = null;
	int workingRadius = 0;
	Circle workingCircle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initMap();
		gMap.setOnMapClickListener(this);
		locationClient = new LocationClient(this, this, this);
		locationClient.connect();
		Button btnShow = (Button) findViewById(R.id.btnShow);
		Button btnSave = (Button) findViewById(R.id.btnSave);
		btnShow.setOnClickListener(this);
		btnSave.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.currentLocation:
			gotoCurrentLocation();
			break;
		}
		return true;
	}

	private void gotoCurrentLocation() {
		// startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		if (locationClient.isConnected()) {
			Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		}
		Location currentLoc = locationClient.getLastLocation();
		if (currentLoc != null) {
			LatLng latLng = new LatLng(currentLoc.getLatitude(),
			        currentLoc.getLongitude());
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 18);
			gMap.animateCamera(update);
//			MarkerOptions marker = new MarkerOptions().position(latLng);
//			gMap.addMarker(marker);
		} else {
			Toast.makeText(this, "Its null", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnShow) {
			EditText etRadius = (EditText) findViewById(R.id.editText1);
			if (etRadius.getText() != null
			        && etRadius.getText().toString() != null
			        && !etRadius.getText().toString().equals("")) {
				workingRadius = Integer.parseInt(etRadius.getText().toString());
				if (workingLatLng != null) {
					if (workingCircle == null) {
						workingCircle = gMap.addCircle(new CircleOptions()
						        .center(workingLatLng).radius(workingRadius)
						        .strokeColor(Color.BLUE));
					} else {
						workingCircle.setRadius(workingRadius);
						workingCircle.setCenter(workingLatLng);
					}

				}
			}
		} else {
			addFence();
		}
	}

	private void addFence() {
		PendingIntent mGeofencePendingIntent = createRequestPendingIntent();
		if (workingLatLng != null && workingRadius != 0) {
			List<Geofence> geoFenceList = new ArrayList<Geofence>();
			Date date = new java.util.Date();
			// Send a request to add the current geofences
			Geofence geoFence = new Geofence.Builder()
			        .setRequestId(
			                date.getDate() + "-" + date.getHours() + "-"
			                        + date.getMinutes())
			        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
			        .setCircularRegion(workingLatLng.latitude,
			                workingLatLng.longitude, workingRadius)
			        .setExpirationDuration(Geofence.NEVER_EXPIRE).build();
			geoFenceList.add(geoFence);
			locationClient.addGeofences(geoFenceList, mGeofencePendingIntent,
			        this);
		}

	}

	private static void generateNotification(Context context, String message) {
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
		        .getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);

		String title = context.getString(R.string.app_name);

		Intent notificationIntent = new Intent(context, MainActivity.class);
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
		        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
		        notificationIntent, 0);
		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Play default notification sound
		notification.defaults |= Notification.DEFAULT_SOUND;

		// Vibrate if vibrate is enabled
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(0, notification);

	}

	private PendingIntent createRequestPendingIntent() {
		// Create an Intent pointing to the IntentService
		Intent intent = new Intent(this,
		        CNSGeofenceTransitionIntentService.class);

		return PendingIntent.getService(this, 0, intent,
		        PendingIntent.FLAG_UPDATE_CURRENT);
	}

	private void initMap() {
		SupportMapFragment mMap = (SupportMapFragment) getSupportFragmentManager()
		        .findFragmentById(R.id.cnsmap);
		gMap = mMap.getMap();
	}

	@Override
	public void onAddGeofencesResult(int statusCode, String[] arg1) {
		if (LocationStatusCodes.SUCCESS == statusCode) {
			generateNotification(this, "Geo fence added");
		} else {
			Toast.makeText(this, "Adding GeoFence Failed", Toast.LENGTH_LONG)
			        .show();
		}
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapClick(LatLng arg0) {
		if (marker == null)
			marker = gMap.addMarker(new MarkerOptions().position(arg0));
		else
			marker.setPosition(arg0);
		workingLatLng = arg0;
	}
}
