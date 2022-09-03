package com.euler;

import com.euler.common.EulerUtils.CombinatorialNumberModCache;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.map.IntLongCursor;
import com.koloboke.collect.map.IntLongMap;
import com.koloboke.collect.map.hash.HashIntLongMaps;

public class Euler709 {
	private final static int SIZE=24680;
	
	private static class BagPackings	{
		private final static long MOD=1020202009l;
		private final static CombinatorialNumberModCache COMBINATORIALS=new CombinatorialNumberModCache(1,MOD);
		private final IntLongMap distributions;
		private BagPackings(IntLongMap distributions)	{
			this.distributions=distributions;
		}
		public static BagPackings allBagsEmpty(int nBags)	{
			IntLongMap distributions=HashIntLongMaps.newImmutableMapOf(nBags,1l);
			return new BagPackings(distributions);
		}
		public BagPackings addNewBag()	{
			IntLongMap newDistributions=HashIntLongMaps.newMutableMap();
			for (IntLongCursor cursor=distributions.cursor();cursor.moveNext();)	{
				int howManyBags=cursor.key();
				long howManyCases=cursor.value();
				for (int i=0;i<=howManyBags;i+=2)	{
					long choices=COMBINATORIALS.get(howManyBags,i);
					newDistributions.compute(howManyBags+1-i,(int unusedKey,long currentCount)->(currentCount+(choices*howManyCases))%MOD);
				}
			}
			return new BagPackings(newDistributions);
		}
		public long getTotalCount()	{
			long result=0;
			for (LongCursor cursor=distributions.values().cursor();cursor.moveNext();) result+=cursor.elem();
			return result%MOD;
		}
	}
	
	public static void main(String[] args)	{
		/*
		773479144
		Elapsed 11457.913213215 seconds.
		 */
		long tic=System.nanoTime();
		BagPackings cases=BagPackings.allBagsEmpty(2);
		for (int i=3;i<=SIZE;++i)	{
			cases=cases.addNewBag();
			System.out.println(i+"...");
		}
		long result=cases.getTotalCount();
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
