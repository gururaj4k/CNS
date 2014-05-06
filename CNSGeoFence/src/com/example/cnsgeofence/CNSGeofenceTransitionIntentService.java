package com.example.cnsgeofence;

import java.util.Random;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class CNSGeofenceTransitionIntentService extends IntentService {

	public CNSGeofenceTransitionIntentService() {
		super("CNSGeofenceTransitionIntentService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		int transition = LocationClient.getGeofenceTransition(intent);
		if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
			generateNotification(this, "Entered with transition " + transition);
		} else {
			generateNotification(this, "Exited with transition " + transition);
		}

	}

	private static void generateNotification(Context context, String message) {
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
		        .getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);

		String title = context.getString(R.string.app_name);

		Intent notificationIntent = new Intent();
		notificationIntent.setComponent(new ComponentName(
		        "com.example.cns", "com.example.cns.MainActivity"));
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
		notificationManager.notify(new Random().nextInt(1000), notification);

	}

}
