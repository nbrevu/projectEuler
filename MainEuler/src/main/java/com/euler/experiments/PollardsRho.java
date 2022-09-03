package com.euler.experiments;

import java.math.BigInteger;
import java.util.function.Function;

import com.euler.common.EulerUtils;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;

public class PollardsRho {
	private final PrimalityTest primeTester;
	public PollardsRho(int maxSmallPrime,int maxWitnesses)	{
		primeTester=new PrimalityTest(maxSmallPrime,maxWitnesses);
	}
	public PollardsRho()	{
		primeTester=new PrimalityTest();
	}
	public ObjIntMap<BigInteger> factor(BigInteger in)	{
		ObjIntMap<BigInteger> result=HashObjIntMaps.newMutableMap();
		for (;;)	{
			BigInteger[] division=in.divideAndRemainder(BigInteger.TWO);
			if (division[1].signum()==0)	{
				in=division[0];
				result.addValue(BigInteger.TWO,1);
			}	else break;
		}
		if (in.compareTo(BigInteger.ONE)>0) factorRecursive(in,result);
		return result;
	}
	private void factorRecursive(BigInteger n,ObjIntMap<BigInteger> result)	{
		if (primeTester.isPrime(n))	{
			result.addValue(n,1);
			return;
		}
		Function<BigInteger,BigInteger> g=(x)->x.multiply(x).add(BigInteger.ONE).mod(n);
		for (BigInteger x0=BigInteger.TWO;x0.compareTo(BigInteger.TEN)<=0;x0=x0.add(BigInteger.ONE))	{
			BigInteger factor=findFactor(n,x0,g);
			if (!factor.equals(n))	{
				factorRecursive(factor,result);
				factorRecursive(n.divide(factor),result);
				return;
			}
		}
		throw new IllegalStateException("I've tried, but I can't find a factor :'(.");
	}
	private static BigInteger findFactor(BigInteger n,BigInteger x0,Function<BigInteger,BigInteger> g)	{
		BigInteger x=x0;
		BigInteger y=x0;
		for (;;)	{
			x=g.apply(x);
			y=g.apply(g.apply(y));
			BigInteger d=EulerUtils.gcd(x.subtract(y).abs(),n);
			// NOTE: d might still be equal to n. This must be checked by caller!!
			if (!d.equals(BigInteger.ONE)) return d;
		}
	}
	
	/*
	BigInteger n1=new BigInteger("1238926361552897");
	BigInteger n2=new BigInteger("93461639715357977769163558199606896584051237541638188580280321");
	BigInteger n=n1.multiply(n2);
	{93461639715357977769163558199606896584051237541638188580280321=1, 1238926361552897=1}
	Elapsed 387.953786089 seconds.
	
	The stricter algorithm (with primality test and so on) takes 419.439154346, still very good.
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		PollardsRho objectToTest=new PollardsRho();
		// BigInteger n=BigInteger.valueOf(600851475143l);
		/*-
		BigInteger n1=new BigInteger("1238926361552897");
		BigInteger n2=new BigInteger("93461639715357977769163558199606896584051237541638188580280321");
		BigInteger n=n1.multiply(n2);
		*/
		BigInteger n=BigInteger.valueOf(3l*5*7*11*13*17*19*23*29*31*37*41*43);
		ObjIntMap<BigInteger> factors=objectToTest.factor(n);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(factors);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
