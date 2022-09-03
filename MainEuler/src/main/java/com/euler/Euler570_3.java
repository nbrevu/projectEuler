package com.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.Primes;

public class Euler570_3 {
	private final static int ORDER=30000;
	
	private final static BigInteger TWO=BigInteger.valueOf(2);
	private final static BigInteger THREE=BigInteger.valueOf(3);
	private final static BigInteger FOUR=BigInteger.valueOf(4);
	private final static BigInteger SIX=BigInteger.valueOf(6);
	
	private static void add(SortedMap<BigInteger,List<Integer>> map,BigInteger value,int position)	{
		List<Integer> list=map.get(value);
		if (list==null)	{
			list=new ArrayList<>();
			map.put(value,list);
		}
		list.add(position);
	}
	
	private static List<Long> PRIMES=Primes.listLongPrimes(1000000l);
	
	private static String getDecomposition(long in)	{
		List<Long> factors=new ArrayList<>();
		for (long p:PRIMES)	{
			while ((in%p)==0)	{
				factors.add(p);
				in/=p;
			}
			if (in==1) break;
		}
		StringBuilder sb=new StringBuilder();
		boolean first=true;
		for (long p:factors)	{
			if (first) first=false;
			else sb.append('*');
			sb.append(p);
		}
		return sb.toString();
	}
	
	public static void main(String[] args)	{
		BigInteger A1=BigInteger.valueOf(12);
		BigInteger A2=BigInteger.valueOf(6);
		BigInteger B=BigInteger.valueOf(6);
		BigInteger F1=BigInteger.valueOf(-17);
		BigInteger F2=BigInteger.valueOf(17);
		BigInteger res=BigInteger.ZERO;
		SortedMap<BigInteger,List<Integer>> specialValues=new TreeMap<>();
		for (int i=3;i<=ORDER;++i)	{
			A1=A1.multiply(FOUR);
			A2=A2.multiply(FOUR);
			B=B.multiply(THREE);
			F1=F1.add(THREE);
			F2=F2.add(TWO);
			BigInteger t1=A1.subtract(B);
			BigInteger t3=F1.multiply(A2).add(F2.multiply(B));
			if (i<=20) System.out.println("t3("+i+")="+t3+".");
			BigInteger augend=t1.gcd(t3);
			if (!augend.equals(SIX)) add(specialValues,augend,i);
			res=res.add(augend);
		}
		for (Map.Entry<BigInteger,List<Integer>> entry:specialValues.entrySet())	{
			long l=entry.getKey().longValue()/6;
			System.out.println(getDecomposition(l)+" => "+entry.getValue().toString());
		}
		System.out.println(res.toString());
	}
}
