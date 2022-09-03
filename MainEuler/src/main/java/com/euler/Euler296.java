package com.euler;

import java.util.Arrays;
import java.util.stream.Stream;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;

public class Euler296 {
	private final static int LIMIT=100_000;
	
	private static class Delta	{
		public final int diff;
		public final int gcd;
		public Delta(int diff,int gcd)	{
			this.diff=diff;
			this.gcd=gcd;
		}
	}
	
	private static class DeltaStrip	{
		private final Delta[] deltas;
		private final int ac;
		private int multiplier;
		private int index;
		public DeltaStrip(Delta[] deltas,int ac)	{
			this.deltas=deltas;
			this.ac=ac;
			multiplier=1;
			index=0;
		}
		public int getNumber()	{
			return ac*multiplier+deltas[index].diff;
		}
		public int getGcd()	{
			return deltas[index].gcd;
		}
		public void next()	{
			++index;
			if (index>=deltas.length)	{
				++multiplier;
				index=0;
			}
		}
	}
	
	private static class DeltaStripGenerator	{
		private final int[] firstPrimes;
		public DeltaStripGenerator(int maxAc)	{
			firstPrimes=Primes.firstPrimeSieve(maxAc);
		}
		public DeltaStrip getDeltaStrip(int ac)	{
			LongIntMap decomp=DivisorHolder.getFromFirstPrimes(ac,firstPrimes).getFactorMap();
			int[] array=new int[ac];
			Arrays.fill(array,1);
			for (LongIntCursor cursor=decomp.cursor();cursor.moveNext();)	{
				int prime=(int)cursor.key();
				int maxPower=cursor.value();
				int base=1;
				for (int p=1;p<=maxPower;++p)	{
					base*=prime;
					for (int x=0;x<ac;x+=base) array[x]*=prime;
				}
			}
			Stream.Builder<Delta> builder=Stream.builder();
			for (int i=0;i<ac;++i) if (array[i]>1) builder.accept(new Delta(i,array[i]));
			return new DeltaStrip(builder.build().toArray(Delta[]::new),ac);
		}
	}
	
	private static int countMultiples(int n,int minIncluded,int maxNotIncluded)	{
		return ((maxNotIncluded-1)/n)-((minIncluded-1)/n);
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int maxAc=LIMIT/3;
		DeltaStripGenerator generator=new DeltaStripGenerator(maxAc);
		long result=0l;
		for (int ac=2;ac<=maxAc;++ac)	{
			int maxBc=(LIMIT-ac)/2;
			DeltaStrip strip=generator.getDeltaStrip(ac);
			for (;;)	{
				int bc=strip.getNumber();
				if (bc>maxBc) break;
				int g=strip.getGcd();
				int sum=ac+bc;
				int maxAb=Math.min(sum,LIMIT+1-sum);
				int factor=sum/g;
				result+=countMultiples(factor,bc,maxAb);
				strip.next();
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
