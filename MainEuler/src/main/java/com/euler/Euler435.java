package com.euler;

import java.math.BigInteger;

public class Euler435 {
	private final static long MOD=1307674368000l;
	private final static long N=1000000000000000l;
	private final static int SIZE=100;
	
	/*
	private static Iterator<Long> FIBONACCI_GENERATOR=new Iterator<Long>()	{
		private long prev2=1;
		private long prev=1;
		@Override
		public boolean hasNext() {
			return true;
		}
		@Override
		public Long next() {
			long curr=(prev+prev2)%MOD;
			prev2=prev;
			prev=curr;
			return curr;
		}
	};
	
	private static class PowerGenerator implements Iterator<Long>	{
		private long curr;
		private final long base;
		public PowerGenerator(long base)	{
			curr=1l;
			this.base=base;
		}

		@Override
		public boolean hasNext() {
			return true;
		}

		@Override
		public Long next() {
			long toReturn=curr;
			curr=(curr*base)%MOD;
			return toReturn;
		}
	};
	
	private static class Cycle	{
		private long[] initial;
		private long[] cycle;
		
		private Cycle(long[] initial,long[] cycle)	{
			this.initial=initial;
			this.cycle=cycle;
		}
		private static Cycle of(Iterator<Long> generator)	{
			Map<Long,Integer> positions=new HashMap<>();
			for (int index=0;;++index)	{
				long next=generator.next();
				if (positions.containsKey(next))	{
					int cycleStart=positions.get(next);
					int cycleLength=index-cycleStart;
					long[] start=new long[cycleStart];
					long[] cycle=new long[cycleLength];
					for (Map.Entry<Long,Integer> entry:positions.entrySet())	{
						long value=entry.getKey();
						int pos=entry.getValue();
						if (pos<cycleStart) start[pos]=value;
						else cycle[pos-cycleStart]=value;
					}
					return new Cycle(start,cycle);
				}
				positions.put(next,index);
			}
		}
		public int length()	{
			return cycle.length;
		}
		public int startupLength()	{
			return initial.length;
		}
	}
	
	public static void main(String[] args)	{
		Cycle fibo=Cycle.of(FIBONACCI_GENERATOR);
		System.out.println("Fibonacci: "+(2+fibo.startupLength())+"+["+fibo.length()+"]");
		for (int i=0;i<=SIZE;++i)	{
			Cycle powers=Cycle.of(new PowerGenerator(i));
			System.out.println("Ciclo para "+i+": "+powers.startupLength()+"+["+powers.length()+"]");
		}
	}
	*/
	
	private static BigInteger[][] product(BigInteger[][] matrixA,BigInteger[][] matrixB,BigInteger mod)	{
		BigInteger[][] result=new BigInteger[2][2];
		BigInteger a00=matrixA[0][0];
		BigInteger a01=matrixA[0][1];
		BigInteger a10=matrixA[1][0];
		BigInteger a11=matrixA[1][1];
		BigInteger b00=matrixB[0][0];
		BigInteger b01=matrixB[0][1];
		BigInteger b10=matrixB[1][0];
		BigInteger b11=matrixB[1][1];
		result[0][0]=(a00.multiply(b00)).add(a01.multiply(b10)).mod(mod);
		result[0][1]=(a00.multiply(b01)).add(a01.multiply(b11)).mod(mod);
		result[1][0]=(a10.multiply(b00)).add(a11.multiply(b10)).mod(mod);
		result[1][1]=(a10.multiply(b01)).add(a11.multiply(b11)).mod(mod);
		return result;
	}
	
	private static BigInteger[][] identity2()	{
		BigInteger[][] result=new BigInteger[2][2];
		result[0][0]=BigInteger.ONE;
		result[0][1]=BigInteger.ZERO;
		result[1][0]=BigInteger.ZERO;
		result[1][1]=BigInteger.ONE;
		return result;
	}
	
	private static BigInteger[][] fiboBase()	{
		BigInteger[][] result=new BigInteger[2][2];
		result[0][0]=BigInteger.ONE;
		result[0][1]=BigInteger.ONE;
		result[1][0]=BigInteger.ONE;
		result[1][1]=BigInteger.ZERO;
		return result;
	}
	
	private static BigInteger[][] power(BigInteger[][] matrix,long exp,BigInteger mod)	{
		BigInteger[][] result=identity2();
		BigInteger[][] tmpProduct=matrix;
		while (exp>0)	{
			if ((exp%2)==1l) result=product(result,tmpProduct,mod);
			tmpProduct=product(tmpProduct,tmpProduct,mod);
			exp/=2;
		}
		return result;
	}
	
	private static BigInteger power(BigInteger base,long exp,BigInteger mod)	{
		BigInteger result=BigInteger.ONE;
		BigInteger tmpProduct=base;
		while (exp>0)	{
			if ((exp%2)==1l) result=result.multiply(tmpProduct).mod(mod);
			tmpProduct=tmpProduct.multiply(tmpProduct).mod(mod);
			exp/=2;
		}
		return result;
	}
	
	private static long fiboPoly(long n,long x)	{
		long den=x*x+x-1;
		BigInteger bigMod=BigInteger.valueOf(den*MOD);
		BigInteger[][] fiboMatrix=power(fiboBase(),n,bigMod);
		BigInteger biX=BigInteger.valueOf(x);
		BigInteger x_n1=power(biX,n+1,bigMod);
		BigInteger x_n2=x_n1.multiply(biX).mod(bigMod);
		BigInteger biNum=fiboMatrix[0][1].multiply(x_n2).add(fiboMatrix[0][0].multiply(x_n1)).subtract(biX).mod(bigMod);
		long num=biNum.longValue();
		if ((num%den)!=0) System.out.println("Pues algo he hecho mal.");
		return num/den;
	}
	
	public static void main(String[] args)	{
		long result=0l;
		for (int i=1;i<=SIZE;++i) result=(result+fiboPoly(N,i))%MOD;
		System.out.println(result);
	}
}
