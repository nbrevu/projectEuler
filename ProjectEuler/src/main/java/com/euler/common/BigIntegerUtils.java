package com.euler.common;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.valueOf;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BigIntegerUtils {
	public static int sumDigits(final BigInteger in)	{
		String str=in.toString();
		int res=0;
		for (char c:str.toCharArray())	{
			res+=c-'0';
		}
		return res;
	}
	
	public static BigInteger pow(BigInteger base,int exp)	{
		BigInteger result=base;
		for (int i=2;i<=exp;++i) result=result.multiply(base);
		return result;
	}
	
	public static BigInteger pow(int base,int exp)	{
		return pow(valueOf(base),exp);
	}

	public static int numDigits(BigInteger in)	{
		return in.toString().length();
	}
	
	public static BigInteger powMod(BigInteger base,int exp,BigInteger mod)	{
		BigInteger prod=ONE;
		BigInteger curProd=base;
		while (exp>0)	{
			if ((exp%2)>0) prod=(prod.multiply(curProd)).mod(mod);
			curProd=(curProd.multiply(curProd)).mod(mod);
			exp/=2;
		}
		return prod;
	}
	public static BigInteger[] factorials(int upTo)	{
		BigInteger[] facts=new BigInteger[1+upTo];
		facts[0]=ONE;
		for (int i=1;i<=upTo;++i) facts[i]=valueOf((long)i).multiply(facts[i-1]);
		return facts;
	}
	
	public static class BigFactorialCache	{
		private List<BigInteger> cache;
		public BigFactorialCache(int precalculation)	{
			cache=new ArrayList<>(1+precalculation);
			cache.add(BigInteger.ONE);
			addToCache(1,precalculation);
		}
		private void addToCache(int start,int end)	{
			BigInteger last=cache.get(cache.size()-1);
			for (int i=start;i<=end;++i)	{
				last=last.multiply(BigInteger.valueOf(i));
				cache.add(last);
			}
		}
		public BigInteger get(int index)	{
			if (index>=cache.size()) addToCache(cache.size(),index);
			return cache.get(index);
		}
	}
	
	public static class BigCombinatorialNumberCache	{
		private List<List<BigInteger>> cache;
		public BigCombinatorialNumberCache(int precalculation)	{
			cache=new ArrayList<>();
			cache.add(Arrays.asList(BigInteger.ONE));
			addToCache(1,precalculation);
		}
		private void addToCache(int start,int end)	{
			List<BigInteger> lastRow=cache.get(cache.size()-1);
			for (int i=start;i<=end;++i)	{
				List<BigInteger> nextRow=new ArrayList<>(lastRow.size()+1);
				nextRow.add(BigInteger.ONE);
				for (int j=1;j<i;++j) nextRow.add(lastRow.get(j).add(lastRow.get(j-1)));
				nextRow.add(BigInteger.ONE);
				cache.add(nextRow);
				lastRow=nextRow;
			}
		}
		public BigInteger get(int n,int k)	{
			if (k>n) return BigInteger.ZERO;
			if (n>=cache.size()) addToCache(cache.size(),n);
			return cache.get(n).get(k);
		}
	}
}
