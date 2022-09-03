package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Stack;

import com.euler.common.EulerUtils.Pair;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

public class Euler463_2 {
	private final static long N=LongMath.pow(3l,37);
	private final static BigInteger MOD=BigInteger.valueOf(LongMath.pow(10l,9));
	
	private final static BigInteger THREE=BigInteger.valueOf(3l);
	private final static BigInteger FIVE=BigInteger.valueOf(5l);

	private static class SingleValueCache	{
		private final LongObjMap<BigInteger> cache;
		public SingleValueCache()	{
			cache=HashLongObjMaps.newMutableMap();
			cache.put(1l,BigInteger.ONE);
			cache.put(3l,THREE);
		}
		public BigInteger getValue(long n)	{
			BigInteger result=cache.get(n);
			if (result==null)	{
				result=calculateValue(n);
				cache.put(n,result);
			}
			return result;
		}
		private BigInteger calculateValue(long in)	{
			int mod=(int)(in%4);
			switch (mod)	{
			case 0:	// Fall-through.
			case 2:
				return getValue(in/2);
			case 1:
				return getValue((in+1)/2).multiply(BigInteger.TWO).subtract(getValue((in-1)/4));
			case 3:
				return THREE.multiply(getValue((in-1)/2)).subtract(BigInteger.TWO.multiply(getValue((in-3)/4)));
			default:
				throw new ArithmeticException("Yeah, right.");
			}
		}
	}
	
	private static class AlgorithmState	{
		/*
		 * The bounds "totalSum" refers to are always [1..X] for some X.
		 * The bounds "currentEvenSums" and "currentOddSums" refer to are the same, [A..B].
		 * The exact values of X, A and B are known externally and there is no need to keep them in this object.
		 * And yes, this object is very much mutable.
		 */
		public BigInteger currentEvenSums;
		public BigInteger currentOddSums;
		public BigInteger totalSum;
		public AlgorithmState()	{
			currentEvenSums=BigInteger.ZERO;
			currentOddSums=BigInteger.ZERO;
			totalSum=BigInteger.ZERO;
		}
	}
	
	private static interface AlgorithmStateMutator	{
		public void updateState(AlgorithmState state,SingleValueCache singleValueCalculator);
	}
	
	private static class AddStandalone implements AlgorithmStateMutator	{
		private final long n0;
		private final long nf;
		public AddStandalone(long n0,long nf)	{
			this.n0=n0;
			this.nf=nf;
		}
		@Override
		public void updateState(AlgorithmState state,SingleValueCache singleValueCalculator)	{
			for (long n=n0;n<=nf;++n) state.totalSum=state.totalSum.add(singleValueCalculator.getValue(n));
		}
	}
	private static class AddToSums implements AlgorithmStateMutator	{
		private final long n0;
		private final long nf;
		public AddToSums(long n0,long nf)	{
			this.n0=n0;
			this.nf=nf;
		}
		@Override
		public void updateState(AlgorithmState state,SingleValueCache singleValueCalculator)	{
			for (long n=n0;n<=nf;++n)	{
				BigInteger toAdd=singleValueCalculator.getValue(n);
				if ((n%2)==1) state.currentOddSums=state.currentOddSums.add(toAdd);
				else state.currentEvenSums=state.currentEvenSums.add(toAdd);
			}
		}
	}
	private static enum StatelessMutator implements AlgorithmStateMutator	{
		ADD_CURRENT_INTERVAL	{
			@Override
			public void updateState(AlgorithmState state,SingleValueCache singleValueCalculator)	{
				state.totalSum=state.totalSum.add(state.currentEvenSums).add(state.currentOddSums);
			}
		},
		DUPLICATE_CURRENT_INTERVAL	{
			@Override
			public void updateState(AlgorithmState state,SingleValueCache singleValueCalculator)	{
				BigInteger newEven=state.currentEvenSums.add(state.currentOddSums);
				BigInteger newOdd=state.currentOddSums.multiply(FIVE).subtract(state.currentEvenSums.multiply(THREE));
				state.currentEvenSums=newEven;
				state.currentOddSums=newOdd;
			}
		}
	}
	
	private static class SequenceSumCalculator	{
		private final SingleValueCache singleValues;
		public SequenceSumCalculator()	{
			singleValues=new SingleValueCache();
		}
		public BigInteger sumSequence(long upTo)	{
			Pair<AlgorithmState,Stack<AlgorithmStateMutator>> summary=summarise(upTo);
			AlgorithmState state=summary.first;
			Stack<AlgorithmStateMutator> additionalSteps=summary.second;
			while (!additionalSteps.isEmpty()) additionalSteps.pop().updateState(state, singleValues);
			return state.totalSum;
		}
		private static long firstMultipleOf4AfterHalf(long n)	{
			long half=LongMath.divide(n,2l,RoundingMode.UP);
			long mod=half%4;
			return (mod==0)?half:(half+4-mod);
		}
		private AlgorithmState getInitialState(long sumUpTo,long intervalStart)	{
			AlgorithmState result=new AlgorithmState();
			for (long n=1;n<intervalStart;++n) result.totalSum=result.totalSum.add(singleValues.getValue(n));
			for (long n=intervalStart;n<=sumUpTo;++n)	{
				BigInteger toAdd=singleValues.getValue(n);
				result.totalSum=result.totalSum.add(toAdd);
				if ((n%2)==1) result.currentOddSums=result.currentOddSums.add(toAdd);
				else result.currentEvenSums=result.currentEvenSums.add(toAdd);
			}
			return result;
		}
		private Pair<AlgorithmState,Stack<AlgorithmStateMutator>> summarise(long n)	{
			long mod4=n%4;
			Stack<AlgorithmStateMutator> additionalSteps=new Stack<>();
			if (mod4!=3)	{
				additionalSteps.push(new AddStandalone(n-mod4,n));
				n-=(mod4+1);
			}
			long currentIntervalEnd=n;
			long currentIntervalStart=firstMultipleOf4AfterHalf(n);
			while (currentIntervalEnd>=15)	{
				additionalSteps.push(StatelessMutator.ADD_CURRENT_INTERVAL);
				additionalSteps.push(StatelessMutator.DUPLICATE_CURRENT_INTERVAL);
				long previousIntervalEnd=(currentIntervalEnd-1)/2;
				long previousIntervalStart=currentIntervalStart/2;
				/*-
				 * At this point the current interval is [A,B] and the "half" one is [A',B'] where 2*A' = A and 2*B'+1 = B.
				 * (For the record: A is currentIntervalStart, B is currentIntervalEnd, A' is previousIntervalStart and B' is previousIntervalEnd).
				 * A and A' are always even since A must be a multiple of 4.
				 * Plus, B and B' are always odd because B is of the form 4n+3.
				 * Three different issues might happen:
				 * First, we might need to close a gap between B' and A.
				 */
				if (currentIntervalStart-previousIntervalEnd>1) additionalSteps.push(new AddStandalone(previousIntervalEnd+1,currentIntervalStart-1));
				// Second, maybe B' is not of the form 4n+3. We would add two elements at the end.
				if ((previousIntervalEnd%4)!=3)	{	// Can only be 1 or 3, so if this is true, it's 1.
					additionalSteps.push(new AddToSums(previousIntervalEnd-1, previousIntervalEnd));
					additionalSteps.push(new AddStandalone(previousIntervalEnd-1, previousIntervalEnd));
					previousIntervalEnd-=2l;
				}
				// Third, maybe A' is not of the form 4n. We must add two elements at the start.
				if ((previousIntervalStart%4)!=0)	{	// Can only be 0 or 2, so if this is true, it's 2.
					additionalSteps.push(new AddToSums(previousIntervalStart,previousIntervalStart+1));
					previousIntervalStart+=2l;
				}
				currentIntervalEnd=previousIntervalEnd;
				currentIntervalStart=previousIntervalStart;
			}
			AlgorithmState state=getInitialState(currentIntervalEnd,currentIntervalStart);
			return new Pair<>(state,additionalSteps);
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		SequenceSumCalculator calculator=new SequenceSumCalculator();
		BigInteger bigResult=calculator.sumSequence(N);
		BigInteger result=bigResult.mod(MOD);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(bigResult);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}