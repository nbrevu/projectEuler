package com.euler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Euler244_2 {
	private enum Direction	{
		L(76l),R(82l),U(85l),D(68l);
		private long id;
		Direction(long id)	{
			this.id=id;
		}
		public long getId()	{
			return id;
		}
	}
	
	private static class Configuration	{
		private int x,y;
		private boolean[][] colours;
		private Configuration(int x,int y,boolean[][] colours)	{
			this.x=x;
			this.y=y;
			this.colours=colours;	// No defensive copy.
		}
		@Override
		public int hashCode()	{
			int result=4*x+y;
			for (int i=0;i<4;++i) for (int j=0;j<4;++j)	{
				result*=2;
				if (colours[i][j]) ++result;
			}
			return result;
		}
		@Override
		public boolean equals(Object other)	{
			return (other instanceof Configuration)&&(other.hashCode()==this.hashCode());
		}
		public Map<Direction,Configuration> getChildren()	{
			Map<Direction,Configuration> result=new EnumMap<>(Direction.class);
			if (x<3) result.put(Direction.L,getLeftChild());
			if (x>0) result.put(Direction.R,getRightChild());
			if (y<3) result.put(Direction.U,getUpChild());
			if (y>0) result.put(Direction.D,getDownChild());
			return result;
		}
		private static boolean[][] duplicate(boolean[][] colours)	{
			boolean[][] result=new boolean[4][4];
			for (int i=0;i<4;++i) for (int j=0;j<4;++j) result[i][j]=colours[i][j];
			return result;
		}
		private Configuration getLeftChild()	{
			int newX=x+1;
			boolean[][] newColours=duplicate(colours);
			newColours[x][y]=colours[newX][y];
			newColours[newX][y]=colours[x][y];
			return new Configuration(newX,y,newColours);
		}
		private Configuration getRightChild()	{
			int newX=x-1;
			boolean[][] newColours=duplicate(colours);
			newColours[x][y]=colours[newX][y];
			newColours[newX][y]=colours[x][y];
			return new Configuration(newX,y,newColours);
		}
		private Configuration getUpChild()	{
			int newY=y+1;
			boolean[][] newColours=duplicate(colours);
			newColours[x][y]=colours[x][newY];
			newColours[x][newY]=colours[x][y];
			return new Configuration(x,newY,newColours);
		}
		private Configuration getDownChild()	{
			int newY=y-1;
			boolean[][] newColours=duplicate(colours);
			newColours[x][y]=colours[x][newY];
			newColours[x][newY]=colours[x][y];
			return new Configuration(x,newY,newColours);
		}
		public static Configuration getStartingConfiguration()	{
			boolean[][] colours=new boolean[4][4];
			for (int i=0;i<4;++i)	{
				colours[0][i]=false;
				colours[1][i]=false;
				colours[2][i]=true;
				colours[3][i]=true;
			}
			return new Configuration(0,0,colours);
		}
		public static Configuration getGoalConfiguration()	{
			boolean[][] colours=new boolean[4][4];
			for (int i=0;i<4;++i) for (int j=0;j<4;++j)	{
				boolean colour=((i+j)%2)==1;
				colours[i][j]=colour;
			}
			return new Configuration(0,0,colours);
		}
	}
	
	private static class Path	{
		private List<Direction> currentPath;
		public Path()	{
			currentPath=Collections.emptyList();
		}
		private Path(List<Direction> currentPath)	{
			this.currentPath=currentPath;
		}
		public Path child(Direction d)	{
			List<Direction> newList=new ArrayList<>(currentPath.size()+1);
			newList.addAll(currentPath);
			newList.add(d);
			return new Path(newList);
		}
		public long getChecksum()	{
			long result=0;
			for (Direction d:currentPath)	{
				result*=243l;
				result+=d.getId();
				result%=100000007l;
			}
			return result;
		}
		@Override
		public String toString()	{
			StringBuilder sb=new StringBuilder();
			sb.append('[');
			for (Direction d:currentPath) sb.append(d.name());
			sb.append(']');
			return sb.toString();
		}
	}
	
	private static class ExtendedInfo	{
		private Configuration conf;
		private List<Path> paths;
		public ExtendedInfo(Configuration conf)	{
			this.conf=conf;
			paths=new ArrayList<>();
		}
		public void useBasePath()	{
			paths.add(new Path());
		}
		public void addPaths(List<Path> paths)	{
			this.paths.addAll(paths);
		}
		public List<Path> getPaths()	{
			return paths;
		}
		public List<Path> getExtendedPaths(Direction d)	{
			List<Path> result=new ArrayList<>(paths.size());
			for (Path p:paths) result.add(p.child(d));
			return result;
		}
		private Map<Direction,Configuration> getChildren()	{
			return conf.getChildren();
		}
	}
	
	private static Map<Integer,ExtendedInfo> getNextGeneration(Map<Integer,ExtendedInfo> currentGen,Set<Integer> oldHashCodes)	{
		Map<Integer,ExtendedInfo> result=new HashMap<>();
		for (ExtendedInfo info:currentGen.values())	{
			Map<Direction,Configuration> newConfs=info.getChildren();
			for (Map.Entry<Direction,Configuration> entry:newConfs.entrySet())	{
				Configuration child=entry.getValue();
				int hash=child.hashCode();
				if (oldHashCodes.contains(hash)) continue;
				ExtendedInfo childInfo=result.get(hash);
				if (childInfo==null)	{
					childInfo=new ExtendedInfo(child);
					result.put(hash,childInfo);
				}
				childInfo.addPaths(info.getExtendedPaths(entry.getKey()));
			}
		}
		return result;
	}
	
	public static void main(String[] args)	{
		Set<Integer> oldHashCodes=new HashSet<>();
		Map<Integer,ExtendedInfo> currentGen=new HashMap<>();
		Configuration baseConf=Configuration.getStartingConfiguration();
		int goalHashCode=Configuration.getGoalConfiguration().hashCode();
		ExtendedInfo baseInfo=new ExtendedInfo(baseConf);
		baseInfo.useBasePath();
		currentGen.put(baseConf.hashCode(),baseInfo);
		for (;;)	{
			if (currentGen.size()==0) throw new RuntimeException("Nop.");
			Map<Integer,ExtendedInfo> nextGen=getNextGeneration(currentGen,oldHashCodes);
			oldHashCodes.addAll(currentGen.keySet());
			if (nextGen.containsKey(goalHashCode))	{
				ExtendedInfo goalInfo=nextGen.get(goalHashCode);
				long sum=0;
				for (Path p:goalInfo.getPaths()) sum+=p.getChecksum();
				System.out.println(sum+".");
				return;
			}
			currentGen=nextGen;
		}
	}
}
