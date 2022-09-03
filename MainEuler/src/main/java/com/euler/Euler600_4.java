package com.euler;

import java.util.EnumMap;
import java.util.Map;

public class Euler600_4 {
	private final static long LIMIT=55106;
	
	private static long triangular(long n)	{
		return (n*(n+1))/2;
	}
	
	private static long getTrapezium1(long n)	{
		long result=0;
		long maxA=n/5;
		for (long a=2;a<=maxA;++a) result+=Math.min(n-5*a,a-1);
		return result;
	}
	
	private static long getTrapezium2(long n)	{
		long result=0;
		long maxA=n/6;
		for (long a=2;a<=maxA;++a)	{
			long minC=Math.max(1,7*a-n);
			result+=a-minC;
			/*-
			long maxC=a-1;
			if (minC<=maxC) result+=1+maxC-minC;
			*/
		}
		return result;
	}
	
	private static enum HexagonDistribution	{
		REGULAR(1)	{
			@Override
			public long sumUpTo(long n) {
				return n/6;
			}
		},
		ELONGATED(3)	{
			@Override
			public long sumUpTo(long n) {
				long half=n/2;
				long quarter=half/2;
				long sixth=n/6;
				return quarter*(half-1-quarter)-sixth;
			}
		},
		SUPERMAN_REGULAR(2)	{
			@Override
			public long sumUpTo(long n) {
				long third=n/3;
				long sixth=n/6;
				long sq=third*third;
				long all=(sq-third)/2;
				long nonSym=all-sixth;
				return nonSym/2;
			}
		},
		PRISM(6)	{
			@Override
			public long sumUpTo(long n) {
				long result=0;
				long half=n/2;
				long sixth=n/6;
				for (long a=1;a<=sixth;++a)	{
					long reduced=(half-a)/2;
					result+=(half-a)*(reduced-a)-2*(triangular(reduced)-triangular(a));
				}
				return result;
			}
		},
		TRAPEZIUM(6)	{
			@Override
			public long sumUpTo(long n) {
				return getTrapezium1(n)+getTrapezium2(n);
			}
		},
		SUPERMAN_IRREGULAR(6)	{
			@Override
			public long sumUpTo(long n) {
				long result=0;
				long fourth=n/4;
				for (long a=1;a<=fourth;++a)	{
					long maxDelta=(n-2-4*a)/3;
					for (long delta=1;delta<=maxDelta;++delta)	{
						long maxC=(n-4*a-3*delta)/2;
						long restr=0;
						if (maxC>=a) ++restr;
						if (maxC>=a+delta) ++restr;
						long diff=a-delta;
						if ((diff>=1)&&(maxC>=diff)) ++restr;
						result+=maxC-restr;
					}
				}
				return result;
			}
		}
		;
		
		public final int multiplier;
		public abstract long sumUpTo(long n);
		private HexagonDistribution(int multiplier)	{
			this.multiplier=multiplier;
		}
	}
	
	private static long countCasesLesserE(long n,long a,long b)	{
		// b>=e.
		long maxE=b;
		long minE=Math.max(3*b+2*a+2-n,1);
		if (minE>maxE) return 0l;
		long result=0l;
		long fmin=n-2*a-3*b+minE;
		long fmax=n-2*a-3*b+maxE;
		if (fmin==fmax) return fmin/2;
		if ((fmin%2)==1)	{
			result+=fmin/2;
			++fmin;
		}
		if ((fmax%2)==0)	{
			result+=fmax/2;
			--fmax;
		}
		if (fmin<=fmax)	{
			fmin/=2;
			fmax/=2;
			result+=(fmax+1-fmin)*(fmin+fmax);
		}
		return result;
	}
	
	private static long countCasesGreaterE(long n,long a,long b)	{
		// b<e.
		long maxE=Math.min(n-2*a-b-2,a+b-1);
		long minE=b+1;
		if (minE>maxE) return 0l;
		long result=0l;
		long fmin=n-2*a-b-minE;
		long fmax=n-2*a-b-maxE;
		if ((fmin%2)==0)	{
			result+=fmin/2;
			--fmin;
		}
		if ((fmax%2)==1)	{
			result+=fmax/2;
			++fmax;
		}
		if (fmax<=fmin)	{
			fmin/=2;
			fmax/=2;
			result+=(fmin+1-fmax)*(fmin+fmax);
		}
		return result;
	}
	
	private static long getAll(long n)	{
		long half=n/2;
		long maxA=half-1;
		long result=0l;
		for (long a=1;a<=maxA;++a)	{
			long maxB=half-(a+1);
			for (long b=1;b<=maxB;++b) result+=countCasesLesserE(n,a,b)+countCasesGreaterE(n,a,b);
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Map<HexagonDistribution,Long> symmetricCases=new EnumMap<>(HexagonDistribution.class);
		for (HexagonDistribution h:HexagonDistribution.values()) symmetricCases.put(h,h.sumUpTo(LIMIT));
		// System.out.println(symmetricCases);
		long result=getAll(LIMIT);
		for (Map.Entry<HexagonDistribution,Long> entry:symmetricCases.entrySet()) result-=entry.getKey().multiplier*entry.getValue().longValue();
		result/=12l;
		for (Long cases:symmetricCases.values()) result+=cases.longValue();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
