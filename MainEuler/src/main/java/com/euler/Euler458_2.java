package com.euler;

import com.euler.common.LongMatrix;
import com.google.common.math.LongMath;

public class Euler458_2 {
	private final static long LIMIT=LongMath.pow(10l,12);
	private final static long MOD=LongMath.pow(10l,9);
	
	private static LongMatrix getMatrix()	{
		LongMatrix result=new LongMatrix(8);
		for (int i=1;i<=6;++i)	{
			result.assign(i,i-1,8-i);
			for (int j=i;j<=6;++j) result.assign(i,j,1l);
		}
		result.assign(7,6,1l);
		result.assign(7,7,7l);
		return result;
	}
	
	public static void main(String[] args)	{
		LongMatrix base=getMatrix();
		LongMatrix pow=base.pow(LIMIT,MOD);
		long minuend=pow.get(7,7);	// Which is (7^(10^12))%(10^9) = 1!
		long subtrahend=pow.get(7,0);
		long result=(minuend+MOD-subtrahend)%MOD;
		System.out.println(result);
	}
}
