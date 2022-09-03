package com.euler;

import static com.euler.Euler362.FILE_PATH;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.euler.Euler362.Euler362State;
import com.euler.Euler362.FactorCollection;
import com.euler.common.EulerUtils.Pair;
import com.koloboke.collect.map.ObjLongCursor;
import com.koloboke.collect.map.ObjLongMap;
import com.koloboke.collect.map.hash.HashObjLongMaps;

public class Euler362_2	{
	private static class DecompositionSummary	{
		/*
		 * This array codifies the repetition of factors in a decomposition.
		 * The first element is the amount of numbers not repeated, the second is the amount of numbers repeated one time, etc.
		 * For example, for 54=2*3*3*3 we would have [1,0,1] since there is one element that appears once, none that appears twice, and one
		 * that appears twice. Or, for 54=3*3*6 we would have [1,1,0].
		 */
		private final int[] factorDecomposition;
		public DecompositionSummary(int[] factorDecomposition)	{
			this.factorDecomposition=factorDecomposition;
		}
		@Override
		public boolean equals(Object other)	{
			try	{
				DecompositionSummary dcOther=(DecompositionSummary)other;
				return Arrays.equals(factorDecomposition,dcOther.factorDecomposition);
			}	catch (ClassCastException exc)	{
				return false;
			}
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(factorDecomposition);
		}
		public ObjLongMap<DecompositionSummary> getChildren(int newFactorTimes)	{
			// TODO! ZUTUN! ZU VIELE SCHWARZE MAGICK!!!!! ODER?????
			return null;
		}
	}
	private static class DecompositionHolder	{
		private ObjLongMap<DecompositionSummary> decompositions;
		private DecompositionHolder(ObjLongMap<DecompositionSummary> decompositions)	{
			this.decompositions=decompositions;
		}
		public static DecompositionHolder getFromSingleFactorRepeated(int times)	{
			// There is only one possible decomposition: a*a*a*a...*a, n times.
			int[] singlePossibility=new int[1+times];
			singlePossibility[times]=0;
			DecompositionSummary summary=new DecompositionSummary(singlePossibility);
			return new DecompositionHolder(HashObjLongMaps.newImmutableMapOf(summary,1l));
		}
		public DecompositionHolder addNewFactorRepeated(int newFactorTimes)	{
			ObjLongMap<DecompositionSummary> result=HashObjLongMaps.newMutableMap();
			for (ObjLongCursor<DecompositionSummary> cursor=decompositions.cursor();cursor.moveNext();)	{
				long multiplier=cursor.value();
				DecompositionSummary previous=cursor.key();
				for (ObjLongCursor<DecompositionSummary> cursor2=previous.getChildren(newFactorTimes).cursor();cursor2.moveNext();)	{
					long value=cursor2.value()*multiplier;
					result.addValue(cursor2.key(),value);
				}
			}
			return new DecompositionHolder(result);
		}
		public long getAllDecompositions()	{
			long sum=0;
			for (long l:decompositions.values()) sum+=l;
			return sum;
		}
	}
	private static class CompleteDecompositionStorage	{
		private Map<FactorCollection,DecompositionHolder> data;
		public CompleteDecompositionStorage()	{
			data=new HashMap<>();
		}
		public DecompositionHolder getForFactorCollection(FactorCollection in)	{
			return data.computeIfAbsent(in,this::calculateForFactorCollection);
		}
		private DecompositionHolder calculateForFactorCollection(FactorCollection in)	{
			Pair<int[],Integer> parentInfo=getParent(in.exponents);
			int[] previous=parentInfo.first;
			int newFactorTimes=parentInfo.second;
			if (previous.length==0) return DecompositionHolder.getFromSingleFactorRepeated(newFactorTimes);
			else	{
				FactorCollection newFactorCollection=new FactorCollection(previous);
				DecompositionHolder parent=getForFactorCollection(newFactorCollection);
				return parent.addNewFactorRepeated(newFactorTimes);
			}
		}
		private static Pair<int[],Integer> getParent(int[] exponents)	{
			int N=exponents.length-1;
			int[] parentArray=new int[N];
			System.arraycopy(exponents,1,parentArray,0,N);
			// We use the first number as the "separate" one because the smaller this one is, the more tolerable the combinatorial explosion will be.
			return new Pair<>(parentArray,exponents[0]);
		}
	}
	
	public static void main(String[] args)	{
		Euler362State state=new Euler362State();
		try	{
			state.readStateFromFile(Files.newBufferedReader(FILE_PATH));
		}	catch (IOException exc)	{
			System.out.println("Pues va a tener que ser que no.");
			return;
		}
		//assert state.currentNumber==LongMath.pow(10l,10);
		{
			long mierdas=0;
			for (Long value:state.stateCount.values()) mierdas+=value;
			System.out.println("ACHTUNG! ICH HABE "+mierdas+" DINGE!!!!!");	// 10000000009. JAJA SI.
		}
		long sum=0;
		CompleteDecompositionStorage storage=new CompleteDecompositionStorage();
		for (ObjLongCursor<FactorCollection> cursor=state.stateCount.cursor();cursor.moveNext();) sum+=cursor.value()*storage.getForFactorCollection(cursor.key()).getAllDecompositions();
		System.out.println(sum);
	}
}