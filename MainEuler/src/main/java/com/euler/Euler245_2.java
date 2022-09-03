package com.euler;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.IntMath;

public class Euler245_2 {
	private final static int LIMIT=1000000000;
	
	private final static int[] firstPrimes=Primes.firstPrimeSieve(LIMIT);
	
	private static void addFactor(Map<Integer,Integer> divs,int prime)	{
		Integer val=divs.get(prime);
		int newVal=1+((val==null)?0:val.intValue());
		divs.put(prime,newVal);
	}
	
	private static Map<Integer,Integer> getDivisors(int n)	{
		Map<Integer,Integer> divs=new HashMap<>();
		for (;;)	{
			int prime=firstPrimes[n];
			if (prime==0)	{
				addFactor(divs,n);
				return divs;
			}
			addFactor(divs,prime);
			n/=prime;
		}
	}
	
	private static int getTotient(Map<Integer,Integer> divs)	{
		int result=1;
		for (Map.Entry<Integer,Integer> entry:divs.entrySet())	{
			int prime=entry.getKey();
			int power=entry.getValue();
			result*=(prime-1)*IntMath.pow(prime,power-1);
		}
		return result;
	}
	
	private static int getCoresilienceUnitFraction(int n,Map<Integer,Integer> divs)	{
		int totient=getTotient(divs);
		int num=n-totient;
		int den=n-1;
		return ((den%num)==0)?(den/num):-1; 
	}
	
	private static boolean isComposite(int n)	{
		return firstPrimes[n]!=0;
	}
	
	public static boolean hasRepeatedFactors(Map<Integer,Integer> divs)	{
		for (int pow:divs.values()) if (pow>1) return true;
		return false;
	}
	
	public static void main(String[] args)	{
		Map<Integer,Integer> countByFactorCount=new TreeMap<>();
		for (int i=4;i<=LIMIT;++i) if (isComposite(i))	{
			Map<Integer,Integer> divisors=getDivisors(i);
			int den=getCoresilienceUnitFraction(i,divisors);
			if (den!=-1)	{
				if (hasRepeatedFactors(divisors)||divisors.size()>2)	{
					System.out.println("DAS BIZARREN DING!!!!!");
					System.out.print(""+i+"=");
					boolean first=true;
					for (Map.Entry<Integer,Integer> entry:divisors.entrySet())	{
						if (first) first=false;
						else System.out.print('·');
						System.out.print(""+entry.getKey()+'^'+entry.getValue());
					}
					System.out.println(" => "+den);
				}	else EulerUtils.increaseCounter(countByFactorCount,divisors.size());
			}
		}
		System.out.println("En total he encontrado: ");
		for (Map.Entry<Integer,Integer> entry:countByFactorCount.entrySet()) System.out.println("Con "+entry.getKey()+" factores: "+entry.getValue()+" casos.");
	}
}
