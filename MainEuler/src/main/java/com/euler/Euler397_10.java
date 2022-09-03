package com.euler;

import java.util.Arrays;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;

public class Euler397_10 {
	private final static int K=10;
	private final static int X=100;
	
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
		},	C	{
			@Override
			public long getK(long p,long q,long y) {
				return q-y;
			}
		};
		public abstract long getK(long p,long q,long y);
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
	
	private static class FullCalculator	{
		private final static KCalculator[] CALCUS=KCalculator.values();
		private final static YConversion[] CONVS=YConversion.values();
		private final long y;
		public FullCalculator(long y)	{
			this.y=y;
		}
		private long count(long q,long p)	{
			long result=0;
			long bigDelta=p-q;
			for (YConversion yConv:CONVS)	{
				if ((y==0)&&(yConv==YConversion.NEGATIVE)) continue;
				long realY=yConv.convert(y);
				long smallDelta=p+q-2*realY;
				if (smallDelta<0) continue;
				for (KCalculator calcu:CALCUS)	{
					long k=calcu.getK(p,q,realY);
					if ((k<=0)||(k>K)) continue;
					long minC=Math.max(realY+1-X,smallDelta+1-X);
					long maxC=Math.min(X,realY-1+X);
					if (maxC<minC) continue;
					// result+=1+maxC-minC;
					for (long c=minC;c<=maxC;++c)	{
						long a=c-smallDelta;
						long b=realY-c;
						// DAS DEBUG!
						long ax=k*a;
						long ay=a*a;
						long bx=k*b;
						long by=b*b;
						long cx=k*c;
						long cy=c*c;
						long abx=bx-ax;
						long bcx=cx-bx;
						long aby=by-ay;
						long bcy=cy-by;
						long ab2=abx*abx+aby*aby;
						long bc2=bcx*bcx+bcy*bcy;
						long abbc=abx*bcx+aby*bcy;
						long abbc2=abbc*abbc;
						if (abbc2*2!=ab2*bc2) throw new IllegalStateException("Ay mecachis.");
						String ordering;
						if (b<a) ordering="b<a<c";
						else if (a==b) ordering="a<=b<c";
						else if (b<c) ordering="a<b<c";
						else if (b==c) ordering="a<b<=c";
						else if (b>c) ordering="a<c<b";
						else throw new IllegalStateException(":(");
						String validity=(abbc>=0)?"MAL":"BIEN";
						// DAS ENDE DES DEBUGS!
						if (abbc<0) System.out.println(String.format("y=%d, p=%d, q=%d, δ=%d, Δ=%d (tipo %s): k=%d, a=%d, b=%d, c=%d (%s, %s).",realY,p,q,smallDelta,bigDelta,calcu.name(),k,a,b,c,validity,ordering));
						boolean isValidReal=(abbc<0);
						boolean isValidExpected;
						if (calcu==KCalculator.B) isValidExpected=(a<b)&&(b<c);
						else isValidExpected=(b<a)||(b>c);
						if (isValidReal!=isValidExpected) throw new IllegalStateException("Scheiße!!!!!");
						if (abbc<0) ++result;
					}
				}
			}
			return result;
		}
	}
	
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int maxY=2*X;
		int[] lastPrimes=Primes.lastPrimeSieve(maxY);
		long result=0l;
		// There are still missing solutions :O. 12404, but I expect 12492+20=12512.
		// Next stop: take these solution and the brute force case, look for differences.
		{
			FullCalculator calcu0=new FullCalculator(0);
			for (long p=1;p<=K;++p) result+=calcu0.count(0,p);
		}
		for (int y=1;y<maxY;++y)	{
			DivisorHolder divs=(y==1)?new DivisorHolder():DivisorHolder.getFromFirstPrimes(y,lastPrimes);
			divs.powInPlace(2);
			divs.addFactor(2l,1);
			long y2_2=(2l*y)*(long)y;
			FullCalculator calcu=new FullCalculator(y); 
			long[] qs=divs.getUnsortedListOfDivisors().toLongArray();
			Arrays.sort(qs);
			for (long q:qs)	{
				long p=y2_2/q;
				if (q>p) break;
				result+=calcu.count(q,p);
			}
		}
		System.out.println(result);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
