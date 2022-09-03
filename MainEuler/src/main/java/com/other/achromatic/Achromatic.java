package com.other.achromatic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;

import org.apache.commons.lang3.mutable.MutableInt;

import com.google.common.base.Splitter;
import com.koloboke.collect.map.CharObjMap;
import com.koloboke.collect.map.hash.HashCharObjMaps;

public class Achromatic {
	private static enum Shape	{
		CIRCLE('C'),SQUARE('S');
		private final char symbol;
		private Shape(char symbol)	{
			this.symbol=symbol;
		}
		private final static CharObjMap<Shape> CHARMAP=buildCharMap();
		public static CharObjMap<Shape> buildCharMap()	{
			CharObjMap<Shape> result=HashCharObjMaps.newMutableMap();
			for (Shape shape:values()) result.put(shape.symbol,shape);
			return result;
		}
		@Override
		public String toString()	{
			return Character.toString(symbol);
		}
	}
	private static class Symbol	{
		public final int colour;
		public final Shape shape;
		public Symbol(int colour,Shape shape)	{
			this.colour=colour;
			this.shape=shape;
		}
		public Symbol changeColour(int newColour)	{
			return new Symbol(newColour,shape);
		}
		public static Symbol parse(String in)	{
			Shape shape=Shape.CHARMAP.get(in.charAt(0));
			if (shape==null) return null;
			return new Symbol(Integer.parseInt(in.substring(1)),shape);
		}
		@Override
		public int hashCode()	{
			return colour*(1+shape.ordinal());
		}
		@Override
		public boolean equals(Object other)	{
			Symbol sOther=(Symbol)other;
			return (colour==sOther.colour)&&(shape==sOther.shape);
		}
		@Override
		public String toString()	{
			return shape.toString()+colour;
		}
	}
	private static class Point implements Comparable<Point>	{
		public final int row;
		public final int column;
		public Point(int row,int column)	{
			this.row=row;
			this.column=column;
		}
		@Override
		public int hashCode()	{
			return (1+row)*(1+column);
		}
		@Override
		public boolean equals(Object other)	{
			Point pOther=(Point)other;
			return (row==pOther.row)&&(column==pOther.column);
		}
		@Override
		public int compareTo(Point other)	{
			int diffR=row-other.row;
			return (diffR==0)?(column-other.column):diffR;
		}
		@Override
		public String toString()	{
			return "["+row+","+column+"]";
		}
	}
	private static enum Direction	{
		RIGHT	{
			@Override
			public void doStep(MutableInt row,MutableInt column)	{
				column.increment();
			}
		},UP	{
			@Override
			public void doStep(MutableInt row,MutableInt column)	{
				row.decrement();
			}
		},LEFT	{
			@Override
			public void doStep(MutableInt row,MutableInt column) {
				column.decrement();
			}
		},DOWN	{
			@Override
			public void doStep(MutableInt row,MutableInt column)	{
				row.increment();
			}
		};
		
		public abstract void doStep(MutableInt row,MutableInt column);
		public final static Map<Direction,Set<Direction>> ROTATIONS=buildRotationsMap();
		private static Map<Direction,Set<Direction>> buildRotationsMap()	{
			Map<Direction,Set<Direction>> result=new EnumMap<>(Direction.class);
			result.put(RIGHT,EnumSet.of(UP,DOWN));
			result.put(UP,EnumSet.of(LEFT,RIGHT));
			result.put(LEFT,EnumSet.of(DOWN,UP));
			result.put(DOWN,EnumSet.of(RIGHT,LEFT));
			return result;
		}
	}
	private static class Arrow	{
		public final Direction direction;
		public final int steps;
		public Arrow(Direction direction,int steps)	{
			this.direction=direction;
			this.steps=steps;
		}
		@Override
		public String toString()	{
			return direction.toString()+steps;
		}
	}
	private static class Move	{
		public final Point startPoint;
		public final List<Arrow> directions;
		public Move(Point startPoint,List<Arrow> directions)	{
			this.startPoint=startPoint;
			this.directions=directions;
		}
		@Override
		public String toString()	{
			StringBuilder sb=new StringBuilder();
			sb.append("<");
			sb.append(startPoint.toString());
			sb.append(",");
			sb.append(directions.toString());
			sb.append(">");
			return sb.toString();
		}
	}
	private static class Grid	{
		private class MoveFinder	{
			private final Point startingPoint;
			private final int startingColour;
			private final Direction direction;
			private final List<Arrow> progress;
			private final MutableInt currentRow;
			private final MutableInt currentColumn;
			private int currentSteps;
			private OptionalInt innerColour;
			private final Set<Point> visitedSymbols;
			public MoveFinder(int row,int column,Direction direction)	{
				startingPoint=new Point(row,column);
				startingColour=data[row][column].colour;
				this.direction=direction;
				progress=new ArrayList<>();
				currentRow=new MutableInt(row);
				currentColumn=new MutableInt(column);
				currentSteps=0;
				innerColour=OptionalInt.empty();
				visitedSymbols=new HashSet<>();
			}
			private MoveFinder(MoveFinder parent,Arrow lastArrow,Direction newDirection)	{
				startingPoint=parent.startingPoint;
				startingColour=parent.startingColour;
				direction=newDirection;
				progress=new ArrayList<>(parent.progress);
				progress.add(lastArrow);
				currentRow=parent.currentRow;
				currentColumn=parent.currentColumn;
				currentSteps=0;
				innerColour=parent.innerColour;
				visitedSymbols=new HashSet<>(parent.visitedSymbols);
			}
			public void findMoves(List<Move> output)	{
				for (;;)	{
					direction.doStep(currentRow,currentColumn);
					++currentSteps;
					if (!isInsideBounds(currentRow.intValue(),currentColumn.intValue())) return;
					Symbol currentSymbol=data[currentRow.intValue()][currentColumn.intValue()];
					if (currentSymbol==null) continue;
					Point currentPoint=new Point(currentRow.intValue(),currentColumn.intValue());
					if (visitedSymbols.contains(currentPoint)) return;	// Maybe wrong? Maybe I can cross it twice?
					visitedSymbols.add(currentPoint);
					if (currentSymbol.colour==startingColour)	{
						if (innerColour.isPresent()/*&&(currentPoint.compareTo(startingPoint)>=0)*/)	{
							progress.add(new Arrow(direction,currentSteps));
							output.add(new Move(startingPoint,progress));
						}
						return;
					} else if (innerColour.isEmpty()) innerColour=OptionalInt.of(currentSymbol.colour);
					else if (innerColour.getAsInt()!=currentSymbol.colour) return;
					if (currentSymbol.shape==Shape.SQUARE)	{
						Arrow thisArrow=new Arrow(direction,currentSteps);
						for (Direction rotation:Direction.ROTATIONS.get(direction))	{
							MoveFinder finder=new MoveFinder(this,thisArrow,rotation);
							finder.findMoves(output);
						}
					}
				}
			}
		}
		private final static Splitter SPACE_SPLITTER=Splitter.on(" ");
		private final Symbol[][] data;
		private final int hashCode;
		private static Symbol[] parse(String in)	{
			return SPACE_SPLITTER.splitToList(in).stream().map(Symbol::parse).toArray(Symbol[]::new);
		}
		public Grid(String[] symbols)	{
			this(Arrays.stream(symbols).map(Grid::parse).toArray(Symbol[][]::new));
		}
		private Grid(Symbol[][] data)	{
			this.data=data;
			ensureHomogeneity();
			hashCode=calculateHashCode();
		}
		private void ensureHomogeneity()	{
			int n=data[0].length;
			for (int i=1;i<data.length;++i) if (data[i].length!=n) throw new IllegalArgumentException("Unbalanced grid.");
		}
		private int calculateHashCode()	{
			int result=0;
			for (Symbol[] array:data) for (Symbol s:array)	{
				result*=13;
				if (s!=null) result+=s.hashCode();
			}
			return result;
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
		@Override
		public boolean equals(Object other)	{
			Grid gOther=(Grid)other;
			if (data.length!=gOther.data.length) return false;
			for (int i=0;i<data.length;++i)	{
				if (data[i].length!=gOther.data[i].length) return false;
				for (int j=0;j<data[i].length;++j) if (!Objects.equals(data[i][j],gOther.data[i][j])) return false;
			}
			return true;
		}
		private boolean isInsideBounds(int row,int column)	{
			return (row>=0)&&(column>=0)&&(row<data.length)&&(column<data[row].length);
		}
		public boolean isFinished()	{
			OptionalInt colour=OptionalInt.empty();
			for (int i=0;i<data.length;++i) for (int j=0;j<data[i].length;++j)	{
				Symbol s=data[i][j];
				if (s==null) continue;
				if (colour.isEmpty()) colour=OptionalInt.of(s.colour);
				else if (colour.getAsInt()!=s.colour) return false;
			}
			return true;
		}
		private Grid doMove(Move move)	{
			// The move is assumed to be valid.
			Symbol[][] stateCopy=Arrays.stream(data).map(Symbol[]::clone).toArray(Symbol[][]::new);
			MutableInt row=new MutableInt(move.startPoint.row);
			MutableInt column=new MutableInt(move.startPoint.column);
			int colour=data[row.intValue()][column.intValue()].colour;
			for (Arrow arrow:move.directions)	{
				for (int i=0;i<arrow.steps;++i)	{
					arrow.direction.doStep(row,column);
					if (stateCopy[row.intValue()][column.intValue()]!=null) stateCopy[row.intValue()][column.intValue()]=stateCopy[row.intValue()][column.intValue()].changeColour(colour);
				}
			}
			return new Grid(stateCopy);
		}
		public List<Move> getPossibleMoves()	{
			Direction[] dirs=Direction.values();
			List<Move> result=new ArrayList<>();
			for (int i=0;i<data.length;++i) for (int j=0;j<data[i].length;++j) if (data[i][j]!=null) for (Direction d:dirs)	{
				MoveFinder finder=new MoveFinder(i,j,d);
				finder.findMoves(result);
			}
			return result;
		}
		public List<Move> solve()	{
			Set<Grid> visited=new HashSet<>();
			Deque<Move> currentMoves=new ArrayDeque<>();
			visited.add(this);
			return solve(visited,currentMoves);
		}
		private List<Move> solve(Set<Grid> visited,Deque<Move> currentMoves)	{
			// DFS. Theoretically there could be infinite loops?
			if (isFinished()) return new ArrayList<>(currentMoves);
			for (Move move:getPossibleMoves()) {
				Grid newState=doMove(move);
				if (visited.contains(newState)) continue;
				visited.add(newState);
				currentMoves.addLast(move);
				List<Move> result=newState.solve(visited,currentMoves);
				if (result!=null) return result;
				currentMoves.removeLast();
			}
			return null;
		}
	}
	
	public static void main(String[] args)	{
		// String[] lines=new String[] {"S1 * C2 C1","C2 * S3 *","S3 C1 S4 S4"};
		String[] lines=new String[] {"S1 S2 C3 C4",
									 "C4 *  *  *",
									 "C3 C5 C3 *",
									 "S1 C2 C5 *",
									 "*  S1 S3 C4"};
		Grid grid=new Grid(lines);
		List<Move> solution=grid.solve();
		System.out.println(solution);
	}
}