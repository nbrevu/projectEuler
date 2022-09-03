package com.other.layton;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.other.layton.search.BreadthSearch;
import com.other.layton.search.ProblemStatus;
import com.other.layton.search.SearchPath;

public class Layton7_182 {
	private final static int HEIGHT=5;
	private final static int WIDTH=5;
	
	private enum Move	{
		RIGHT,UP,LEFT,DOWN;
	}
	
	private static class DogPos	{
		public final int pos;
		public final boolean facingRight;
		public DogPos(int pos,boolean facingRight)	{
			this.pos=pos;
			this.facingRight=facingRight;
		}
		@Override
		public int hashCode()	{
			return pos+(facingRight?WIDTH:0);
		}
		@Override
		public boolean equals(Object other)	{
			DogPos d=(DogPos)other;
			return d.hashCode()==hashCode();
		}
	}
	
	private static class Status182 implements ProblemStatus<Move,Status182>	{
		private Point handPos;
		private DogPos[] dogs;
		public Status182()	{
			handPos=new Point(3,0);
			dogs=new DogPos[HEIGHT];
			dogs[0]=new DogPos(0,true);
			dogs[1]=new DogPos(4,false);
			dogs[2]=new DogPos(0,true);
			dogs[3]=new DogPos(4,false);
			dogs[4]=new DogPos(0,true);
		}
		private Status182(Point handPos,DogPos[] dogs)	{
			this.handPos=handPos;
			this.dogs=dogs;
		}
		@Override
		public int hashCode()	{
			int result=0;
			int multiplier=1;
			for (int i=0;i<HEIGHT;++i)	{
				result+=dogs[i].hashCode()*multiplier;
				multiplier*=2*WIDTH;
			}
			result+=handPos.x*multiplier;
			multiplier*=WIDTH;
			result+=handPos.y*multiplier;
			return result;
		}
		@Override
		public boolean equals(Object other)	{
			Status182 s=(Status182)other;
			return s.hashCode()==hashCode();
		}
		@Override
		public String toString()	{
			return "["+handPos.y+","+handPos.x+"]";
		}
		@Override
		public boolean isFinal() {
			int pos=dogs[0].pos;
			for (int i=1;i<HEIGHT;++i) if (dogs[i].pos!=pos) return false;
			return true;
		}
		@Override
		public List<Move> availableMoves() {
			List<Move> result=new ArrayList<>();
			if (handPos.x<WIDTH-1)	{
				int curX=handPos.x;
				int dogX=dogs[handPos.y].pos;
				if (dogX!=(1+curX)) result.add(Move.RIGHT);
			}
			if (handPos.y>0)	{
				int curX=handPos.x;
				int dogX=dogs[handPos.y-1].pos;
				if (dogX!=curX) result.add(Move.UP);
			}
			if (handPos.x>0)	{
				int curX=handPos.x;
				int dogX=dogs[handPos.y].pos;
				if (dogX!=(curX-1)) result.add(Move.LEFT);
			}
			if (handPos.y<HEIGHT-1)	{
				int curX=handPos.x;
				int dogX=dogs[handPos.y+1].pos;
				if (dogX!=curX) result.add(Move.DOWN);
			}
			return result;
		}
		@Override
		public Status182 move(Move move) {
			Point newPos;
			switch (move)	{
				case RIGHT:newPos=new Point(handPos.x+1,handPos.y);break;
				case UP:newPos=new Point(handPos.x,handPos.y-1);break;
				case LEFT:newPos=new Point(handPos.x-1,handPos.y);break;
				case DOWN:newPos=new Point(handPos.x,handPos.y+1);break;
				default:throw new IllegalArgumentException("No.");
			}
			DogPos[] newDogs=new DogPos[HEIGHT];
			for (int i=0;i<HEIGHT;++i) if (i==newPos.y)	{
				if (dogs[i].facingRight) newDogs[i]=new DogPos(newPos.x-1,true);
				else newDogs[i]=new DogPos(newPos.x+1,false);
			}	else	{
				if (dogs[i].facingRight) newDogs[i]=new DogPos(WIDTH-1,false);
				else newDogs[i]=new DogPos(0,true);
			}
			return new Status182(newPos,newDogs);
		}
	}

	public static void main(String[] args)	{
		Status182 initial=new Status182();
		BreadthSearch<Move,Status182> search=new BreadthSearch<>(initial);
		Collection<SearchPath<Move,Status182>> result=search.solve();
		if (result.isEmpty()) System.out.println("Leider habe ich keine Lösung gefunden!!!!!");
		else for (SearchPath<Move,Status182> path:result) path.print(System.out);
	}
}
