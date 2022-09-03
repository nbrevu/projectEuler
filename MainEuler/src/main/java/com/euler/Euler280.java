package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.euler.common.EulerUtils.BooleanPermutationGenerator;
import com.euler.common.EulerUtils.Pair;
import com.koloboke.collect.IntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.IntObjCursor;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.map.hash.HashObjIntMaps;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler280 {
	// I find it INCREDIBLE that I got this at the first try, and that it runs so fucking fast.
	private static class DoubleSquareMatrixPlusColumn	{
		private final int size;
		private final double[][] data;
		public DoubleSquareMatrixPlusColumn(int size)	{
			this.size=size;
			data=new double[size][size+1];
		}
		public double get(int i,int j)	{
			return data[i][j];
		}
		public void set(int i,int j,double value)	{
			data[i][j]=value;
		}
		public void gaussianElimination()	{
			// Needless to say, this only performs the necessary calculations. The matrix is not fully inverted.
			for (int i=0;i<size;++i)	{
				double toDivide=data[i][i];
				if (toDivide==0) throw new RuntimeException("I thought this wouldn't happen in this problem?");
				if (toDivide!=1)	{
					double toMultiply=1/toDivide;
					data[i][i]=1;
					for (int k=i+1;k<=size;++k) data[i][k]*=toMultiply;
				}
				for (int j=i+1;j<size;++j)	{
					double toMultiply=data[j][i];
					if (toMultiply!=0)	{
						data[j][i]=0;
						for (int k=i+1;k<=size;++k) data[j][k]-=data[i][k]*toMultiply;
					}
				}
			}
			// Now we have an upper triangular matrix and the diagonal elements are 1, so we can move upwards creating a diagonal one.
			for (int i=size-1;i>0;--i) for (int j=i-1;j>=0;--j)	{
				double toSubtract=data[j][i];
				if (toSubtract!=0) data[j][size]-=data[i][size]*toSubtract;
			}
		}
		public double getLastColumnSum()	{
			double result=0;
			for (int i=0;i<size;++i) result+=data[i][size];
			return result;
		}
	}
	
	private static class BooleanArrayIdCache	{
		private static class HashedArray	{
			private final boolean[] array;
			public HashedArray(boolean[] array)	{
				this.array=array;
			}
			@Override
			public boolean equals(Object other)	{
				return Arrays.equals(array,((HashedArray)other).array);
			}
			@Override
			public int hashCode()	{
				return Arrays.hashCode(array);
			}
		}
		private final ObjIntMap<HashedArray> idMap;
		private final IntObjMap<HashedArray> reverseMap;
		public BooleanArrayIdCache()	{
			idMap=HashObjIntMaps.newMutableMap();
			reverseMap=HashIntObjMaps.newMutableMap();
			BooleanPermutationGenerator perm=new BooleanPermutationGenerator(11,5);
			int index=0;
			for (boolean[] array:perm)	{
				HashedArray hashed=new HashedArray(array);
				idMap.put(hashed,index);
				reverseMap.put(index,hashed);
				++index;
			}
		}
		public int getId(boolean[] array)	{
			return idMap.getInt(new HashedArray(array));
		}
		public boolean[] getFromId(int id)	{
			return reverseMap.get(id).array;
		}
	}
	
	private static class BoardState	{
		private final static BooleanArrayIdCache CACHE=new BooleanArrayIdCache();
		private final boolean[] seedPositions;	// [0-5): bottom row positions. [5-10): top row positions: [10-11): carried by ant. Total must be 5.
		private final int i,j;	// Ant position.
		public BoardState(boolean[] seedPositions,int i,int j)	{
			this.seedPositions=seedPositions;
			this.i=i;
			this.j=j;
		}
		public static IntIntMap getSymmetryMap(IntSet states)	{
			IntIntMap result=HashIntIntMaps.newMutableMap();
			states.forEach((int state)->result.put(state,fromId(state).symmetricState().getId()));
			return result;
		}
		public static BoardState initialState()	{
			boolean[] seedPositions=new boolean[11];
			for (int i=0;i<5;++i) seedPositions[i]=true;
			return new BoardState(seedPositions,2,2);
		}
		public BoardState autoTransition()	{
			if ((i==0)&&(!seedPositions[10])&&(seedPositions[j])) {
				// The ant must take the seed in the current position.
				boolean[] newPositions=Arrays.copyOf(seedPositions,11);
				newPositions[j]=false;
				newPositions[10]=true;
				return new BoardState(newPositions,i,j);
			}	else if ((i==4)&&(seedPositions[10])&&(!seedPositions[j+5]))	{
				// The ant must leave the seed in the current position.
				boolean[] newPositions=Arrays.copyOf(seedPositions,11);
				newPositions[10]=false;
				newPositions[j+5]=true;
				return new BoardState(newPositions,i,j);
			}	else return null;
		}
		public int getId()	{
			return 25*CACHE.getId(seedPositions)+5*i+j;
		}
		public static BoardState fromId(int id)	{
			int j=id%5;
			id/=5;
			int i=id%5;
			id/=5;
			boolean[] seedPositions=CACHE.getFromId(id);
			return new BoardState(seedPositions,i,j);
		}
		public boolean isFinal()	{
			for (int i=5;i<10;++i) if (!seedPositions[i]) return false;
			return true;
		}
		public List<BoardState> neighbors()	{
			List<BoardState> result=new ArrayList<>();
			if (i>0) addNeighbor(result,i-1,j);
			if (i<4) addNeighbor(result,i+1,j);
			if (j>0) addNeighbor(result,i,j-1);
			if (j<4) addNeighbor(result,i,j+1);
			return result;
		}
		private void addNeighbor(List<BoardState> results,int newI,int newJ)	{
			BoardState state=new BoardState(seedPositions,newI,newJ);
			for (;;)	{
				BoardState auto=state.autoTransition();
				if (auto==null) break;
				else state=auto;
			}
			results.add(state);
		}
		private BoardState symmetricState()	{
			boolean[] newSeedPositions=new boolean[11];
			for (int k=0;k<5;++k)	{
				newSeedPositions[k]=seedPositions[4-k];
				newSeedPositions[k+5]=seedPositions[9-k];
			}
			newSeedPositions[10]=seedPositions[10];
			return new BoardState(newSeedPositions,i,4-j);
		}
	}
	
	private static IntObjMap<IntSet> generateTransitions()	{
		IntObjMap<IntSet> result=HashIntObjMaps.newMutableMap();
		IntSet alreadyVisited=HashIntSets.newMutableSet();
		NavigableMap<Integer,BoardState> pending=new TreeMap<>();
		BoardState initial=BoardState.initialState();
		pending.put(initial.getId(),initial);
		while (!pending.isEmpty())	{
			Map.Entry<Integer,BoardState> entry=pending.pollFirstEntry();
			int key=entry.getKey();
			alreadyVisited.add(key);
			BoardState state=entry.getValue();
			if (state.isFinal())	{
				result.put(key,HashIntSets.newMutableSet());
				continue;
			}
			IntSet children=HashIntSets.newMutableSet();
			for (BoardState child:state.neighbors())	{
				int childId=child.getId();
				children.add(childId);
				if (!alreadyVisited.contains(childId)) pending.putIfAbsent(childId,child);
			}
			result.put(key,children);
		}
		return result;
	}
	
	/*
	 * Used during the problem analysis. This returned true so the amount of states is reduced
	 * to about half (8 times less computation time). Hooray!
	private static boolean checkSymmetries(IntObjMap<IntSet> transitions)	{
		IntIntMap symmetries=BoardState.getSymmetryMap(transitions.keySet());
		IntObjCursor<IntSet> cursor=transitions.cursor();
		while (cursor.moveNext())	{
			int sym=symmetries.get(cursor.key());
			if (!getSymmetricMap(cursor.value(),symmetries).equals(transitions.get(sym))) return false;
		}
		return true;
	}
	
	private static IntSet getSymmetricMap(IntSet original,IntIntMap symmetries)	{
		IntSet result=HashIntSets.newMutableSet();
		original.forEach((int state)->result.add(symmetries.get(state)));
		return result;
	}
	*/
	
	private static Pair<IntIntMap,Integer> reassignKeys(IntObjMap<IntSet> transitions,int initial)	{
		IntIntMap result=HashIntIntMaps.newMutableMap();
		IntIntMap symmetries=BoardState.getSymmetryMap(transitions.keySet());
		int index=0;
		result.put(initial,index);
		++index;
		IntCursor cursor=transitions.keySet().cursor();
		while (cursor.moveNext())	{
			int key=cursor.elem();
			if (result.containsKey(key)) continue;
			else if (transitions.get(key).isEmpty()) continue;	// Screw final states!
			int symKey=symmetries.get(key);
			if (symKey==key)	{
				if (!result.containsKey(key))	{
					result.put(key,index);
					++index;
				}
			}	else if (result.containsKey(symKey)) result.put(key,result.get(symKey));
			else	{
				result.put(key,index);
				++index;
			}
		}
		return new Pair<>(result,index);
	}
	
	private static DoubleSquareMatrixPlusColumn getBaseMatrix(IntObjMap<IntSet> transitions)	{
		Pair<IntIntMap,Integer> reassignment=reassignKeys(transitions, BoardState.initialState().getId());
		IntIntMap reassigned=reassignment.first;
		// If the "obvious" matrix generated by this transition is Q, then we want to invert (I-Q^T).
		DoubleSquareMatrixPlusColumn result=new DoubleSquareMatrixPlusColumn(reassignment.second);
		IntSet alreadyMapped=HashIntSets.newMutableSet();
		IntObjCursor<IntSet> cursor=transitions.cursor();
		while (cursor.moveNext())	{
			if (!reassigned.containsKey(cursor.key())) continue;
			int reassignedKey=reassigned.get(cursor.key());
			if (alreadyMapped.contains(reassignedKey)) continue;
			IntSet moves=cursor.value();
			double value=-1d/(double)(moves.size());
			moves.forEach((int move)->	{
				if (!reassigned.containsKey(move)) return;
				int moveKey=reassigned.get(move);
				result.set(moveKey,reassignedKey,result.get(moveKey,reassignedKey)+value);
			});
			alreadyMapped.add(reassignedKey);
		}
		for (int i=0;i<reassignment.second;++i) result.set(i,i,1-result.get(i,i));
		result.set(0,result.size,1);
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		IntObjMap<IntSet> transitions=generateTransitions();
		DoubleSquareMatrixPlusColumn matrix=getBaseMatrix(transitions);
		matrix.gaussianElimination();
		double result=matrix.getLastColumnSum();
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
