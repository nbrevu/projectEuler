package com.euler;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.euler.common.EulerUtils;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

public class Euler241_2 {
	// https://oeis.org/A159907/b159907.txt
	private final static List<Long> FIRST_TERMS=Arrays.asList(2l,24l,4320l,4680l,26208l,8910720l,17428320l,20427264l,91963648l,197064960l,8583644160l,10200236032l,21857648640l,57575890944l,57629644800l,206166804480l,17116004505600l,1416963251404800l,15338300494970880l);
	
	private static class HalfAbundantInfo implements Comparable<HalfAbundantInfo>	{
		private long number;
		private Map<Long,Integer> factors;
		private long sigma;
		private long div;
		public HalfAbundantInfo(long in)	{
			number=in;
			factors=getFactors(in);
			sigma=getSigma(factors);
			assert ((2*sigma)%in)==0;
			assert (((2*sigma)/in)%2)==1;
			div=sigma/in;
		}
		@Override
		public int compareTo(HalfAbundantInfo other)	{
			return Long.valueOf(number).compareTo(Long.valueOf(other.number));
		}
		@Override
		public String toString()	{
			StringBuilder sb=new StringBuilder();
			sb.append(number).append(" = ");
			boolean first=true;
			for (Map.Entry<Long,Integer> entry:factors.entrySet())	{
				if (first) first=false;
				else sb.append(" · ");
				sb.append(entry.getKey());
				int pow=entry.getValue();
				if (pow>1) sb.append('^').append(pow);
			}
			sb.append(". Sigma=").append(sigma).append("; ").append(sigma).append('/').append(number).append('=').append(div).append(".5.");
			return sb.toString();
		}
		public long getDiv()	{
			return div;
		}
		private static long addDivisor(long in,long p,Map<Long,Integer> divs)	{
			while ((in%p)==0)	{
				in/=p;
				EulerUtils.increaseCounter(divs,p);
			}
			return in;
		}
		private static Map<Long,Integer> getFactors(long in)	{
			Map<Long,Integer> result=new TreeMap<>();
			in=addDivisor(in,2l,result);
			in=addDivisor(in,3l,result);
			long p=5;
			boolean add4=false;
			while (in>1)	{
				in=addDivisor(in,p,result);
				p+=(add4?4:2);
				add4=!add4;
			}
			return result;
		}
		private static long getSigma(Map<Long,Integer> factors)	{
			long prod=1l;
			for (Map.Entry<Long,Integer> entry:factors.entrySet())	{
				long prime=entry.getKey();
				int power=entry.getValue();
				long highPow=prime;
				for (int i=0;i<power;++i) highPow*=prime;
				long factor=(highPow-1)/(prime-1);
				prod*=factor;
			}
			return prod;
		}
	}

	public static void main(String[] args)	{
		Multimap<Long,HalfAbundantInfo> knownInfo=TreeMultimap.create();
		for (long l:FIRST_TERMS)	{
			HalfAbundantInfo info=new HalfAbundantInfo(l);
			knownInfo.put(info.getDiv(),info);
		}
		for (Map.Entry<Long,Collection<HalfAbundantInfo>> entry:knownInfo.asMap().entrySet())	{
			SortedSet<HalfAbundantInfo> sorted=new TreeSet<>(entry.getValue());
			for (HalfAbundantInfo info:sorted) System.out.println(info.toString());
			System.out.println();
		}
		long s=0;
		for (long l:FIRST_TERMS) s+=l;
		System.out.println(s);
	}
}
