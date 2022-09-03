package com.euler;

import com.google.common.math.LongMath;

public class Euler479 {
	// I love this!
	// 1/x = (k/x)^2·(k+x^2) - kx => Multiply by x^2.
	// x = k^2(k+x^2) - kx^3 => Reorder...
	// kx^3 - k^2x^2 + x - k^3 = 0.
	// So, if a, b, c are the roots, we have:
	// (a+b+c) = k^2 / k = k
	// ab+ac+bc = 1/k
	// abc = k^3/k = k^2
	// Now, we need to calculate (a+b)(a+c)(b+c). If we call S=a+b+c, this is (S-a)(S-b)(S-c).
	// We can expand this: S^3-(a+b+c)S^2+(ab+ac+bc)S-abc.
	// Obviously S^3-(a+b+c)S^2 = S^3-S·S^2=0.
	// Therefore (a+b)(a+c)(b+c) = (ab+ac+bc)(a+b+c)-abc = (1/k)·k - k^2 = 1-k^2.
	// So we're being asked the following:
	// sum( sum((1-k^2)^p,p,1,N), k,1,N).
	// sum([(1-k^2)^(N+1)-1]/(-k^2),k,1,N)
	// Can this be further simplified? Sure it can...
	
	private final static int N=4;
	
	public static void main(String[] args)	{
		long sum=0;
		for (long k=2;k<=N;++k)	{
			long k2=k*k;
			long num=1-k2-LongMath.pow(1-k2,N+1);
			System.out.println("k="+k+": "+(num/k2)+".");
			sum+=num/k2;
		}
		System.out.println(sum);
	}
}
