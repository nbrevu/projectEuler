package com.euler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Euler253 {
	private final static int N=40;
	
	private static interface ArrayFactory<T>	{
		T[] createArray(int length);
	}
	
	private static enum BigIntegerArrayFactory implements ArrayFactory<BigInteger>	{
		INSTANCE;
		
		@Override
		public BigInteger[] createArray(int length)	{
			BigInteger[] result=new BigInteger[length];
			for (int i=0;i<length;++i) result[i]=BigInteger.ZERO;
			return result;
		}
	}
	
	private static enum IntegerArrayFactory implements ArrayFactory<Integer>	{
		INSTANCE;
	
		@Override
		public Integer[] createArray(int length)	{
			Integer[] result=new Integer[length];
			for (int i=0;i<length;++i) result[i]=Integer.valueOf(0);
			return result;
		}
	}
	
	private static class NumArray<T extends Number>	{
		private final static int SIZE=1+N;
		
		private final T[] array;
		private final ArrayFactory<T> arrayFactory;
		
		public NumArray(T[] array,ArrayFactory<T> arrayFactory)	{
			this.array=arrayFactory.createArray(SIZE);
			this.arrayFactory=arrayFactory;
			System.arraycopy(array,0,this.array,0,SIZE);
		}
		
		public T get(int pos)	{
			return array[pos];
		}
		
		public T[] getAsArray()	{
			T[] result=arrayFactory.createArray(SIZE);
			System.arraycopy(array,0,result,0,SIZE);
			return result;
		}
		
		@Override
		public int hashCode()	{
			return Arrays.hashCode(array);
		}
		
		@Override
		public boolean equals(Object other)	{
			try	{
				@SuppressWarnings("unchecked")
				NumArray<T> o=(NumArray<T>)other;
				return Arrays.equals(array,o.array);
			}	catch (ClassCastException cce)	{
				return false;
			}
		}
	}
	
	private static int sum(NumArray<Integer> in)	{
		int result=0;
		for (int i=1;i<1+N;++i) result+=in.get(i).intValue();
		return result;
	}
	
	private static BigInteger bigSum(NumArray<BigInteger> in)	{
		BigInteger result=BigInteger.ZERO;
		for (int i=1;i<1+N;++i) result=result.add(in.get(i));
		return result;
	}
	
	private static BigInteger getWeightedSum(NumArray<BigInteger> in)	{
		BigInteger result=BigInteger.ZERO;
		for (int i=1;i<1+N;++i) result=result.add(in.get(i).multiply(BigInteger.valueOf(i)));
		return result;
	}
	
	private static NumArray<BigInteger> BASE_ARRAY;
	
	private static NumArray<BigInteger> getBaseArray()	{
		if (BASE_ARRAY==null)	{
			BigInteger[] array=new BigInteger[1+N];
			for (int i=0;i<=N;++i) array[i]=BigInteger.ZERO;
			array[1]=BigInteger.ONE;
			BASE_ARRAY=new NumArray<BigInteger>(array,BigIntegerArrayFactory.INSTANCE);
		}
		return BASE_ARRAY;
	}
	
	private final static Map<NumArray<Integer>,NumArray<BigInteger>> arrayCache=new HashMap<>();
	
	public static NumArray<BigInteger> getCombinations(NumArray<Integer> array)	{
		int nbSegs=sum(array);
		if ((nbSegs==1)&&(array.get(1)==1)) return getBaseArray();
		NumArray<BigInteger> result=arrayCache.get(array);
		if (result!=null) return result;
		Integer[] sizes=array.getAsArray();
		BigInteger[] res=BigIntegerArrayFactory.INSTANCE.createArray(1+N);
		for (int size=1;size<sizes.length;++size)	{
			int nb=sizes[size];
			if (nb==0) continue;
			--sizes[size];
			if (size==0)	{
				NumArray<BigInteger> tmp=getCombinations(new NumArray<Integer>(sizes,IntegerArrayFactory.INSTANCE));
				BigInteger[] array2=tmp.getAsArray();
				for (int s=1;s<array2.length;++s)	{
					BigInteger nbCombs=array2[s].multiply(BigInteger.valueOf(nb));
					if (s<nbSegs) res[nbSegs]=res[nbSegs].add(nbCombs);
					else res[s]=res[s].add(nbCombs);
				}
			}	else	{
				for (int size1=0;size1<size;++size1)	{
					int size2=size-size1-1;
					if (size1!=0) ++sizes[size1];
					if (size2!=0) ++sizes[size2];
					NumArray<BigInteger> tmp=getCombinations(new NumArray<>(sizes,IntegerArrayFactory.INSTANCE));
					BigInteger[] array2=tmp.getAsArray();
					for (int s=1;s<array2.length;++s)	{
						BigInteger nbCombs=array2[s].multiply(BigInteger.valueOf(nb));
						if (s<nbSegs) res[nbSegs]=res[nbSegs].add(nbCombs);
						else res[s]=res[s].add(nbCombs);
					}
					if (size1!=0) --sizes[size1];
					if (size2!=0) --sizes[size2];
				}
			}
			++sizes[size];
		}
		NumArray<BigInteger> resultArray=new NumArray<>(res,BigIntegerArrayFactory.INSTANCE);
		arrayCache.put(array,resultArray);
		return resultArray;
	}
	
	private static BigDecimal getResultFromCombinations(NumArray<BigInteger> combinations)	{
		BigDecimal sum=new BigDecimal(bigSum(combinations));
		BigDecimal weightedSum=new BigDecimal(getWeightedSum(combinations));
		MathContext context=new MathContext(10);
		return weightedSum.divide(sum, context);
	}
	
	public static void main(String[] args)	{
		Integer[] input=new Integer[1+N];
		for (int i=0;i<N;++i) input[i]=0;
		input[N]=1;
		NumArray<BigInteger> combs=getCombinations(new NumArray<>(input,IntegerArrayFactory.INSTANCE));
		System.out.println(getResultFromCombinations(combs).toString());
	}
}
