package com.euler;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.euler.common.EulerUtils.LongPair;
import com.euler.common.PythagoreanTriples.PrimitiveTriplesIterator;
import com.google.common.math.LongMath;

public class Euler292 {
	// Result for N=100: 291805675.
	// Result for N=120: 3600060866
	// Result for N=140: 37094934981
	// Result for N=160: 330169267155.
	// Result for N=180: 2601016454857.
	// Result for N=200: 18473139184059.
	private final static long N=120;
	
	private static class Slope implements Comparable<Slope>	{
		// Assumes x and y positive.
		public final long x;
		public final long y;
		public Slope(long x,long y)	{
			this.x=x;
			this.y=y;
		}
		@Override
		public int hashCode()	{
			return Long.hashCode(x+y);
		}
		@Override
		public boolean equals(Object other)	{
			Slope sOther=(Slope)other;
			return (x==sOther.x)&&(y==sOther.y);
		}
		@Override
		public int compareTo(Slope other)	{
			return Long.compare(other.x*y,x*other.y);
		}
		@Override
		public String toString()	{
			return String.format("<%d,%d>",x,y);
		}
	}
	
	private static class Segment	{
		private final long dx;
		private final long dy;
		private final long length;
		public Segment(long dx,long dy,long length)	{
			this.dx=dx;
			this.dy=dy;
			this.length=length;
		}
	}
	
	private static enum Rotation	{
		ONE_QUADRANT	{
			@Override
			public long getDx(LongPair original)	{
				return -original.y;
			}
			@Override
			public long getDy(LongPair original)	{
				return original.x;
			}
		},
		TWO_QUADRANTS	{
			@Override
			public long getDx(LongPair original)	{
				return -original.x;
			}
			@Override
			public long getDy(LongPair original)	{
				return -original.y;
			}
		},
		THREE_QUADRANTS	{
			@Override
			public long getDx(LongPair original)	{
				return original.y;
			}
			@Override
			public long getDy(LongPair original)	{
				return -original.x;
			}
		};
		public abstract long getDx(LongPair original);
		public abstract long getDy(LongPair original);
		public LongPair add(LongPair base,LongPair rotated)	{
			long x=base.x+getDx(rotated);
			long y=base.y+getDy(rotated);
			return new LongPair(x,y);
		}
	}
	
	private static long countHorizontalAndVerticalCombinations(long remainingLength)	{
		long pairs=remainingLength/2;
		return ((pairs+1)*(pairs+2))/2;
	}

	private static class BrokenLineCollection	{
		private static class ConnectionSummary	{
			public long connections;
			public boolean containsStraightLine;
			public int negatingStraightLines;
			public ConnectionSummary()	{
				connections=0;
				containsStraightLine=false;
				negatingStraightLines=0;
			}
			public void add(boolean isStraightLine)	{
				++connections;
				containsStraightLine|=isStraightLine;
			}
			public static ConnectionSummary combine(ConnectionSummary one,ConnectionSummary other,boolean resultIsOrigin)	{
				ConnectionSummary result=new ConnectionSummary();
				result.connections=one.connections*other.connections;
				result.containsStraightLine=false;
				if (resultIsOrigin&&one.containsStraightLine&&other.containsStraightLine) ++result.negatingStraightLines;
				return result;
			}
			public void add(ConnectionSummary other)	{
				connections+=other.connections;
				containsStraightLine|=other.containsStraightLine;
				negatingStraightLines+=other.negatingStraightLines;
			}
		}
		private final NavigableMap<Long,Map<LongPair,ConnectionSummary>> pieces;
		public BrokenLineCollection()	{
			pieces=new TreeMap<>();
		}
		public void add(boolean isSingleLine,long dx,long dy,Long totalLength)	{
			Map<LongPair,ConnectionSummary> innerMap=pieces.computeIfAbsent(totalLength,(Long unused)->new HashMap<>());
			ConnectionSummary summary=innerMap.computeIfAbsent(new LongPair(dx,dy),(LongPair unused)->new ConnectionSummary());
			summary.add(isSingleLine);
		}
		public BrokenLineCollection combineWith(BrokenLineCollection other,long maxLength,Rotation rotation)	{
			BrokenLineCollection result=new BrokenLineCollection();
			for (Map.Entry<Long,Map<LongPair,ConnectionSummary>> entry:pieces.entrySet())	{
				long myLength=entry.getKey();
				Map<LongPair,ConnectionSummary> connections=entry.getValue();
				for (Map.Entry<Long,Map<LongPair,ConnectionSummary>> otherEntry:other.pieces.headMap(maxLength-myLength,true).entrySet())	{
					long totalLength=myLength+otherEntry.getKey();
					Map<LongPair,ConnectionSummary> targetSubmap=result.pieces.computeIfAbsent(totalLength,(Long unused)->new HashMap<>());
					Map<LongPair,ConnectionSummary> otherConnections=otherEntry.getValue();
					for (Map.Entry<LongPair,ConnectionSummary> subEntry1:connections.entrySet()) for (Map.Entry<LongPair,ConnectionSummary> subEntry2:otherConnections.entrySet())	{
						LongPair finalPosition=rotation.add(subEntry1.getKey(),subEntry2.getKey());
						long extraLength=LongMath.sqrt(finalPosition.x*finalPosition.x+finalPosition.y*finalPosition.y,RoundingMode.UP);
						if (totalLength+extraLength>maxLength) continue;
						boolean isOrigin=((finalPosition.x==0)&&(finalPosition.y==0));
						ConnectionSummary newConnections=ConnectionSummary.combine(subEntry1.getValue(),subEntry2.getValue(),isOrigin);
						targetSubmap.merge(finalPosition,newConnections,(ConnectionSummary oldCs,ConnectionSummary newCs)->	{
							oldCs.add(newCs);
							return oldCs;
						});
					}
				}
			}
			return result;
		}
		public long countAllPolygons(long maxLength)	{
			long result=0l;
			for (Map.Entry<Long,Map<LongPair,ConnectionSummary>> entry1:pieces.entrySet())	{
				long remainingLength=maxLength-entry1.getKey();
				for (Map.Entry<LongPair,ConnectionSummary> entry2:entry1.getValue().entrySet())	{
					LongPair dist=entry2.getKey();
					long manhattanDistance=Math.abs(dist.x)+Math.abs(dist.y);
					long toCount=remainingLength-manhattanDistance;
					if (toCount>=0)	{
						ConnectionSummary summary=entry2.getValue();
						long baseCount=countHorizontalAndVerticalCombinations(toCount);
						result+=baseCount*summary.connections-summary.negatingStraightLines;
					}
				}
			}
			return result;
		}
	}
	
	private static class QuadrantPieceGenerator	{
		private final long maxLength;
		private final NavigableMap<Slope,Segment[]> segments;
		public QuadrantPieceGenerator(long maxLength)	{
			this.maxLength=maxLength;
			segments=new TreeMap<>();
		}
		public void addSlope(long dx,long dy,long length)	{
			Segment[] thisSegments=new Segment[(int)(maxLength/length)];
			for (int i=1;i<=thisSegments.length;++i) thisSegments[i-1]=new Segment(dx*i,dy*i,length*i);
			segments.put(new Slope(dx,dy),thisSegments);
		}
		public BrokenLineCollection generateAllPieces()	{
			BrokenLineCollection result=new BrokenLineCollection();
			for (Map.Entry<Slope,Segment[]> entry:segments.entrySet())	{
				Slope slope=entry.getKey();
				for (Segment s:entry.getValue()) generateAllPiecesRecursive(slope,slope,s.dx,s.dy,s.length,result);
			}
			return result;
		}
		private void generateAllPiecesRecursive(Slope initialSlope,Slope currentSlope,long dx,long dy,long length,BrokenLineCollection result)	{
			if (length>maxLength) return;
			result.add(initialSlope==currentSlope,dx,dy,length);
			for (Map.Entry<Slope,Segment[]> entry:segments.tailMap(currentSlope,false).entrySet())	{
				Slope slope=entry.getKey();
				for (Segment s:entry.getValue()) generateAllPiecesRecursive(initialSlope,slope,dx+s.dx,dy+s.dy,length+s.length,result);
			}
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long maxLength=(long)Math.ceil(N/Math.sqrt(2));
		QuadrantPieceGenerator generator=new QuadrantPieceGenerator(maxLength);
		PrimitiveTriplesIterator iterator=new PrimitiveTriplesIterator();
		for (;;)	{
			iterator.next();
			long c=iterator.c();
			if (c<=maxLength)	{
				long a=iterator.a();
				long b=iterator.b();
				generator.addSlope(a,b,c);
				generator.addSlope(b,a,c);
			}	else if (iterator.isSmallestN()) break;
		}
		BrokenLineCollection quadrant=generator.generateAllPieces();
		BrokenLineCollection twoQuadrants=quadrant.combineWith(quadrant,N,Rotation.ONE_QUADRANT);
		BrokenLineCollection threeQuadrants=twoQuadrants.combineWith(quadrant,N,Rotation.TWO_QUADRANTS);
		BrokenLineCollection fourQuadrants=threeQuadrants.combineWith(quadrant,N,Rotation.THREE_QUADRANTS);
		BrokenLineCollection oppositeQuadrants=quadrant.combineWith(quadrant,N,Rotation.TWO_QUADRANTS);
		/*
		 * Combinations of quadrants (the ones that don't appear are substituted by straight lines):
		 * 0000 -> rectangles.
		 * 0001, 0010, 0100, 1000 -> one quadrant.
		 * 0011, 0110, 1100, 1001 -> two consecutive quadrants.
		 * 0111, 1110, 1101, 1011 -> three quadrants.
		 * 1111 -> all quadrants.
		 * 0101, 1010 -> two opposite quadrants.
		 */
		long result=countHorizontalAndVerticalCombinations(N-4)+4*quadrant.countAllPolygons(N)+4*twoQuadrants.countAllPolygons(N)+4*threeQuadrants.countAllPolygons(N)+fourQuadrants.countAllPolygons(N)+2*oppositeQuadrants.countAllPolygons(N);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
