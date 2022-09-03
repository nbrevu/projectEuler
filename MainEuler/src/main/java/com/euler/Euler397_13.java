package com.euler;

import java.math.RoundingMode;
import java.util.Arrays;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.euler.common.Rational;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;

public class Euler397_13 {
	private final static int K=IntMath.pow(10,6);
	private final static int X=IntMath.pow(10,9);
	
	private static enum KCalculator	{
		A	{
			@Override
			public long getK(long p,long q,long y) {
				return p-y;
			}
		},	B	{
			@Override
			public long getK(long p,long q,long y) {
				return y-q;
			}
			@Override
			public long countCs(long minC,long maxC,long y,long smallDelta)	{
				long bound1=((y+smallDelta+1)/2)-1;
				long bound2=(y/2)+1;
				long realMin=Math.max(minC,bound2);
				long realMax=Math.min(maxC,bound1);
				return (realMax>=realMin)?(realMax+1-realMin):0;
			}
		},	C	{
			@Override
			public long getK(long p,long q,long y) {
				return q-y;
			}
		};
		public abstract long getK(long p,long q,long y);
		// Valid implementation for cases A and C. B redefines it.
		public long countCs(long minC,long maxC,long y,long smallDelta)	{
			long bound1=LongMath.divide(y+smallDelta,2,RoundingMode.FLOOR)+1;
			long bound2=LongMath.divide(y,2,RoundingMode.CEILING)-1;
			long result=0l;
			if (maxC>=bound1) result=(maxC+1-bound1);
			if (bound2>=minC) result+=(bound2+1-minC);
			return result;
		}
	}
	
	private static enum YConversion	{
		POSITIVE	{
			@Override
			public long convert(long y) {
				return y;
			}
			@Override
			public char getName() {
				return '+';
			}
		},	NEGATIVE	{
			@Override
			public long convert(long y) {
				return -y;
			}
			@Override
			public char getName() {
				return '-';
			}
		};
		public abstract long convert(long y);
		public abstract char getName();
	}
	
	private static class FullSingleCalculator	{
		private final static KCalculator[] CALCUS=KCalculator.values();
		private final static YConversion[] CONVS=YConversion.values();
		private final long y;
		public FullSingleCalculator(long y)	{
			this.y=y;
		}
		private long getTriangles(long q,long p)	{
			long result=0l;
			for (YConversion yConv:CONVS)	{
				if ((y==0)&&(yConv==YConversion.NEGATIVE)) continue;
				long realY=yConv.convert(y);
				long smallDelta=p+q-2*realY;
				if (smallDelta<0) continue;
				for (KCalculator calcu:CALCUS)	{
					long k=calcu.getK(p,q,realY);
					if ((k<=0)||(k>K)) continue;
					long minC=Math.max(realY,smallDelta)-X;
					long maxC=Math.min(0,realY)+X;
					if (maxC<minC) continue;
					result+=calcu.countCs(minC,maxC,realY,smallDelta);
				}
			}
			return result;
		}
	}
	
	private static long pow(long base,int exp)	{
		long current=base;
		long prod=1;
		while (exp>0)	{
			if ((exp%2)==1) prod=(prod*current);
			current=(current*current);
			exp/=2;
		}
		return prod;
	}
	
	private static class DoubleCounter	{
		private final long maxK;
		private final long maxX;
		private long counter;
		public DoubleCounter(long maxK,long maxX)	{
			this.maxK=maxK;
			this.maxX=maxX;
			counter=maxK;	// Initial special case for K=1 and its multiples.
		}
		public void countTriangles(long numA,long numB,long numC,long denom,long k)	{
			Rational aRat=new Rational(numA,denom);
			Rational bRat=new Rational(numB,denom);
			Rational cRat=new Rational(numC,denom);
			long aNum=aRat.num();
			long bNum=bRat.num();
			long cNum=cRat.num();
			if ((Math.abs(aNum)>maxX)||(Math.abs(bNum)>maxX)||(Math.abs(cNum)>maxX)) return;
			long aDen=aRat.den();
			long bDen=bRat.den();
			long cDen=cRat.den();
			long neededFactor=EulerUtils.lcm(aDen,EulerUtils.lcm(bDen,cDen));
			if (neededFactor>maxK) return;
			long a=aNum*(neededFactor/aDen);
			long b=bNum*(neededFactor/bDen);
			long c=cNum*(neededFactor/cDen);
			long realK=k*neededFactor;
			long kMultiplier=maxK/realK;
			long multiples=0;
			if (kMultiplier>0)	{
				long maxTerm=Math.max(Math.abs(a),Math.max(Math.abs(b),Math.abs(c)));
				long termMultiplier=maxX/maxTerm;
				multiples=Math.min(kMultiplier,termMultiplier);
				counter+=multiples;
			}
		}
		public long getCount()	{
			return counter;
		}
	}

	private static long countSingle()	{
		int maxY=2*X;
		int[] lastPrimes=Primes.lastPrimeSieve(maxY);
		long result=0l;
		{
			FullSingleCalculator calcu0=new FullSingleCalculator(0);
			for (long p=1;p<=K;++p) result+=calcu0.getTriangles(0,p);
		}
		long show=1000000;
		for (int y=1;y<maxY;++y)	{
			if (y>=show)	{
				System.out.println(show+"...");
				show+=1000000;
			}
			DivisorHolder divs=(y==1)?new DivisorHolder():DivisorHolder.getFromFirstPrimes(y,lastPrimes);
			divs.powInPlace(2);
			divs.addFactor(2l,1);
			long y2_2=(2l*y)*(long)y;
			FullSingleCalculator calcu=new FullSingleCalculator(y); 
			long[] qs=divs.getUnsortedListOfDivisors().toLongArray();
			Arrays.sort(qs);
			for (long q:qs)	{
				long p=y2_2/q;
				if (q>p) break;
				result+=calcu.getTriangles(q,p);
			}
		}
		return result;
	}
	
	private static long countDouble()	{
		long[] firstPrimes=Primes.lastPrimeSieve((long)K);
		DoubleCounter doubleCounter=new DoubleCounter(K,X);
		for (long k=2l;k<=K;++k)	{
			long k2=k*k;
			long k3=k2*k;
			LongIntMap divs=DivisorHolder.getFromFirstPrimes(k,firstPrimes).getFactorMap();
			int size=divs.size();
			long[] factors=new long[size];
			int index=0;
			for (LongIntCursor cursor=divs.cursor();cursor.moveNext();)	{
				long power=pow(cursor.key(),cursor.value());
				factors[index]=power*power;
				++index;
			}
			int maxBoolean=1<<size;
			for (int i=0;i<maxBoolean;++i)	{
				long p=1;
				int n=i;
				for (int j=0;j<size;++j)	{
					if ((n&1)!=0) p*=factors[j];
					n>>=1;
				}
				long p2=p*p;
				long p3=p2*p;
				long diff3=p3-k3;
				long sum3=p3+k3;
				long pk2=p*k*2;
				long ppk2=p*pk2;
				long pkk2=k*pk2;
				doubleCounter.countTriangles(diff3-ppk2,sum3,-diff3-pkk2,2*p*(k-p),k);
				doubleCounter.countTriangles(-sum3-ppk2,diff3,sum3+pkk2,2*p*(k+p),k);
			}
		}
		return doubleCounter.getCount();
	}
	
	/*
	141630459461893686
	Elapsed 34502.485917800004 seconds.
	AND WRONG. Fuck. Probably an overflow somewhere?
	Apparently the real answer is 141630459461893728, so I'm miscounting by just 42 :(.
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=countSingle()-countDouble();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
