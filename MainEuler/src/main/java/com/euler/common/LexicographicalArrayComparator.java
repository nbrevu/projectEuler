package com.euler.common;

import java.util.Comparator;

public enum LexicographicalArrayComparator implements Comparator<int[]> {
	INSTANCE;
	@Override
	public int compare(int[] a1,int[] a2) {
		// This line ensures that, for the rest of the code, a1.length <= a2.length.
		if (a1.length>a2.length) return -compare(a2,a1);
		for (int i=0;i<a1.length;++i)	{
			int diff=a2[i]-a1[i];
			if (diff!=0) return diff;
		}
		return a2.length-a1.length;
	}
}
