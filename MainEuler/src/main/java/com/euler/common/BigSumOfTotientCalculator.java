package com.euler.common;

import java.math.BigInteger;
import java.math.RoundingMode;

import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

public class BigSumOfTotientCalculator {
	// Slight variation from https://mathproblems123.wordpress.com/2018/05/10/sum-of-the-euler-totient-function/
	private final LongObjMap<BigInteger> cache;
	public BigSumOfTotientCalculator()	{
		cache=HashLongObjMaps.newMutableMap();
		cache.put(1l,BigInteger.ONE);
	}
	public final BigInteger getTotientSum(long in)	{
		return cache.computeIfAbsent(in,this::calculateTotientSum);
	}
	private BigInteger calculateTotientSum(long n)	{
		BigInteger bigN=BigInteger.valueOf(n);
		BigInteger result=bigN.multiply(bigN.add(BigInteger.ONE)).divide(BigInteger.TWO);
		long sn=LongMath.sqrt(n,RoundingMode.DOWN);
		for (long m=2;m<=sn;++m) result=result.subtract(getTotientSum(n/m));
		long curLimInf=1+sn;
		long d=n/curLimInf;
		long curLimSup=n/d;
		for (;;)	{
			long howMany=1+curLimSup-curLimInf;
			result=result.subtract(getTotientSum(d).multiply(BigInteger.valueOf(howMany)));
			--d;
			if (d<=0) break;
			curLimInf=1+curLimSup;
			curLimSup=n/d;
		}
		return result;
	}
}
