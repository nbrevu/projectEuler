package com.euler;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Euler244 {
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
				colours[i][0]=false;
				colours[i][1]=false;
				colours[i][2]=true;
				colours[i][3]=true;
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
	
	private static class ExtendedInfo	{
		private Configuration conf;
		private List<Long> checksums;
		public ExtendedInfo(Configuration conf)	{
			this.conf=conf;
			checksums=new ArrayList<>();
		}
		public void addCheckSum(long cs)	{
			checksums.add(cs);
		}
		public void addCheckSums(List<Long> checksums)	{
			this.checksums.addAll(checksums);
		}
		public List<Long> getChecksums()	{
			return checksums;
		}
		public List<Long> getExtendedChecksums(Direction d)	{
			List<Long> result=new ArrayList<>(checksums.size());
			for (long l:checksums) result.add((243*l+d.getId())%100000007l);
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
				childInfo.addCheckSums(info.getExtendedChecksums(entry.getKey()));
			}
		}
		return result;
	}
	
	public static void main(String[] args)	{
		Set<Integer> oldHashCodes=new HashSet<>();
		Map<Integer,ExtendedInfo> currentGen=new HashMap<>();
		Configuration baseConf=Configuration.getStartingConfiguration();
		int goalHashCode=Configuration.getGoalConfiguration().hashCode();
		System.out.println("Original position: "+baseConf.hashCode());
		System.out.println("Goal position: "+goalHashCode);
		ExtendedInfo baseInfo=new ExtendedInfo(baseConf);
		baseInfo.addCheckSum(0);
		currentGen.put(baseConf.hashCode(),baseInfo);
		for (int s=0;;++s)	{
			System.out.println("I've done "+s+" steps and I have "+currentGen.size()+" different positions. I have "+oldHashCodes.size()+" old positions.");
			if (currentGen.size()==0) throw new RuntimeException("Nop.");
			Map<Integer,ExtendedInfo> nextGen=getNextGeneration(currentGen,oldHashCodes);
			oldHashCodes.addAll(currentGen.keySet());
			if (nextGen.containsKey(goalHashCode))	{
				ExtendedInfo goalInfo=nextGen.get(goalHashCode);
				long sum=0;
				System.out.println(goalInfo.getChecksums().size()+" paths found.");
				for (long cs:goalInfo.getChecksums()) sum+=cs;
				System.out.println(sum+".");
				return;
			}
			currentGen=nextGen;
		}
	}
}
