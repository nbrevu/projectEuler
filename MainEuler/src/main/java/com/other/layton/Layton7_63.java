package com.other.layton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.other.layton.search.BreadthSearch;
import com.other.layton.search.ProblemStatus;
import com.other.layton.search.SearchPath;

public class Layton7_63 {
	private final static int DIGITS=5;
	
	private enum Colour	{
		RED,BLUE,PINK,YELLOW,EMPTY;
	}
	
	private static class Move7_63	{
		private final static String LEFT="LEFT";
		private final static String RIGHT="RIGHT";
		public final boolean leftToRight;
		public final int sourceDigit;
		public final int targetDigit;
		private Move7_63(boolean leftToRight,int sourceDigit,int targetDigit)	{
			this.leftToRight=leftToRight;
			this.sourceDigit=sourceDigit;
			this.targetDigit=targetDigit;
		}
		@Override
		public String toString()	{
			String source=leftToRight?LEFT:RIGHT;
			String target=leftToRight?RIGHT:LEFT;
			return "["+source+sourceDigit+"] => ["+target+targetDigit+"]";
		}
		public static List<Move7_63> getAllMoves()	{
			List<Move7_63> result=new ArrayList<>();
			getAllMoves(result,true);
			getAllMoves(result,false);
			return result;
		}
		private static void getAllMoves(List<Move7_63> result,boolean leftToRight)	{
			for (int i=0;i<DIGITS;++i) for (int j=0;j<DIGITS;++j) result.add(new Move7_63(leftToRight,i,j));
		}
	}
	
	private static class Status7_63 implements ProblemStatus<Move7_63,Status7_63>	{
		private final static List<Move7_63> ALL_MOVES=Move7_63.getAllMoves();
		
		private final Colour[] leftHand;
		private final Colour[] rightHand;
		private final int hashCode;
		public Status7_63()	{
			leftHand=new Colour[DIGITS];
			rightHand=new Colour[DIGITS];
			leftHand[0]=Colour.EMPTY;
			leftHand[1]=Colour.RED;
			leftHand[2]=Colour.PINK;
			leftHand[3]=Colour.BLUE;
			leftHand[4]=Colour.YELLOW;
			rightHand[0]=Colour.PINK;
			rightHand[1]=Colour.BLUE;
			rightHand[2]=Colour.YELLOW;
			rightHand[3]=Colour.RED;
			rightHand[4]=Colour.EMPTY;
			hashCode=calculateHashCode();
		}
		private Status7_63(Colour[] leftHand,Colour[] rightHand)	{
			this.leftHand=leftHand;
			this.rightHand=rightHand;
			hashCode=calculateHashCode();
		}
		@Override
		public boolean isFinal() {
			return (leftHand[0]==Colour.RED)&&(leftHand[4]==Colour.BLUE)&&(rightHand[0]==Colour.BLUE)&&(rightHand[4]==Colour.RED);
		}
		@Override
		public List<Move7_63> availableMoves() {
			List<Move7_63> result=new ArrayList<>();
			for (Move7_63 move:ALL_MOVES) if (canMove(move)) result.add(move);
			return result;
		}
		@Override
		public Status7_63 move(Move7_63 move) {
			Colour[] newLeftHand=Arrays.copyOf(leftHand,DIGITS);
			Colour[] newRightHand=Arrays.copyOf(rightHand,DIGITS);
			if (move.leftToRight)	{
				Colour moving=newLeftHand[move.sourceDigit];
				newLeftHand[move.sourceDigit]=Colour.EMPTY;
				newRightHand[move.targetDigit]=moving;
			}	else	{
				Colour moving=newRightHand[move.sourceDigit];
				newRightHand[move.sourceDigit]=Colour.EMPTY;
				newLeftHand[move.targetDigit]=moving;
			}
			return new Status7_63(newLeftHand,newRightHand);
		}
		private boolean canMove(Move7_63 move)	{
			Colour[] sourceHand=move.leftToRight?leftHand:rightHand;
			Colour sourceColour=sourceHand[move.sourceDigit];
			if (sourceColour==Colour.EMPTY) return false;
			Colour[] targetHand=move.leftToRight?rightHand:leftHand;
			Colour targetColour=targetHand[move.targetDigit];
			if (targetColour!=Colour.EMPTY) return false;
			if (move.targetDigit>0)	{
				if (targetHand[move.targetDigit-1]==sourceColour) return false;
			}
			if (move.targetDigit<DIGITS-1)	{
				if (targetHand[move.targetDigit+1]==sourceColour) return false;
			}
			return true;
		}
		private int calculateHashCode()	{
			int result=0;
			int multiplier=1;
			for (int i=0;i<DIGITS;++i)	{
				result+=leftHand[i].ordinal()*multiplier;
				multiplier*=Colour.values().length;
			}
			for (int i=0;i<DIGITS;++i)	{
				result+=rightHand[i].ordinal()*multiplier;
				multiplier*=Colour.values().length;
			}
			return result;
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
		@Override
		public boolean equals(Object other)	{
			Status7_63 o=(Status7_63)other;
			return o.hashCode==hashCode;
		}
		@Override
		public String toString()	{
			return "{"+Arrays.toString(leftHand)+","+Arrays.toString(rightHand)+"}";
		}
	}
	
	public static void main(String[] args)	{
		Status7_63 initial=new Status7_63();
		BreadthSearch<Move7_63,Status7_63> search=new BreadthSearch<>(initial);
		Collection<SearchPath<Move7_63,Status7_63>> result=search.solve();
		if (result.isEmpty()) System.out.println("Leider habe ich keine L�sung gefunden!!!!!");
		else for (SearchPath<Move7_63,Status7_63> path:result) path.print(System.out);
	}
}
