package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.LongMatrix;
import com.google.common.math.LongMath;

public class Euler624 {
	// Más info en http://goatleaps.xyz/euler/maths/Project-Euler-624.html.
	private final static long LIMIT=LongMath.pow(10l,18);
	private final static long MOD=1000000009l;
	
	private static long q(long a,long b,long mod)	{
		long inverse=EulerUtils.modulusInverse(b,mod);
		long result=(a*inverse)%mod;
		if (result<0) result+=mod;
		return result;
	}
	
	private static long det2(LongMatrix matrix,long mod)	{
		long result=matrix.get(0,0)*matrix.get(1,1)-matrix.get(1,0)*matrix.get(0,1);
		result%=mod;
		if (result<0) result+=mod;
		return result;
	}
	
	private static LongMatrix quasiInverse(LongMatrix in)	{
		LongMatrix result=new LongMatrix(2);
		result.assign(0,0,in.get(1,1));
		result.assign(0,1,-in.get(0,1));
		result.assign(1,0,-in.get(1,0));
		result.assign(1,1,in.get(0,0));
		return result;
	}
	
	private static LongMatrix pow2Minus(long exp,LongMatrix F,long mod)	{
		LongMatrix result=new LongMatrix(2);
		long pow=EulerUtils.expMod(2l,exp,mod);
		result.assign(0,0,pow-F.get(0,0));
		result.assign(1,0,-F.get(1,0));
		result.assign(0,1,-F.get(0,1));
		result.assign(1,1,pow-F.get(1,1));
		return result;
	}
	
	public static void main(String[] args)	{
		LongMatrix F=new LongMatrix(2);
		F.assign(0,0,1l);
		F.assign(0,1,1l);
		F.assign(1,0,1l);
		F.assign(1,1,0l);
		LongMatrix F2=F.pow(2l);
		LongMatrix F_N_2=F.pow(LIMIT-2,MOD);
		LongMatrix F_N=F_N_2.multiply(F2);
		LongMatrix M_1=pow2Minus(LIMIT,F_N,MOD);
		long det=det2(M_1,MOD);
		LongMatrix M=quasiInverse(M_1);
		long num=F_N_2.get(0,0)*M.get(0,0)+F_N_2.get(0,1)*M.get(1,0);
		num%=MOD;
		if (num<0) num+=MOD;
		long result=q(num,det,MOD);
		if (result<0) result+=MOD;
		System.out.println(result);
	}
}
