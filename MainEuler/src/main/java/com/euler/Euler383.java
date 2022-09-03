package com.euler;

import java.util.HashMap;
import java.util.Map;

import com.euler.common.EulerUtils;

public class Euler383 {
	private static long instancesOf5InFact(long in)	{
		long result=0;
		while (in>4)	{
			in/=5;
			result+=in;
		}
		return result;
	}
	
	private static int countByBruteForce(long limit)	{
		int counter=0;
		for (long i=1l;i<=limit;++i)	{
			long instancesI=instancesOf5InFact(i);
			long instances2I_1=instancesOf5InFact(2*i-1);
			if (instances2I_1<2*instancesI) ++counter; 
		}
		return counter;
	}
	
	private static class BoxIdentifier	{
		// Identifier. Has a boolean indicating whether duplicating the number results in a carry, an an integer with the difference 2f(i)-f(2i-1)
		public final boolean generatesCarry;
		public final int difference;
		public BoxIdentifier(boolean generatesCarry,int difference)	{
			this.generatesCarry=generatesCarry;
			this.difference=difference;
		}
		@Override
		public boolean equals(Object other)	{
			BoxIdentifier bOther=(BoxIdentifier)other;
			return (generatesCarry==bOther.generatesCarry)&&(difference==bOther.difference);
		}
		@Override
		public int hashCode()	{
			return (4*Boolean.hashCode(generatesCarry))+Integer.hashCode(difference);
		}
		@Override
		public String toString()	{
			return "{"+generatesCarry+","+difference+"}";
		}
	}
	
	// This can be done using a three dimensional recursion formula, but I'm not confident that I have the mathematical baggage to pull it.
	private static Map<BoxIdentifier,Long> iterate(Map<Integer,Map<BoxIdentifier,Long>> previous,int digits)	{
		Map<BoxIdentifier,Long> result=new HashMap<>();
		BoxIdentifier firstId=new BoxIdentifier(false,digits);
		result.put(firstId,2l);	// 100000... and 200000...
		BoxIdentifier secondId=new BoxIdentifier(true,digits-1);
		result.put(secondId,2l);
		for (Map.Entry<Integer,Map<BoxIdentifier,Long>> subMap:previous.entrySet()) for (Map.Entry<BoxIdentifier,Long> entry:subMap.getValue().entrySet())	{
			BoxIdentifier id=entry.getKey();
			long amount=entry.getValue();
			BoxIdentifier idFalse=new BoxIdentifier(false,id.difference);
			BoxIdentifier idTrue=(id.difference>1)?new BoxIdentifier(true,id.difference-1):null;
			if (id.generatesCarry&&(subMap.getKey()==(digits-1)))	{
				EulerUtils.increaseCounter(result,idFalse,amount);	// Putting an 1.
				if (idTrue!=null) EulerUtils.increaseCounter(result,idTrue,3*amount);	// Putting a 2, 3 or 4.
			}	else	{
				EulerUtils.increaseCounter(result,idFalse,2*amount);	// Putting an 1 or 2.
				if (idTrue!=null) EulerUtils.increaseCounter(result,idTrue,2*amount);	// Putting a 3 or 4.
			}
		}
		return result;
	}
	
	private static Map<Integer,Map<BoxIdentifier,Long>> getRecursiveMap(int digits)	{
		Map<Integer,Map<BoxIdentifier,Long>> result=new HashMap<>();
		// First we create the base results (n=1).
		Map<BoxIdentifier,Long> current=new HashMap<>();
		BoxIdentifier base=new BoxIdentifier(false,1);
		current.put(base,2l);	// 10 and 20 (in base 5, always!).
		result.put(1,current);
		for (int i=2;i<digits;++i)	{
			current=iterate(result,i);
			result.put(i,current);
		}
		return result;
	}
	
	private static long countWithRecursion(int digits)	{
		Map<Integer,Map<BoxIdentifier,Long>> map=getRecursiveMap(digits);
		long result=0;
		for (Map<BoxIdentifier,Long> subMap:map.values()) for (long value:subMap.values()) result+=value;
		return result;
	}
	
	public static void main(String[] args)	{
		long powOf5=5;
		for (int i=2;i<=10;++i)	{
			powOf5*=5;
			long result1=countByBruteForce(powOf5-1);
			long result2=countWithRecursion(i);
			if (result1!=result2)	{
				System.out.println("ES IST FURCHBAR!!!!! ODER?????");
				System.out.println("\ti="+i+".");
				System.out.println("\tbase="+powOf5+".");
				System.out.println("\tresult1="+result1+".");
				System.out.println("\tresult2="+result2+".");
			}	else	{
				System.out.println("Es moliert!!!!! Fürs i="+i+", die Lösung ist "+result1+"!!!!! Oder?????");
			}
		}
	}
}
