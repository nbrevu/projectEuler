package com.euler.common;

import com.euler.common.AlternatePrimeCounter.AlternatePrimeCounts;

public class AlternatePrimeCounter extends GenericLucyHedgehogSieve<AlternatePrimeCounts> {
	public static class AlternatePrimeCounts	{
		public final long primeCount;
		public final long sumFunction;
		public AlternatePrimeCounts(long primeCount,long sumFunction)	{
			this.primeCount=primeCount;
			this.sumFunction=sumFunction;
		}
		public long getSum4k1()	{
			return (primeCount+sumFunction-1)/2;
		}
		public long getSum4k3()	{
			return (primeCount-sumFunction-1)/2;
		}
	}
	
	private final static AlternatePrimeCounts F_NON_PRIME=new AlternatePrimeCounts(0,0);
	private final static AlternatePrimeCounts F2=new AlternatePrimeCounts(1,0);
	private final static AlternatePrimeCounts F4K1=new AlternatePrimeCounts(1,1);
	private final static AlternatePrimeCounts F4K3=new AlternatePrimeCounts(1,-1);

	public AlternatePrimeCounter(long upTo) {
		super(upTo);
	}

	@Override
	protected AlternatePrimeCounts f(long in) {
		if (in==1) return F_NON_PRIME;
		else if (in==2) return F2;
		int mod=(int)(in%4);
		switch (mod)	{
			case 1:return F4K1;
			case 3:return F4K3;
			default:return F_NON_PRIME;
		}
	}
	@Override
	protected AlternatePrimeCounts s(long in) {
		long fullCount=in-1;
		long count4k1=(in-1)/4;
		long count4k3=(in+1)/4;
		return new AlternatePrimeCounts(fullCount,count4k1-count4k3);
	}
	@Override
	protected AlternatePrimeCounts subtract(AlternatePrimeCounts min,AlternatePrimeCounts sub) {
		return new AlternatePrimeCounts(min.primeCount-sub.primeCount,min.sumFunction-sub.sumFunction);
	}
	@Override
	protected AlternatePrimeCounts multiply(AlternatePrimeCounts a,AlternatePrimeCounts b) {
		return new AlternatePrimeCounts(a.primeCount*b.primeCount,a.sumFunction*b.sumFunction);
	}
	@Override
	protected boolean containsPrime(AlternatePrimeCounts sXMinusOne,AlternatePrimeCounts sX) {
		return sXMinusOne.primeCount!=sX.primeCount;
	}
}
