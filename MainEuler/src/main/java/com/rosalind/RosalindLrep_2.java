package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.euler.common.EulerUtils;

public class RosalindLrep_2 {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_lrep.txt";
	
	// I'm not very proud of this...
	private static Map<String,Integer> getRepeatMap(String str,int size)	{
		int begin=0;
		int end=size;
		Map<String,Integer> result=new HashMap<>();
		for (;end<=str.length();++begin,++end) EulerUtils.increaseCounter(result,str.substring(begin,end));
		return result;
	}
	
	private static String getRepeatedStringIfMin(Map<String,Integer> repeated,int minRepeat)	{
		for (Map.Entry<String,Integer> entry:repeated.entrySet()) if (entry.getValue()>=minRepeat) return entry.getKey();
		return null;
	}
	
	private static String findLongestRepeat(String str,int minRepeat)	{
		int N=str.length();
		int start=2*N/minRepeat;
		for (int l=start;l>=0;--l)	{
			Map<String,Integer> repeatMap=getRepeatMap(str,l);
			String result=getRepeatedStringIfMin(repeatMap,minRepeat);
			if (result!=null) return result;
		}
		return null;
	}
	
	public static void main(String[] args) throws IOException	{
		String baseString;
		int minRepeat;
		// List<NodeInfo> nodes=new ArrayList<>();
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			baseString=reader.readLine();
			minRepeat=Integer.parseInt(reader.readLine());
			/*
			for (;;)	{
				String line=reader.readLine();
				if (line==null) break;
				String[] split=line.split(" ");
				nodes.add(new NodeInfo(split[0],split[1],Integer.parseInt(split[2]),Integer.parseInt(split[3])));
			}
			*/
		}
		String result=findLongestRepeat(baseString,minRepeat);
		System.out.println(result);
	}
}
