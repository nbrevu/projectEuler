package com.euler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.Pair;
import com.google.common.primitives.Ints;

public class Euler383_2 {
	private final static long LIMIT=1000000000000000000l;
	
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
		BoxIdentifier firstId=new BoxIdentifier(false,digits-1);
		result.put(firstId,2l);	// 100000... and 200000...
		BoxIdentifier secondId=new BoxIdentifier(true,digits-2);
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
		result.put(2,current);
		for (int i=3;i<=digits;++i)	{
			current=iterate(result,i);
			result.put(i,current);
		}
		return result;
	}
	
	private static class Addend	{
		// This class represents an addend with the form {prefix,amount of zeros in suffix}
		private final int[] prefixDigits;
		private final int zerosInSuffix;
		public Addend(int[] prefixDigits,int zerosInSuffix)	{
			this.prefixDigits=prefixDigits;
			this.zerosInSuffix=zerosInSuffix;
		}
		public long getSum(Map<Integer,Map<BoxIdentifier,Long>> map)	{
			int carryWithoutAddition=getAdditionalCarry(false);
			int carryWithAddition=getAdditionalCarry(true);
			long result=hasAdditionalAddend()?1:0;
			for (int i=2;i<=zerosInSuffix;++i) for (Map.Entry<BoxIdentifier,Long> entry:map.get(i).entrySet())	{
				BoxIdentifier id=entry.getKey();
				int additionalCarry=(id.generatesCarry&&(i==zerosInSuffix))?carryWithAddition:carryWithoutAddition;
				if (additionalCarry<id.difference) result+=entry.getValue();
			}
			return result;
		}
		private int getAdditionalCarry(boolean initialCarry)	{
			int counter=0;
			boolean currentCarry=initialCarry;
			for (int digit:prefixDigits) if ((digit>2)||(currentCarry&(digit==2)))	{
				++counter;
				currentCarry=true;
			}	else currentCarry=false;
			return counter;
		}
		public static List<Addend> generateAddends(int[] representation)	{
			List<Addend> result=new ArrayList<>();
			Pair<Integer,int[]> simplifiedRepresentation=stripLeadingZeros(representation);
			int zeros=simplifiedRepresentation.first;
			int[] prefix=simplifiedRepresentation.second;
			int N=prefix.length;
			for (int i=0;i<N;++i)	{
				int digit=prefix[i];
				int additionalDigits=N-1-i;
				int prefixLength=zeros+i;
				for (int j=0;j<digit;++j)	{
					// There is an edge case where the prefix is just [0]. This is not pathological and the algorithm works nicely.
					int[] tmpPrefix=new int[1+additionalDigits];
					tmpPrefix[0]=j;
					System.arraycopy(prefix,i+1,tmpPrefix,1,additionalDigits);
					result.add(new Addend(tmpPrefix,prefixLength));
				}
			}
			return result;
		}
		private static Pair<Integer,int[]> stripLeadingZeros(int[] representation)	{
			int zeros=0;
			while (representation[zeros]==0) ++zeros;
			int stripLength=representation.length-zeros;
			int[] stripped=new int[stripLength];
			System.arraycopy(representation,zeros,stripped,0,stripLength);
			return new Pair<>(zeros,stripped);
		}
		private boolean hasAdditionalAddend()	{
			// Returns true if the number represented (i.e., 4022000000000 or 3142034000000000000000000) should, itself, be added.
			int carrys=0;
			boolean isCarrying=false;
			for (int i=0;i<prefixDigits.length;++i)	{
				int digit=prefixDigits[i];
				if ((digit>2)||(isCarrying&&(i==digit)))	{
					++carrys;
					isCarrying=true;
				}
			}
			return zerosInSuffix>carrys;
		}
	}
	
	private static int[] getRepresentationInBase5(long in)	{
		List<Integer> result=new ArrayList<>();
		while (in>0)	{
			result.add((int)(in%5));
			in/=5;
		}
		return Ints.toArray(result);
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int[] representation=getRepresentationInBase5(LIMIT);
		long result=0;
		Map<Integer,Map<BoxIdentifier,Long>> counters=getRecursiveMap(representation.length);
		for (Addend addend:Addend.generateAddends(representation)) result+=addend.getSum(counters);
		long tac=System.nanoTime();
		double seconds=(tac-tic)/1e9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
