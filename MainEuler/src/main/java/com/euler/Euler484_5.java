package com.euler;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

public class Euler484_5 {
	private final static long LIMIT=5*LongMath.pow(10l,15);
	
	private final static int MAX_PRIMES_NEEDED=8;	// I believe 8 is actually enough, but the overhead is small, so let's be safe.
	
	private static class SequenceLists	{
		private final long limit;
		public final long[] toAdd;
		public final long[] toSubtract;
		public int toAddSize;
		public int toSubtractSize;
		public SequenceLists(int maxSize,long limit)	{
			this.limit=limit;
			toAdd=new long[maxSize];
			toSubtract=new long[maxSize];
			toAddSize=0;
			toSubtractSize=0;
		}
		public long countElements()	{
			long result=0;
			for (int i=0;i<toAddSize;++i) result+=limit/toAdd[i];
			for (int i=0;i<toSubtractSize;++i) result-=limit/toSubtract[i];
			return result;
		}
		public void initWith(SequenceLists previous,long addMultiplier)	{
			long maxToAdd=limit/addMultiplier;
			toAddSize=0;
			toSubtractSize=0;
			for (int i=0;i<previous.toAddSize;++i) if (previous.toAdd[i]<=maxToAdd)	{
				toAdd[toAddSize]=previous.toAdd[i]*addMultiplier;
				++toAddSize;
			}
			for (int i=0;i<previous.toSubtractSize;++i) if (previous.toSubtract[i]<=maxToAdd)	{
				toSubtract[toSubtractSize]=previous.toSubtract[i]*addMultiplier;
				++toSubtractSize;
			}
		}
		public void initWith(SequenceLists previous,long addMultiplier,long subtractMultiplier)	{
			long maxToAdd=limit/addMultiplier;
			toAddSize=0;
			toSubtractSize=0;
			for (int i=0;i<previous.toAddSize;++i) if (previous.toAdd[i]<=maxToAdd)	{
				toAdd[toAddSize]=previous.toAdd[i]*addMultiplier;
				++toAddSize;
			}
			for (int i=0;i<previous.toSubtractSize;++i) if (previous.toSubtract[i]<=maxToAdd)	{
				toSubtract[toSubtractSize]=previous.toSubtract[i]*addMultiplier;
				++toSubtractSize;
			}
			long maxToSubtract=limit/subtractMultiplier;
			for (int i=0;i<previous.toAddSize;++i) if (previous.toAdd[i]<=maxToSubtract)	{
				toSubtract[toSubtractSize]=previous.toAdd[i]*subtractMultiplier;
				++toSubtractSize;
			}
			for (int i=0;i<previous.toSubtractSize;++i) if (previous.toSubtract[i]<=maxToSubtract)	{
				toAdd[toAddSize]=previous.toSubtract[i]*subtractMultiplier;
				++toAddSize;
			}
		}
		public void setEmpty()	{
			toAdd[0]=1l;
			toAddSize=1;
			toSubtractSize=0;
		}
	}
	
	private static class PrimePowerData	{
		private final long power;
		private final long nextPower;
		private final long funValue;
		public PrimePowerData(long power,long nextPower,long funValue)	{
			this.power=power;
			this.nextPower=nextPower;
			this.funValue=funValue;
		}
	}
	
	private static class FullNumberData	{
		private final PrimePowerData[] members;
		private long alreadyCounted;
		public FullNumberData(PrimePowerData[] members)	{
			this.members=members;
			alreadyCounted=0;
		}
		public FullNumberData(FullNumberData parent,PrimePowerData additionalValue)	{
			this(append(parent.members,additionalValue));
		}
		private static PrimePowerData[] append(PrimePowerData[] array,PrimePowerData additionalValue)	{
			PrimePowerData[] result=Arrays.copyOf(array,array.length+1);
			result[array.length]=additionalValue;
			return result;
		}
	}
	
	private static class InitialPrimeData	{
		public final PrimePowerData[] powers;
		public InitialPrimeData(PrimePowerData[] powers)	{
			this.powers=powers;
		}
	}
	
	private static class PrimeDataGenerator	{
		private final long limit;
		private final long[] holder;
		public final InitialPrimeData[] data;
		public PrimeDataGenerator(long limit)	{
			this.limit=limit;
			holder=new long[64];
			holder[0]=1l;
			List<Long> primes=Primes.listLongPrimes(LongMath.sqrt(limit,RoundingMode.DOWN));
			data=primes.stream().map(this::generateData).toArray(InitialPrimeData[]::new);
		}
		private InitialPrimeData generateData(long prime)	{
			long thisLimit=limit/prime;
			int maxIndex=2;
			holder[1]=prime;
			long lastPower=prime;
			do	{
				lastPower*=prime;
				holder[maxIndex]=lastPower;
				++maxIndex;
			}	while (lastPower<=thisLimit);
			PrimePowerData[] powers=new PrimePowerData[maxIndex-2];
			for (int i=2;i<maxIndex;++i)	{
				long funValue=((i%prime)==0)?holder[i]:holder[i-1];
				long subtractingSequence=(i>=(maxIndex-1))?-1:holder[i+1];
				powers[i-2]=new PrimePowerData(holder[i],subtractingSequence,funValue);
			}
			return new InitialPrimeData(powers);
		}
		public LongObjMap<FullNumberData> getFullPowers()	{
			LongObjMap<FullNumberData> result=HashLongObjMaps.newMutableMap(154000000);
			FullNumberData base=new FullNumberData(new PrimePowerData[0]);
			result.put(1l,base);
			getFullPowersRecursive(0,limit,1l,base,result);
			return result;
		}
		private void getFullPowersRecursive(int currentIndex,long currentLimit,long currentNumber,FullNumberData currentAccumulator,LongObjMap<FullNumberData> result)	{
			for (int i=currentIndex;i<data.length;++i)	{
				PrimePowerData[] primeData=data[i].powers;
				if (primeData[0].power>currentLimit) break;
				for (int j=0;j<primeData.length;++j)	{
					PrimePowerData toAdd=primeData[j];
					if (toAdd.power>currentLimit) break;
					FullNumberData nextAccumulator=new FullNumberData(currentAccumulator,toAdd);
					long nextNumber=currentNumber*toAdd.power;
					long nextLimit=currentLimit/toAdd.power;
					result.put(nextNumber,nextAccumulator);
					getFullPowersRecursive(1+i,nextLimit,nextNumber,nextAccumulator,result);
				}
			}
		}
	}
	
	private static class ArithmeticDerivativeSummator	{
		private final LongObjMap<FullNumberData> allNumbers;
		private final SequenceLists seqs1;
		private final SequenceLists seqs2;
		private final long[] affectedNumbers;
		private int affectedNumbersSize;
		public ArithmeticDerivativeSummator(long limit)	{
			PrimeDataGenerator generator=new PrimeDataGenerator(limit);
			allNumbers=generator.getFullPowers();
			int seqsSize=1<<MAX_PRIMES_NEEDED;
			seqs1=new SequenceLists(seqsSize,LIMIT);
			seqs2=new SequenceLists(seqsSize,LIMIT);
			affectedNumbers=new long[seqsSize];
		}
		private SequenceLists getCounters(FullNumberData number)	{
			SequenceLists a=seqs1;
			SequenceLists b=seqs2;
			a.setEmpty();
			for (PrimePowerData element:number.members)	{
				if (element.nextPower>0) b.initWith(a,element.power,element.nextPower);
				else b.initWith(a,element.power);
				SequenceLists swap=a;
				a=b;
				b=swap;
			}
			return a;
		}
		private void fillAffectedNumbers(FullNumberData number)	{
			affectedNumbers[0]=1l;
			affectedNumbersSize=1;
			for (PrimePowerData element:number.members)	{
				for (int i=0;i<affectedNumbersSize;++i) affectedNumbers[i+affectedNumbersSize]=element.power*affectedNumbers[i];
				affectedNumbersSize+=affectedNumbersSize;
			}
		}
		private long getFunValue(FullNumberData number)	{
			long result=1l;
			for (PrimePowerData element:number.members) result*=element.funValue;
			return result;
		}
		public long sum()	{
			long result=0l;
			long[] array=allNumbers.keySet().toLongArray();
			Arrays.sort(array);
			for (int j=array.length-1;j>=0;--j)	{
				FullNumberData data=allNumbers.get(array[j]);
				long value=getFunValue(data);
				SequenceLists counters=getCounters(data);
				long totalElements=counters.countElements();
				long exactElements=totalElements-data.alreadyCounted;
				result+=value*exactElements;
				fillAffectedNumbers(data);
				for (int i=0;i<affectedNumbersSize;++i)	{
					long affected=affectedNumbers[i];
					allNumbers.get(affected).alreadyCounted+=exactElements;
				}
			}
			// Why -1? Easy! We need to subtract f(1), which is counted in the scheme above.
			return result-1;
		}
	}
	
	/*
	8907904768686152599
	Elapsed 278.1782489 seconds.
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		ArithmeticDerivativeSummator calculator=new ArithmeticDerivativeSummator(LIMIT);
		long result=calculator.sum();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
