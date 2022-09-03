package com.euler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class Euler701_3 {
	private final static int W=7;
	private final static int H=7;
	
	private static class OpenConnection	{
		private final int totalCount;
		private final BitSet indices;
		private final int hashCode;
		public OpenConnection(int totalCount,BitSet indices)	{
			this.totalCount=totalCount;
			this.indices=indices;
			hashCode=Objects.hash(indices,totalCount);
		}
		public int getTotalCount()	{
			return totalCount;
		}
		public BitSet getIndices()	{
			return indices;
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
		@Override
		public boolean equals(Object other)	{
			OpenConnection ocOther=(OpenConnection)other;
			return (totalCount==ocOther.totalCount)&&indices.equals(ocOther.indices);
		}
		@Override
		public String toString()	{
			return "["+indices+"+"+totalCount+"]";
		}
	}
	
	private static class TwoDirectionConnections	{
		public final Multimap<OpenConnection,BitSet> existingToNew;
		public final Multimap<BitSet,OpenConnection> newToExisting;
		public final List<BitSet> unusedConnections;
		public TwoDirectionConnections(Multimap<OpenConnection,BitSet> existingToNew,Multimap<BitSet,OpenConnection> newToExisting,List<BitSet> unusedConnections)	{
			this.existingToNew=existingToNew;
			this.newToExisting=newToExisting;
			this.unusedConnections=unusedConnections;
		}
		public Set<OpenConnection> condense()	{
			Set<OpenConnection> result=new HashSet<>();
			Set<BitSet> alreadyIteratedOver=new HashSet<>();
			for (Map.Entry<BitSet,Collection<OpenConnection>> entry:newToExisting.asMap().entrySet()) if (!alreadyIteratedOver.contains(entry.getKey()))	{
				Set<OpenConnection> includedExisting=new HashSet<>();
				Set<BitSet> includedNew=new HashSet<>();
				includedNew.add(entry.getKey());
				createConnectionRecursive1(includedNew,includedExisting,entry.getValue());
				BitSet bigSet=new BitSet(W);
				includedNew.forEach(bigSet::or);
				int count=bigSet.cardinality();
				for (OpenConnection included:includedExisting) count+=included.getTotalCount();
				alreadyIteratedOver.addAll(includedNew);
				result.add(new OpenConnection(count,bigSet));
			}
			for (BitSet connection:unusedConnections) result.add(new OpenConnection(connection.cardinality(),connection));
			return result;
		}
		private void createConnectionRecursive1(Set<BitSet> includedNew,Set<OpenConnection> includedExisting,Collection<OpenConnection> pendingExisting)	{
			Set<BitSet> pendingNew=new HashSet<>();
			for (OpenConnection connection:pendingExisting)	{
				includedExisting.add(connection);
				for (BitSet connected:existingToNew.get(connection)) if (!includedNew.contains(connected)) pendingNew.add(connected);
			}
			if (!pendingNew.isEmpty()) createConnectionRecursive2(includedNew,includedExisting,pendingNew);
		}
		private void createConnectionRecursive2(Set<BitSet> includedNew,Set<OpenConnection> includedExisting,Collection<BitSet> pendingNew)	{
			Set<OpenConnection> pendingExisting=new HashSet<>();
			for (BitSet connection:pendingNew)	{
				includedNew.add(connection);
				for (OpenConnection connected:newToExisting.get(connection)) if (!includedExisting.contains(connected)) pendingExisting.add(connected);
			}
			if (!pendingExisting.isEmpty()) createConnectionRecursive1(includedNew,includedExisting,pendingExisting);
		}
	}

	private static class State	{
		public static long counter=0;
		private static class RowIdInfo	{
			public final List<BitSet> connections;
			public RowIdInfo(List<BitSet> connections)	{
				this.connections=connections;
			}
			public static RowIdInfo getFromId(int n)	{
				List<BitSet> connections=new ArrayList<>();
				BitSet currentBitSet=null;
				for (int i=0;i<W;++i,n>>=1) if ((n&1)==0) currentBitSet=null;
				else	{
					if (currentBitSet==null)	{
						currentBitSet=new BitSet(W);
						connections.add(currentBitSet);
					}
					currentBitSet.set(i);
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
		private final int hashCode;
		private State(int maxConnected,Set<OpenConnection> openConnections)	{
			this.maxConnected=maxConnected;
			this.openConnections=openConnections;
			reverseConnectionMap=new OpenConnection[W];
			for (OpenConnection connection:openConnections) for (int i=connection.getIndices().nextSetBit(0);i>=0;i=connection.getIndices().nextSetBit(1+i)) reverseConnectionMap[i]=connection;
			hashCode=Objects.hash(openConnections,maxConnected);
		}
		public static State getFromInitialRow(int rowBits)	{
			int maxConnected=0;
			Set<OpenConnection> openConnections=new HashSet<>();
			for (BitSet connection:ID_CACHE[rowBits].connections)	{
				int currentConnected=connection.cardinality();
				maxConnected=Math.max(maxConnected,currentConnected);
				openConnections.add(new OpenConnection(currentConnected,connection));
			}
			return new State(maxConnected,openConnections);
		}
		private TwoDirectionConnections getBidirectionalConnectionsFor(int rowBits)	{
			Multimap<OpenConnection,BitSet> existingToNew=HashMultimap.create();
			Multimap<BitSet,OpenConnection> newToExisting=HashMultimap.create();
			List<BitSet> unusedConnections=new ArrayList<>();
			for (BitSet connection:ID_CACHE[rowBits].connections)	{
				boolean used=false;
				for (int i=connection.nextSetBit(0);i>=0;i=connection.nextSetBit(1+i))	{
					OpenConnection connected=reverseConnectionMap[i];
					if (connected!=null)	{
						used=true;
						existingToNew.put(connected,connection);
						newToExisting.put(connection,connected);
					}
				}
				if (!used) unusedConnections.add(connection);
			}
			return new TwoDirectionConnections(existingToNew,newToExisting,unusedConnections);
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
			return hashCode;
		}
		@Override
		public boolean equals(Object other)	{
			++counter;
			State sOther=(State)other;
			return (maxConnected==sOther.maxConnected)&&openConnections.equals(sOther.openConnections);
		}
		@Override
		public String toString()	{
			return "{{"+openConnections+"}}+"+maxConnected;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int maxRowIndex=1<<W;
		Map<State,Long> currentGeneration=new HashMap<>();
		for (int j=0;j<maxRowIndex;++j) currentGeneration.put(State.getFromInitialRow(j),1l);
		for (int i=1;i<H;++i)	{
			System.out.println("Row "+i+": "+currentGeneration.size()+" states.");
			Map<State,Long> nextGeneration=new HashMap<>();
			for (Map.Entry<State, Long> entry:currentGeneration.entrySet()) for (int j=0;j<maxRowIndex;++j) nextGeneration.compute(entry.getKey().connect(j),(State unused,Long value)->((value==null)?0:value.longValue())+entry.getValue());
			currentGeneration=nextGeneration;
		}
		System.out.println("End: "+currentGeneration.size()+" states.");
		long num=0;
		for (Map.Entry<State,Long> entry:currentGeneration.entrySet()) num+=entry.getKey().getCount()*entry.getValue();
		long den=1l<<(W*H);
		long tac=System.nanoTime();
		BigDecimal result=new BigDecimal(num).divide(new BigDecimal(den));
		System.out.println(result);
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
		System.out.println(State.counter+" calls to State.equals().");
	}
}
