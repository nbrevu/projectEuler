package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler601 {
	// Parece que est� mal, pero no tengo ni reputa idea de por qu�.
	private final static int MAX_STREAK_LENGTH=31;
	private final static long GROWTH_FACTOR=4;
	
	private static class LcmCache	{
		private List<Long> cache;
		public LcmCache()	{
			cache=new ArrayList<>();
			cache.add(0l);
			cache.add(1l);
		}
		public long get(int index)	{
			int N=cache.size()-1;
			while (N<index)	{
				long lastLcm=cache.get(N);
				++N;
				long newLcm=lastLcm*N/EulerUtils.gcd(lastLcm,N);
				cache.add(newLcm);
			}
			return cache.get(index);
		}
	}
	
	private static long getStreaks(int streakLength,long limit,LcmCache cache)	{
		long myStreak=cache.get(streakLength);
		long nextStreak=cache.get(1+streakLength);
		return LongMath.divide(limit,myStreak,RoundingMode.CEILING)-LongMath.divide(limit,nextStreak,RoundingMode.CEILING);
	}
	
	public static void main(String[] args)	{
		long sum=0;
		long limit=1;
		LcmCache cache=new LcmCache();
		for (int i=1;i<=MAX_STREAK_LENGTH;++i)	{
			limit*=GROWTH_FACTOR;
			long result=getStreaks(i,limit-1,cache);
			System.out.println("P("+i+","+limit+")="+result);
			sum+=result;
		}
		System.out.println(sum);
	}
}
