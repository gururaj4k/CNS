package com.example.fingerprints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testSoap.wcf.RIKcnsServicePortBinding;
import com.example.testSoap.wcf.RIKsaveWifiFingerPrints;
import com.example.testSoap.wcf.RIKwifiAccessPoint;

public class MainActivity extends Activity implements OnClickListener {
	WifiManager wifiManager;
	int scanTimes;
	String x, y, z;
	int timeout;
	HashMap<String, String> macSsid = null;
	HashMap<String, Integer> macCount=null;
	private static String endpointUrl = "http://54.186.110.123:8080/CNSServer/CNSController?wsdl";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button btn = (Button) findViewById(R.id.button1);
		btn.setOnClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		// update co-ordinates
		EditText etx = (EditText) findViewById(R.id.editText1);
		EditText ety = (EditText) findViewById(R.id.editText2);
		EditText etz = (EditText) findViewById(R.id.editText3);
		EditText etTimeout = (EditText) findViewById(R.id.editText4);
		EditText etScanTimes = (EditText) findViewById(R.id.editText5);

		TextView txtStatus = (TextView) findViewById(R.id.textStatus);
		txtStatus.setMovementMethod(new ScrollingMovementMethod());
		if (etx.getText() != null && isNotNullNEmpty(etx.getText().toString())) {
			x = etx.getText().toString();
		}

		if (ety.getText() != null && isNotNullNEmpty(ety.getText().toString())) {
			y = ety.getText().toString();
		}

		if (etz.getText() != null && isNotNullNEmpty(etz.getText().toString())) {
			z = etz.getText().toString();
		}
		if (etTimeout.getText() != null
		        && isNotNullNEmpty(etTimeout.getText().toString())) {
			timeout = Integer.parseInt(etTimeout.getText().toString());
		} else {
			timeout = 3;
		}
		if (etScanTimes.getText() != null
		        && isNotNullNEmpty(etScanTimes.getText().toString())) {
			scanTimes = Integer.parseInt(etScanTimes.getText().toString());
		} else {
			scanTimes = 15;
		}
		new WiFiScan().execute();

	}

	private class WiFiScan extends
	        AsyncTask<Void, Void, List<RIKwifiAccessPoint>> {

		@Override
		protected List<RIKwifiAccessPoint> doInBackground(Void... arg0) {
			List<RIKwifiAccessPoint> accessPointsList = new ArrayList<RIKwifiAccessPoint>();
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
					Thread.sleep(timeout * 1000);
					List<ScanResult> scanResults = wifiManager.getScanResults();
					for (ScanResult scanResult : scanResults) {
						RIKwifiAccessPoint accessPoint = new RIKwifiAccessPoint();
						accessPoint.ssid = scanResult.SSID;
						accessPoint.strength = WifiManager
						        .calculateSignalLevel(scanResult.level, 150)
						        + "";
						accessPoint.dbm=accessPoint.strength;
						accessPoint.bssid = scanResult.BSSID;
						accessPointsList.add(accessPoint);
					}
					
				}
			//	System.out.println(sb.toString());
			} catch (Exception e) {

				e.printStackTrace();
			}
			return accessPointsList;
		}

		@Override
		protected void onPostExecute(List<RIKwifiAccessPoint> result) {
			super.onPostExecute(result);
			TextView txtStatus = (TextView) findViewById(R.id.textStatus);
			try {
				StringBuilder acSB = new StringBuilder("");
				int count = 1;

				TextView txtAps = (TextView) findViewById(R.id.textAccessPoints);
				txtAps.setMovementMethod(new ScrollingMovementMethod());
				ArrayList<RIKwifiAccessPoint> accessPoints = new ArrayList<RIKwifiAccessPoint>();
				if (result != null && result.size() > 0) {
					txtStatus.setText("Total AccessPoints: " + result.size()
					        + "\n");
					HashMap<String, Float> macStrength = calculateAverages(result);
					txtStatus.setText(txtStatus.getText()
					        + "After Average Distinct AccessPoints: "
					        + macStrength.size() + "\n");
					for (Map.Entry<String, Float> mapEntry : macStrength
					        .entrySet()) {
						RIKwifiAccessPoint wap = new RIKwifiAccessPoint();
						wap.bssid = mapEntry.getKey();
						wap.ssid = macSsid.get(mapEntry.getKey());
						wap.strength = Math.round(mapEntry.getValue()) + "";
						wap.dbm =macCount.get(mapEntry.getKey())+"";
						acSB.append(count + ":" + wap.ssid + " --> "
						        + wap.bssid + " --> " + wap.strength + "\n");
						accessPoints.add(wap);
						count++;
					}
					txtAps.setText(acSB);
					new WifiSave().execute(accessPoints);

				} else {
					txtStatus.setText(txtStatus.getText()
					        + "Results Are Null OR count =0\n");
				}
			} catch (Exception ex) {
				txtStatus.setText(txtStatus.getText() + ex.getMessage());
			}
		}

		private HashMap<String, Float> calculateAverages(
		        List<RIKwifiAccessPoint> result) {
			HashMap<String, Float> macStrength = new HashMap<String, Float>();
			macCount = new HashMap<String, Integer>();
			macSsid = new HashMap<String, String>();
			macSsid.clear();
			macCount.clear();
			for (RIKwifiAccessPoint wifiAccessPoint : result) {
				// Strengths -- one
				if (macStrength.containsKey(wifiAccessPoint.bssid)) {
					Float strength = macStrength.get(wifiAccessPoint.bssid);
					macStrength.put(wifiAccessPoint.bssid, (strength + Float
					        .parseFloat(wifiAccessPoint.strength)));
				} else {
					macStrength.put(wifiAccessPoint.bssid,
					        Float.parseFloat(wifiAccessPoint.strength));
				}
				// Counts -- one
				if (macCount.containsKey(wifiAccessPoint.bssid)) {
					int count = macCount.get(wifiAccessPoint.bssid);
					macCount.put(wifiAccessPoint.bssid, (count + 1));
				} else {
					macCount.put(wifiAccessPoint.bssid, 1);
				}
				// Handle Mac SSIDs
				macSsid.put(wifiAccessPoint.bssid, wifiAccessPoint.ssid);
			}
			// Get Averages
			for (Map.Entry<String, Float> mapEntry : macStrength.entrySet()) {
				int count = macCount.get(mapEntry.getKey());
				macStrength.put(mapEntry.getKey(),
				        (mapEntry.getValue() / count));
			}
			return macStrength;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			TextView txtStatus = (TextView) findViewById(R.id.textStatus);
			TextView txtaps = (TextView) findViewById(R.id.textAccessPoints);
			txtStatus.setText("Scanning For Wifi...\n");
			txtaps.setText("");
		}

	}

	public static boolean isNotNullNEmpty(final String string) {
		return string != null && !string.equals("")
		        && !string.trim().equals("");
	}

	private class WifiSave extends
	        AsyncTask<ArrayList<RIKwifiAccessPoint>, Void, String> {

		@Override
		protected String doInBackground(ArrayList<RIKwifiAccessPoint>... params) {
			String resp = "";
			try {
				if (params != null && params.length > 0) {
					ArrayList<RIKwifiAccessPoint> accessPoints = params[0];
					RIKcnsServicePortBinding b = new RIKcnsServicePortBinding(
					        endpointUrl);
					RIKsaveWifiFingerPrints swf = new RIKsaveWifiFingerPrints();
					swf.arg0 = accessPoints;
					swf.arg1 = x;
					swf.arg2 = y;
					swf.arg3 = z;
					swf.arg4 = "0";
					resp = b.saveWifiFingerPrints(swf);

				}
			} catch (Exception ex) {
				resp = ex.getMessage();
			}
			return resp;
		}

		@Override
		protected void onPostExecute(String result) {
			TextView txtStatus = (TextView) findViewById(R.id.textStatus);
			if (result != null && !result.equals("")) {
				txtStatus.setText(txtStatus.getText() + "Failed with error.. "
				        + result + "\n");
			} else {
				txtStatus.setText(txtStatus.getText()
				        + "Successfully Saved.. \n");
			}

		}

		@Override
		protected void onPreExecute() {
			TextView txtStatus = (TextView) findViewById(R.id.textStatus);
			txtStatus.setText(txtStatus.getText()
			        + "Saving the Access Points.." + "\n");
		}

	}
}
