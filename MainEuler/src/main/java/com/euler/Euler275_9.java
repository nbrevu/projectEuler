package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.EulerUtils;
import com.google.common.math.IntMath;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Euler275_9 {
	// OK. Next stop: MAN IN THE MIDDLE! I really think this is the way.
	private final static int SIZE=6;
	
	private static class Position	{
		public final int x;
		public final int y;
		private final int hashCode;
		//private static Table<Integer,Integer,Position> cache=HashBasedTable.create();
		private static IntObjMap<IntObjMap<Position>> cache=HashIntObjMaps.newMutableMap();
		public static Position of(int x,int y)	{
			IntObjMap<Position> subCache=cache.computeIfAbsent(x,(int unused)->HashIntObjMaps.newMutableMap());
			return subCache.computeIfAbsent(y,(int unused)->new Position(x,y));
		}
		private Position(int x,int y)	{
			this.x=x;
			this.y=y;
			hashCode=SIZE*x+y;
		}
		public Position moveRight()	{
			return of(x+1,y);
		}
		public Position moveUp()	{
			return of(x,y+1);
		}
		public Position moveLeft()	{
			return of(x-1,y);
		}
		public Position moveDown()	{
			return of(x,y-1);
		}
		public List<Position> getNeighbours()	{
			return Arrays.asList(moveRight(),moveUp(),moveLeft(),moveDown());
		}
		@Override
		public boolean equals(Object other)	{
			return ((Object)this)==other;
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
		@Override
		public String toString()	{
			return "("+x+","+y+")";
		}
		public boolean isNeighbour(Position other)	{
			int deltaX=Math.abs(x-other.x);
			int deltaY=Math.abs(y-other.y);
			return deltaX+deltaY==1;
		}
	}
	private static class Sculpture	{
		public final static Sculpture EMPTY_SCULPTURE=new Sculpture(Set.of());
		public final static Sculpture SINGLE_BLOCK=new Sculpture(Set.of(Position.of(1,1)));
		private final Set<Position> blocks;
		private final int hashCode;
		private Sculpture(Sculpture base,Position newBlock)	{
			int incX=((newBlock.x==0)?1:0);
			int incY=((newBlock.y==0)?1:0);
			blocks=new HashSet<>();
			for (Position p:base.blocks) blocks.add(Position.of(p.x+incX,p.y+incY));
			blocks.add(Position.of(newBlock.x+incX,newBlock.y+incY));
			hashCode=blocks.hashCode();
		}
		private Sculpture(Set<Position> blocks)	{
			this.blocks=blocks;
			hashCode=blocks.hashCode();
		}
		public int size()	{
			return blocks.size();
		}
		public int getMassCenter()	{
			int sum=0;
			int N=size();
			for (Position p:blocks) sum+=p.x;
			if ((sum%N)==0) return sum/N;
			return -1;
		}
		public boolean isAcceptable()	{
			int massCenter=getMassCenter();
			if (massCenter==-1) return false;
			return blocks.contains(Position.of(massCenter,1));
		}
		public List<Sculpture> getChildren()	{
			Set<Position> neighbours=new HashSet<>();
			for (Position p:blocks) neighbours.addAll(p.getNeighbours());
			neighbours.removeAll(blocks);
			List<Sculpture> result=new ArrayList<>();
			for (Position p:neighbours) result.add(new Sculpture(this,p));
			return result;
		}
		@Override
		public boolean equals(Object other)	{
			Sculpture sOther=(Sculpture)other;
			return blocks.equals(sOther.blocks);
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
		@Override
		public String toString()	{
			return blocks.toString();
		}
		private int getMinX()	{
			int result=Integer.MAX_VALUE;
			for (Position p:blocks) result=Math.min(result,p.x);
			return result;
		}
		private int getMinY()	{
			int result=Integer.MAX_VALUE;
			for (Position p:blocks) result=Math.min(result,p.y);
			return result;
		}
		public int getMaxX()	{
			int result=1;
			for (Position p:blocks) result=Math.max(result,p.x);
			return result;
		}
		public int getMaxY()	{
			int result=1;
			for (Position p:blocks) result=Math.max(result,p.y);
			return result;
		}
		public Sculpture shift(int addX,int addY)	{
			Set<Position> newBlocks=new HashSet<>();
			for (Position p:blocks) newBlocks.add(Position.of(p.x+addX,p.y+addY));
			return new Sculpture(newBlocks);
		}
		public Sculpture addShifted(Sculpture other,int addX,int addY)	{
			// Returns null if can't add because of overlap.
			if (other.blocks.isEmpty()) return this;
			Set<Position> newBlocks=new HashSet<>(blocks);
			for (Position p:other.blocks)	{
				Position newPos=Position.of(p.x+addX,p.y+addY);
				if (newBlocks.contains(newPos)) return null;
				else newBlocks.add(newPos);
			}
			return new Sculpture(newBlocks);
		}
		public Sculpture normalise()	{
			int minX=getMinX();
			int minY=getMinY();
			Sculpture shifted=((minX==1)&&(minY==1))?this:shift(1-minX,1-minY);
			return shifted.normaliseSymmetrically();
		}
		private Sculpture normaliseSymmetrically()	{
			int maxX=getMaxX();
			int maxY=getMaxY();
			BitSet[] data=new BitSet[maxY];
			for (int i=0;i<maxY;++i) data[i]=new BitSet();
			for (Position p:blocks) data[p.y-1].set(p.x-1);
			for (BitSet row:data)	{
				for (int i=0;i<maxX;++i)	{
					boolean thisBlock=row.get(i);
					boolean oppositeBlock=row.get(maxX-1-i);
					if (thisBlock!=oppositeBlock)	{
						// Non-symmetry detected.
						return thisBlock?this:mirrorImage(maxX);
					}
				}
			}
			return this;
		}
		private Sculpture mirrorImage(int maxX)	{
			Set<Position> result=new HashSet<>();
			for (Position p:blocks) result.add(Position.of(maxX-p.x,p.y));
			return new Sculpture(result);
		}
		public boolean isConnected()	{
			// I keep needing more and more additional methods...
			List<Position> pending=new ArrayList<>(blocks);
			List<Position> currentGen=new ArrayList<>();
			Position last=pending.remove(pending.size()-1);
			currentGen.add(last);
			do	{
				List<Position> nextGen=new ArrayList<>();
				for (Position p:pending) for (Position q:currentGen) if (p.isNeighbour(q))	{
					nextGen.add(p);
					break;	// Next p.
				}
				pending.removeAll(nextGen);
				currentGen=nextGen;
			}	while (!currentGen.isEmpty());
			return pending.isEmpty();
		}
	}

	/*
	 * Manages borders in "standard" directions. Left and right borders go from bottom to top; bottom and top borders go from left to right.
	 * In each case, border[i] indicates the first/last border that matches said direction. For example, in a border with 5 elements consisting
	 * of a cross, we would have rightBorders=[1,2,1]; topBorders=[1,2,1]; leftBorders=[1,0,1]; and bottomBorders=[1,0,1].
	 * 
	 * All indices are zero-based.
	 */
	private static class BorderSummary	{
		public final int[] rightBorders;
		public final int[] topBorders;
		public final int[] leftBorders;
		public final int[] bottomBorders;
		public BorderSummary(Sculpture sculpture)	{
			int maxX=sculpture.getMaxX();
			int maxY=sculpture.getMaxY();
			rightBorders=new int[maxY];
			topBorders=new int[maxX];
			leftBorders=new int[maxY];
			bottomBorders=new int[maxX];
			Arrays.fill(leftBorders,maxX);
			Arrays.fill(bottomBorders,maxY);
			for (Position p:sculpture.blocks)	{
				int x=p.x-1;
				int y=p.y-1;
				rightBorders[y]=Math.max(x,rightBorders[y]);
				topBorders[x]=Math.max(y,topBorders[x]);
				leftBorders[y]=Math.min(x,leftBorders[y]);
				bottomBorders[x]=Math.max(y,topBorders[x]);
			}
		}
	}

	private static class ShiftedSculpture	{
		private final Sculpture blocks;
		private final int shift;
		public ShiftedSculpture(Sculpture blocks,int shift)	{
			this.blocks=blocks;
			this.shift=shift;
		}
	}
	
	private static class SculptureCombiner	{
		private final static List<Sculpture> ADDITIONAL_LIST=List.of(Sculpture.EMPTY_SCULPTURE);
		private final Map<Sculpture,BorderSummary> borderCache;
		public SculptureCombiner()	{
			borderCache=new HashMap<>();
		}
		private BorderSummary getBorders(Sculpture s)	{
			return borderCache.computeIfAbsent(s,BorderSummary::new);
		}
		/*
		private int getMaxX(Sculpture s)	{
			return getBorders(s).bottomBorders.length;
		}
		*/
		private int getMaxY(Sculpture s)	{
			return getBorders(s).leftBorders.length;
		}
		private void combine(ShiftedSculpture s1,ShiftedSculpture s2,Set<Sculpture> canonicalResult)	{
			int shiftX=s2.shift-s1.shift;
			int minShiftY=-getMaxY(s2.blocks);
			int maxShiftY=getMaxY(s1.blocks)-1;
			for (int y=minShiftY;y<=maxShiftY;++y)	{
				Sculpture result=s1.blocks.addShifted(s2.blocks,shiftX,y);
				// Maybe I don't need to check whether it's acceptable? I'll do it just in case.
				if ((result!=null)&&result.isConnected()&&result.isAcceptable()) canonicalResult.add(result.normalise());
			}
		}
		private void combine2(SortedMap<Integer,List<ShiftedSculpture>> commonList,Set<Sculpture> canonicalResult)	{
			SortedMap<Integer,List<ShiftedSculpture>> toIterate=commonList.headMap(0);
			for (Map.Entry<Integer,List<ShiftedSculpture>> entry:toIterate.entrySet())	{
				Integer myWeight=entry.getKey();
				Integer otherWeight=-myWeight;
				List<ShiftedSculpture> otherList=commonList.get(otherWeight);
				if (otherList==null) continue;
				List<ShiftedSculpture> myList=entry.getValue();
				for (ShiftedSculpture s1:myList) for (ShiftedSculpture s2:otherList) combine(s1,s2,canonicalResult);
			}
			List<ShiftedSculpture> zeroList=commonList.getOrDefault(0,List.of());
			for (int i=0;i<zeroList.size();++i) for (int j=i;j<zeroList.size();++j) combine(zeroList.get(i),zeroList.get(j),canonicalResult);
		}
		public void combine2(SortedMap<Integer,List<ShiftedSculpture>> minorList,SortedMap<Integer,List<ShiftedSculpture>> majorList,Set<Sculpture> canonicalResult)	{
			for (Map.Entry<Integer,List<ShiftedSculpture>> entry:minorList.entrySet())	{
				Integer myWeight=entry.getKey();
				Integer otherWeight=-myWeight;
				List<ShiftedSculpture> otherList=majorList.get(otherWeight);
				if (otherList==null) continue;	// I don't think this happens, since majorList should be more ample than minorList.
				List<ShiftedSculpture> myList=entry.getValue();
				for (ShiftedSculpture s1:myList) for (ShiftedSculpture s2:otherList) combine(s1,s2,canonicalResult);
			}
		}
		public void combine3WithHub(List<Sculpture> listA,List<Sculpture> listB,List<Sculpture> listC,Set<Sculpture> canonicalResult)	{
			combine4WithHub(listA,listB,listC,ADDITIONAL_LIST,canonicalResult);
		}
		private void combine4Sorted(List<Sculpture> listA,List<Sculpture> listB,List<Sculpture> listC,List<Sculpture> listD,Set<Sculpture> canonicalResult)	{
			// Also very unholy. I'm sorry.
			Sculpture s0=Sculpture.SINGLE_BLOCK;
			for (Sculpture pieceA:listA)	{
				// Adding at the right. We need to match each LEFT border so that its new position is 1.
				BorderSummary borderA=getBorders(pieceA);
				for (int lA=0;lA<borderA.leftBorders.length;++lA)	{
					Sculpture sA=s0.addShifted(pieceA,1-borderA.leftBorders[lA],-lA);
					if (sA==null) continue;
					for (Sculpture pieceB:listB)	{
						// Now we add to the top and we need to match each BOTTOM border.
						BorderSummary borderB=getBorders(pieceB);
						for (int bB=0;bB<borderB.bottomBorders.length;++bB)	{
							Sculpture sB=sA.addShifted(pieceB,-lA,1-borderB.bottomBorders[bB]);
							if (sB==null) continue;
							for (Sculpture pieceC:listC)	{
								// Now we add at the left. We match the RIGHT border.
								BorderSummary borderC=getBorders(pieceC);
								for (int rC=0;rC<borderC.rightBorders.length;++rC)	{
									Sculpture sC=sB.addShifted(pieceC,-1-borderC.rightBorders[rC],-rC);
									if (sC==null) continue;
									for (Sculpture pieceD:listD)	{
										// Finally we add at tht bottom, so we will match the TOP border.
										BorderSummary borderD=getBorders(pieceD);
										for (int tD=0;tD<borderD.topBorders.length;++tD)	{
											Sculpture sD=sC.addShifted(pieceD,-tD,-1-borderD.topBorders[tD]);
											if (sD==null) continue;
											else if (sD.isAcceptable()&&sD.isConnected()) canonicalResult.add(sD.normalise());
										}
									}
								}
							}
						}
					}
				}
			}
		}
		public void combine4WithHub(List<Sculpture> listA,List<Sculpture> listB,List<Sculpture> listC,List<Sculpture> listD,Set<Sculpture> canonicalResult)	{
			// This is unholy, but it's the least horrible way I've found.
			class Combination	{
				private final List<Sculpture> top;
				private final List<Sculpture> bottom;
				private final List<Sculpture> oneSide;
				private final List<Sculpture> otherSide;
				public Combination(List<Sculpture> oneSide,List<Sculpture> top,List<Sculpture> otherSide,List<Sculpture> bottom)	{
					this.oneSide=oneSide;
					this.top=top;
					this.otherSide=otherSide;
					this.bottom=bottom;
				}
				@Override
				public boolean equals(Object other)	{
					Combination cOther=(Combination)other;
					boolean areSidesEqual=((oneSide==cOther.oneSide)&&(otherSide==cOther.otherSide))||((oneSide==cOther.otherSide)&&(otherSide==cOther.oneSide));
					return areSidesEqual&&(top==cOther.top)&&(bottom==cOther.bottom);
				}
			}
			List<Combination> alreadyChecked=new ArrayList<>();
			List<List<Sculpture>> unsortedListOfLists=List.of(listA,listB,listC,listD);
			int[] perms=new int[] {0,1,2,3};
			do	{
				Combination comb=new Combination(unsortedListOfLists.get(perms[0]),unsortedListOfLists.get(perms[1]),unsortedListOfLists.get(perms[2]),unsortedListOfLists.get(perms[3]));
				if (!alreadyChecked.contains(comb))	{
					combine4Sorted(comb.oneSide,comb.top,comb.otherSide,comb.bottom,canonicalResult);
					alreadyChecked.add(comb);
				}
			}	while (EulerUtils.nextPermutation(perms));
		}
	}
	
	private static Set<Sculpture> getNextGeneration(Collection<Sculpture> prev)	{
		Set<Sculpture> result=new HashSet<>(4*prev.size());
		for (Sculpture sc:prev) result.addAll(sc.getChildren());
		return result;
	}
	
	private static void addVariations(int size,Set<Sculpture> sculptures,SortedMap<Integer,List<ShiftedSculpture>> result)	{
		for (Sculpture s:sculptures)	{
			int maxX=s.getMaxX();
			int baseWeight=0;
			for (Position p:s.blocks) baseWeight+=p.x;
			for (int j=-1;j<=1+maxX;++j)	{
				List<ShiftedSculpture> toAdd=result.computeIfAbsent(baseWeight-size*j,(Integer unused)->new ArrayList<>());
				toAdd.add(new ShiftedSculpture(s,j));
			}
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		/*
		 * First index: size.
		 * Second index: weight.
		 */
		IntObjMap<SortedMap<Integer,List<ShiftedSculpture>>> sortedSculptures=HashIntObjMaps.newMutableMap();
		IntObjMap<List<Sculpture>> unsortedSculptures=HashIntObjMaps.newMutableMap();
		Set<Sculpture> currentGen=Set.of(Sculpture.SINGLE_BLOCK);
		{
			SortedMap<Integer,List<ShiftedSculpture>> subMap=new TreeMap<>();
			addVariations(1,currentGen,subMap);
			sortedSculptures.put(1,subMap);
			unsortedSculptures.put(1,new ArrayList<>(currentGen));
		}
		int halfUp=IntMath.divide(SIZE,2,RoundingMode.UP);
		for (int i=2;i<=halfUp;++i)	{
			Set<Sculpture> nextGen=getNextGeneration(currentGen);
			SortedMap<Integer,List<ShiftedSculpture>> subMap=new TreeMap<>();
			currentGen=nextGen;
			addVariations(i,currentGen,subMap);
			sortedSculptures.put(i,subMap);
			unsortedSculptures.put(i,new ArrayList<>(currentGen));
			System.out.println("Tamaño "+i+": "+nextGen.size()+" elementos.");
		}
		Set<Sculpture> canonicalResults=new HashSet<>();
		int halfDown=IntMath.divide(SIZE,2,RoundingMode.DOWN);
		SculptureCombiner combiner=new SculptureCombiner();
		// Combinations of 2 elements and nothing else.
		if (halfUp==halfDown) combiner.combine2(sortedSculptures.get(halfDown),canonicalResults);
		else combiner.combine2(sortedSculptures.get(halfDown),sortedSculptures.get(halfUp),canonicalResults);
		// Combinations of 3 elements linked by a single common block.
		for (int a=1;;++a)	{
			int maxB=IntMath.divide(SIZE-1-a,2,RoundingMode.DOWN);
			if (maxB<a) break;
			for (int b=a;b<=maxB;++b)	{
				int c=SIZE-1-(a+b);
				if (c>=halfUp) continue;
				System.out.println("Case 3: "+a+","+b+","+c+".");
				combiner.combine3WithHub(unsortedSculptures.get(a),unsortedSculptures.get(b),unsortedSculptures.get(c),canonicalResults);
			}
		}
		// Combinations of 4 elements linked by a single common block.
		for (int a=1;;++a)	{
			boolean canContinue=false;
			for (int b=a;;++b)	{
				int maxC=IntMath.divide(SIZE-1-(a+b),2,RoundingMode.DOWN);
				if (maxC<b) break;
				canContinue=true;
				for (int c=b;c<=maxC;++c)	{
					int d=SIZE-1-(a+b+c);
					if (d>=halfUp) continue;
					System.out.println("Case 4: "+a+","+b+","+c+","+d+".");
					combiner.combine4WithHub(unsortedSculptures.get(a),unsortedSculptures.get(b),unsortedSculptures.get(c),unsortedSculptures.get(d),canonicalResults);
				}
			}
			if (!canContinue) break;
		}
		int result=canonicalResults.size();
		System.out.println(result);
		System.out.println(canonicalResults);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
