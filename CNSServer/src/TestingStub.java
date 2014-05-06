import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import com.cns.domains.Point;
import com.cns.domains.PointDistance;
import com.cns.domains.PointDistanceComparator;
import com.cns.domains.WifiAccessPoint;
import com.cns.services.CNSController;
import com.cns.services.PostGresDB;

public class TestingStub {

	public static void main(String[] args) {
//		 PostGresDB p = new PostGresDB();
//		 CNSController c=new CNSController();
//		 try {
//		 Point p1 = new Point("45", "50", "1");
//		 
//		 List<Point> pidList = c.getDirections(p1, "105");
//		 //System.out.println("Before Deleting......");
//		 for (Point point : pidList) {
//		 System.out.println(point.toString());
//		 }
//		
//		
//		
//		
//		 } catch (Exception ex) {
//		 System.out.println(ex.getMessage());
//		 } finally {
//		 try {
//		 if (p.conn != null && !p.conn.isClosed()) {
//		 p.conn.close();
//		 }
//		 } catch (SQLException e) {
//		 // TODO Auto-generated catch block
//		 e.printStackTrace();
//		 }
//		 }
		// //// //saveWifiPoints();
		// //// Point p1=new Point("7", "6", "1");
		// //// Point p2=new Point("77", "6", "1");
		// //// Point p3=new Point("85", "3", "1");
		// //// System.out.println(pointToLineDistance(p1, p2, p3));
		// //
		// // // saveWifiPoints();
//
//		HashMap<String, String> ch = getCurrentHashMap(227);
//		CNSController c = new CNSController();
//		List<PointDistance> pdList = c.getUserEstimation(ch);
//		for (PointDistance pointDistance : pdList) {
//			System.out.println("(" + pointDistance.getPoint().getX() + ","
//			        + pointDistance.getPoint().getY() + ") -->"
//			        + pointDistance.getDistance());
//		}
		//saveWifiPoints();
		CNSController c=new CNSController();
		List<Point> p= c.getPreCollectedPrints();
	System.out.println(p.size());
	}

	private static HashMap<String, String> getCurrentHashMap() {
		Random r = new Random();
		HashMap<String, String> h = new HashMap<String, String>();
		for (int j = 0; j < 1; j++) {
			h.put("Wifi" + j, (r.nextInt(150) + ""));
		}
		return h;
	}

	private static HashMap<String, String> getCurrentHashMap(int pid) {
		PostGresDB p = new PostGresDB();
		Point point = null;
		try {
			point = p.getWifiFingerPrints(pid);

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
		return point.getAccessPoints();
	}

	private static List<WifiAccessPoint> getWifiPoints() {
		List<WifiAccessPoint> accessPoints = new ArrayList<WifiAccessPoint>();
		for (int i = 0; i < 3; i++) {
			WifiAccessPoint w = new WifiAccessPoint();
			w.setBssid("BSSID" + i);
			w.setSsid("SSID" + i);
			w.setStrength(i + "");
			w.setDbm(i + "");
			accessPoints.add(w);
		}
		return accessPoints;

	}

	private static void testNN(HashMap<String, String> currentAccessPoints) {
		CNSController c = new CNSController();
		List<PointDistance> closestPts = c
		        .getUserEstimation(currentAccessPoints);
		for (PointDistance pointDistance : closestPts) {
			System.out.println("(" + pointDistance.getPoint().getX() + ","
			        + pointDistance.getPoint().getY() + ")--->"
			        + pointDistance.getDistance());
		}
	}

	public static void saveWifiPoints() {
		PostGresDB p = new PostGresDB();
		List<WifiAccessPoint> aps = new ArrayList<WifiAccessPoint>();
		WifiAccessPoint w = new WifiAccessPoint();
		w.setBssid("BSSID");
		w.setSsid("SSID");
		w.setDbm("-88");
		w.setStrength("107");
		aps.add(w);
		String s = p.saveWifiAccessPoints(aps, 0, 0, 1, 20);
		System.out.println(s);
	}

	public static double pointToLineDistance(Point A, Point B, Point P) {
		double normalLength = Math.sqrt((Integer.parseInt(B.getX()) - Integer
		        .parseInt(A.getX()))
		        * (Integer.parseInt(B.getX()) - Integer.parseInt(A.getX()))
		        + (Integer.parseInt(B.getY()) - Integer.parseInt(A.getY()))
		        * (Integer.parseInt(B.getY()) - Integer.parseInt(A.getY())));
		return Math
		        .abs((Integer.parseInt(P.getX()) - Integer.parseInt(A.getX()))
		                * (Integer.parseInt(B.getY()) - Integer.parseInt(A
		                        .getY()))
		                - (Integer.parseInt(P.getY()) - Integer.parseInt(A
		                        .getY()))
		                * (Integer.parseInt(B.getX()) - Integer.parseInt(A
		                        .getX())))
		        / normalLength;
	}
	
	public static void partitions(){
		CNSController n=new CNSController();
		String[] s=n.getPartitionsWithDirections();
		for (String string : s) {
	        System.out.println(string);
        }
	}
}
