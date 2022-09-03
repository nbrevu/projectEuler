package com.euler;

import java.math.RoundingMode;
import java.util.Optional;

import com.google.common.math.LongMath;

public class Euler373 {
	// Radius of circumscribed circle: abc/sqrt[(a+b+c)(a+b-c)(b+c-a)(c+a-b)].
	private final static long LIMIT=100;
	
	private final static long SIDE_LIMIT=LIMIT+LIMIT;
	
	private static Optional<Long> getSqrt(long in)	{
		long sq=LongMath.sqrt(in,RoundingMode.DOWN);
		if ((sq*sq)==in) return Optional.of(sq);
		else return Optional.empty();
	}
	
	public static void main(String[] args)	{
		long sum=0;
		for (long a=1;a<=SIDE_LIMIT;++a) for (long b=a;b<=SIDE_LIMIT;++b)	{
			long maxC=Math.min(SIDE_LIMIT,a+b-1);
			for (long c=b;c<=maxC;++c)	{
				long p1=(a+b+c)*(a+b-c)*(b+c-a)*(c+a-b);
				Optional<Long> s=getSqrt(p1);
				if (s.isPresent())	{
					long p=a*b*c;
					if ((p%s.get())==0)	{
						long r=p/s.get();
						if (r>LIMIT) continue;
						System.out.println("Found triangle: "+a+", "+b+", "+c+" [R="+r+"].");
						sum+=r;
					}
				}
			}
		}
		System.out.println(sum);
	}
}
