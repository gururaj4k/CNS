package com.cns.domains;

import java.util.Comparator;

public class PointDistanceComparator implements Comparator<PointDistance> {

	@Override
	public int compare(PointDistance o1, PointDistance o2) {
		if (Double.parseDouble(o1.getDistance()) < Double.parseDouble(o2.getDistance()))
			return 1;
		if (Double.parseDouble(o1.getDistance()) > Double.parseDouble(o2.getDistance()))
			return -1;
		return 0;
	}
}
