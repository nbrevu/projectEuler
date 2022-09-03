package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.euler.common.EulerUtils;
import com.google.common.math.DoubleMath;
import com.rosalind.utils.IoUtils;

public class RosalindConv {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_conv.txt";
	
	private final static double PRECISION=1e-5;
	
	private static long truncateDoubleToPrecision(double in)	{
		return DoubleMath.roundToLong(in/PRECISION,RoundingMode.HALF_UP);
	}
	
	private static double returnToDouble(long in)	{
		return PRECISION*(double)in;
	}
	
	private static Map.Entry<Long,Integer> findBest(Map<Long,Integer> map)	{
		Map.Entry<Long,Integer> result=null;
		for (Map.Entry<Long,Integer> entry:map.entrySet()) if (result==null) result=entry;
		else if (result.getValue()<entry.getValue()) result=entry;
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		double[] set1;
		double[] set2;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			set1=IoUtils.parseStringAsArrayOfDoubles(reader.readLine(),-1);
			set2=IoUtils.parseStringAsArrayOfDoubles(reader.readLine(),-1);
		}
		Map<Long,Integer> counter=new HashMap<>();
		for (double d1:set1) for (double d2:set2)	{
			double diff=d1-d2;
			long id=truncateDoubleToPrecision(diff);
			EulerUtils.increaseCounter(counter,id);
		}
		Map.Entry<Long,Integer> entry=findBest(counter);
		double diff=returnToDouble(entry.getKey());
		int count=entry.getValue();
		System.out.println(count);
		System.out.println(String.format(Locale.UK,"%.5f",diff));
	}
}
