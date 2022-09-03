package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.google.common.math.IntMath;

public class Euler359Simulation {
	// Look at http://blog.cyclopsgroup.org/2011/12/project-euler-359-hilberts-new-hotel.html.
	private static class Floor	{
		private int id;
		private List<Integer> contents;
		public Floor(int id,int first)	{
			this.id=id;
			contents=new ArrayList<>();
			contents.add(first);
		}
		public boolean accept(int number)	{
			int latest=contents.get(contents.size()-1);
			if (isSquare(number+latest))	{
				contents.add(number);
				return true;
			}
			return false;
		}
		private static boolean isSquare(int n)	{
			int sq=IntMath.sqrt(n,RoundingMode.FLOOR);
			return (sq*sq)==n;
		}
		@Override
		public String toString()	{
			return "Floor "+id+": "+contents;
		}
	}
	
	private static class Hotel	{
		private int lastId;
		List<Floor> floors;
		public Hotel()	{
			lastId=0;
			floors=new ArrayList<>();
		}
		public void addGuest(int number)	{
			for (Floor f:floors) if (f.accept(number)) return;
			++lastId;
			floors.add(new Floor(lastId,number));
		}
		@Override
		public String toString()	{
			StringBuilder builder=new StringBuilder();
			boolean first=true;
			for (Floor f:floors)	{
				if (first) first=false;
				else builder.append(System.getProperty("line.separator"));
				builder.append(f.toString());
			}
			return builder.toString();
		}
	}
	
	public static void main(String[] args)	{
		Hotel h=new Hotel();
		for (int i=1;i<=1000;++i) h.addGuest(i);
		System.out.println(h.toString());
	}
}
