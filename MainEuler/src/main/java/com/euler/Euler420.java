package com.euler;

import com.euler.common.EulerUtils;

public class Euler420 {
	private final static long N=1000;
	
	public static void main(String[] args)	{
		long count=0;
		for (long t1=1;t1<N;++t1)	{
			long t=t1*t1;
			for (long t2=t1+2;t2<=N;t2+=2)	{
				long tx=t2*t2;
				long s=(tx-t)/4;
				long l=EulerUtils.lcm(t1,t2);
				for (long x=1;x<t;++x)	{
					long y=t-x;
					if (((x*y)%t)!=0) continue;
					if (((x+2*s)%t2)!=0) continue;
					// if ((x%t1)!=0) continue;	// Doesn't happen :).
					// if (((y+2*s)%t2)!=0) throw new IllegalStateException(); // Doesn't happen :).
					// if ((y%t1)!=0) throw new IllegalStateException(); // Doesn't happen :).
					long ll=l*l;
					long num=s*t+x*y;
					// if ((num%ll)!=0) throw new IllegalStateException();	// Doesn't happen :).
					long bc=num/ll;
					// Ugly, but meh, I only want to do a quick check.
					for (long b=1;b<=bc;++b)	{
						long c=bc/b;
						if (b*c!=bc) continue;
						// VALID MATRIX!
						if (2*s+x+y<N)	{
							++count;
							System.out.println(String.format("[%d %d]",s+x,b*l));
							System.out.println(String.format("[%d %d]",c*l,s+y));
							System.out.println(String.format("t=%d, t+2s=%d, t+4s=%d, s=%d, x=%d, y=%d, l=%d, num=%d, b=%d, g=%d, k=%d, qA1=%d, qA2=%d, qD1=%d, qD2=%d.",t,t+2*s,tx,s,x,y,l,num,b,c,(x*y)/t,(s+x)/t2,x/t1,(s+y)/t2,y/t1));
							System.out.println();
						}
					}
				}
			}
		}
		System.out.println(count);
	}
}
