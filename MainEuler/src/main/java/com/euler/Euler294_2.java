package com.euler;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

public class Euler294_2 {
	private final static int PRIME=23;
	private final static long DIGITS=LongMath.pow(11l,12);
	private final static long MOD=LongMath.pow(10l,9);
	
	private static class SliceMapper	{
		private static LongObjMap<SliceMapper> cache=HashLongObjMaps.newMutableMap();
		private final int[] map;
		private SliceMapper(long n)	{
			map=new int[PRIME];
			map[0]=0;
			map[1]=(int)EulerUtils.expMod(10,n,PRIME);
			for (int i=2;i<PRIME;++i) map[i]=(map[i-1]+map[1])%PRIME;
		}
		public static SliceMapper getForN(long n)	{
			// I'm the first one who shits on Java every time I can, but sometimes it comes in handy.
			return cache.computeIfAbsent(n,SliceMapper::new);
		}
		public final int[] getMap()	{
			return map;
		}
	}
	
	private static class Slice	{
		/*
		 * Each slice is associated with a pair of (amount of digits, "gap"). So if the amount of digits is 7 and the "gap" is 2,
		 * then this slice contains numbers with an amount of digits between 2+1=3 and 2+7=9 (inclusive), where the first two are always zeros.
		 * First index: sum of digits (0-23).
		 * Second index: mod %23 (0-22).
		 * The value of data[i][j] specifies how many numbers correspond with said characteristics.
		 */
		private final long[][] data;
		public Slice()	{
			data=new long[1+PRIME][PRIME];
		}
		public void initializeForBaseResult()	{
			data[0][0]=1;
		}
		public void initializeForOneDigit()	{
			for (int i=0;i<10;++i) data[i][i]=1;
		}
		/*
		 * "Gaps" must be added before combining result. If we have the result for N digits and M digits, we need to compute either
		 * (N gapped with M)+M or N+(M gapped with N).
		 * 
		 * Basically the "gap" transforms the set of values for x digits into the set of values for x+y digits, the last y of which
		 * are all zeros. That is, it multiplies the result times 10^y so that we can just add using the convolution defined in the
		 * combineWith() method.
		 */
		public Slice addGap(long n)	{
			int[] mapper=SliceMapper.getForN(n).getMap();
			Slice result=new Slice();
			for (int j=0;j<PRIME;++j)	{
				int mapped=mapper[j];
				for (int i=0;i<=PRIME;++i) result.data[i][mapped]=data[i][j];
			}
			return result;
		}
		public Slice combineWith(Slice other)	{
			Slice result=new Slice();
			for (int i=0;i<=PRIME;++i)	{
				int maxK=PRIME-i;
				for (int j=0;j<PRIME;++j) for (int l=0;l<PRIME;++l)	{
					int jlMod=(j+l)%PRIME;
					for (int k=0;k<=maxK;++k)	{
						result.data[i+k][jlMod]+=data[i][j]*other.data[k][l];
						result.data[i+k][jlMod]%=MOD;
					}
				}
			}
			return result;
		}
		public long getResult()	{
			return data[PRIME][0];
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Slice result=new Slice();
		result.initializeForBaseResult();
		Slice currentPowerOfTwo=new Slice();
		currentPowerOfTwo.initializeForOneDigit();
		long gapToAdd=0;
		for (long n=DIGITS,gap=1;n>0;n/=2,gap*=2)	{
			if ((n%2)==1)	{
				result=result.combineWith(currentPowerOfTwo.addGap(gapToAdd));
				gapToAdd+=gap;
			}
			Slice gapped=currentPowerOfTwo.addGap(gap);
			currentPowerOfTwo=currentPowerOfTwo.combineWith(gapped);
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result.getResult());
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
