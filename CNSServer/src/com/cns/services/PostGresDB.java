package com.cns.services;

import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.postgis.Geometry;
import org.postgis.PGgeometry;

import com.cns.domains.Point;
import com.cns.domains.WifiAccessPoint;

public class PostGresDB {
	static String CONNECTION_URL = "jdbc:postgresql://54.186.110.123:5432/CNS_DB";
	static String USERNAME = "postgres";
	static String PASSWORD = "password";
	public static Connection conn = null;

	public PostGresDB() {
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(CONNECTION_URL, USERNAME,
			        PASSWORD);
		} catch (Exception ex) {

		}
	}

	public String saveWifiAccessPoints(List<WifiAccessPoint> accessPoints,
	        int x, int y, int z, int scanno) {
		String error = "";
		String sqlInsert = "";
		if (scanno == 20) {
			sqlInsert = "{call insertwifi_test( ?,?,?,?,?,?,?,?) }";
		} else {
			sqlInsert = "{call insertwifi( ?,?,?,?,?,?,?,?) }";
		}
		try {
			for (WifiAccessPoint wifiAccessPoint : accessPoints) {
				CallableStatement cs = conn.prepareCall(sqlInsert);
				cs.setString(1, wifiAccessPoint.getSsid());
				cs.setString(2, wifiAccessPoint.getBssid());
				cs.setInt(3, Integer.parseInt(wifiAccessPoint.getStrength()));
				cs.setInt(4, x);
				cs.setInt(5, y);
				cs.setInt(6, z);
				cs.setInt(7, scanno);
				cs.setInt(8, Integer.parseInt(wifiAccessPoint.getDbm()));
				cs.execute();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			error = e.getMessage();
		}
		return error;
	}

	public List<Point> getWifiFingerPrints() throws Exception {
		List<Point> pointList = new ArrayList<Point>();
		try {
			PreparedStatement pst = null;
			ResultSet rs = null;
			pst = conn
			        .prepareStatement("select pid,ST_X(co_ord),ST_Y(co_ord),floor,partition from points2 where isactive=true");
			rs = pst.executeQuery();
			while (rs.next()) {
				Point p = new Point();
				int pid = rs.getInt(1);
				p.setX(rs.getInt(2) + "");
				p.setY(rs.getInt(3) + "");
				p.setZ(rs.getInt(4) + "");
				p.setPartition(rs.getInt(5) + "");
				PreparedStatement pst1 = conn
				        .prepareStatement("select BSSID, strength from wifi where pid="
				                + pid);
				ResultSet rs1 = pst1.executeQuery();
				HashMap<String, String> hm = new HashMap<String, String>();
				while (rs1.next()) {
					String bssid = rs1.getString(1);
					Float strength = rs1.getFloat(2);
					hm.put(bssid, strength + "");
				}
				if (hm != null && hm.size() > 3) {
					p.setAccessPoints(hm);
					pointList.add(p);
				}
			}

		} catch (Exception e) {
			throw e;
		}
		return pointList;

	}

	public Point getWifiFingerPrints(int pointid) {
		Point p = new Point();
		try {
			PreparedStatement pst = null;
			ResultSet rs = null;
			pst = conn
			        .prepareStatement("select pid,ST_X(co_ord),ST_Y(co_ord),floor from points2 where pid="
			                + pointid);
			rs = pst.executeQuery();
			while (rs.next()) {
				int pid = rs.getInt(1);
				p.setX(rs.getInt(2) + "");
				p.setY(rs.getInt(3) + "");
				p.setZ(rs.getInt(4) + "");
				PreparedStatement pst1 = conn
				        .prepareStatement("select BSSID, strength from wifi where pid="
				                + pid);
				ResultSet rs1 = pst1.executeQuery();
				HashMap<String, String> hm = new HashMap<String, String>();
				while (rs1.next()) {
					String bssid = rs1.getString(1);
					Float strength = rs1.getFloat(2);
					hm.put(bssid, strength + "");
				}
				p.setAccessPoints(hm);

			}

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return p;

	}

	public int getPointId(Point p) {
		int pid = 0;
		try {
			PreparedStatement pst = null;
			ResultSet rs = null;
			pst = conn
			        .prepareStatement("select pid from points_test where ST_X(co_ord)="
			                + p.getX()
			                + " and ST_Y(co_ord)="
			                + p.getY()
			                + " and floor=" + p.getZ());
			rs = pst.executeQuery();
			while (rs.next()) {
				pid = rs.getInt(1);
			}

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return pid;
	}

	public List<Point> getDirections(Point source, String parkingSpot) {
		List<Point> wayPoints = new ArrayList<Point>();
		try {
			PGgeometry pgGeom;
			Geometry geom;
			Statement stmt = conn.createStatement();
			stmt.execute("CREATE OR REPLACE FUNCTION GetRoute(x1 integer, y1 integer, z1 integer, plot1 integer) RETURNS refcursor AS '"
			        + " DECLARE "
			        + " co_ord1 geometry;"
			        + " source1 int;"
			        + " co_ord2 geometry;"
			        + " target1 int;"
			        + " mycurs refcursor;"
			        + " BEGIN "
			        + " 	SELECT co_ord,source into co_ord1,source1 from getSource(x1,y1,z1);"
			        + " 	SELECT co_ord,source into co_ord2,target1 from getTarget(plot1);"
			        + " 	OPEN mycurs FOR "
			        + "     SELECT co_ord1,1 union all"
			        + " 	SELECT p.co_ord,p.floor FROM pgr_dijkstra(''SELECT id, source, target, cost FROM lines2'',source1,target1,true, false) pd "
			        + " 	inner join points2 p on pd.id1=p.pid "
			        + " 	union all SELECT co_ord2,1; "
			        + " 	RETURN mycurs; "
			        + " END;' language plpgsql");
			stmt.close();
			conn.setAutoCommit(false);
			CallableStatement cs = null;
			ResultSet rs = null;
			cs = conn.prepareCall("{? = call GetRoute(?, ?, ?, ?)}");
			cs.registerOutParameter(1, Types.OTHER);
			cs.setInt(2, Integer.parseInt(source.getX()));
			cs.setInt(3, Integer.parseInt(source.getY()));
			cs.setInt(4, Integer.parseInt(source.getZ()));
			cs.setInt(5, Integer.parseInt(parkingSpot)); // This is parking
			                                             // lot Id
			// cs.setInt(6, Integer.parseInt(target.getY()));
			// cs.setInt(7, Integer.parseInt(target.getZ()));
			cs.execute();
			rs = (ResultSet) cs.getObject(1);
			while (rs.next()) {
				pgGeom = (PGgeometry) rs.getObject(1);
				geom = pgGeom.getGeometry();
				wayPoints.add(new Point(geom.getFirstPoint().getX() + "", geom
				        .getFirstPoint().getY() + "", rs.getInt(2) + ""));
			}
			rs.close();
			cs.close();

			if (wayPoints.size() > 2) {
				wayPoints.remove(1);
			}
			if (wayPoints.size() > 2) {
				wayPoints.remove(wayPoints.size() - 2);
			}
		} catch (Exception e) {
			wayPoints.add(new Point(e.getMessage(), "0", "0"));
		}
		// System.out.println("After Deleting......");

		// Handle First corner case
		if (wayPoints.size() > 2) {
			Point firstPoint = wayPoints.get(0);
			Point secondPoint = wayPoints.get(1);
			if (!(firstPoint.getX().equals(secondPoint.getX()))
			        && !(firstPoint.getY().equals(secondPoint.getY()))) {
				// Introduce one more point here.
				wayPoints.add(1,
				        new Point(firstPoint.getX(), secondPoint.getY(),
				                firstPoint.getZ()));
			}
		}

		return wayPoints;
	}

	public Point getCoordByPid(int pid) {
		Point p = new Point();
		try {
			PreparedStatement pst = null;
			ResultSet rs = null;
			pst = conn
			        .prepareStatement("select ST_X(co_ord),ST_Y(co_ord),floor from points_test where pid="
			                + pid);
			rs = pst.executeQuery();
			while (rs.next()) {
				p.setX(rs.getInt(1) + "");
				p.setY(rs.getInt(2) + "");
				p.setZ(rs.getInt(3) + "");
			}

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return p;
	}

	public String[] getPartitionsWithDirections() {
		String[] partitios = new String[4];
		try {
			PreparedStatement pst = null;
			ResultSet rs = null;

			for (int i = 0; i < 4; i++) {
				partitios[i] = "";
			}
			pst = conn
			        .prepareStatement("select * from direction_partitions order by direction");
			rs = pst.executeQuery();
			while (rs.next()) {
				int index = rs.getInt(1);
				partitios[index] = partitios[index] + rs.getString(2)+",";
			}
			for (int i=0;i<partitios.length;i++) {
	            if(partitios[i]!=null && partitios[i].length()>0){
	            	partitios[i]=partitios[i].substring(0, partitios[i].length()-1);
	            }
            }

		} catch (Exception e) {
		}
		return partitios;
	}

}
