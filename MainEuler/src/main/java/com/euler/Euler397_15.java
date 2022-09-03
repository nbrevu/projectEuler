package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;

import com.euler.common.BigRational;
import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;

public class Euler397_15 {
	private final static int K=IntMath.pow(10,6);
	private final static int X=IntMath.pow(10,9);
	
	private final static int NUM_THREADS=19;
	
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
				long minC=Math.max(realY,smallDelta)-X;
				long maxC=Math.min(0,realY)+X;
				if (maxC<minC) continue;
				for (KCalculator calcu:CALCUS)	{
					long k=calcu.getK(p,q,realY);
					if ((k<=0)||(k>K)) continue;
					result+=calcu.countCs(minC,maxC,realY,smallDelta);
				}
			}
			return result;
		}
	}
	
	private static BigInteger pow(BigInteger base,int exp)	{
		BigInteger current=base;
		BigInteger prod=BigInteger.ONE;
		while (exp>0)	{
			if ((exp%2)==1) prod=prod.multiply(current);
			current=current.multiply(current);
			exp/=2;
		}
		return prod;
	}
	
	private static BigInteger max(BigInteger a,BigInteger b)	{
		return (a.compareTo(b)>0)?a:b;
	}
	
	private static BigInteger min(BigInteger a,BigInteger b)	{
		return (a.compareTo(b)<0)?a:b;
	}
	
	private static BigInteger maxAbs(BigInteger a,BigInteger b,BigInteger c)	{
		a=a.abs();
		b=b.abs();
		c=c.abs();
		return max(a,max(b,c));
	}
	
	private static class DoubleCounter	{
		private final BigInteger maxK;
		private final BigInteger maxX;
		private BigInteger counter;
		public DoubleCounter(int maxK,int maxX)	{
			this.maxK=BigInteger.valueOf(maxK);
			this.maxX=BigInteger.valueOf(maxX);
			counter=BigInteger.valueOf(maxK);	// Initial special case for K=1 and its multiples.
		}
		public void countTriangles(BigInteger numA,BigInteger numB,BigInteger numC,BigInteger denom,BigInteger k)	{
			BigRational aRat=new BigRational(numA,denom);
			BigRational bRat=new BigRational(numB,denom);
			BigRational cRat=new BigRational(numC,denom);
			BigInteger aNum=aRat.num;
			BigInteger bNum=bRat.num;
			BigInteger cNum=cRat.num;
			if ((aNum.abs().compareTo(maxX)>0)||(bNum.abs().compareTo(maxX)>0)||(cNum.abs().compareTo(maxX)>0)) return;
			BigInteger aDen=aRat.den;
			BigInteger bDen=bRat.den;
			BigInteger cDen=cRat.den;
			BigInteger neededFactor=EulerUtils.lcm(aDen,EulerUtils.lcm(bDen,cDen));
			if (neededFactor.compareTo(maxK)>0) return;
			BigInteger a=aNum.multiply(neededFactor.divide(aDen));
			BigInteger b=bNum.multiply(neededFactor.divide(bDen));
			BigInteger c=cNum.multiply(neededFactor.divide(cDen));
			BigInteger realK=k.multiply(neededFactor);
			BigInteger kMultiplier=maxK.divide(realK);
			if (kMultiplier.compareTo(BigInteger.ZERO)>0)	{
				BigInteger maxTerm=maxAbs(a,b,c);
				BigInteger termMultiplier=maxX.divide(maxTerm);
				counter=counter.add(min(kMultiplier,termMultiplier));
			}
		}
		public BigInteger getCount()	{
			return counter;
		}
	}
	
	private static class SingleCountThread implements Runnable	{
		private final int maxY;
		private final int[] lastPrimes;
		private final int id;
		private long result;
		public SingleCountThread(int maxY,int[] lastPrimes,int id)	{
			this.maxY=maxY;
			this.lastPrimes=lastPrimes;
			this.id=id;
			result=0l;
		}
		@Override
		public void run()	{
			long show=1000000;
			for (int y=id;y<maxY;y+=NUM_THREADS)	{
				if (y>=show)	{
					if (y==show) System.out.println(show+"...");
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
		}
		public long getResult()	{
			return result;
		}
	}

	private static long countSingle() throws InterruptedException	{
		int maxY=2*X;
		int[] lastPrimes=Primes.lastPrimeSieve(maxY);
		long result=0l;
		{
			FullSingleCalculator calcu0=new FullSingleCalculator(0);
			for (long p=1;p<=K;++p) result+=calcu0.getTriangles(0,p);
		}
		SingleCountThread[] threadObjects=new SingleCountThread[NUM_THREADS];
		Thread[] threads=new Thread[NUM_THREADS];
		for (int i=0;i<NUM_THREADS;++i)	{
			threadObjects[i]=new SingleCountThread(maxY,lastPrimes,i+1);
			threads[i]=new Thread(threadObjects[i]);
			threads[i].start();
		}
		for (int i=0;i<NUM_THREADS;++i)	{
			threads[i].join();
			result+=threadObjects[i].getResult();
		}
		return result;
	}
	
	private static long countDouble()	{
		int[] firstPrimes=Primes.lastPrimeSieve(K);
		DoubleCounter doubleCounter=new DoubleCounter(K,X);
		for (int ik=2;ik<=K;++ik)	{
			BigInteger k=BigInteger.valueOf(ik);
			BigInteger k2=k.multiply(k);
			BigInteger k3=k2.multiply(k);
			LongIntMap divs=DivisorHolder.getFromFirstPrimes(ik,firstPrimes).getFactorMap();
			int size=divs.size();
			BigInteger[] factors=new BigInteger[size];
			int index=0;
			for (LongIntCursor cursor=divs.cursor();cursor.moveNext();)	{
				BigInteger power=pow(BigInteger.valueOf(cursor.key()),cursor.value());
				factors[index]=power.multiply(power);
				++index;
			}
			int maxBoolean=1<<size;
			for (int i=0;i<maxBoolean;++i)	{
				BigInteger p=BigInteger.ONE;
				int n=i;
				for (int j=0;j<size;++j)	{
					if ((n&1)!=0) p=p.multiply(factors[j]);
					n>>=1;
				}
				BigInteger p2=p.multiply(p);
				BigInteger p3=p2.multiply(p);
				BigInteger diff3=p3.subtract(k3);
				BigInteger sum3=p3.add(k3);
				BigInteger pk2=p.multiply(k.add(k));
				BigInteger ppk2=p.multiply(pk2);
				BigInteger pkk2=k.multiply(pk2);
				doubleCounter.countTriangles(diff3.subtract(ppk2),sum3,diff3.negate().subtract(pkk2),BigInteger.TWO.multiply(p.multiply(k.subtract(p))),k);
				doubleCounter.countTriangles(sum3.negate().subtract(ppk2),diff3,sum3.add(pkk2),BigInteger.TWO.multiply(p).multiply(k.add(p)),k);
			}
		}
		return doubleCounter.getCount().longValueExact();
	}
	
	// 141630459461893728
	// Elapsed 5100.4862693000005 seconds.
	public static void main(String[] args) throws InterruptedException	{
		long tic=System.nanoTime();
		long result=countSingle()-countDouble();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
