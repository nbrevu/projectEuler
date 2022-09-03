package com.euler;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.euler.common.EulerUtils.FactorialCache;
import com.google.common.base.Objects;

public class Euler398_2 {
	// Experiment to verify combinations...
	private static interface Extension	{
		public int getRemainingToDistribute(int total);
		public int getRemainingGaps(int totalGaps);
	}
	private static class SingleExtension implements Extension	{
		public final int number;
		public final int times;
		public SingleExtension(int number,int times)	{
			this.number=number;
			this.times=times;
		}
		@Override
		public int getRemainingToDistribute(int total)	{
			return total-number*times;
		}
		@Override
		public int getRemainingGaps(int totalGaps)	{
			return totalGaps-times;
		}
		@Override
		public int hashCode()	{
			return Objects.hashCode(number,times);
		}
		@Override
		public boolean equals(Object other)	{
			if (other instanceof SingleExtension)	{
				SingleExtension seOther=(SingleExtension)other;
				return number==seOther.number&&times==seOther.times;
			}	else return false;
		}
		@Override
		public String toString()	{
			return "("+number+"x"+times+")";
		}
	}
	public static class DoubleExtension implements Extension	{
		public final int initialNumber;
		public final int otherNumber;
		public final int times;
		public DoubleExtension(int initialNumber,int otherNumber,int times)	{
			this.initialNumber=initialNumber;
			this.otherNumber=otherNumber;
			this.times=times;
		}
		@Override
		public int getRemainingToDistribute(int total)	{
			return total-initialNumber-otherNumber*times;
		}
		@Override
		public int getRemainingGaps(int totalGaps)	{
			return totalGaps-1-times;
		}
		@Override
		public int hashCode()	{
			return Objects.hashCode(initialNumber,otherNumber,times);
		}
		@Override
		public boolean equals(Object other)	{
			if (other instanceof DoubleExtension)	{
				DoubleExtension deOther=(DoubleExtension)other;
				return initialNumber==deOther.initialNumber&&otherNumber==deOther.otherNumber&&times==deOther.times;
			}	else return false;
		}
		@Override
		public String toString()	{
			return "("+initialNumber+"x1, "+otherNumber+"x"+times+")";
		}
	}
	private static class Combination	{
		private final static FactorialCache CACHE=new FactorialCache(5);
		private final int[] numbers;
		public Combination(int[] numbers)	{
			this.numbers=Arrays.copyOf(numbers,numbers.length);
		}
		public Extension getExtension()	{
			if (numbers[0]==numbers[1])	{
				int times=2;
				while (times<numbers.length&&numbers[times]==numbers[0]) ++times;
				return new SingleExtension(numbers[0],times);
			}	else	{
				int times=2;
				while (times<numbers.length&&numbers[times]==numbers[1]) ++times;
				return new DoubleExtension(numbers[0],numbers[1],times-1);
			}
		}
		public long getCombinations()	{
			long total=CACHE.get(numbers.length);
			int currentValue=numbers[0];
			int currentStartingIndex=0;
			for (int i=1;i<numbers.length;++i) if (numbers[i]!=currentValue)	{
				total/=CACHE.get(i-currentStartingIndex);
				currentValue=numbers[i];
				currentStartingIndex=i;
			}
			total/=CACHE.get(numbers.length-currentStartingIndex);
			return total;
		}
		@Override
		public String toString()	{
			return Arrays.toString(numbers);
		}
	}
	private static boolean fillRecursive(int[] array,int value,int fromPosition,List<Combination> results,int sumTo)	{
		int currentSum=0;
		for (int i=0;i<fromPosition;++i) currentSum+=array[i];
		for (int i=fromPosition;i<array.length-1;++i) array[i]=value;
		currentSum+=(array.length-1-fromPosition)*value;
		int lastValue=sumTo-currentSum;
		if (lastValue>=value)	{
			array[array.length-1]=lastValue;
			results.add(new Combination(array));
			for (int j=array.length-2;j>fromPosition;--j) for (int newValue=1+value;;++newValue) if (!fillRecursive(array,newValue,j,results,sumTo)) break;
			return true;
		}	else return false;
	}
		
	private static List<Combination> generateAllCombinations(int numbers,int sumTo)	{
		List<Combination> result=new LinkedList<>();
		int[] array=new int[numbers];
		for (int i=1;;++i) if (!fillRecursive(array,i,0,result,sumTo)) break;
		return result;
	}
	
	public static void main(String[] args)	{
		// ZUTUN! Seguir haciendo cálculos y comprobaciones para el tema de los factores...
		generateAllCombinations(5,12).stream().map(Combination::toString).forEach(System.out::println);
	}
}
