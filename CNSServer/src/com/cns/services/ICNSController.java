package com.cns.services;

import java.util.HashMap;
import java.util.List;

import javax.jws.WebService;

import com.cns.domains.Point;
import com.cns.domains.PointDistance;
import com.cns.domains.WifiAccessPoint;

@WebService
public interface ICNSController {
	List<Point> getPreCollectedPrints();
    String saveWifiFingerPrints(List<WifiAccessPoint> accesspoints,
	        int x, int y, int z, int scanno);

	List<Point> getDirections(Point source, String parkingSpot);
	
	
	String[] getPartitionsWithDirections();

	
	
}
