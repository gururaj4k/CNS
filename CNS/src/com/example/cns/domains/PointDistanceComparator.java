package com.example.cns.domains;

import java.util.Comparator;


public class PointDistanceComparator implements Comparator<PointDistance> {

	@Override
	public int compare(PointDistance o1, PointDistance o2) {
		if (o1.distance < o2.distance)
			return 1;
		if (o1.distance > o2.distance)
			return -1;
		return 0;
	}
}

