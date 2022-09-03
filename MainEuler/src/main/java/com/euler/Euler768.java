package com.euler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Euler768 {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\Euler768.txt";
	private final static String REGEX="\\{(\\d+),\\s(\\d+),\\s(\\d+),\\s(\\d+),\\s(\\d+),\\s(\\d+)\\}";
	
	private static boolean isSpecialCase(int[] array,int indexDiff,int valueDiff)	{
		for (int i=0;i<6-indexDiff;++i) if (array[i]+valueDiff!=array[i+indexDiff]) return false;
		return true;
	}
	
	private static boolean isCase2(int[] array)	{
		return isSpecialCase(array,3,18);
	}
	
	private static boolean isCase3(int[] array)	{
		return isSpecialCase(array,2,12);
	}
	
	public static void main(String[] args) throws IOException	{
		Path path=Paths.get(FILE);
		List<String> lines=Files.readAllLines(path);
		if (lines.size()<1) throw new IllegalStateException("Es gefällt mir nicht :'(.");
		String line=lines.get(0);
		Matcher m=Pattern.compile(REGEX).matcher(line);
		List<int[]> totalCases=new ArrayList<>();
		List<int[]> cases2=new ArrayList<>();
		List<int[]> cases3=new ArrayList<>();
		List<int[]> cases6=new ArrayList<>();
		List<int[]> rareCases=new ArrayList<>();
		while (m.find())	{
			int[] parsed=new int[6];
			for (int i=0;i<6;++i) parsed[i]=Integer.parseInt(m.group(i+1));
			boolean is2=isCase2(parsed);
			boolean is3=isCase3(parsed);
			totalCases.add(parsed);
			if (is2) cases2.add(parsed);
			if (is3) cases3.add(parsed);
			if (is2&&is3) cases6.add(parsed);
			if (!is2&&!is3) rareCases.add(parsed);
			int expected2=(18*17*16)/6;
			int expected3=(12*11)/2;
			int expected6=6;
			System.out.println("Total cases: "+totalCases.size()+".");
			System.out.println("Type 2 cases (expected="+expected2+"): "+cases2.size()+".");
			System.out.println("Type 3 cases (expected="+expected3+"): "+cases3.size()+".");
			System.out.println("Type 6 cases (expected="+expected6+"): "+cases6.size()+".");
			System.out.println("Unexpected cases: "+rareCases.size()+".");
		}
	}
}
