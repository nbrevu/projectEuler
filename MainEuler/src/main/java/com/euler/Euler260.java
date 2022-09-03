package com.euler;

public class Euler260 {
	private final static int SIZE=1001;
	
	private static enum Filler	{
		X	{
			@Override
			public void fill(boolean[][][] winningConfigurations,int x,int y,int z)	{
				int max=SIZE-x-1;
				for (int i=1;i<=max;++i) winningConfigurations[x+i][y][z]=true;
			}
		},
		Y	{
			@Override
			public void fill(boolean[][][] winningConfigurations,int x,int y,int z)	{
				int max=SIZE-y-1;
				for (int i=1;i<=max;++i) winningConfigurations[x][y+i][z]=true;
			}
		},
		Z	{
			@Override
			public void fill(boolean[][][] winningConfigurations,int x,int y,int z)	{
				int max=SIZE-z-1;
				for (int i=1;i<=max;++i) winningConfigurations[x][y][z+i]=true;
			}
		},
		XY	{
			@Override
			public void fill(boolean[][][] winningConfigurations,int x,int y,int z)	{
				int max=SIZE-Math.max(x,y)-1;
				for (int i=1;i<=max;++i) winningConfigurations[x+i][y+i][z]=true;
			}
		},
		XZ	{
			@Override
			public void fill(boolean[][][] winningConfigurations,int x,int y,int z)	{
				int max=SIZE-Math.max(x,z)-1;
				for (int i=1;i<=max;++i) winningConfigurations[x+i][y][z+i]=true;
			}
		},
		YZ	{
			@Override
			public void fill(boolean[][][] winningConfigurations,int x,int y,int z)	{
				int max=SIZE-Math.max(y,z)-1;
				for (int i=1;i<=max;++i) winningConfigurations[x][y+i][z+i]=true;
			}
		},
		XYZ	{
			@Override
			public void fill(boolean[][][] winningConfigurations,int x,int y,int z)	{
				int max=SIZE-Math.max(Math.max(x,y),z)-1;
				for (int i=1;i<=max;++i) winningConfigurations[x+i][y+i][z+i]=true;
			}
		};
		
		public abstract void fill(boolean[][][] winningConfigurations,int x,int y,int z);
	}
	
	private static void markAsLosing(boolean[][][] winningConfigurations,int x,int y,int z)	{
		for (Filler f:Filler.values()) f.fill(winningConfigurations,x,y,z);
	}
	
	public static void main(String[] args)	{
		boolean winningConfigurations[][][]=new boolean[SIZE][SIZE][SIZE];
		for (int i=0;i<SIZE;++i) for (int j=0;j<SIZE;++j) for (int k=0;k<SIZE;++k) if (!winningConfigurations[i][j][k]) markAsLosing(winningConfigurations,i,j,k);
		long count=0;
		for (int i=0;i<SIZE;++i) for (int j=i;j<SIZE;++j) for (int k=j;k<SIZE;++k) if (!winningConfigurations[i][j][k]) count+=i+j+k;
		System.out.println(count);
	}
}
