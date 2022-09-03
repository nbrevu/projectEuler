package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.ExtendedGcdLongInfo;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;

public class Euler758_3 {
	private final static long MOD=1_000_000_007l;
	
	private static class ModularCalculator	{
		private final long mod;
		private final LongLongMap powersOfTwo;
		private final LongLongMap inverses;
		public ModularCalculator(long mod)	{
			this.mod=mod;
			powersOfTwo=HashLongLongMaps.newMutableMap();
			inverses=HashLongLongMaps.newMutableMap();
		}
		private long doCalculatePower(long exp)	{
			return EulerUtils.expMod(2l,exp,mod);
		}
		public long getPowerOfTwo(long exp)	{
			return powersOfTwo.computeIfAbsent(exp,this::doCalculatePower);
		}
		private long doCalculateInverse(long n)	{
			return EulerUtils.modulusInverse(n,mod);
		}
		public long getInverse(long n)	{
			return inverses.computeIfAbsent(n,this::doCalculateInverse);
		}
		private long makePositive(long n)	{
			return ((n%mod)+mod)%mod;
		}
		public void specialEuclidAlgorithm(long a,long b,long[] result)	{
			// Stores in "result" a pair of numbers (x,y) such that x*(2^a-1)+y*(2^b-1).
			// Assumes that a and b are coprime (ends when r=1). Undefined results (probably an infinite loop) happen otherwise.
			long oldR=a;
			long r=b;
			long oldS=1l;
			long s=0l;
			long oldT=0l;
			long t=1l;
			for (;;)	{
				long q=oldR/r;
				long newR=oldR%r;
				long qq=getPowerOfTwo(newR);
				if (q>1)	{
					long num=getPowerOfTwo(q*r)-1;
					long den=getPowerOfTwo(r)-1;
					long factor=num*getInverse(den)%mod;
					qq=(qq*factor)%mod;
				}
				oldR=r;
				r=newR;
				long newS=makePositive(oldS-qq*s);
				oldS=s;
				s=newS;
				long newT=makePositive(oldT-qq*t);
				oldT=t;
				t=newT;	//'s eye. Ha. Ha. Ha.
				if (newR==1l)	{
					result[0]=s;
					result[1]=t;
					return;
				}
			}
		}
	}
	
	public static void main(String[] args)	{
		ModularCalculator calculator=new ModularCalculator(MOD);
		long[] tmpArray=new long[2];
		for (int i=3;i<50;++i) for (int j=2;j<i;++j) if (EulerUtils.areCoprime(i,j))	{
			long a=(1l<<i)-1;
			long b=(1l<<j)-1;
			ExtendedGcdLongInfo info=EulerUtils.extendedGcd(a,b);
			calculator.specialEuclidAlgorithm(i,j,tmpArray);
			long diff1=(info.coeffs[0]-tmpArray[0])%MOD;
			long diff2=(info.coeffs[1]-tmpArray[1])%MOD;
			if ((diff1!=0)||(diff2!=0)) throw new RuntimeException("Scheiße...");
			else System.out.println("Chachiguay :D.");
		}
	}
}
