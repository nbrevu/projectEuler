package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.rosalind.RosalindCat.StringId;

public class RosalindRnas {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_rnas.txt";
	private final static int MIN_LENGTH=5;
	
	private static class MotzkinFinderModified	{
		private final static boolean[][] CONNECTIONS=new boolean[][]{{false,false,false,true},{false,false,true,false},{false,true,false,true},{true,false,true,false}};
		private final static Map<Character,Integer> CHAR_MAPPING=ImmutableMap.of('A',0,'C',1,'G',2,'U',3);
		private final static BigInteger TWO=BigInteger.valueOf(2l);
		private final Map<StringId,BigInteger> counters;
		private final int minLength;
		public MotzkinFinderModified(int minLength)	{
			counters=new HashMap<>();
			this.minLength=minLength;
		}
		public BigInteger getPathsForString(String in)	{
			StringId id=new StringId(in);
			BigInteger result=counters.get(id);
			if (result==null)	{
				result=calculatePathsForString(in);
				counters.put(id,result);
			}
			return result;
		}
		private BigInteger calculatePathsForString(String in)	{
			int N=in.length();
			if (N<minLength) return BigInteger.ONE;
			else if (N==minLength)	{
				char c1=in.charAt(0);
				char c2=in.charAt(minLength-1);
				return canConnect(c1,c2)?TWO:BigInteger.ONE;
			}
			else	{
				// We need to actually calculate it!
				char c1=in.charAt(0);
				BigInteger result=BigInteger.ZERO;
				for (int i=minLength-1;i<N;++i) if (canConnect(c1,in.charAt(i)))	{
					String s1=in.substring(1,i);
					String s2=in.substring(i+1,N);
					BigInteger v1=getPathsForString(s1);
					BigInteger v2=getPathsForString(s2);
					result=result.add(v1.multiply(v2));
				}
				result=result.add(getPathsForString(in.substring(1)));
				return result;
			}
		}
		private static boolean canConnect(char c1,char c2)	{
			return CONNECTIONS[CHAR_MAPPING.get(c1)][CHAR_MAPPING.get(c2)];
		}
	}
	
	public static void main(String[] args) throws IOException	{
		String in;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			in=reader.readLine();
		}
		MotzkinFinderModified finder=new MotzkinFinderModified(MIN_LENGTH);
		BigInteger result=finder.getPathsForString(in);
		System.out.println(result);
	}
}
