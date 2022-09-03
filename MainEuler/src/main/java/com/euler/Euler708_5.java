package com.euler;

import java.math.RoundingMode;

import com.euler.common.MeisselLehmerPrimeCounter;
import com.google.common.math.LongMath;

public class Euler708_5 {
	/*-
	Result for i=1: 3204941750802 (elapsed 425.676551452 seconds).
	Result for i=2: 11715902308080 (elapsed 1147.048317623 seconds).
	Result for i=3: 19717814526785 (elapsed 1736.7198914730002 seconds).
	Result for i=4: 21306682904040 (elapsed 1727.570302731 seconds).
	Result for i=5: 17323079621014 (elapsed 1334.082282944 seconds).
	Result for i=6: 11694704489580 (elapsed 941.2326754210001 seconds).
	Result for i=7: 7001087934965 (elapsed 608.305710825 seconds).
	Result for i=8: 3882841742380 (elapsed 380.427455527 seconds).
	Result for i=9: 2052389350029 (elapsed 206.02583241000002 seconds).
	Result for i=10: 1052666075366 (elapsed 128.752903983 seconds).
	Result for i=11: 529781669333 (elapsed 79.301150636 seconds).
	Result for i=12: 263439785143 (elapsed 47.226985313 seconds).
	Result for i=13: 129986121851 (elapsed 27.551127 seconds).
	Result for i=14: 63809981451 (elapsed 15.934124055000002 seconds).
	Result for i=15: 31214953362 (elapsed 9.144533417 seconds).
	Result for i=16: 15231822577 (elapsed 5.24573191 seconds).
	Result for i=17: 7418588349 (elapsed 2.996512391 seconds).
	Result for i=18: 3607646060 (elapsed 1.692249512 seconds).
	Result for i=19: 1752071168 (elapsed 0.979918347 seconds).
	Result for i=20: 849839564 (elapsed 0.5643121790000001 seconds).
	Result for i=21: 411731233 (elapsed 0.322331117 seconds).
	Result for i=22: 199229191 (elapsed 0.185596274 seconds).
	Result for i=23: 96282883 (elapsed 0.10801546000000001 seconds).
	Result for i=24: 46470510 (elapsed 0.062364127000000005 seconds).
	Result for i=25: 22396073 (elapsed 0.036093409 seconds).
	Result for i=26: 10777861 (elapsed 0.020782436 seconds).
	Result for i=27: 5177692 (elapsed 0.012249696000000001 seconds).
	Result for i=28: 2483121 (elapsed 0.007378359 seconds).
	Result for i=29: 1188355 (elapsed 0.004240377 seconds).
	Result for i=30: 567563 (elapsed 0.002524095 seconds).
	Result for i=31: 270366 (elapsed 0.0014940700000000001 seconds).
	Result for i=32: 128415 (elapsed 8.89868E-4 seconds).
	Result for i=33: 60879 (elapsed 5.313210000000001E-4 seconds).
	Result for i=34: 28696 (elapsed 3.1726400000000004E-4 seconds).
	Result for i=35: 13519 (elapsed 2.2679900000000002E-4 seconds).
	Result for i=36: 6313 (elapsed 1.24867E-4 seconds).
	Result for i=37: 2940 (elapsed 8.358500000000001E-5 seconds).
	Result for i=38: 1358 (elapsed 4.8417E-5 seconds).
	Result for i=39: 626 (elapsed 3.1089E-5 seconds).
	Result for i=40: 285 (elapsed 2.1660000000000003E-5 seconds).
	Result for i=41: 126 (elapsed 1.529E-5 seconds).
	Result for i=42: 57 (elapsed 1.0703E-5 seconds).
	Result for i=43: 22 (elapsed 8.154E-6 seconds).
	Result for i=44: 11 (elapsed 7.135E-6 seconds).
	Result for i=45: 4 (elapsed 6.881000000000001E-6 seconds).
	Result for i=46: 1 (elapsed 6.881000000000001E-6 seconds).
	Result for i=47: 0 (elapsed 7.64E-7 seconds).
	28874142998632109
	Elapsed 8827.497391174 seconds.
	 */
	private final static long LIMIT=LongMath.pow(10l,14);
	
	private static class AlmostPrimeCounter	{
		private final long limit;
		private final MeisselLehmerPrimeCounter counter;
		private final long[] primes;
		private long root(long x,int index)	{
			long result=(long)Math.floor(Math.pow(x,1d/index));
			// Horrible and kind of slow, but safe.
			while (Math.pow(result,index)>x) --result;
			while (Math.pow(result+1,index)<=x) ++result;
			return result;
		}
		public AlmostPrimeCounter(long limit)	{
			this.limit=limit;
			counter=new MeisselLehmerPrimeCounter(LongMath.sqrt(limit,RoundingMode.DOWN));
			primes=counter.getPrimes();
		}
		public long getCount(int amount)	{
			if (amount==1) return counter.pi(limit);
			long primeLimit=root(limit,amount);
			if (primeLimit<2) return 0l;
			else return getCountRecursive(amount,0,primeLimit,1l);
		}
		private long getCountRecursive(int levels,int startingIndex,long primeLimit,long currentProduct)	{
			if (levels==1) return counter.piWithCacheClearing(primeLimit)-startingIndex;
			long result=0l;
			for (int i=startingIndex;i<primes.length;++i)	{
				long p=primes[i];
				if (p>primeLimit) break;
				long newProduct=currentProduct*p;
				int nextLevel=levels-1;
				long newLimit=root(limit/newProduct,nextLevel);
				result+=getCountRecursive(nextLevel,i,newLimit,newProduct);
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=1l;
		long power=1l;
		AlmostPrimeCounter counter=new AlmostPrimeCounter(LIMIT);
		for (int i=1;;++i)	{
			System.out.println(i+"...");
			power+=power;
			long miniTic=System.nanoTime();
			long tmpResult=counter.getCount(i);
			long miniTac=System.nanoTime();
			double miniSeconds=1e-9*(miniTac-miniTic);
			System.out.println("Result for i="+i+": "+tmpResult+" (elapsed "+miniSeconds+" seconds).");
			if (tmpResult==0) break;
			result+=tmpResult*power;
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
