package com.other.layton;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.other.layton.search.BreadthSearch;
import com.other.layton.search.ProblemStatus;
import com.other.layton.search.SearchPath;

public class Layton7_185 {
	private final static int SIZE=5;
	private final static Point[][] ALL_POINTS=new Point[2][5];
	private final static List<Point> POINTS_LIST=new ArrayList<>();
	
	static	{
		for (int i=0;i<2;++i) for (int j=0;j<SIZE;++j)	{
			Point p=new Point(j,i);
			ALL_POINTS[i][j]=p;
			POINTS_LIST.add(p);
		}
	}
	
	private static class Move	{
		public final Point pos1,pos2;
		private Move(Point pos1,Point pos2)	{
			this.pos1=pos1;
			this.pos2=pos2;
		}
		public static List<Move> getAllMoves()	{
			List<Move> result=new ArrayList<>();
			for (int i=0;i<SIZE-1;++i)	{
				result.add(new Move(ALL_POINTS[0][i],ALL_POINTS[0][i+1]));
				result.add(new Move(ALL_POINTS[1][i],ALL_POINTS[1][i+1]));
			}
			for (int i=0;i<SIZE;++i) result.add(new Move(ALL_POINTS[0][i],ALL_POINTS[1][i]));
			return result;
		}
		@Override
		public String toString()	{
			return "["+pos1.y+","+pos1.x+"]<->["+pos2.y+","+pos2.x+"]";
		}
	}
	
	private static enum ElementType	{
		TURTLE,GATE;
	}
	
	private static class Element	{
		public final ElementType type;
		public final int pos;
		private static Element[] turtles=getElements(ElementType.TURTLE);
		private static Element[] gates=getElements(ElementType.GATE);
		private Element(ElementType type,int pos)	{
			this.type=type;
			this.pos=pos;
		}
		private static Element[] getElements(ElementType type)	{
			Element[] result=new Element[SIZE];
			for (int i=0;i<SIZE;++i) result[i]=new Element(type,i);
			return result;
		}
		public static Element[] getTurtles()	{
			return Arrays.copyOf(turtles,SIZE);
		}
		public static Element[] getGates()	{
			return Arrays.copyOf(gates,SIZE);
		}
	}
	
	private static class Status185 implements ProblemStatus<Move,Status185>	{
		private final Element[][] elements;
		private final List<Move> remainingMoves;
		public Status185()	{
			elements=new Element[2][SIZE];
			Element[] turtles=Element.getTurtles();
			Element[] gates=Element.getGates();
			elements[0][0]=turtles[0];
			elements[0][1]=turtles[2];
			elements[0][2]=turtles[4];
			elements[0][3]=turtles[3];
			elements[0][4]=turtles[1];
			elements[1][0]=gates[0];
			elements[1][1]=gates[2];
			elements[1][2]=gates[1];
			elements[1][3]=gates[4];
			elements[1][4]=gates[3];
			remainingMoves=Move.getAllMoves();
		}
		private Status185(Element[][] elements,List<Move> availableMoves)	{
			this.elements=elements;
			this.remainingMoves=availableMoves;
		}
		@Override
		public int hashCode()	{
			return Arrays.deepHashCode(elements)+997*remainingMoves.hashCode();
		}
		@Override
		public boolean equals(Object other)	{
			Status185 s=(Status185)other;
			return Arrays.deepEquals(elements,s.elements)&&remainingMoves.equals(s.remainingMoves);
		}
		@Override
		public boolean isFinal() {
			for (Point p:POINTS_LIST) if (!isPositionCorrect(p)) return false;
			return true;
		}
		@Override
		public List<Move> availableMoves() {
			// Return an empty list if there is an object in the wrong position which can't be moved any more.
			for (Point p:getLockedPoints()) if (!isPositionCorrect(p)) return Collections.emptyList();
			return remainingMoves;
		}
		@Override
		public Status185 move(Move move) {
			Element[][] newElements=duplicate(elements);
			List<Move> newAvailableMoves=new ArrayList<>(remainingMoves);
			newAvailableMoves.remove(move);
			Element p1=newElements[move.pos1.y][move.pos1.x];
			Element p2=newElements[move.pos2.y][move.pos2.x];
			newElements[move.pos2.y][move.pos2.x]=p1;
			newElements[move.pos1.y][move.pos1.x]=p2;
			return new Status185(newElements,newAvailableMoves);
		}
		private List<Point> getLockedPoints()	{
			List<Point> result=new ArrayList<>(POINTS_LIST);
			for (Move move:remainingMoves)	{
				result.remove(move.pos1);
				result.remove(move.pos2);
			}
			return result;
		}
		private boolean isPositionCorrect(Point p)	{
			Element element=elements[p.y][p.x];
			ElementType expectedType=(p.y==0)?ElementType.TURTLE:ElementType.GATE;
			return (element.pos==p.x)&&(element.type==expectedType);
		}
		private static Element[][] duplicate(Element[][] array)	{
			Element[][] result=new Element[2][SIZE];
			for (int i=0;i<2;++i) for (int j=0;j<SIZE;++j) result[i][j]=array[i][j];
			return result;
		}
	}

	public static void main(String[] args)	{
		Status185 initial=new Status185();
		BreadthSearch<Move,Status185> search=new BreadthSearch<>(initial);
		Collection<SearchPath<Move,Status185>> result=search.solve();
		if (result.isEmpty()) System.out.println("Leider habe ich keine Lösung gefunden!!!!!");
		else for (SearchPath<Move,Status185> path:result) path.print(System.out);
	}
}
