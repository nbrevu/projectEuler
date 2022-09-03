package com.euler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.koloboke.collect.IntCursor;
import com.koloboke.collect.map.ObjIntCursor;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler701 {
	// Pues está mal, pero estoy convencido de que estoy muy cerca.
	private final static int W=2;
	private final static int H=2;
	
	private static class OpenConnection	{
		private final int totalCount;
		private final IntSet indices;
		public OpenConnection(int totalCount,IntSet indices)	{
			this.totalCount=totalCount;
			this.indices=indices;
		}
		public int getTotalCount()	{
			return totalCount;
		}
		public IntSet getIndices()	{
			return indices;
		}
		@Override
		public int hashCode()	{
			return Objects.hash(indices,totalCount);
		}
		@Override
		public boolean equals(Object other)	{
			OpenConnection ocOther=(OpenConnection)other;
			return (totalCount==ocOther.totalCount)&&indices.equals(ocOther.indices);
		}
	}
	
	private static class State	{
		private static class RowIdInfo	{
			public final List<IntSet> connections;
			public RowIdInfo(List<IntSet> connections)	{
				this.connections=connections.stream().map(HashIntSets::newImmutableSet).collect(Collectors.toUnmodifiableList());
			}
			public static RowIdInfo getFromId(int n)	{
				List<IntSet> connections=new ArrayList<>();
				IntSet currentIntSet=null;
				for (int i=0;i<W;++i,n>>=1) if ((n&1)==0) currentIntSet=null;
				else	{
					if (currentIntSet==null)	{
						currentIntSet=HashIntSets.newMutableSet();
						connections.add(currentIntSet);
					}
					currentIntSet.add(i);
				}
				return new RowIdInfo(connections);
			}
		}
		private final static RowIdInfo[] ID_CACHE=new RowIdInfo[1<<W];
		static	{
			int maxRowIndex=1<<W;
			for (int i=0;i<maxRowIndex;++i) ID_CACHE[i]=RowIdInfo.getFromId(i);
		}
		private final int maxConnected;
		private final Set<OpenConnection> openConnections;
		private final OpenConnection[] reverseConnectionMap;
		private class TwoDirectionConnections	{
			public final Multimap<OpenConnection,IntSet> existingToNew;
			public final Multimap<IntSet,OpenConnection> newToExisting;
			private TwoDirectionConnections(Multimap<OpenConnection,IntSet> existingToNew,Multimap<IntSet,OpenConnection> newToExisting)	{
				this.existingToNew=existingToNew;
				this.newToExisting=newToExisting;
			}
			private Set<OpenConnection> condense()	{
				Set<OpenConnection> result=new HashSet<>();
				Set<IntSet> alreadyIteratedOver=new HashSet<>();
				for (Map.Entry<IntSet,Collection<OpenConnection>> entry:newToExisting.asMap().entrySet()) if (!alreadyIteratedOver.contains(entry.getKey()))	{
					Set<OpenConnection> includedExisting=new HashSet<>();
					Set<IntSet> includedNew=new HashSet<>();
					includedNew.add(entry.getKey());
					createConnectionRecursive1(includedNew,includedExisting,entry.getValue());
					IntSet bigSet=HashIntSets.newMutableSet();
					for (IntSet included:includedNew) included.forEach((IntConsumer)bigSet::add);
					int count=bigSet.size();
					for (OpenConnection included:includedExisting) count+=included.getTotalCount();
					alreadyIteratedOver.addAll(includedNew);
					result.add(new OpenConnection(count,bigSet));
				}
				return result;
			}
			private void createConnectionRecursive1(Set<IntSet> includedNew,Set<OpenConnection> includedExisting,Collection<OpenConnection> pendingExisting)	{
				Set<IntSet> pendingNew=new HashSet<>();
				for (OpenConnection connection:pendingExisting) for (IntSet connected:existingToNew.get(connection)) if (!includedNew.contains(connected)) pendingNew.add(connected);
				if (!pendingNew.isEmpty()) createConnectionRecursive2(includedNew,includedExisting,pendingNew);
			}
			private void createConnectionRecursive2(Set<IntSet> includedNew,Set<OpenConnection> includedExisting,Collection<IntSet> pendingNew)	{
				Set<OpenConnection> pendingExisting=new HashSet<>();
				for (IntSet connection:pendingNew) for (OpenConnection connected:newToExisting.get(connection)) if (!includedExisting.contains(connected)) pendingExisting.add(connected);
				if (!pendingExisting.isEmpty()) createConnectionRecursive1(includedNew,includedExisting,pendingExisting);
			}
		}
		private TwoDirectionConnections getBidirectionalConnectionsFor(int rowBits)	{
			Multimap<OpenConnection,IntSet> existingToNew=HashMultimap.create();
			Multimap<IntSet,OpenConnection> newToExisting=HashMultimap.create();
			for (IntSet connection:ID_CACHE[rowBits].connections) for (IntCursor cursor=connection.cursor();cursor.moveNext();)	{
				OpenConnection connected=reverseConnectionMap[cursor.elem()];
				existingToNew.put(connected,connection);
				newToExisting.put(connection,connected);
			}
			return new TwoDirectionConnections(existingToNew,newToExisting);
		}
		private State(int maxConnected,Set<OpenConnection> openConnections)	{
			this.maxConnected=maxConnected;
			this.openConnections=openConnections;
			reverseConnectionMap=new OpenConnection[W];
			for (OpenConnection connection:openConnections) for (IntCursor cursor=connection.getIndices().cursor();cursor.moveNext();) reverseConnectionMap[cursor.elem()]=connection;
		}
		public static State getFromInitialRow(int rowBits)	{
			int maxConnected=0;
			Set<OpenConnection> openConnections=new HashSet<>();
			for (IntSet connection:ID_CACHE[rowBits].connections)	{
				int currentConnected=connection.size();
				maxConnected=Math.max(maxConnected,currentConnected);
				openConnections.add(new OpenConnection(currentConnected,connection));
			}
			return new State(maxConnected,openConnections);
		}
		public State connect(int rowBits)	{
			int newMaxConnected=maxConnected;
			Set<OpenConnection> condensed=getBidirectionalConnectionsFor(rowBits).condense();
			for (OpenConnection connection:condensed) newMaxConnected=Math.max(newMaxConnected,connection.getTotalCount());
			return new State(newMaxConnected,condensed);
		}
		public int getCount()	{
			return maxConnected;
		}
		@Override
		public int hashCode()	{
			return Objects.hash(openConnections,maxConnected);
		}
		@Override
		public boolean equals(Object other)	{
			State sOther=(State)other;
			return (maxConnected==sOther.maxConnected)&&openConnections.equals(sOther.openConnections);
		}
	}
	
	public static void main(String[] args)	{
		int maxRowIndex=1<<W;
		ObjIntMap<State> currentGeneration=HashObjIntMaps.newMutableMap();
		for (int j=0;j<maxRowIndex;++j) currentGeneration.addValue(State.getFromInitialRow(j),1);
		for (int i=1;i<H;++i)	{
			ObjIntMap<State> nextGeneration=HashObjIntMaps.newMutableMap();
			for (ObjIntCursor<State> cursor=currentGeneration.cursor();cursor.moveNext();) for (int j=0;j<maxRowIndex;++j) nextGeneration.addValue(cursor.key().connect(j),cursor.value());
			currentGeneration=nextGeneration;
		}
		long num=0;
		for (ObjIntCursor<State> cursor=currentGeneration.cursor();cursor.moveNext();) num+=cursor.key().getCount()*cursor.value();
		long den=1<<(W*H);
		double result=((double)num)/(double)den;
		System.out.println(result);
	}
}
