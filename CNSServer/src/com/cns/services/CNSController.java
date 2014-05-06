package com.cns.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import javax.jws.WebService;

import com.cns.domains.Point;
import com.cns.domains.PointDistance;
import com.cns.domains.PointDistanceComparator;
import com.cns.domains.WifiAccessPoint;

@WebService(endpointInterface = "com.cns.services.ICNSController", portName = "cnsServicePort", serviceName = "cnsService")
public class CNSController implements ICNSController {

	public String saveWifiFingerPrints(List<WifiAccessPoint> accesspoints,
	        int x, int y, int z, int scanno) {
		String error = "";
		PostGresDB p = new PostGresDB();
		try {
			error = p.saveWifiAccessPoints(accesspoints, x, y, z, scanno);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		} finally {
			try {
				if (p.conn != null && !p.conn.isClosed()) {
					p.conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return error;
	}

	public List<PointDistance> getUserEstimation(
	        HashMap<String, String> accessPoints) {
		List<PointDistance> closestPointsList = new ArrayList<PointDistance>();
		PostGresDB p = new PostGresDB();
		List<Point> preCollectedPrints = new ArrayList<Point>();
		try {
			preCollectedPrints = p.getWifiFingerPrints();
		} catch (Exception ex) {
			Point fp = new Point(ex.getMessage(), "0", "0");
			PointDistance pd = new PointDistance();
			pd.setPoint(fp);
			closestPointsList.add(pd);
			return closestPointsList;

		} finally {
			try {
				if (p.conn != null && !p.conn.isClosed()) {
					p.conn.close();
				}
			} catch (SQLException e) {

			}
		}
		Comparator<PointDistance> pointDistComparator = new PointDistanceComparator();
		PriorityQueue<PointDistance> pointDistanceQueue = new PriorityQueue<PointDistance>(
		        10, pointDistComparator);
		String distance = "0";
		for (Point point : preCollectedPrints) {
			distance = calculateDistance(point.getAccessPoints(), accessPoints);
			PointDistance pd = new PointDistance();
			pd.setPoint(point);
			pd.setDistance(distance);
			pointDistanceQueue.add(pd);
			// if (pointDistanceQueue.size() > 3) {
			// pointDistanceQueue.remove();
			// }
		}
		// After this pointDistanceQueue should contains max of 3 Reference
		// Points with their distances.
		PointDistance pd = null;
		while (pointDistanceQueue.peek() != null) {
			pd = pointDistanceQueue.poll();
			closestPointsList.add(pd);
			// System.out.println(pd.getPoint().toString() + "--->"+
			// pd.getDistance());
		}

		return closestPointsList;
	}

	// public List<PointDistance> getUserEstimation(
	// HashMap<String, String> accessPoints) {
	// List<PointDistance> closestPointsList = new ArrayList<PointDistance>();
	// PointDistance p1=new PointDistance();
	// PointDistance p2=new PointDistance();
	// PointDistance p3=new PointDistance();
	// p1.setPoint(new Point("10", "10", "1"));
	// p1.setDistance("10");
	// p2.setPoint(new Point("20", "20", "1"));
	// p2.setDistance("20");
	// p3.setPoint(new Point("30", "30", "1"));
	// p3.setDistance("30");
	// closestPointsList.add(p1);
	// closestPointsList.add(p2);
	// closestPointsList.add(p3);
	// return closestPointsList;
	// }

	public List<Point> getPreCollectedPrints() {
		PostGresDB p = new PostGresDB();
		List<Point> preCollectedPrints = new ArrayList<Point>();
		try {
			preCollectedPrints = p.getWifiFingerPrints();
		} catch (Exception ex) {
			Point fp = new Point(ex.getMessage(), "0", "0");
			PointDistance pd = new PointDistance();
			pd.setPoint(fp);
		} finally {
			try {
				if (p.conn != null && !p.conn.isClosed()) {
					p.conn.close();
				}
			} catch (SQLException e) {

			}
		}
		return preCollectedPrints;
	}

	public List<Point> getPreCollectedTest() {
		PostGresDB p = new PostGresDB();
		List<Point> preCollectedPrints = new ArrayList<Point>();
		try {
			preCollectedPrints = p.getWifiFingerPrints();
		} catch (Exception ex) {
			Point fp = new Point(ex.getMessage(), "0", "0");
			PointDistance pd = new PointDistance();
			pd.setPoint(fp);
		} finally {
			try {
				if (p.conn != null && !p.conn.isClosed()) {
					p.conn.close();
				}
			} catch (SQLException e) {

			}
		}
		return preCollectedPrints;
	}

	public List<PointDistance> getUserEstimationTesting(
	        List<WifiAccessPoint> accessPoints) {
		List<PointDistance> closestPointsList = new ArrayList<PointDistance>();
		PointDistance p1 = new PointDistance();
		PointDistance p2 = new PointDistance();
		PointDistance p3 = new PointDistance();
		p1.setPoint(new Point("10", "10", "1"));
		p1.setDistance("10");
		p2.setPoint(new Point("500", "500", "1"));
		p2.setDistance("20");
		p3.setPoint(new Point("30", "30", "1"));
		p3.setDistance("30");
		closestPointsList.add(p1);
		closestPointsList.add(p2);
		closestPointsList.add(p3);
		return closestPointsList;
	}

	public List<Point> getDirections(Point source, String parkingSpot) {
		PostGresDB p = new PostGresDB();
		List<Point> wayPoints = new ArrayList<Point>();
		try {
			wayPoints = p.getDirections(source, parkingSpot);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		} finally {
			try {
				if (p.conn != null && !p.conn.isClosed()) {
					p.conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return wayPoints;
	}

	private String calculateDistance(HashMap<String, String> preCollectedAps,
	        HashMap<String, String> currentAps) {
		float distance = 0;
		boolean isOneApAvailable = false;
		float pcStrength = 0;
		float currentStrength = 0;
		for (Map.Entry<String, String> entry : currentAps.entrySet()) {
			if (preCollectedAps.containsKey(entry.getKey())) {
				isOneApAvailable = true;
				pcStrength = Float.parseFloat(preCollectedAps.get(entry
				        .getKey()));
				currentStrength = Float.parseFloat(entry.getValue());
				distance += Math.abs(pcStrength - currentStrength);
			}
		}

		if (isOneApAvailable)
			return distance + "";
		else
			return Float.MAX_VALUE + "";
	}

	private static List<Point> getPreCollectedPoints() {
		Random r = new Random();
		List<Point> pointList = new ArrayList<Point>();
		for (int i = 0; i < 10; i++) {
			Point p = new Point();
			p.setX(i * 2 + "");
			p.setY(i * 2 + "");
			p.setZ("1");
			HashMap<String, String> h = new HashMap<String, String>();
			for (int j = 0; j < 9; j++) {
				h.put("Wifi" + j, r.nextInt(150) + "");
			}
			p.setAccessPoints(h);
			pointList.add(p);
		}
		return pointList;
	}

	

	

	public String[] getPartitionsWithDirections() {
		PostGresDB p = new PostGresDB();
		String[] partitionArr = null;
		try {
			partitionArr = p.getPartitionsWithDirections();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		} finally {
			try {
				if (p.conn != null && !p.conn.isClosed()) {
					p.conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return partitionArr;
	}

}
