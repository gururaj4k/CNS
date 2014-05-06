package com.example.cns;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cns.domains.DEVICEORIENTATION;
import com.example.cns.domains.DIRECTION;
import com.example.cns.domains.Point;
import com.example.cns.domains.PointDistance;
import com.example.cns.domains.PointDistanceComparator;
import com.example.cns.domains.TURN;
import com.example.testSoap.wcf.BSKcnsServicePortBinding;
import com.example.testSoap.wcf.BSKgetDirectionsResponse;
import com.example.testSoap.wcf.BSKgetPartitionsWithDirectionsResponse;
import com.example.testSoap.wcf.BSKgetPreCollectedPrintsResponse;
import com.example.testSoap.wcf.BSKpoint;
import com.example.testSoap.wcf.BSKpoint_accessPoints_entry;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends FragmentActivity implements OnClickListener,
        SensorEventListener, TextToSpeech.OnInitListener {

	Marker sourceMarker;
	Marker targetMarker;
	GoogleMap gMap;
	LocationClient locationClient;
	GroundOverlay groundOverlay1;
	WifiManager wifiManager;
	List<Point> preCollectedPrints = null;
	Polyline routeLine = null;
	List<BSKpoint> navigationPath = null;
	Point location = new Point();
	int scanTimes = 3;
	float timeout = 1.5f;
	HashMap<String, Integer> macCount = null;
	List<Point> neighbourList = null;
	short numberofAttempts = 1;
	short numberOfAps = 0;
	boolean iscurrLocationUpdated = false;

	private long lastUpdate;
	private SensorManager sensorManager;
	private DEVICEORIENTATION deviceOrt;
	private DIRECTION currDirection;
	float[] mGravity = null;
	float[] mGeomagnetic = null;
	private static String endpointUrl = "http://54.186.110.123:8080/CNSServer/CNSController?wsdl";
	private static final LatLng STARTPOINT = new LatLng(41.874483, -87.654578);
	long[] directionsArr = new long[4];
	String[] partitionArr = new String[4];
	TextView tvStatus;
	TextView tvLocation;
	EditText etScanTimes;
	List<String> instructionsSet;
	AlertDialog.Builder builder;
	AlertDialog alt_bld;
	Point destinationPoint;
	List<ScanResult> scanResults;
	Set<String> apsSet = new HashSet<String>();
	private TextToSpeech tts;
	MainActivity m;
	short navigationPathIndex = 0;
	boolean isRerouting = false;
	CheckBox chkReroute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tvStatus = (TextView) findViewById(R.id.txtCurrentstatus);
		tvLocation = (TextView) findViewById(R.id.txtLocation);
		etScanTimes = (EditText) findViewById(R.id.etScanTimes);
		chkReroute = (CheckBox) findViewById(R.id.chkReroute);
		new FingerPrintCollection().execute();
		initMap();
		m = this;
		initializePosition();
		addGroundOverlay1();
		builder = new AlertDialog.Builder(this);
		builder.setTitle("Select");
		builder.setCancelable(false);
		builder.setPositiveButton("Start",
		        new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int id) {
				        dialog.cancel();
				        drawRoute();
				        iscurrLocationUpdated = true;
				        startWifiScan();

				        // Hide all buttons
				        ImageButton btnLocate = (ImageButton) findViewById(R.id.btnLocate);
				        ImageButton btnNavigate = (ImageButton) findViewById(R.id.btnNavigate);
				        TextView txParkingSpot = (TextView) findViewById(R.id.txtParkingSpot);
				        EditText etParkingSport = (EditText) findViewById(R.id.etParkingSpot);
				        btnLocate.setVisibility(View.INVISIBLE);
				        txParkingSpot.setVisibility(View.INVISIBLE);
				        etParkingSport.setVisibility(View.INVISIBLE);

				        btnNavigate.setImageResource(R.drawable.cross);

				        tts.speak(instructionsSet.get(0),
				                TextToSpeech.QUEUE_FLUSH, null);
				        navigationPathIndex = 1;
			        }
		        })
		        .setNegativeButton("Cancel",
		                new DialogInterface.OnClickListener() {
			                public void onClick(DialogInterface dialog, int id) {
				                // Action for 'NO' Button
				                dialog.cancel();
			                }
		                })
		        .setNeutralButton("Show",
		                new DialogInterface.OnClickListener() {
			                public void onClick(DialogInterface dialog, int id) {
				                drawRoute();
			                }
		                });

		ImageButton btn2 = (ImageButton) findViewById(R.id.btnLocate);
		btn2.setOnClickListener(this);
		ImageButton btn3 = (ImageButton) findViewById(R.id.btnNavigate);
		btn3.setOnClickListener(this);
		// Drawable drawable = getResources().getDrawable(R.drawable.route);
		// drawable.setBounds((int) (drawable.getIntrinsicWidth() * 0.5), 0,
		// (int) (drawable.getIntrinsicWidth() * 1.5),
		// drawable.getIntrinsicHeight());
		// btn3.setCompoundDrawables(drawable, null, null, null);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		lastUpdate = System.currentTimeMillis();
		tts = new TextToSpeech(this, this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			getAccelerometer(event);
		}
		checkCompassVal(event);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnNavigate) {
			if (!iscurrLocationUpdated) {
				isRerouting = false;
				navigationPathIndex = 0;
				getDirections();
			} else {
				ImageButton btnLocate = (ImageButton) findViewById(R.id.btnLocate);
				ImageButton btnNavigate = (ImageButton) findViewById(R.id.btnNavigate);
				TextView txParkingSpot = (TextView) findViewById(R.id.txtParkingSpot);
				EditText etParkingSport = (EditText) findViewById(R.id.etParkingSpot);
				btnLocate.setVisibility(View.VISIBLE);
				txParkingSpot.setVisibility(View.VISIBLE);
				etParkingSport.setVisibility(View.VISIBLE);

				btnNavigate.setImageResource(R.drawable.route);
				iscurrLocationUpdated = false;
			}
		} else {
			startWifiScan();
		}

	}

	private void loadPartitions() {
		try {
			BSKcnsServicePortBinding cb = new BSKcnsServicePortBinding();
			BSKgetPartitionsWithDirectionsResponse response = cb
			        .getPartitionsWithDirections();
			int i = 0;
			for (String string : response) {
				partitionArr[i] = string;
				i++;
			}
		} catch (Exception ex) {

		}

	}

	private void initializePosition() {
		location.x = 5;
		location.y = 11;
		deviceOrt = DEVICEORIENTATION.STRAIGHT;
		currDirection = DIRECTION.NORTH;
		drawPoint(location);
	}

	private void initMap() {
		SupportMapFragment mMap = (SupportMapFragment) getSupportFragmentManager()
		        .findFragmentById(R.id.cnsmap);
		gMap = mMap.getMap();
	}

	private LatLng getPoint(Double dist, Double b, LatLng cuurentPos) {
		dist = dist / 6371;
		b = toRad(b);
		Double lat1 = (toRad(cuurentPos.latitude));
		Double lon1 = (toRad(cuurentPos.longitude));
		Double lat2 = Math.asin(Math.sin(lat1) * Math.cos(dist)
		        + Math.cos(lat1) * Math.sin(dist) * Math.cos(b));
		Double lon2 = lon1
		        + Math.atan2(Math.sin(b) * Math.sin(dist) * Math.cos(lat1),
		                Math.cos(dist) - Math.sin(lat1) * Math.sin(lat2));
		lat2 = toDeg(lat2);
		lon2 = toDeg(lon2);
		// Toast.makeText(this, lat2 + "," + lon2, Toast.LENGTH_LONG).show();
		return new LatLng(lat2, lon2);
	}

	private Double toRad(Double v) {
		return (Double) (v * (Math.PI / 180));
	}

	private void addGroundOverlay1() {
		groundOverlay1 = gMap.addGroundOverlay(new GroundOverlayOptions()
		        .image(BitmapDescriptorFactory.fromResource(R.drawable.f1))
		        .anchor(1, 1).position(STARTPOINT, 80f, 90f));
		groundOverlay1.setTransparency(.6f);
		groundOverlay1.setVisible(true);

	}

	private void getDirections() {
		try {
			BSKpoint source = new BSKpoint();
			source.x = location.x + "";
			source.y = location.y + "";
			source.z = 1 + "";
			int plot = 0;
			EditText etParkingSpot = (EditText) findViewById(R.id.etParkingSpot);
			if (etParkingSpot.getText() != null
			        && etParkingSpot.getText().toString() != null) {
				plot = Integer.parseInt(etParkingSpot.getText().toString());
			}
			BSKpoint target = new BSKpoint();
			target.x = plot + "";
			new GetRouteCall().execute(source, target);

		} catch (Exception ex) {
			System.out.println();
		}
	}

	private void startWifiScan() {
		new WiFiScan().execute();
	}

	private class WiFiScan extends AsyncTask<Void, Void, List<PointDistance>> {

		@Override
		protected List<PointDistance> doInBackground(Void... arg0) {
			if (etScanTimes != null && etScanTimes.getText() != null) {
				if (etScanTimes.getText().toString() != null
				        && !etScanTimes.getText().toString().equals(""))
					scanTimes = Integer.parseInt(etScanTimes.getText()
					        .toString());
			}
			List<PointDistance> closedPoints = null;
			try {
				for (int i = 0; i < scanTimes; i++) {
					wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
					if (wifiManager.isWifiEnabled() == false) {
						Toast.makeText(getApplicationContext(),
						        "wifi is disabled..making it enabled",
						        Toast.LENGTH_LONG).show();
						wifiManager.setWifiEnabled(true);
					}
					wifiManager.startScan();
					Thread.sleep(Math.round(timeout * 1000));
					if (scanResults == null)
						scanResults = wifiManager.getScanResults();
					else
						scanResults.addAll(wifiManager.getScanResults());
					// for (ScanResult scanResult : scanResults) {
					// if (apsSet.contains(scanResult.BSSID)) {
					// WifiPoint accessPoint = new WifiPoint();
					// accessPoint.strength = WifiManager
					// .calculateSignalLevel(scanResult.level, 150);
					// accessPoint.bssid = scanResult.BSSID;
					// accessPointsList.add(accessPoint);
					// }
					// }

				}
				// Create a hashmap by calculating the averages
				HashMap<String, Float> macStrength = calculateAverages(scanResults);
				numberOfAps = (short) macStrength.size();
				closedPoints = getClosedPoints(macStrength);
				Thread.sleep(500);

			} catch (Exception e) {
				tvStatus.setText(e.getMessage() + "....");
			}
			return closedPoints;
		}

		@Override
		protected void onPostExecute(List<PointDistance> result) {
			super.onPostExecute(result);
			PointDistance closestPoint = null;

			try {
				if (result != null && result.size() > 0) {

					// approximate the location by finding distance with the
					// neighbours.
					closestPoint = approximateLocation(result);
					if (closestPoint.distance > 15 && numberofAttempts < 3) {
						numberofAttempts++;
						startWifiScan();
					} else {
						drawPoint(closestPoint.point);

						scanResults.clear();
						if (closestPoint.point != null) {
							tvLocation.setText(numberOfAps + " - "
							        + numberofAttempts + ",   Dist:"
							        + Math.round(closestPoint.distance)
							        + ",   (" + closestPoint.point.x + ","
							        + closestPoint.point.y + ")   "
							        + currDirection.toString().substring(0, 1)
							        + "  [ "
							        + partitionArr[currDirection.ordinal()]
							        + " ] ");
						}
						numberofAttempts = 1;
						tvStatus.setText("Estimating the position...DONE");
					}

				} else {
					tvStatus.setText("Result is null for User estimation");
				}

				// Repeating the location service
				// CheckBox chkRepeat = (CheckBox) findViewById(R.id.chkRepeat);
				if (iscurrLocationUpdated) {
					if (navigationPath != null) {
						if (destinationPoint != null) {
							double destinationDis = getEuclideanDistance(
							        closestPoint.point, destinationPoint);
							if (destinationDis < 10) {
								tts.speak(
								        "You have arrived to the Destination",
								        TextToSpeech.QUEUE_FLUSH, null);
								tvStatus.setText("You have arrived to the Destination");
								ImageButton btnLocate = (ImageButton) findViewById(R.id.btnLocate);
								ImageButton btnNavigate = (ImageButton) findViewById(R.id.btnNavigate);
								TextView txParkingSpot = (TextView) findViewById(R.id.txtParkingSpot);
								EditText etParkingSport = (EditText) findViewById(R.id.etParkingSpot);
								btnLocate.setVisibility(View.VISIBLE);
								txParkingSpot.setVisibility(View.VISIBLE);
								etParkingSport.setVisibility(View.VISIBLE);

								btnNavigate.setImageResource(R.drawable.route);
								iscurrLocationUpdated = false;
							} else {
								// Check for Voice
								checkForVoice();
								new WiFiScan().execute();
							}
						}
					}
				}
			} catch (Exception ex) {
				tvStatus.setText(ex.getMessage());
			}
		}

		@Override
		protected void onPreExecute() {
			tvStatus.setText("Estimating the position...");
		}

	}

	private void checkForVoice() {
		if (navigationPathIndex < instructionsSet.size()) {
			for (short i = navigationPathIndex; i < navigationPath.size(); i++) {
				Point p = new Point((int) Float.parseFloat(navigationPath
				        .get(i).x), (int) Float.parseFloat(navigationPath
				        .get(i).y), (int) Float.parseFloat(navigationPath
				        .get(i).z));
				if (isPossbleNeighbor(p)) {
					double dis = getEuclideanDistance(p, location);
					if (dis < 16) {
						// Toast.makeText(m, "less tahn 16", Toast.LENGTH_LONG)
						// .show();
						if (i < instructionsSet.size()) {
							tts.speak(instructionsSet.get(i),
							        TextToSpeech.QUEUE_FLUSH, null);
							navigationPathIndex = (short) (i + 1);
						}

					}
				}
			}
		}
	}

	private PointDistance approximateLocation(List<PointDistance> closedPoints) {
		PointDistance closestPoint = new PointDistance();
		CheckBox chkneighbor = (CheckBox) findViewById(R.id.chkNeighbor);
		// StringBuilder sb=new StringBuilder();
		for (PointDistance pointDistance : closedPoints) {
			// sb.append("("+pointDistance.point.x+","+pointDistance.point.y+")");
			for (Point neighbour : neighbourList) {
				float distance = getEuclideanDistance(pointDistance.point,
				        neighbour);
				if ((closestPoint.point == null ? Float.MAX_VALUE
				        : closestPoint.distance) > distance) {
					if (chkneighbor.isChecked())
						closestPoint.point = neighbour;
					else
						closestPoint.point = pointDistance.point;
					closestPoint.distance = distance;
				}
			}
		}
		// TextView tvNeighbors = (TextView)findViewById(R.id.txtNeoghbors);
		// tvNeighbors.setText(sb.toString());
		return closestPoint;
	}

	private DIRECTION getRouteDirection() {
		int maxIndex = 0;
		synchronized (this) {

			for (int i = 0; i < directionsArr.length; i++) {
				if (directionsArr[i] > directionsArr[maxIndex])
					maxIndex = i;
			}
			for (int i = 0; i < directionsArr.length; i++) {
				directionsArr[i] = 0l;
			}
		}
		return DIRECTION.values()[maxIndex];
	}

	@Override
	protected void onResume() {
		super.onResume();
		// register this class as a listener for the orientation and
		// accelerometer sensors
		sensorManager.registerListener(this,
		        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
		        SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this,
		        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
		        SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		// unregister listener
		super.onPause();
		sensorManager.unregisterListener(this);
	}

	private void drawPoint(Point estimatedPosition) {
		int x = location.x;
		int y = location.y;

		if (estimatedPosition != null) {
			x = estimatedPosition.x;
			y = estimatedPosition.y;
		}

		// if (location.x < 80) {
		// x = 4 + location.x;
		// y = location.y;
		// } else {
		// y = 4 + location.y;
		// x = 80;
		// }

		location.x = x;
		location.y = y;

		LatLng xLatLng = getPoint((double) x / 1000, 0d, STARTPOINT);
		// move in Y-Direction
		LatLng yLatLng = getPoint((double) y / 1000, 270d, xLatLng);
		if (sourceMarker != null) {
			sourceMarker.setPosition(yLatLng);
		} else {
			sourceMarker = gMap
			        .addMarker(new MarkerOptions().position(yLatLng));
		}
		// Update the Neighbours
		new Neighbours().execute();
		if (chkReroute.isChecked() && !isRerouting) {
			BSKpoint currPoint = new BSKpoint();
			currPoint.x = location.x + "";
			currPoint.y = location.y + "";
			// Logic to reroute
			float distance = pointToPolyLineDistance(currPoint);
			if (distance > 15) {
				isRerouting = true;
				tts.speak("We are Rerouting", TextToSpeech.QUEUE_FLUSH, null);
				getDirections();
			}
		}
	}

	private HashMap<String, Float> calculateAverages(List<ScanResult> result) {
		HashMap<String, Float> macStrength = new HashMap<String, Float>();
		macCount = new HashMap<String, Integer>();
		macCount.clear();
		for (ScanResult scanresult : result) {
			// if (apsSet.contains(scanresult.BSSID)) {
			// Strengths -- one
			if (macStrength.containsKey(scanresult.BSSID)) {
				Float strength = macStrength.get(scanresult.BSSID);
				macStrength.put(scanresult.BSSID, (strength + WifiManager
				        .calculateSignalLevel(scanresult.level, 150)));
			} else {
				macStrength.put(scanresult.BSSID, (float) WifiManager
				        .calculateSignalLevel(scanresult.level, 150));
			}
			// Counts -- one
			if (macCount.containsKey(scanresult.BSSID)) {
				int count = macCount.get(scanresult.BSSID);
				macCount.put(scanresult.BSSID, (count + 1));
			} else {
				macCount.put(scanresult.BSSID, 1);
			}
			// }
		}
		// Get Averages
		for (Map.Entry<String, Float> mapEntry : macStrength.entrySet()) {
			int count = macCount.get(mapEntry.getKey());
			macStrength.put(mapEntry.getKey(), (mapEntry.getValue() / count));
		}
		return macStrength;
	}

	public List<PointDistance> getClosedPoints(
	        HashMap<String, Float> macStrength) {
		// CheckBox chkNew = (CheckBox) findViewById(R.id.chkNew);
		List<Point> partitionCollectedPoints = getCurrentPartitionPoints();
		List<PointDistance> closestPointsList = new ArrayList<PointDistance>();
		Comparator<PointDistance> pointDistComparator = new PointDistanceComparator();
		PriorityQueue<PointDistance> pointDistanceQueue = new PriorityQueue<PointDistance>(
		        10, pointDistComparator);
		float distance = 0f;
		for (Point point : partitionCollectedPoints) {
			// if (chkNew.isChecked())
			// distance = calculateDistanceNew(point.accessPoints, macStrength);
			// else
			distance = calculateDistance(point.accessPoints, macStrength);
			PointDistance pd = new PointDistance();
			pd.point = point;
			pd.distance = distance;
			pointDistanceQueue.add(pd);
			if (pointDistanceQueue.size() > 4) {
				pointDistanceQueue.remove();
			}
		}
		// After this pointDistanceQueue should contains max of 3 Reference
		// Points with their distances.
		PointDistance pd = null;
		while (pointDistanceQueue.peek() != null) {
			pd = pointDistanceQueue.poll();
			closestPointsList.add(pd);
		}
		return closestPointsList;
	}

	private float calculateDistanceNew(HashMap<String, Float> preCollectedAps,
	        HashMap<String, Float> currentAps) {
		float distance = 0;
		boolean isOneApAvailable = false;
		float pcStrength = 0;
		float currentStrength = 0;
		for (Map.Entry<String, Float> entry : preCollectedAps.entrySet()) {
			if (currentAps.containsKey(entry.getKey())) {
				isOneApAvailable = true;
				currentStrength = currentAps.get(entry.getKey());
				pcStrength = entry.getValue();
				distance += Math.abs(pcStrength - currentStrength);
			}
		}

		if (isOneApAvailable)
			return distance;
		else
			return Float.MAX_VALUE;
	}

	private float calculateDistance(HashMap<String, Float> preCollectedAps,
	        HashMap<String, Float> currentAps) {
		float distance = 0;
		// Get all distinct Mac addresses.
		// enumerate thru all keys in Precollected.
		HashSet<String> macSets = new HashSet<String>();
		for (Map.Entry<String, Float> entry : preCollectedAps.entrySet()) {
			if (!macSets.contains(entry.getKey())) {
				macSets.add(entry.getKey());
			}
		}
		// Enumerate thru all keys in currentAps
		for (Map.Entry<String, Float> entry : currentAps.entrySet()) {
			if (!macSets.contains(entry.getKey())) {
				macSets.add(entry.getKey());
			}
		}
		// Now macset contains all MAc Address which needs to be used
		// calculating the distance.
		float pcStrength = 0;
		float currentStrength = 0;

		for (String mac : macSets) {
			pcStrength = 0;
			currentStrength = 0;
			pcStrength = preCollectedAps.containsKey(mac) ? preCollectedAps
			        .get(mac) : 0;
			currentStrength = currentAps.containsKey(mac) ? currentAps.get(mac)
			        : 0;
			distance += Math.pow((pcStrength - currentStrength), 2);
		}
		return (float) Math.sqrt(distance);
	}

	private List<Point> getCurrentPartitionPoints() {
		if (preCollectedPrints == null) {
			updatePreCollectedFingerPrints();
		}
		if (partitionArr == null) {
			loadPartitions();
		}
		currDirection = getRouteDirection();

		String currentPartitions = partitionArr[currDirection.ordinal()];
		List<Point> partitionPoints = new ArrayList<Point>();
		for (Point point : preCollectedPrints) {
			if (currentPartitions.contains(point.partition + "")) {
				partitionPoints.add(point);
			}
		}

		return partitionPoints;
	}

	private class GetRouteCall extends
	        AsyncTask<BSKpoint, Void, BSKgetDirectionsResponse> {
		@Override
		protected BSKgetDirectionsResponse doInBackground(BSKpoint... params) {
			BSKgetDirectionsResponse response = null;
			try {
				BSKpoint source = params[0];
				BSKpoint target = params[1];
				BSKcnsServicePortBinding cb = new BSKcnsServicePortBinding();
				response = cb.getDirections(source, target.x);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
			return response;
		}

		@Override
		protected void onPreExecute() {
			tvStatus.setText("Calling directions...");
		}

		@Override
		protected void onPostExecute(BSKgetDirectionsResponse result) {
			navigationPath = new ArrayList<BSKpoint>();
			for (BSKpoint point : result) {
				navigationPath.add(point);
			}
			int i = 0;
			for (; i < navigationPath.size(); i++) {
				if (navigationPath.get(i).x.equals("75.0")
				        && navigationPath.get(i).y.equals("50.0"))
					break;
			}
			if (i > 0 && i < (navigationPath.size() - 1)) {
				navigationPath.remove(i);
			}
			destinationPoint = new Point((int) Float.parseFloat(navigationPath
			        .get(navigationPath.size() - 1).x),
			        (int) Float.parseFloat(navigationPath.get(navigationPath
			                .size() - 1).y), 1);
			updateInstructions();
			if (!isRerouting) {
				ListAdapter adapter = new ArrayAdapter<String>(
				        getApplicationContext(), R.layout.list_row,
				        instructionsSet) {

					ViewHolder holder;

					class ViewHolder {
						ImageView icon;
						TextView title;
					}

					public View getView(int position, View convertView,
					        ViewGroup parent) {
						final LayoutInflater inflater = (LayoutInflater) getApplicationContext()
						        .getSystemService(
						                Context.LAYOUT_INFLATER_SERVICE);

						if (convertView == null) {
							convertView = inflater.inflate(R.layout.list_row,
							        null);

							holder = new ViewHolder();
							holder.icon = (ImageView) convertView
							        .findViewById(R.id.icon);
							holder.title = (TextView) convertView
							        .findViewById(R.id.title);
							convertView.setTag(holder);
						} else {
							// view already defined, retrieve view holder
							holder = (ViewHolder) convertView.getTag();
						}

						Drawable leftDrawable = getResources().getDrawable(
						        R.drawable.left);
						Drawable straightDrawable = getResources().getDrawable(
						        R.drawable.straight); // this is an image from
						                              // the
						                              // drawables folder

						holder.title.setText(instructionsSet.get(position));
						if (instructionsSet.get(position).contains("left"))
							holder.icon.setImageDrawable(leftDrawable);
						else
							holder.icon.setImageDrawable(straightDrawable);

						return convertView;
					}
				};

				builder.setAdapter(adapter, null);
				builder.setTitle("Route Instructions");
				alt_bld = builder.create();
				alt_bld.setOnShowListener(new OnShowListener() {

					@Override
					public void onShow(DialogInterface dialogInterface) {
						Button button = alt_bld
						        .getButton(AlertDialog.BUTTON_POSITIVE);
						Drawable drawable = getResources().getDrawable(
						        R.drawable.up);
						drawable.setBounds(
						        (int) (drawable.getIntrinsicWidth() * 0.5), 0,
						        (int) (drawable.getIntrinsicWidth() * 1.5),
						        drawable.getIntrinsicHeight());
						button.setCompoundDrawables(drawable, null, null, null);
					}
				});
				alt_bld.show();
			} else {
				drawRoute();
				isRerouting = false;
			}
			tvStatus.setText("Calling Directions.. DONE");
		}

	}

	private void drawRoute() {
		List<LatLng> wayPoints = new ArrayList<LatLng>();
		for (BSKpoint point : navigationPath) {
			wayPoints.add(getLatLng(point));
		}
		if (targetMarker == null) {
			targetMarker = gMap.addMarker(new MarkerOptions().position(
			        wayPoints.get(wayPoints.size() - 1)).icon(
			        BitmapDescriptorFactory
			                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
		} else {
			targetMarker.setPosition(wayPoints.get(wayPoints.size() - 1));
		}
		if (routeLine != null)
			routeLine.remove();
		PolylineOptions routinLineOptions = new PolylineOptions();
		routinLineOptions = new PolylineOptions();
		routinLineOptions.addAll(wayPoints);
		routinLineOptions.width(8);
		routinLineOptions.color(Color.BLUE);
		routeLine = gMap.addPolyline(routinLineOptions);

		tvStatus.setText("Calling Directions -- Done");
	}

	public LatLng getLatLng(BSKpoint point) {
		LatLng xLatlng = getPoint(Double.parseDouble(point.x) / 1000, 0d,
		        STARTPOINT);
		LatLng yLatLng = getPoint(Double.parseDouble(point.y) / 1000, 270d,
		        xLatlng);
		return yLatLng;
	}

	private class FingerPrintCollection extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			int preCount = 0;
			if (preCollectedPrints == null)
				updatePreCollectedFingerPrints();
			preCount = preCollectedPrints.size();
			loadPartitions();
			return preCount;
		}

		@Override
		protected void onPostExecute(Integer result) {
			tvStatus.setText("Downloading the Map .. DONE" + result);
			// List<Point> paritions = getCurrentPartitionPoints();
			// TextView tv = (TextView) findViewById(R.id.txtNeoghbors);
			// tv.setText(preCollectedPrints.size() + ",,," + paritions.size()
			// + ",," + currDirection);
		}

		@Override
		protected void onPreExecute() {
			tvStatus.setText("Downloading the Map");
		}

	}

	private int updatePreCollectedFingerPrints() {
		int preCount = 0;
		try {
			BSKcnsServicePortBinding cb = new BSKcnsServicePortBinding();
			BSKgetPreCollectedPrintsResponse preCollectedResponse = cb
			        .getPreCollectedPrints();
			preCollectedPrints = new ArrayList<Point>();
			if (preCollectedResponse != null) {
				for (BSKpoint BSKpoint : preCollectedResponse) {
					preCount++;
					Point point = new Point();
					point.x = Integer.parseInt(BSKpoint.x);
					point.y = Integer.parseInt(BSKpoint.y);
					point.z = Integer.parseInt(BSKpoint.z);
					point.partition = Integer.parseInt(BSKpoint.partition);
					point.accessPoints = new HashMap<String, Float>();
					for (BSKpoint_accessPoints_entry entry : BSKpoint.accessPoints) {
						if (!apsSet.contains(entry.key))
							apsSet.add(entry.key);

						point.accessPoints.put(entry.key,
						        Float.parseFloat(entry.value));
					}
					preCollectedPrints.add(point);
				}
			}

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return preCount;
	}

	private class Neighbours extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			Comparator<PointDistance> pointDisComparator = new PointDistanceComparator();
			PriorityQueue<PointDistance> neighbourPointsQueue = new PriorityQueue<PointDistance>(
			        10, pointDisComparator);
			List<Point> partitionCollectedPoints = getCurrentPartitionPoints();
			for (Point point : partitionCollectedPoints) {
				// Ignore the point equal to current location
				if (isPossbleNeighbor2(point)) {
					float distance = getEuclideanDistance(location, point);
					PointDistance pd = new PointDistance();
					pd.point = point;
					pd.distance = distance;
					neighbourPointsQueue.add(pd);
					if (neighbourPointsQueue.size() > 4) {
						neighbourPointsQueue.remove();
					}
				}
			}
			PointDistance pd = null;
			if (neighbourList == null)
				neighbourList = new ArrayList<Point>();
			else
				neighbourList.clear();
			while (neighbourPointsQueue.peek() != null) {
				pd = neighbourPointsQueue.poll();
				neighbourList.add(pd.point);
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			// tvStatus.setText("Updating the Neighbours");
		}

		@Override
		protected void onPostExecute(Void result) {
			StringBuilder sb = new StringBuilder();
			if (neighbourList != null) {
				for (Point p : neighbourList) {
					sb.append("(" + p.x + "," + p.y + ")\n");
				}
			}
			// TextView tvNeighbors = (TextView)
			// findViewById(R.id.txtNeoghbors);
			// tvNeighbors.setText(sb.toString());
		}

	}

	private boolean isPossbleNeighbor(Point p) {
		if (currDirection == DIRECTION.NORTH) {
			if (p.y == location.y && p.x > location.x) {
				return true;
			}
			return false;
		} else if (currDirection == DIRECTION.WEST) {
			if (p.x == location.x && p.y > location.y) {
				return true;
			}
			return false;
		} else if (currDirection == DIRECTION.SOUTH) {
			if (p.y == location.y && p.x < location.x) {
				return true;
			}
			return false;
		} else if (currDirection == DIRECTION.EAST) {
			if (p.x == location.x && p.y < location.y) {
				return true;
			}
			return false;
		}
		return false;
	}

	private boolean isPossbleNeighbor2(Point p) {
		if (currDirection == DIRECTION.NORTH) {
			if (p.y == 11 || p.y == 30) {
				return true;
			}
			return false;
		} else if (currDirection == DIRECTION.WEST) {
			if (p.x == 80) {
				return true;
			}
			return false;
		} else if (currDirection == DIRECTION.SOUTH) {
			if (p.y == 69 || p.y == 50) {
				return true;
			}
			return false;
		} else if (currDirection == DIRECTION.EAST) {
			if (p.x == 10) {
				return true;
			}
			return false;
		}
		return false;
	}

	private float getEuclideanDistance(Point currLoc, Point neighbour) {
		if (currLoc != null && neighbour != null) {
			return (float) Math.sqrt(Math.pow((currLoc.x - neighbour.x), 2)
			        + Math.pow((currLoc.y - neighbour.y), 2));
		}
		return (float) Double.MAX_VALUE;
	}

	private Double toDeg(Double v) {
		return (v * 180) / Math.PI;
	}

	public static boolean isNotNullNEmpty(final String string) {
		return string != null && string != "";
	}

	public float pointToPolyLineDistance(BSKpoint currPoint) {
		float distance = Float.MAX_VALUE;
		if (navigationPath != null) {
			int index = 0;
			float pDistance = 0;
			float x3 = Float.parseFloat(currPoint.x);
			float y3 = Float.parseFloat(currPoint.y);
			while ((index + 1) < navigationPath.size()) {
				float x1 = Float.parseFloat(navigationPath.get(index).x);
				float y1 = Float.parseFloat(navigationPath.get(index).y);
				float x2 = Float.parseFloat(navigationPath.get(index + 1).x);
				float y2 = Float.parseFloat(navigationPath.get(index + 1).y);
				float px = x2 - x1;
				float py = y2 - y1;
				float p = px * px + py * py;
				float u = ((x3 - x1) * px + (y3 - y1) * py) / p;
				if (u > 1)
					u = 1;
				else if (u < 0)
					u = 0;
				px = (x1 + u * px) - x3;
				py = (y1 + u * py) - y3;
				pDistance = (float) Math.sqrt(px * px + py * py);
				distance = Math.min(distance, pDistance);
				index++;
			}
		} else {
			distance = 0;
		}
		return distance;

	}

	private void updateInstructions() {
		instructionsSet = new ArrayList<String>();
		if (navigationPath != null) {
			int index = 0;
			BSKpoint pre = null;
			BSKpoint curr = null;
			BSKpoint next = null;
			// Initialize Instruction set
			if (navigationPath.size() > 2) {
				curr = navigationPath.get(0);
				next = navigationPath.get(1);
				pre = curr;
			}
			instructionsSet.add(getInstrucitoString(pre, curr, next));
			while ((index + 2) < navigationPath.size()) {
				next = navigationPath.get(index + 2);

				curr = navigationPath.get(index + 1);
				pre = navigationPath.get(index);
				instructionsSet.add(getInstrucitoString(pre, curr, next));
				index++;
			}

		}
	}

	private String getInstrucitoString(BSKpoint pre, BSKpoint curr,
	        BSKpoint next) {
		float distance = 0;
		TURN turn;
		if ((pre.x.equals(next.x)) || (pre.y.equals(next.y))) {
			turn = TURN.STRAIGHT;
		} else {
			turn = TURN.LEFT;
		}
		distance = getEuclideanDistance(
		        new Point((int) Float.parseFloat(curr.x),
		                (int) Float.parseFloat(curr.y), 0),
		        new Point((int) Float.parseFloat(next.x), (int) Float
		                .parseFloat(next.y), 0));
		if (turn == TURN.STRAIGHT)
			return "Head " + turn.toString().toLowerCase() + " for "
			        + Math.round(distance) + " meters";
		else
			return "Turn " + turn.toString().toLowerCase() + " travel for "
			        + Math.round(distance) + " meters";
	}

	private void getAccelerometer(SensorEvent event) {
		float[] curr = event.values.clone();
		long actualTime = System.currentTimeMillis();
		int maxIndex = 0;
		if (actualTime - lastUpdate > 1000) {
			lastUpdate = actualTime;
			if (Math.abs(curr[0]) > Math.abs(curr[1]))
				maxIndex = 0;
			else
				maxIndex = 1;

			if (Math.abs(curr[2]) > Math.abs(curr[maxIndex]))
				maxIndex = 2;
			if (maxIndex == 1) {
				deviceOrt = DEVICEORIENTATION.STRAIGHT;
			} else if (maxIndex == 2) {
				deviceOrt = DEVICEORIENTATION.STRAIGHT_FLAT;
			} else {
				if (curr[maxIndex] > 0) {
					deviceOrt = DEVICEORIENTATION.LEFT;
				} else {
					deviceOrt = DEVICEORIENTATION.RIGHT;
				}
			}

		}
	}

	private void checkCompassVal(SensorEvent event) {

		float azimuth;
		DIRECTION nextDirection = null;
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
			mGravity = event.values.clone();
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
			mGeomagnetic = event.values.clone();
		if (mGravity != null && mGeomagnetic != null) {
			float Ro[] = new float[9];
			float I[] = new float[9];
			boolean success = SensorManager.getRotationMatrix(Ro, I, mGravity,
			        mGeomagnetic);
			if (success) {
				float orientation[] = new float[3];
				SensorManager.getOrientation(Ro, orientation);

				azimuth = (float) Math.round(Math.toDegrees(orientation[0]));
				azimuth = (azimuth + 360) % 360;

				if (deviceOrt == DEVICEORIENTATION.LEFT)
					azimuth = azimuth + 90;
				else if (deviceOrt == DEVICEORIENTATION.RIGHT) {
					azimuth = azimuth - 90;
				}
				if (azimuth >= 330 && azimuth <= 360) {
					azimuth = 0;
					nextDirection = DIRECTION.NORTH;
				} else if (azimuth >= 230 && azimuth <= 300) {
					azimuth = 270;
					nextDirection = DIRECTION.WEST;
				} else if (azimuth >= 140 && azimuth <= 220) {
					azimuth = 180;
					nextDirection = DIRECTION.SOUTH;
				} else if (azimuth > 50 && azimuth <= 130) {
					azimuth = 90;
					nextDirection = DIRECTION.EAST;
				} else {
					azimuth = 0;
					nextDirection = DIRECTION.NORTH;
				}
				long val = directionsArr[nextDirection.ordinal()];
				directionsArr[nextDirection.ordinal()] = (val + 1);
			}
		}

	}

	@Override
	public void onInit(int arg0) {
		if (arg0 == TextToSpeech.SUCCESS) {

			int result = tts.setLanguage(Locale.US);

		} else {
			Log.e("TTS", "Initilization Failed!");
		}

	}

}
