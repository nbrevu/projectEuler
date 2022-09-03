package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.LongMatrix;
import com.koloboke.collect.map.IntIntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;

public class Euler440_4 {
	private final static int N=2000;
	private final static long MOD=987898789l;
	
	private static class PowerCounter	{
		public final int baseCounter;
		public final IntIntMap otherPowerCounter;
		private PowerCounter(int baseCounter,IntIntMap otherPowerCounter)	{
			this.baseCounter=baseCounter;
			this.otherPowerCounter=otherPowerCounter;
		}
		public static PowerCounter calculateFor(int maxPower)	{
			int baseCounter=0;
			IntIntMap otherPowerCounter=HashIntIntMaps.newMutableMap();
			for (int i=1;i<=maxPower;++i)	{
				EulerUtils.increaseCounter(otherPowerCounter,i);
				for (int j=1;j<i;++j)	{
					int gcd=EulerUtils.gcd(i,j);
					int a=i/gcd;
					int b=j/gcd;
					if (((a%2)==1)&&((b%2)==1))	{
						EulerUtils.increaseCounter(otherPowerCounter,gcd,2);
						continue;
					}
					baseCounter+=2;
				}
			}
			return new PowerCounter(baseCounter,otherPowerCounter);
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long t0=1;
		long t1=10;
		PowerCounter counters=PowerCounter.calculateFor(N);
		LongMatrix baseMatrix=new LongMatrix(2);
		baseMatrix.assign(0,0,10);
		baseMatrix.assign(0,1,1);
		baseMatrix.assign(1,0,1);
		baseMatrix.assign(1,1,0);
		// Initialised for the special case c=1: N^2 cases, all of them equal to t(1).
		long result=t1*N*N;
		for (int c=2;c<=N;++c)	{
			long[] matrixValues=new long[1+N];
			LongMatrix currentMatrix=baseMatrix;
			for (int i=1;i<=N;++i)	{
				currentMatrix=currentMatrix.pow(c,MOD);
				matrixValues[i]=currentMatrix.get(0,0);
			}
			result+=counters.baseCounter*(((c%2)==1)?t1:t0);
			for (IntIntCursor cursor=counters.otherPowerCounter.cursor();cursor.moveNext();) result+=(matrixValues[cursor.key()]*cursor.value());
			result%=MOD;
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);	// Ooooh, wrong result :(. I'm sure that I'm veeeeeeeeeery close.
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
