package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler241 {
	private final static double LIMIT_LOG=18.0*Math.log(10.0);
	private final static long SIGMA_LIMIT=100;
	private final static long SAFE_LIMIT=17000;
	
	private static int[] firstPrimes;
	
	private static void addFactor(NavigableMap<Long,Integer> factors,long factor)	{
		Integer power=factors.get(factor);
		int newPower=1+((power==null)?0:power.intValue());
		factors.put(factor,newPower);
	}
	
	private static NavigableMap<Long,Integer> getFactors(int num)	{
		NavigableMap<Long,Integer> factors=new TreeMap<>();
		for (;;)	{
			long prime=firstPrimes[num];
			if (prime==0)	{
				addFactor(factors,num);
				return factors;
			}
			addFactor(factors,prime);
			num/=prime;
		}
	}
	
	private static class PrimePower	{
		public final long base;
		public final int exponent;
		public final long power;
		public final long sigma;
		public final double powerLog;
		public final NavigableMap<Long,Integer> sigmaFactors;
		public PrimePower(long base,int exponent)	{
			this.base=base;
			this.exponent=exponent;
			{
				long prod=1l;
				long sum=1l;
				for (int i=1;i<=exponent;++i)	{
					prod*=base;
					sum+=prod;
				}
				power=prod;
				sigma=sum;
			}
			powerLog=Math.log(power);
			sigmaFactors=(isAcceptable()||(base==2))?getFactors((int)sigma):null;
		}
		public boolean isAcceptable()	{
			return sigma<SAFE_LIMIT;
		}
	}
	
	private static void addPower(Map<Long,List<PrimePower>> powers,long factor,PrimePower pow)	{
		List<PrimePower> thisFactor=powers.get(factor);
		if (thisFactor==null)	{
			thisFactor=new ArrayList<>();
			powers.put(factor,thisFactor);
		}
		thisFactor.add(pow);
	}
	
	private static Map<Long,List<PrimePower>> getSigmasSortedByDivisors()	{
		Map<Long,List<PrimePower>> result=new HashMap<>();
		for (int i=3;i<SIGMA_LIMIT;i+=2) if (firstPrimes[i]==0) for (int j=1;;++j)	{
			PrimePower pow=new PrimePower(i,j);
			if (pow.isAcceptable()) for (long l:pow.sigmaFactors.keySet()) addPower(result,l,pow);
			else break;
		}
		return result;
	}
	
	private static class NumBuilder	{
		private final List<PrimePower> presentPowers;
		private final NavigableMap<Long,Integer> numeratorFactors;
		private final NavigableMap<Long,Integer> denominatorFactors;
		public NumBuilder(PrimePower initial)	{
			presentPowers=Arrays.asList(initial);
			numeratorFactors=initial.sigmaFactors;
			denominatorFactors=new TreeMap<>();
			denominatorFactors.put(initial.base,initial.exponent);
		}
		public NumBuilder(NumBuilder base,PrimePower additional)	{
			presentPowers=new ArrayList<>(base.presentPowers);
			presentPowers.add(additional);
			numeratorFactors=new TreeMap<>(base.numeratorFactors);
			denominatorFactors=new TreeMap<>(base.denominatorFactors);
			for (Map.Entry<Long,Integer> entry:additional.sigmaFactors.entrySet()) addToMap(numeratorFactors,entry.getKey(),entry.getValue());
			addToMap(denominatorFactors,additional.base,additional.exponent);
			cancelOut(numeratorFactors,denominatorFactors);
		}
		private static void addToMap(NavigableMap<Long,Integer> factors,long base,int exponent)	{
			Integer current=factors.get(base);
			int newValue=exponent+((current==null)?0:current.intValue());
			factors.put(base,newValue);
		}
		private static void removeFromMap(NavigableMap<Long,Integer> factors,long base,int exponent)	{
			int current=factors.get(base);
			if (current==exponent) factors.remove(base);
			else factors.put(base,current-exponent);
		}
		private static void cancelOut(NavigableMap<Long,Integer> a,NavigableMap<Long,Integer> b)	{
			Map<Long,Integer> toRemove=new TreeMap<>();
			for (Map.Entry<Long,Integer> entry:a.entrySet())	{
				long base=entry.getKey();
				if (b.containsKey(base))	{
					int common=Math.min(entry.getValue(),b.get(base));
					toRemove.put(base,common);
				}
			}
			for (Map.Entry<Long,Integer> entry:toRemove.entrySet())	{
				removeFromMap(a,entry.getKey(),entry.getValue());
				removeFromMap(b,entry.getKey(),entry.getValue());
			}
		}
		public boolean isFinished()	{
			if (denominatorFactors.size()!=1) return false;
			Map.Entry<Long,Integer> factor=denominatorFactors.firstEntry();
			return (factor.getKey()==2)&&(factor.getValue()==1);
		}
		public boolean canContinue(Map<Long, List<PrimePower>> allSigmas)	{
			if (denominatorFactors.size()<1) return false;
			if (denominatorFactors.get(2l)==null) return false;
			for (long l:denominatorFactors.keySet()) if (!allSigmas.containsKey(l)) return false;
			double fullLog=0.0;
			for (PrimePower p:presentPowers) fullLog+=p.powerLog;
			return fullLog<LIMIT_LOG;
		}
		public boolean shouldAdd(PrimePower pow)	{
			for (PrimePower present:presentPowers) if (present.base==pow.base) return false;
			return true;
		}
		public long getNumber()	{
			long result=1l;
			for (PrimePower p:presentPowers) result*=p.power;
			return result;
		}
		public long getNeededFactor()	{
			for (Map.Entry<Long,Integer> entry:denominatorFactors.entrySet()) if (entry.getKey()>2||entry.getValue()>1) return entry.getKey();
			throw new NoSuchElementException();
		}
	}
	
	// DEBUG!
	private static long addDivisor(long in,long p,Map<Long,Integer> divs)	{
		while ((in%p)==0)	{
			in/=p;
			EulerUtils.increaseCounter(divs,p);
		}
		return in;
	}
	
	private static Map<Long,Integer> getFactors(long in)	{
		Map<Long,Integer> result=new TreeMap<>();
		in=addDivisor(in,2l,result);
		in=addDivisor(in,3l,result);
		long p=5;
		boolean add4=false;
		while (in>1)	{
			in=addDivisor(in,p,result);
			p+=(add4?4:2);
			add4=!add4;
		}
		return result;
	}
	
	private static long getSigma(Map<Long,Integer> result)	{
		long prod=1l;
		for (Map.Entry<Long,Integer> entry:result.entrySet())	{
			long prime=entry.getKey();
			int power=entry.getValue();
			long highPow=prime;
			for (int i=0;i<power;++i) highPow*=prime;
			long factor=(highPow-1)/(prime-1);
			prod*=factor;
		}
		return prod;
	}
	
	private static void extendedLog(long n)	{
		Map<Long,Integer> factors=getFactors(n);
		long sigma=getSigma(factors);
		long div=sigma/n;
		System.out.print("\t"+n+"=");
		boolean first=true;
		for (Map.Entry<Long,Integer> entry:factors.entrySet())	{
			if (first) first=false;
			else System.out.print(" · ");
			System.out.print(entry.getKey());
			if (entry.getValue()>1) System.out.print("^"+entry.getValue());
		}
		System.out.println(". Sigma="+sigma+"; "+sigma+"/"+n+"="+div+".5.");
	}
	// DAS ENDE DES DEBUGS!
	
	private static void getAllNumbers(NumBuilder base,Set<Long> toAdd,Map<Long,List<PrimePower>> allSigmas)	{
		if (!base.canContinue(allSigmas)) return;
		if (base.isFinished())	{
			long res=base.getNumber();
			if (!toAdd.contains(res))	{
				System.out.println("Die kleine numerico! "+res);
				extendedLog(res);
				toAdd.add(base.getNumber());
			}
			return;
		}
		long factor=base.getNeededFactor();
		List<PrimePower> possible=allSigmas.get(factor);
		if (possible!=null) for (PrimePower p:possible) if (base.shouldAdd(p))	{
			NumBuilder derived=new NumBuilder(base,p);
			getAllNumbers(derived,toAdd,allSigmas);
		}
	}
	
	public static void main(String[] args)	{
		firstPrimes=Primes.firstPrimeSieve((int)SAFE_LIMIT);
		System.out.println("Tengo los primos...");
		Map<Long,List<PrimePower>> allSigmas=getSigmasSortedByDivisors();
		System.out.println("Tengo los sigmas...");
		Set<Long> found=new TreeSet<>();
		for (int i=1;i<=13;++i)	{
			PrimePower pow=new PrimePower(2,i);
			System.out.println("Das potencias des dos! "+pow.power);
			NumBuilder base=new NumBuilder(pow);
			getAllNumbers(base,found,allSigmas);
		}
		long sum=0l;
		for (long l:found)	{
			sum+=l;
		}
		System.out.println(sum);
	}
}
