package com.euler.experiments;

import com.google.common.math.LongMath;
import com.koloboke.collect.IntCursor;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.map.IntIntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashIntSets;
import com.koloboke.collect.set.hash.HashLongSets;

public class Strobogrammatic {
	private static class StrobogrammaticGenerator	{
		private final static IntIntMap STROBOGRAMMATIC_COUNTERPARTS=HashIntIntMaps.newImmutableMap(new int[] {0,1,6,8,9},new int[] {0,1,9,8,6});
		private final static IntSet STROBOGRAMMATIC_SINGLE=HashIntSets.newImmutableSet(new int[] {0,1,8});
		private final int maxSize;
		private final long[] powersOfTen;
		public StrobogrammaticGenerator(int maxSize)	{
			this.maxSize=maxSize;
			powersOfTen=generatePowersOfTen(maxSize);
		}
		private static long[] generatePowersOfTen(int upTo)	{
			long[] result=new long[upTo];
			result[0]=1l;
			for (int i=1;i<upTo;++i) result[i]=10l*result[i-1];
			return result;
		}
		public LongSet generateStrobogrammaticNumbers(int size)	{
			if (size>maxSize) throw new IllegalArgumentException("Amigo, para el carro.");
			if (size==1) return generate1();
			LongSet result=HashLongSets.newMutableSet();
			for (IntIntCursor cursor=STROBOGRAMMATIC_COUNTERPARTS.cursor();cursor.moveNext();)	{
				int key=cursor.key();
				if (key==0) continue;
				long base=key*powersOfTen[size-1]+cursor.value()*powersOfTen[0];
				if (size==2) result.add(base);
				else generateRecursive(base,1,size-2,result);
			}
			return result;
		}
		private static LongSet generate1()	{
			LongSet result=HashLongSets.newMutableSet();
			for (IntCursor cursor=STROBOGRAMMATIC_SINGLE.cursor();cursor.moveNext();)	{
				int elem=cursor.elem();
				if (elem!=0) result.add(elem);
			}
			return result;
		}
		private void generateRecursive(long current,int lower,int upper,LongSet result)	{
			if (lower==upper)	{
				long pow=powersOfTen[lower];
				for (IntCursor cursor=STROBOGRAMMATIC_SINGLE.cursor();cursor.moveNext();) result.add(current+pow*cursor.elem());
			}	else	{
				long highPow=powersOfTen[upper];
				long lowPow=powersOfTen[lower];
				for (IntIntCursor cursor=STROBOGRAMMATIC_COUNTERPARTS.cursor();cursor.moveNext();)	{
					long newCurrent=current+highPow*cursor.key()+lowPow*cursor.value();
					if (upper==lower+1) result.add(newCurrent);
					else generateRecursive(newCurrent,lower+1,upper-1,result);
				}
			}
		}
	}
	
	private static long sum(LongSet set)	{
		long result=0l;
		for (LongCursor cursor=set.cursor();cursor.moveNext();) result+=cursor.elem();
		return result;
	}
	
	private static long sumStrobogrammaticBruteForce(int maxSize)	{
		StrobogrammaticGenerator generator=new StrobogrammaticGenerator(maxSize);
		long result=0l;
		for (int i=1;i<=maxSize;++i) result+=sum(generator.generateStrobogrammaticNumbers(i));
		return result;
	}
	
	// Es funktionert nicht. Hay que afinar.
	private static long sumStrobogrammaticDynamic(int maxSize)	{
		StrobogrammaticGenerator generator=new StrobogrammaticGenerator(3);
		long strobo1=sum(generator.generateStrobogrammaticNumbers(1));
		long strobo2=sum(generator.generateStrobogrammaticNumbers(2));
		long strobo3=sum(generator.generateStrobogrammaticNumbers(3));
		long cc=strobo2;
		long dd=40*strobo1+24*101;
		long c=cc+10*strobo1;
		long d=dd+10*strobo1;
		long a=strobo1+strobo2;
		long b=a+strobo3;
		long e=4l;
		long f=12l;
		int n=1;
		int digits=2*n+1;
		while (digits<maxSize)	{
			cc=40*c+24*(LongMath.pow(10l,2*n+1)+1)*e;
			dd=40*d+24*(LongMath.pow(10l,2*n+2)+1)*f;
			c=cc+10*c;
			d=dd+10*d;
			a=b+cc;
			b=a+dd;
			e=5*e;
			f=5*f;
			++n;
			digits+=2;
		}
		return ((maxSize%2)==0)?a:b; 
	}
	
	public static void main(String[] args)	{
		StrobogrammaticGenerator generator=new StrobogrammaticGenerator(18);
		for (int i=1;i<=18;++i) System.out.println(String.format("There are %d strobogrammatic numbers with exactly %d digits.",generator.generateStrobogrammaticNumbers(i).size(),i));
		for (int i=3;i<=12;++i)	{
			long res1=sumStrobogrammaticBruteForce(i);
			long res2=sumStrobogrammaticDynamic(i);
			System.out.println(String.format("i=%d. ¿%d==%d?",i,res1,res2));
		}
	}
}
