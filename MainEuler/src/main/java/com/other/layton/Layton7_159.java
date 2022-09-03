package com.other.layton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.other.layton.search.BreadthSearch;
import com.other.layton.search.ProblemStatus;
import com.other.layton.search.SearchPath;

public class Layton7_159 {
	/*
	 * 1 2 3 4
	 * 5 6 7 8
	 * By the way, this code is horrible, but eh, it does what it's meant to do.
	 */
	private enum Ligature	{
		// Note that 1-2 is absent.
		L23(2,3),L34(3,4),L56(5,6),L67(6,7),L78(7,8),L15(1,5),L26(2,6),L37(3,7),L48(4,8);
		public final int n1,n2;
		Ligature(int n1,int n2)	{
			this.n1=n1;
			this.n2=n2;
		}
	}
	private static class Status159 implements ProblemStatus<Ligature,Status159>	{
		private final int posA,posB,posC,posD;
		private final int hashCode;
		public Status159()	{
			posA=4;
			posB=3;
			posC=2;
			posD=1;
			hashCode=calculateHashCode();
		}
		private Status159(int posA,int posB,int posC,int posD)	{
			this.posA=posA;
			this.posB=posB;
			this.posC=posC;
			this.posD=posD;
			hashCode=calculateHashCode();
		}
		private int calculateHashCode()	{
			int result=0;
			int multiplier=1;
			result+=posA-1;
			multiplier*=8;
			result+=(posB-1)*multiplier;
			multiplier*=8;
			result+=(posC-1)*multiplier;
			multiplier*=8;
			result+=(posD-1)*multiplier;
			return result;
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
		@Override
		public boolean equals(Object other)	{
			Status159 s=(Status159)other;
			return s.hashCode==hashCode;
		}
		@Override
		public String toString()	{
			return "A="+posA+", B="+posB+", C="+posC+", D="+posD;
		}
		@Override
		public boolean isFinal() {
			// return (posA==1)&&(posB==2)&&(posC==3)&&(posD==4);
			return ((posA%4)==1)&&((posB%4)==2)&&((posC%4)==3)&&((posD%4)==0);
		}
		@Override
		public List<Ligature> availableMoves() {
			int[] vacants=getVacants();
			List<Ligature> result=new ArrayList<>();
			for (Ligature l:Ligature.values()) if (((vacants[l.n1]==0)&&(vacants[l.n2]!=0))||((vacants[l.n1]!=0)&&(vacants[l.n2]==0))) result.add(l);
			return result;
		}
		@Override
		public Status159 move(Ligature move) {
			int[] vacants=getVacants();
			int pos1=vacants[move.n1];
			int pos2=vacants[move.n2];
			int source,target;
			if (pos1==0)	{
				if (pos2==0) throw new IllegalArgumentException();
				source=vacants[move.n2];
				target=move.n1;
			}	else	{
				if (pos2!=0) throw new IllegalArgumentException();
				source=vacants[move.n1];
				target=move.n2;
			}
			int newA=posA;
			int newB=posB;
			int newC=posC;
			int newD=posD;
			switch (source)	{
				case 1:newA=target;break;
				case 2:newB=target;break;
				case 3:newC=target;break;
				case 4:newD=target;break;
				default:throw new IllegalArgumentException();
			}
			return new Status159(newA,newB,newC,newD);
		}
		private int[] getVacants()	{
			int[] result=new int[9];
			result[posA]=1;
			result[posB]=2;
			result[posC]=3;
			result[posD]=4;
			return result;
		}
	}
	
	public static void main(String[] args)	{
		Status159 initial=new Status159();
		BreadthSearch<Ligature,Status159> search=new BreadthSearch<>(initial);
		Collection<SearchPath<Ligature,Status159>> result=search.solve();
		if (result.isEmpty()) System.out.println("Leider habe ich keine Lösung gefunden!!!!!");
		else for (SearchPath<Ligature,Status159> path:result) path.print(System.out);
	}
}
