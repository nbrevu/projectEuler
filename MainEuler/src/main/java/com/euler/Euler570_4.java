package com.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import com.euler.common.Primes;

public class Euler570_4 {
	private final static int ORDER=30000;
	
	private static List<Long> PRIMES=Primes.listLongPrimes(1000l);
	
	private final static BigInteger TWO=BigInteger.valueOf(2);
	private final static BigInteger THREE=BigInteger.valueOf(3);
	private final static BigInteger FOUR=BigInteger.valueOf(4);
	private final static BigInteger SIX=BigInteger.valueOf(6);
	
	private static class LinearExpression	{
		// The expression is a*k+b.
		public final long a;
		public final long b;
		public LinearExpression(long a,long b)	{
			this.a=a;
			this.b=b;
		}
	}
	
	private static LinearExpression findVanishingPointsA(long prime)	{
		long term4=2;
		long term3=1;
		long first=0,second=0;
		long lim=2*prime+3;
		for (long index=3;index<lim;++index)	{
			term4=(4*term4)%prime;
			term3=(3*term3)%prime;
			if ((term4-term3)==0)	{
				if (first==0) first=index;
				else	{
					second=index;
					break;
				}
			}
		}
		if (second==0) return null;
		return new LinearExpression(second-first,first);
	}
	
	// MAL. Los puntos en los que esto se anula NO son lineales.
	private static LinearExpression findVanishingPointsB(long prime)	{
		long term4=1;
		long term3=1;
		long lin4=-17;
		long lin3=17;
		long first=0,second=0;
		long lim=prime*prime+3;
		for (long index=3;index<lim;++index)	{
			term4=(4*term4)%prime;
			term3=(3*term3)%prime;
			lin4+=3;
			lin3+=2;
			long res=(lin4*term4+lin3*term3)%prime;
			if (res==0)	{
				if (first==0) first=index;
				else	{
					second=index;
					break;
				}
			}
		}
		if (second==0) return null;
		return new LinearExpression(second-first,first);
	}
	
	private static void add(SortedMap<BigInteger,List<Integer>> map,BigInteger value,int position)	{
		List<Integer> list=map.get(value);
		if (list==null)	{
			list=new ArrayList<>();
			map.put(value,list);
		}
		list.add(position);
	}
	
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
		for (long p:PRIMES)	{
			if (p<5) continue;
			LinearExpression exprA=findVanishingPointsA(p);
			if (exprA!=null) System.out.println(p+" => "+exprA.a+"n+"+exprA.b);
			LinearExpression exprB=findVanishingPointsB(p);
			if (exprB!=null) System.out.println("\t"+p+" => "+exprB.a+"n+"+exprB.b);
		}
	}
}
