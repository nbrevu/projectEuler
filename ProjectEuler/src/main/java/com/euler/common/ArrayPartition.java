package com.euler.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayPartition	{
	private final int[] separations;
	public ArrayPartition(int[] separations)	{
		this.separations=separations;
	}
	public boolean canHoldPrimes()	{
		return !((separations[0]>8)||((separations.length>4)&&(separations[4]==1)));
	}
	public List<int[]> separate(int[] array)	{
		List<int[]> result=new ArrayList<>(separations.length);
		int startIndex=0;
		for (int i=0;i<separations.length;++i)	{
			int endIndex=startIndex+separations[i];
			result.add(Arrays.copyOfRange(array,startIndex,endIndex));
			startIndex=endIndex;
		}
		return result;
	}
	@Override
	public String toString()	{
		return Arrays.toString(separations);
	}
}