package com.euler;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.function.ToLongFunction;

import com.google.common.math.LongMath;
import com.koloboke.collect.map.ObjLongMap;
import com.koloboke.collect.map.hash.HashObjLongMaps;

public class Euler270 {
	// I'm actually quite proud of this. It wasn't SO difficult (80% my ass), but it required careful handling and many many cases.
	// And it's FAST! Only 3516 cases, which is very very low. Really: 80% my ass.
	private final static int SIZE=30;
	private final static long MOD=LongMath.pow(10l,8);
	
	private final static Comparator<int[]> ARRAY_SORT=new Comparator<>()	{
		@Override
		public int compare(int[] o1, int[] o2) {
			return Arrays.compare(o1,o2);
		}
	};
	
	private static int[] cycle(int[] in,int shift)	{
		int N=in.length;
		int[] result=new int[N];
		System.arraycopy(in,0,result,shift,N-shift);
		System.arraycopy(in,N-shift,result,0,shift);
		return result;
	}
	
	private static void cycleArray(int[] in,NavigableSet<int[]> sorted)	{
		for (int i=0;i<in.length;++i) sorted.add(cycle(in,i));
	}
	
	private static int[] reverse(int[] array) {
		int N=array.length;
		int[] result=new int[N];
		for (int i=0;i<N;++i) result[i]=array[N-1-i];
		return result;
	}
	
	private static int[] getStandardRepresentation(int[] in)	{
		if (in.length<=1) return in;
		NavigableSet<int[]> sorted=new TreeSet<>(ARRAY_SORT);
		cycleArray(in,sorted);
		cycleArray(reverse(in),sorted);
		return sorted.first();
	}
	
	private static class Polygon	{
		private final static ObjLongMap<Polygon> CACHE=HashObjLongMaps.newMutableMap();
		private final static int[] BASE_CASE=new int[] {2,2,2};
		private final int[] sides;
		private Polygon(int[] sides)	{
			this.sides=getStandardRepresentation(sides);
		}
		private boolean isSmallestCase()	{
			return Arrays.equals(sides,BASE_CASE);
		}
		public static Polygon getSquare(int sideLength)	{
			++sideLength;
			return new Polygon(new int[] {sideLength,sideLength,sideLength,sideLength});
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(sides);
		}
		@Override
		public boolean equals(Object other)	{
			if (!(other instanceof Polygon)) return false;
			Polygon pOther=(Polygon)other;
			return Arrays.equals(sides,pOther.sides);
		}
		@Override
		public String toString()	{
			return Arrays.toString(sides);
		}
		public long getCombinations()	{
			return CACHE.computeIfAbsent(this,(ToLongFunction<Polygon>)Polygon::calculateCombinations);
		}
		private long calculateCombinations()	{
			if (isSmallestCase()) return 1l;
			long result=0;
			for (List<Polygon> childrenList:getChildren()) if (childrenList.size()==1) result=(result+childrenList.get(0).getCombinations())%MOD;
			else if (childrenList.size()==2)	{
				long r1=childrenList.get(0).getCombinations();
				long r2=childrenList.get(1).getCombinations();
				result=(result+r1*r2)%MOD;
			}	else throw new IllegalStateException();
			return result;
		}
		private List<List<Polygon>> getChildren()	{
			List<List<Polygon>> result=new LinkedList<>();
			if (sides[0]==2)	{
				// First side: will be eliminated.
				// Second side: only the first vertex is valid.
				if (sides[1]==2)	{
					// Side will be removed. But only if it can be feasibly so.
					if (sides.length>3)	{
						int[] child=new int[sides.length-1];
						child[0]=2;
						System.arraycopy(sides,2,child,1,sides.length-2);
						result.add(Arrays.asList(new Polygon(child)));
					}
				}	else	{
					// Side will be shortened! Only the first vertex can be trimmed.
					int[] child=Arrays.copyOf(sides,sides.length);
					--child[1];
					result.add(Arrays.asList(new Polygon(child)));
				}
				for (int i=2;i<sides.length-1;++i) for (int j=1;j<sides[i];++j)	{
					if ((i==sides.length-2)&&(j==sides[i]-1)) continue;	// This vertex can't be used.
					int[] child1=new int[1+i];
					child1[0]=2;	// "New" diagonal side.
					for (int k=1;k<i;++k) child1[k]=sides[k];	// Intact sides.
					child1[i]=1+j;	// "Cut" side, so far.
					Polygon poly1=new Polygon(child1);
					int[] child2;
					if (j==sides[i]-1)	{
						// This side has "ended", so it will be removed from the second child.
						child2=new int[sides.length-i];
						child2[0]=2;	// "New", diagonal side.
						System.arraycopy(sides,i+1,child2,1,sides.length-1-i);	// Copy the rest of the sides intact.
					}	else	{
						child2=new int[sides.length+1-i];
						child2[0]=2;	// "New", diagonal side.
						child2[1]=sides[i]-j;	// Remaining part of this side.
						System.arraycopy(sides,i+1,child2,2,sides.length-1-i);	// Copy the rest of the sides intact.
					}
					Polygon poly2=new Polygon(child2);
					result.add(Arrays.asList(poly1,poly2));
				}
				// Last vertex from "behind".
				int[] child;
				if (sides[sides.length-1]>2)	{
					child=Arrays.copyOf(sides,sides.length);
					--child[child.length-1];
				}	else child=Arrays.copyOf(sides,sides.length-1);
				Polygon poly=new Polygon(child);
				result.add(Arrays.asList(poly));
			}	else	{
				for (int i=1;i<sides.length-1;++i) for (int j=1;j<sides[i];++j)	{
					if ((i==sides.length-2)&&(j==sides[i]-1)) continue;	// This vertex can't be used.
					int[] child1=new int[i+2];
					child1[0]=2;	// "New" diagonal side.
					child1[1]=sides[0]-1;	// Remains of the first side.
					for (int k=1;k<i;++k) child1[k+1]=sides[k];	// Intact sides.
					child1[i+1]=1+j;	// "Cut" side, so far.
					Polygon poly1=new Polygon(child1);
					int[] child2;
					if (j==sides[i]-1)	{
						// This side has "ended", so it will be removed from the second child.
						child2=new int[sides.length-i];
						child2[0]=2;	// "New", diagonal side.
						System.arraycopy(sides,i+1,child2,1,sides.length-1-i);	// Copy the rest of the sides intact.
					}	else	{
						child2=new int[sides.length+1-i];
						child2[0]=2;	// "New", diagonal side.
						child2[1]=sides[i]-j;	// Remaining part of this side.
						System.arraycopy(sides,i+1,child2,2,sides.length-1-i);	// Copy the rest of the sides intact.
					}
					Polygon poly2=new Polygon(child2);
					result.add(Arrays.asList(poly1,poly2));
				}
				// Last vertex from "behind".
				int[] child;
				if (sides[sides.length-1]>2)	{
					child=new int[sides.length+1];
					child[0]=2;
					child[1]=sides[0]-1;
					System.arraycopy(sides,1,child,2,sides.length-2);
					child[sides.length]=sides[sides.length-1]-1;
				}	else	{
					// This can never happen! if the first side has length>2, then every other side also does!
					throw new IllegalStateException();
				}
				Polygon poly=new Polygon(child);
				result.add(Arrays.asList(poly));
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		Polygon poly=Polygon.getSquare(SIZE);
		System.out.println(poly.calculateCombinations());
	}
}
