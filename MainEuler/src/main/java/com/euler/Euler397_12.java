package com.euler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;

public class Euler397_12 {
	private final static int K=100;
	private final static int X=1000;
	
	private static class Summariser	{
		private static class InnerSummariser	{
			private long minY;
			private long maxY;
			private long maxQ;
			private final SortedMap<Long,Long> maxPPerQ;
			public InnerSummariser()	{
				minY=Long.MAX_VALUE;
				maxY=Long.MIN_VALUE;
				maxQ=Long.MIN_VALUE;
				maxPPerQ=new TreeMap<>();
			}
			public void store(long p,long q,long y)	{
				minY=Math.min(y,minY);
				maxY=Math.max(y,maxY);
				maxQ=Math.max(q,maxQ);
				maxPPerQ.merge(q,p,Math::max);
			}
		}
		private final SortedMap<String,InnerSummariser> data;
		public Summariser()	{
			data=new TreeMap<>();
		}
		public void store(String type,long p,long q,long y)	{
			InnerSummariser inner=data.computeIfAbsent(type,(String unused)->new InnerSummariser());
			inner.store(p,q,y);
		}
		public void print()	{
			for (Map.Entry<String,InnerSummariser> entry:data.entrySet())	{
				System.out.println(String.format("Type %s:",entry.getKey()));
				InnerSummariser innerData=entry.getValue();
				System.out.println(String.format("\tMin Y: %d.",innerData.minY));
				System.out.println(String.format("\tMax Y: %d.",innerData.maxY));
				System.out.println(String.format("\tMax Q: %d.",innerData.maxQ));
				for (Map.Entry<Long,Long> entry2:innerData.maxPPerQ.entrySet()) System.out.println(String.format("\t\tFor Q=%d, max P=%d.",entry2.getKey().longValue(),entry2.getValue().longValue()));
			}
		}
	}
	
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
		private final Summariser summariser;
		public FullCalculator(long y,Summariser summariser)	{
			this.y=y;
			this.summariser=summariser;
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
						if (isValid)	{
							String type;
							if (calcu==KCalculator.B) type=calcu.name();
							else	{
								String baseName=calcu.name();
								String subtype=(b<a)?"1":"2";
								String signType=(y==0)?"0":((yConv==YConversion.POSITIVE)?"+":"-");
								type=baseName+subtype+signType;
							}
							summariser.store(type,p,q,realY);
							if (!result.add(Triangle.getFromFineCase(k,a,b,c))) throw new IllegalStateException();
						}
					}
				}
			}
		}
	}
	
	private static Set<Triangle> getFinesse()	{
		int maxY=2*X;
		int[] lastPrimes=Primes.lastPrimeSieve(maxY);
		Summariser s=new Summariser();
		Set<Triangle> result=new HashSet<>(12512);
		// There are still missing solutions :O. 12404, but I expect 12492+20=12512.
		// Next stop: take these solution and the brute force case, look for differences.
		{
			FullCalculator calcu0=new FullCalculator(0,s);
			for (long p=1;p<=K;++p) calcu0.getTriangles(0,p,result);
		}
		for (int y=1;y<maxY;++y)	{
			DivisorHolder divs=(y==1)?new DivisorHolder():DivisorHolder.getFromFirstPrimes(y,lastPrimes);
			divs.powInPlace(2);
			divs.addFactor(2l,1);
			long y2_2=(2l*y)*(long)y;
			FullCalculator calcu=new FullCalculator(y,s); 
			long[] qs=divs.getUnsortedListOfDivisors().toLongArray();
			Arrays.sort(qs);
			for (long q:qs)	{
				long p=y2_2/q;
				if (q>p) break;
				calcu.getTriangles(q,p,result);
			}
		}
		s.print();
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Set<Triangle> result=getFinesse();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result.size());
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
