package com.euler;

import java.util.ArrayList;
import java.util.List;

import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;

public class Euler793_4 {
	private static class Point	{
		public final int x;
		public final int y;
		public Point(int x,int y)	{
			this.x=x;
			this.y=y;
		}
		public boolean isStrictlyLowerThan(Point other)	{
			return (x<=other.x)&&(y<=other.y);
		}
	}
	
	private static List<Point> getAllPoints(int n)	{
		List<Point> result=new ArrayList<>();
		for (int x=0;x<n;++x) for (int y=x+1;y<n;++y) result.add(new Point(x,y));
		return result;
	}
	
	private static boolean iterate(List<Point> points)	{
		int size=points.size();
		int cut=(size-1)/2;
		ObjIntMap<Point> lowerCount=HashObjIntMaps.newMutableMap();
		ObjIntMap<Point> upperCount=HashObjIntMaps.newMutableMap();
		for (int i=0;i<points.size();++i)	{
			Point a=points.get(i);
			for (int j=i+1;j<points.size();++j)	{
				Point b=points.get(j);
				if (a.isStrictlyLowerThan(b))	{
					lowerCount.addValue(a,1);
					upperCount.addValue(b,1);
				}	// No need to check b.isStrictlyLowerThan(a). By construction order, it can't happen.
			}
		}
		points.removeIf((Point p)->(lowerCount.getOrDefault(p,0)>cut)||(upperCount.getOrDefault(p,0)>cut));
		int newSize=points.size();
		if ((newSize%2)==0) throw new RuntimeException("Esto sí que no me lo esperaba.");
		else return newSize<size;
	}
	
	public static void main(String[] args)	{
		for (int i=3;i<=503;i+=4)	{
			StringBuilder sb=new StringBuilder();
			List<Point> ps=getAllPoints(i);
			sb.append("N=").append(i).append(": ").append(ps.size());
			while (iterate(ps)) sb.append(" => ").append(ps.size());
			System.out.println(sb.toString());
		}
	}
}
