package com.euler;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import com.euler.common.EulerUtils.Pair;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;

public class Euler793_5 {
	private final static Color BACKGROUND_COLOUR=new Color(112,64,0);
	
	private static enum PointClass	{
		MEDIAN(Color.RED),UNNEEDED(Color.BLUE),BELOW_MEDIAN(Color.GREEN),ABOVE_MEDIAN(Color.YELLOW);
		
		private final Color colour;
		private PointClass(Color colour)	{
			this.colour=colour;
		}
	}
	
	private final static int BLOCK_SIZE=10;
	
	private static class Visualisator extends Canvas	{
		private static final long serialVersionUID = 4181729096375041856l;
		
		private final Multimap<PointClass,Info> pointData;
		
		public Visualisator(int size,Multimap<PointClass,Info> pointData)	{
			this.pointData=pointData;
			int windowSize=(size+2)*BLOCK_SIZE;
			setPreferredSize(new Dimension(windowSize,windowSize));
			setBackground(BACKGROUND_COLOUR);
		}
		
		@Override
		public void paint(Graphics g)	{
			Graphics2D g2d=(Graphics2D)g;
			g2d.setPaintMode();
			for (Map.Entry<PointClass,Collection<Info>> colorData:pointData.asMap().entrySet())	{
				g2d.setColor(colorData.getKey().colour);
				for (Info p:colorData.getValue()) g2d.fillRect(BLOCK_SIZE*(1+p.y), BLOCK_SIZE*(1+p.x), BLOCK_SIZE, BLOCK_SIZE);
			}
		}
	}
	
	private static List<Info> getAllPoints(int n)	{
		long[] numbers=new long[n];
		numbers[0]=290797;
		for (int i=1;i<n;++i) numbers[i]=(numbers[i-1]*numbers[i-1])%50515093l;
		Arrays.sort(numbers);
		List<Info> result=new ArrayList<>();
		for (int x=0;x<n;++x) for (int y=x+1;y<n;++y) result.add(new Info(numbers[x]*numbers[y],x,y));
		return result;
	}
	
	private static Pair<List<Info>,List<Info>> filter(List<Info> points)	{
		int size=points.size();
		int cut=(size-1)/2;
		ObjIntMap<Info> lowerCount=HashObjIntMaps.newMutableMap();
		ObjIntMap<Info> upperCount=HashObjIntMaps.newMutableMap();
		for (int i=0;i<points.size();++i)	{
			Info a=points.get(i);
			for (int j=i+1;j<points.size();++j)	{
				Info b=points.get(j);
				if (a.isStrictlyLowerThan(b))	{
					lowerCount.addValue(a,1);
					upperCount.addValue(b,1);
				}	// No need to check b.isStrictlyLowerThan(a). By construction order, it can't happen.
			}
		}
		List<Info> filtered=new ArrayList<>();
		List<Info> valid=new ArrayList<>();
		for (Info p:points) if ((lowerCount.getOrDefault(p,0)<=cut)&&(upperCount.getOrDefault(p,0)<=cut)) valid.add(p);
		else filtered.add(p);
		return new Pair<>(filtered,valid);
	}
	
	private static class Info implements Comparable<Info>	{
		public final long number;
		public final int x;
		public final int y;
		public Info(long number,int x,int y)	{
			this.number=number;
			this.x=x;
			this.y=y;
		}
		@Override
		public int compareTo(Info other)	{
			return Long.compare(number,other.number);
		}
		public boolean isStrictlyLowerThan(Info other)	{
			return (x<=other.x)&&(y<=other.y);
		}
	}
	
	// Obvious realisation: I can work directly in (t,u,v) coordinates! I'm still retaining the "real" ones for the grid display.
	public static void main(String[] args)	{
		for (int i=19;i<=19;i+=4)	{
		// for (int i=3;i<=71;i+=4)	{
			JFrame window=new JFrame("Median experiments for Project Euler 793");
			window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			List<Info> allPoints=getAllPoints(i);
			Pair<List<Info>,List<Info>> filterResult=filter(allPoints);
			List<Info> filtered=filterResult.first;
			List<Info> valid=filterResult.second;
			valid.sort(null);
			int size=valid.size();
			int mainIndex=(size-1)/2;
			int leftIndex=mainIndex;
			while ((leftIndex>0)&&(valid.get(leftIndex-1).number==valid.get(leftIndex).number)) --leftIndex;
			int rightIndex=mainIndex;
			while ((rightIndex<valid.size()-1)&&(valid.get(rightIndex+1).number==valid.get(rightIndex).number)) ++rightIndex;
			Multimap<PointClass,Info> pointData=MultimapBuilder.enumKeys(PointClass.class).arrayListValues().build();
			pointData.putAll(PointClass.UNNEEDED,filtered);
			pointData.putAll(PointClass.BELOW_MEDIAN,valid.subList(0,leftIndex));
			pointData.putAll(PointClass.MEDIAN,valid.subList(leftIndex,1+rightIndex));
			pointData.putAll(PointClass.ABOVE_MEDIAN,valid.subList(1+rightIndex,valid.size()));
			Visualisator viewer=new Visualisator(i,pointData);
			window.add(viewer);
			window.pack();
			window.setLocationRelativeTo(null);
			window.setVisible(true);
			
		}
	}
}
