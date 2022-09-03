package com.euler;

import java.math.RoundingMode;

import com.euler.common.EulerUtils;
import com.euler.common.SumOfTotientCalculator;
import com.google.common.math.LongMath;

public class Euler625_5 {
	private final static long MOD=998244353l;
	private final static long LIMIT=LongMath.pow(10l,11);
	private final static long SMALL_LIMIT=LongMath.sqrt(LIMIT,RoundingMode.DOWN);
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		SumOfTotientCalculator calc=SumOfTotientCalculator.getWithMod(MOD);
		long result=0l;
		for (int i=1;i<=SMALL_LIMIT;++i)	{
			long totientSum=calc.getTotientSum(LIMIT/i);
			result+=i*totientSum;
			result%=MOD;
		}
		long curLimInf=1+SMALL_LIMIT;
		long d=LIMIT/curLimInf;
		long curLimSup=LIMIT/d;
		long inverseOf2=EulerUtils.modulusInverse(2l,MOD);
		for (;;)	{
			long totientSum=calc.getTotientSum(d);
			long f1=(curLimInf+curLimSup)%MOD;
			long f2=(curLimSup+1-curLimInf)%MOD;
			long triangularSum=(((f1*f2)%MOD)*inverseOf2)%MOD;
			// long triangularSum=((curLimInf+curLimSup)*(curLimSup+1-curLimInf))/2;	<- Overflow.
			triangularSum%=MOD;
			result+=triangularSum*totientSum;
			result%=MOD;
			--d;
			if (d<=0) break;
			curLimInf=1+curLimSup;
			curLimSup=LIMIT/d;
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
