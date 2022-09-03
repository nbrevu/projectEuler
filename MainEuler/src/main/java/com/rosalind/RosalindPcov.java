package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RosalindPcov {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_pcov.txt";

	private static boolean isAPrefixOfB(String a,String b)	{
		return a.substring(1).equals(b.substring(0,b.length()-1));
	}
	
	private static void addOrThrow(Map<Integer,Integer> map,Integer key,Integer value)	{
		if (map.containsKey(key)) throw new RuntimeException();
		else map.put(key,value);
	}
	
	private static Map<Integer,Integer> getAdjacencyGraph(List<String> in)	{
		Map<Integer,Integer> result=new HashMap<>();
		int N=in.size();
		for (int i=0;i<N;++i) for (int j=0;j<N;++j) if (i!=j)	{
			String s1=in.get(i);
			String s2=in.get(j);
			if (isAPrefixOfB(s1,s2)) addOrThrow(result,i,j);
			else if (isAPrefixOfB(s2,s2)) addOrThrow(result,j,i);
		}
		return result;
	}
	
	private static String generateMinString(List<String> strings,Map<Integer,Integer> adjacency)	{
		StringBuilder result=new StringBuilder();
		result.append(strings.get(0));
		int currentIndex=0;
		for (;;)	{
			int next=adjacency.get(currentIndex);
			if (next==0) return result.toString().substring(0,strings.size());
			String nextStr=strings.get(next);
			result.append(nextStr.charAt(nextStr.length()-1));
			currentIndex=next;
		}
	}
	
	public static void main(String[] args) throws IOException	{
		List<String> strings;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			strings=new ArrayList<>();
			for (;;)	{
				String line=reader.readLine();
				if (line==null) break;
				strings.add(line);
			}
		}
		Map<Integer,Integer> deBrujinGraph=getAdjacencyGraph(strings);
		String result=generateMinString(strings,deBrujinGraph);
		System.out.println(result);
	}
}
