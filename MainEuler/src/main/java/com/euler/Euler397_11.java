package com.euler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.google.common.collect.Sets;

public class Euler397_11 {
	private final static int K=10;
	private final static int X=100;
	
	private static class Triangle	{
		private final static long[] HOLDER=new long[3];
		private final long k;
		private final long a;
		private final long b;
		private final long c;
		private final char pos45;
		private Triangle(long k,long a,long b,long c,char pos45)	{
			this.k=k;
			this.a=a;
			this.b=b;
			this.c=c;
			this.pos45=pos45;
		}
		public static Triangle getFromFineCase(long k,long a,long b,long c)	{
			HOLDER[0]=a;
			HOLDER[1]=b;
			HOLDER[2]=c;
			Arrays.sort(HOLDER);
			char pos45;
			if (HOLDER[0]==b) pos45='a';
			else if (HOLDER[1]==b) pos45='b';
			else if (HOLDER[2]==b) pos45='c';
			else throw new IllegalStateException();
			return new Triangle(k,HOLDER[0],HOLDER[1],HOLDER[2],pos45);
		}
		@Override
		public int hashCode()	{
			return Long.hashCode(k+K*(a+2*X*(b+2*X*c)))+Character.hashCode(pos45);
		}
		@Override
		public boolean equals(Object other)	{
			Triangle tOther=(Triangle)other;
			return (k==tOther.k)&&(a==tOther.a)&&(b==tOther.b)&&(c==tOther.c)&&(pos45==tOther.pos45);
		}
	}
	
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
		private void getTriangles(long q,long p,Set<Triangle> result)	{
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
					// result+=1+maxC-minC;
					for (long c=minC;c<=maxC;++c)	{
						long a=c-smallDelta;
						long b=realY-c;
						boolean isValid;
						if (calcu==KCalculator.B) isValid=(a<b)&&(b<c);
						else isValid=(b<a)||(b>c);
						if (isValid) if (!result.add(Triangle.getFromFineCase(k,a,b,c))) throw new IllegalStateException();
					}
				}
			}
		}
	}
	
	private static Set<Triangle> getFinesse()	{
		int maxY=2*X;
		int[] lastPrimes=Primes.lastPrimeSieve(maxY);
		Set<Triangle> result=new HashSet<>(12512);
		// There are still missing solutions :O. 12404, but I expect 12492+20=12512.
		// Next stop: take these solution and the brute force case, look for differences.
		{
			FullCalculator calcu0=new FullCalculator(0);
			for (long p=1;p<=K;++p) calcu0.getTriangles(0,p,result);
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
				calcu.getTriangles(q,p,result);
			}
		}
		return result;
	}
	
	private static Set<Triangle> getBruteForce()	{
		Set<Triangle> result=new HashSet<>(12512);
		for (long k=1;k<=K;++k) for (long a=-X;a<=X;++a) for (long b=a+1;b<=X;++b) for (long c=b+1;c<=X;++c)	{
			long ax=k*a;
			long ay=a*a;
			long bx=k*b;
			long by=b*b;
			long cx=k*c;
			long cy=c*c;
			long abx=bx-ax;
			long bcx=cx-bx;
			long acx=cx-ax;
			long aby=by-ay;
			long bcy=cy-by;
			long acy=cy-ay;
			long ab2=abx*abx+aby*aby;
			long bc2=bcx*bcx+bcy*bcy;
			long ac2=acx*acx+acy*acy;
			long abbc=abx*bcx+aby*bcy;
			long bcac=bcx*acx+bcy*acy;
			long abac=abx*acx+aby*acy;
			long abbc2=abbc*abbc;
			long bcac2=bcac*bcac;
			long abac2=abac*abac;
			if ((abbc<0)&&(abbc2*2==ab2*bc2))	{	// Note the sign!
				if (!result.add(new Triangle(k,a,b,c,'b'))) throw new IllegalStateException();
			}
			if ((bcac>0)&&(bcac2*2==bc2*ac2))	{
				if (!result.add(new Triangle(k,a,b,c,'c'))) throw new IllegalStateException();
			}
			if ((abac>0)&&(abac2*2==ab2*ac2))	{
				if (!result.add(new Triangle(k,a,b,c,'a'))) throw new IllegalStateException();
			}
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Set<Triangle> fineResult=getFinesse();
		Set<Triangle> bruteResult=getBruteForce();
		Set<Triangle> fineNotBrute=new HashSet<>(Sets.difference(fineResult,bruteResult));
		Set<Triangle> bruteNotFine=new HashSet<>(Sets.difference(bruteResult,fineResult));
		System.out.println(String.format("%d fine results, %d brute results. %d fine not brute, %d brute not fine.",fineResult.size(),bruteResult.size(),fineNotBrute.size(),bruteNotFine.size()));
		for (Triangle t:bruteNotFine) System.out.println(String.format("I lost this triangle! k=%d, a=%d, b=%d, c=%d, type %c.",t.k,t.a,t.b,t.c,t.pos45));
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
