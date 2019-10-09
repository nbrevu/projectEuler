package com.euler.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.math.LongMath;

public class PrimePowerCombination	{
	private final int[] powers;
	private PrimePowerCombination(int[] powers)	{
		this.powers=powers;
	}
	public PrimePowerCombination()	{
		powers=new int[] {1};
	}
	public List<PrimePowerCombination> getChildren()	{
		List<PrimePowerCombination> result=new ArrayList<>();
		int N=powers.length;
		if ((N==1)||(powers[N-1]<powers[N-2]))	{
			int[] child1=Arrays.copyOf(powers,N);
			++child1[N-1];
			result.add(new PrimePowerCombination(child1));
		}
		int[] child2=Arrays.copyOf(powers,N+1);
		child2[N]=1;
		result.add(new PrimePowerCombination(child2));
		return result;
	}
	@Override
	public int hashCode()	{
		return Arrays.hashCode(powers);
	}
	@Override
	public boolean equals(Object other)	{
		return (other instanceof PrimePowerCombination)&&Arrays.equals(powers,((PrimePowerCombination)other).powers);
	}
	public long getRepresentative(int[] primes)	{
		long result=1l;
		for (int i=0;i<powers.length;++i) result*=LongMath.pow(primes[i],powers[i]);
		return result;
	}
	public double getRepresentativeLogarithm(double[] primeLogs)	{
		double result=0l;
		for (int i=0;i<powers.length;++i) result+=powers[i]*primeLogs[i];
		return result;
	}
	public long howManyDivisors()	{
		long result=1;
		for (int p:powers) result*=p+1;
		return result;
	}
	public long howManyDivisorsInSquare()	{
		long result=1;
		for (int p:powers) result*=2*p+1;
		return result;
	}
}