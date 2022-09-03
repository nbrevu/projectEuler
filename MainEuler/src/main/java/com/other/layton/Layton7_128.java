package com.other.layton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.other.layton.search.BreadthSearch;
import com.other.layton.search.ProblemStatus;
import com.other.layton.search.SearchPath;

public class Layton7_128 {
	private final static int SIZE=7;
	
	private enum Move	{
		LEFT1(0,-1),RIGHT1(0,1),LEFT2(1,-2),RIGHT2(1,2),LEFT3(2,-3),RIGHT3(2,3);
		public final int rocket;
		public final int move;
		Move(int rocket,int move)	 {
			this.rocket=rocket;
			this.move=move;
		}
	}
	
	private static class Status128 implements ProblemStatus<Move,Status128>	{
		private final int[] positions;
		private int hashCode;
		public Status128()	{
			positions=new int[]{2,3,4};
			hashCode=calculateHashCode();
		}
		private Status128(int[] positions)	{
			this.positions=positions;
			hashCode=calculateHashCode();
		}
		@Override
		public boolean isFinal() {
			return (positions[0]==1+positions[1])&&(positions[1]==1+positions[2]);
		}
		@Override
		public List<Move> availableMoves() {
			List<Move> result=new ArrayList<>();
			for (Move move:Move.values()) if (canMove(move)) result.add(move);
			return result;
		}
		@Override
		public Status128 move(Move move) {
			int[] newPos=Arrays.copyOf(positions,3);
			newPos[move.rocket]+=move.move;
			return new Status128(newPos);
		}
		private int calculateHashCode()	{
			int result=0;
			int multiplier=1;
			for (int i=0;i<3;++i)	{
				result+=positions[i]*multiplier;
				multiplier*=SIZE;
			}
			return result;
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
		@Override
		public boolean equals(Object other)	{
			Status128 s=(Status128)other;
			return s.hashCode==hashCode;
		}
		@Override
		public String toString()	{
			char[] result=new char[SIZE];
			for (int i=0;i<SIZE;++i) result[i]='_';
			result[positions[0]]='1';
			result[positions[1]]='2';
			result[positions[2]]='3';
			return new String(result);
		}
		private boolean canMove(Move move)	{
			int newPos=positions[move.rocket]+move.move;
			return inBounds(newPos)&&!occupied(newPos);
		}
		private boolean inBounds(int pos)	{
			return (pos>=0)&&(pos<SIZE);
		}
		private boolean occupied(int pos)	{
			for (int i=0;i<3;++i) if (pos==positions[i]) return true;
			return false;
		}
	}
	
	public static void main(String[] args)	{
		Status128 initial=new Status128();
		BreadthSearch<Move,Status128> search=new BreadthSearch<>(initial);
		Collection<SearchPath<Move,Status128>> result=search.solve();
		if (result.isEmpty()) System.out.println("Leider habe ich keine Lösung gefunden!!!!!");
		else for (SearchPath<Move,Status128> path:result) path.print(System.out);
	}
}
