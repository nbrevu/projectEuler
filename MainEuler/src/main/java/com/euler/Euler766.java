package com.euler;

import java.awt.Point;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Predicates;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;

public class Euler766 {
	private static enum SquareIdentifier	{
		ORIGIN(0,0),
		RIGHT(1,0),
		DOWN(0,1),
		DOWN_RIGHT(1,1);
		
		public final int posX;
		public final int posY;
		
		private SquareIdentifier(int posX,int posY)	{
			this.posX=posX;
			this.posY=posY;
		}
	}

	private static enum Piece	{
		SMALL_SQUARE(SquareIdentifier.ORIGIN),
		VERTICAL(SquareIdentifier.ORIGIN,SquareIdentifier.DOWN),
		HORIZONTAL(SquareIdentifier.ORIGIN,SquareIdentifier.RIGHT),
		THREEPIECES_R(SquareIdentifier.ORIGIN,SquareIdentifier.RIGHT,SquareIdentifier.DOWN),
		THREEPIECES_J(SquareIdentifier.RIGHT,SquareIdentifier.DOWN,SquareIdentifier.DOWN_RIGHT),
		BIG_SQUARE(SquareIdentifier.ORIGIN,SquareIdentifier.RIGHT,SquareIdentifier.DOWN,SquareIdentifier.DOWN_RIGHT);
		
		public final List<SquareIdentifier> positions;
		
		private Piece(SquareIdentifier... positions)	{
			this.positions=List.of(positions);
		}
	}
	
	private static class RelativeMove	{
		public final List<Point> newlyOccupiedPoints;
		public final List<Point> freedPoints;
		
		public RelativeMove(List<Point> newlyOccupiedPoints,List<Point> freedPoints)	{
			this.newlyOccupiedPoints=newlyOccupiedPoints;
			this.freedPoints=freedPoints;
		}
	}
	
	private static class MoveList	{
		private final Map<Piece,Integer> indiceList;
		private final RelativeMove[][] moves;
		private final Point[] movePoints;
		public final int size;
		
		public MoveList(Collection<Piece> pieces)	{
			indiceList=new EnumMap<>(Piece.class);
			moves=new RelativeMove[pieces.size()][4];
			Point[][] grid=new Point[4][4];
			for (int i=0;i<4;++i) for (int j=0;j<4;++j) grid[i][j]=new Point(j-1,i-1);
			int index=0;
			for (Piece p:pieces)	{
				indiceList.put(p,index);
				fillMoves(p,moves[index],grid);
				++index;
			}
			movePoints=new Point[4];
			movePoints[0]=grid[1][2]; // Right.
			movePoints[1]=grid[0][1]; // Up.
			movePoints[2]=grid[1][0]; // Left.
			movePoints[3]=grid[2][1]; // Down.
			size=index;
		}
		public RelativeMove getMove(int pieceIndex,int moveIndex)	{
			return moves[pieceIndex][moveIndex];
		}
		public Point getMovement(int index)	{
			return movePoints[index];
		}
		public int getPieceIndex(Piece p)	{
			return indiceList.get(p).intValue();
		}
		private static void fillMoves(Piece piece,RelativeMove[] destination,Point[][] grid)	{
			Set<Point> basePoints=new HashSet<>();
			Set<Point> movedRight=new HashSet<>();
			Set<Point> movedUp=new HashSet<>();
			Set<Point> movedLeft=new HashSet<>();
			Set<Point> movedDown=new HashSet<>();
			for (SquareIdentifier square:piece.positions)	{
				int baseX=square.posX+1;
				int baseY=square.posY+1;
				basePoints.add(grid[baseY][baseX]);
				movedRight.add(grid[baseY][baseX+1]);
				movedUp.add(grid[baseY-1][baseX]);
				movedLeft.add(grid[baseY][baseX-1]);
				movedDown.add(grid[baseY+1][baseX]);
			}
			destination[0]=getMove(basePoints,movedRight);
			destination[1]=getMove(basePoints,movedUp);
			destination[2]=getMove(basePoints,movedLeft);
			destination[3]=getMove(basePoints,movedDown);
		}
		private static RelativeMove getMove(Set<Point> pointsBefore,Set<Point> pointsAfter)	{
			List<Point> newlyOccupiedPoints=diff(pointsAfter,pointsBefore);
			List<Point> freedPoints=diff(pointsBefore,pointsAfter);
			return new RelativeMove(newlyOccupiedPoints,freedPoints);
		}
		private static List<Point> diff(Set<Point> minuend,Set<Point> subtrahend)	{
			return minuend.stream().filter(Predicates.not(subtrahend::contains)).collect(Collectors.toUnmodifiableList());
		}
	}
	
	private static class ProblemBoard	{
		public final int sizeX;
		public final int sizeY;
		private final Point[][] pointsCache;
		private final Point[] indexToPosition;
		private final ObjIntMap<Point> positionToIndex;
		
		public ProblemBoard(int sizeX,int sizeY)	{
			int totalSize=sizeX*sizeY;
			if (totalSize>31) throw new IllegalArgumentException("NICHT MÖGLICH!!!!!");
			this.sizeX=sizeX;
			this.sizeY=sizeY;
			pointsCache=new Point[sizeY][sizeX];
			indexToPosition=new Point[totalSize];
			positionToIndex=HashObjIntMaps.newMutableMap();
			int index=0;
			for (int i=0;i<sizeY;++i) for (int j=0;j<sizeX;++j)	{
				Point p=new Point(j,i);
				pointsCache[i][j]=p;
				indexToPosition[index]=p;
				positionToIndex.put(p,index);
				++index;
			}
		}
		public int size()	{
			return sizeX*sizeY;
		}
		public Point getPoint(int row,int column)	{
			return pointsCache[row][column];
		}
		public Point getPoint(int index)	{
			return indexToPosition[index];
		}
		public int getIndex(Point p)	{
			return positionToIndex.getInt(p);
		}
		public Point getPoint(Point basePoint,Point relativePosition)	{
			int x=basePoint.x+relativePosition.x;
			int y=basePoint.y+relativePosition.y;
			if ((x<0)||(x>=sizeX)||(y<0)||(y>=sizeY)) return null;
			else return getPoint(y,x);
		}
	}
	
	/*
	 * The array contains as many entries as distinct pieces are present in the puzzle, plus one. The first N positions represent the "origin"
	 * points of the pieces, as a bit set. That is, if index 0 represents small squares, and the array entry is 000...0100010, then there are
	 * exactly two small squares, whose origins are the positions represented by indices 1 and 5.
	 * 
	 * The last element of the array represents the free positions, using the same "index to position" scheme.
	 * 
	 * The board configuration is encoded in the ProblemBoard class. The available moves are encoded in the MoveList class.
	 */
	private static class CondensedConfiguration	{
		public final int[] positions;
		public CondensedConfiguration(int[] positions)	{
			this.positions=positions;
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(positions);
		}
		@Override
		public boolean equals(Object other)	{
			CondensedConfiguration ccOther=(CondensedConfiguration)other;
			return Arrays.equals(positions,ccOther.positions);
		}
	}
	
	private static class ReachableConfigurationCounter	{
		private final ProblemBoard board;
		private final SetMultimap<Piece,Point> startingPoints;
		private final MoveList moves;
		private final int[] bitCache;
		
		public ReachableConfigurationCounter(ProblemBoard board,SetMultimap<Piece,Point> startingPoints)	{
			this.board=board;
			this.startingPoints=startingPoints;
			moves=new MoveList(startingPoints.keySet());
			bitCache=new int[board.size()];
			bitCache[0]=1;
			for (int i=1;i<bitCache.length;++i) bitCache[i]=bitCache[i-1]<<1;
		}
		private CondensedConfiguration getInitialConfiguration()	{
			int[] result=new int[moves.size+1];
			int emptyValues=(1<<(board.size()))-1;
			for (Map.Entry<Piece,Collection<Point>> piecePoints:startingPoints.asMap().entrySet())	{
				Piece piece=piecePoints.getKey();
				Collection<Point> points=piecePoints.getValue();
				int pieceIndex=moves.getPieceIndex(piece);
				int positionBits=0;
				for (Point p:points)	{
					int originIndex=board.getIndex(p);
					positionBits+=1<<originIndex;
					for (SquareIdentifier id:piece.positions)	{
						Point modifiedPoint=board.getPoint(p.y+id.posY,p.x+id.posX);
						int modifiedPointBit=1<<board.getIndex(modifiedPoint);
						if ((emptyValues&modifiedPointBit)==0) throw new IllegalStateException("Invalid configuration.");
						emptyValues-=modifiedPointBit;
					}
				}
				result[pieceIndex]=positionBits;
			}
			result[moves.size]=emptyValues;
			return new CondensedConfiguration(result);
		}
		private Iterable<CondensedConfiguration> getChildren(CondensedConfiguration config)	{
			class MovesIterator implements Iterator<CondensedConfiguration>	{
				private int currentArrayIndex;
				private int currentBitIndex;
				private int currentMoveIndex;
				private int currentPiecePositions;
				private Point currentBasePoint;
				private CondensedConfiguration next;
				private MovesIterator()	{
					currentArrayIndex=-1;
					currentBitIndex=bitCache.length-1;
					currentMoveIndex=3;
					if (!nextMove()) next=null;
				}
				@Override
				public boolean hasNext() {
					return next!=null;
				}
				@Override
				public CondensedConfiguration next() {
					CondensedConfiguration result=next;
					if (!nextMove()) next=null;
					return result;
				}
				private boolean nextMove()	{
					++currentMoveIndex;
					if (currentMoveIndex>=4) return nextPosition();
					else	{
						RelativeMove move=moves.getMove(currentArrayIndex,currentMoveIndex);
						int occupiedPoints=0;
						for (Point relativePoint:move.newlyOccupiedPoints)	{
							Point absolutePoint=board.getPoint(currentBasePoint,relativePoint);
							if (absolutePoint==null) return nextMove();
							int absolutePointIndex=board.getIndex(absolutePoint);
							int absolutePointBit=1<<absolutePointIndex;
							if ((config.positions[config.positions.length-1]&absolutePointBit)!=absolutePointBit) return nextMove();
							occupiedPoints+=absolutePointBit;
						}
						// At this point, we have successfully verified that the move is valid.
						int freedPoints=0;
						for (Point relativePoint:move.freedPoints)	{
							Point absolutePoint=board.getPoint(currentBasePoint,relativePoint);
							int absolutePointIndex=board.getIndex(absolutePoint);
							int absolutePointBit=1<<absolutePointIndex;
							freedPoints+=absolutePointBit;
						}
						Point newOrigin=board.getPoint(currentBasePoint,moves.getMovement(currentMoveIndex));
						int[] newPositions=Arrays.copyOf(config.positions,config.positions.length);
						newPositions[currentArrayIndex]-=bitCache[currentBitIndex];
						newPositions[currentArrayIndex]+=bitCache[board.getIndex(newOrigin)];
						newPositions[newPositions.length-1]-=occupiedPoints-freedPoints;
						next=new CondensedConfiguration(newPositions);
						return true;
					}
				}
				private boolean nextPosition()	{
					for (;;)	{
						++currentBitIndex;
						if (currentBitIndex>=bitCache.length) return nextPiece();
						int currentBit=bitCache[currentBitIndex];
						if ((currentPiecePositions&currentBit)!=0)	{
							currentMoveIndex=-1;
							currentBasePoint=board.getPoint(currentBitIndex);
							return nextMove();
						}
					}
				}
				private boolean nextPiece()	{
					++currentArrayIndex;
					if (currentArrayIndex>=config.positions.length-1) return false;
					currentPiecePositions=config.positions[currentArrayIndex];
					currentBitIndex=-1;
					return nextPosition();
				}
			}
			return ()->new MovesIterator();
		}
		public int countReachableConfigurations()	{
			Set<CondensedConfiguration> visitedConfigurations=new HashSet<>();
			Set<CondensedConfiguration> pendingConfigurations=new HashSet<>();
			CondensedConfiguration start=getInitialConfiguration();
			pendingConfigurations.add(start);
			while (!pendingConfigurations.isEmpty())	{
				CondensedConfiguration element=pendingConfigurations.iterator().next();
				pendingConfigurations.remove(element);
				visitedConfigurations.add(element);
				for (CondensedConfiguration child:getChildren(element)) if (!visitedConfigurations.contains(child)) pendingConfigurations.add(child);
			}
			return visitedConfigurations.size();
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		ProblemBoard board=new ProblemBoard(6,5);
		SetMultimap<Piece,Point> initialPieces=MultimapBuilder.enumKeys(Piece.class).hashSetValues().build();
		initialPieces.put(Piece.THREEPIECES_R,board.getPoint(0,1));
		initialPieces.put(Piece.THREEPIECES_J,board.getPoint(0,2));
		initialPieces.put(Piece.THREEPIECES_R,board.getPoint(0,4));
		initialPieces.put(Piece.VERTICAL,board.getPoint(1,5));
		initialPieces.put(Piece.SMALL_SQUARE,board.getPoint(2,0));
		initialPieces.put(Piece.SMALL_SQUARE,board.getPoint(2,1));
		initialPieces.put(Piece.BIG_SQUARE,board.getPoint(2,2));
		initialPieces.put(Piece.VERTICAL,board.getPoint(2,4));
		initialPieces.put(Piece.SMALL_SQUARE,board.getPoint(3,0));
		initialPieces.put(Piece.SMALL_SQUARE,board.getPoint(3,1));
		initialPieces.put(Piece.THREEPIECES_J,board.getPoint(3,4));
		initialPieces.put(Piece.SMALL_SQUARE,board.getPoint(4,0));
		initialPieces.put(Piece.SMALL_SQUARE,board.getPoint(4,1));
		initialPieces.put(Piece.HORIZONTAL,board.getPoint(4,2));
		ReachableConfigurationCounter counter=new ReachableConfigurationCounter(board,initialPieces);
		int result=counter.countReachableConfigurations();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
