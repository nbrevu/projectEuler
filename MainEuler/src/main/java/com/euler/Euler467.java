package com.euler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.euler.common.Primes;

public class Euler467 {
	private final static int SIZE=10000;
	private final static long MOD=1000000007;
	
	private static short[] buildDigitalRoots(boolean[] composites,boolean valueToMatch,int length)	{
		short[] result=new short[length];
		int currentPos=0;
		for (int i=2;currentPos<length;++i) if (composites[i]==valueToMatch)	{
			short digit=(short)(i%9);
			result[currentPos]=(digit==0)?9:digit;
			++currentPos;
		}
		return result;
	}
	
	private static class Builder	{
		private enum NextDigitSource	{
			PRIME,COMPOSITE,UNDEFINED
		}
		private static Comparator<List<Short>> LIST_COMPARATOR=new Comparator<List<Short>>()	{
			@Override
			public int compare(List<Short> o1, List<Short> o2) {
				int sizeDiff=o1.size()-o2.size();
				if (sizeDiff!=0) return sizeDiff;
				for (int i=0;i<o1.size();++i)	{
					int numDiff=o1.get(i)-o2.get(i);
					if (numDiff!=0) return numDiff;
				}
				return 0;
			}
		};
		private final short[] primeDRs;
		private final short[] compositeDRs;
		private int indexP;
		private int indexC;
		private List<Short> currentStatus;
		private NextDigitSource nextSource;
		public Builder(short[] primeDRs,short[] compositeDRs)	{
			this.primeDRs=primeDRs;
			this.compositeDRs=compositeDRs;
			indexP=0;
			indexC=0;
			currentStatus=new ArrayList<>();
			nextSource=NextDigitSource.UNDEFINED;
		}
		private Builder(short[] primeDRs,short[] compositeDRs,int indexP,int indexC,List<Short> currentStatus,NextDigitSource nextSource)	{
			this.primeDRs=primeDRs;
			this.compositeDRs=compositeDRs;
			this.indexP=indexP;
			this.indexC=indexC;
			this.currentStatus=currentStatus;
			this.nextSource=nextSource;
		}
		public List<Short> getBestResult()	{
			for (;;)	{
				if (indexP>=primeDRs.length)	{
					for (;indexC<compositeDRs.length;++indexC) currentStatus.add(compositeDRs[indexC]);
					return currentStatus;
				}	else if (indexC>=compositeDRs.length)	{
					for (;indexP<primeDRs.length;++indexP) currentStatus.add(primeDRs[indexP]);
					return currentStatus;
				}	else if (compositeDRs[indexC]==primeDRs[indexP])	{
					currentStatus.add(primeDRs[indexP]);
					++indexC;
					++indexP;
					nextSource=NextDigitSource.UNDEFINED;
					continue;
				}
				switch (nextSource)	{
					case PRIME:	{
						while ((indexP<primeDRs.length)&&(compositeDRs[indexC]!=primeDRs[indexP]))	{
							currentStatus.add(primeDRs[indexP]);
							++indexP;
						}
						break;
					}
					case COMPOSITE:	{
						while ((indexC<compositeDRs.length)&&(compositeDRs[indexC]!=primeDRs[indexP]))	{
							currentStatus.add(compositeDRs[indexC]);
							++indexC;
						}
						break;
					}
					case UNDEFINED:	{
						List<Short> cloneList=new ArrayList<>(currentStatus);
						Builder childP=new Builder(primeDRs,compositeDRs,indexP,indexC,currentStatus,NextDigitSource.PRIME);
						Builder childC=new Builder(primeDRs,compositeDRs,indexP,indexC,cloneList,NextDigitSource.COMPOSITE);
						List<Short> resultP=childP.getBestResult();
						List<Short> resultC=childC.getBestResult();
						return (LIST_COMPARATOR.compare(resultP,resultC)<=0)?resultP:resultC;
					}
					default:
						throw new AssertionError("Tengo que poner aquí algo porque Java es un poquito gilipollas.");
				}
			}
		}
	}
	
	private static long buildFromList(List<Short> digits,long mod)	{
		long result=0;
		for (short d:digits) result=(result*10+d)%mod;
		return result;
	}
	
	public static void main(String[] args)	{
		boolean[] composites=Primes.sieve(SIZE*11);
		short[] primeDRs=buildDigitalRoots(composites,false,SIZE);
		short[] compositeDRs=buildDigitalRoots(composites,true,SIZE);
		Builder builder=new Builder(primeDRs,compositeDRs);
		List<Short> bestMatch=builder.getBestResult();
		System.out.println(buildFromList(bestMatch,MOD));
	}
}
