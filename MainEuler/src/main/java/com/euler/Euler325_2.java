package com.euler;

import java.io.IOException;
import java.io.PrintStream;

public class Euler325_2 {
	private final static int SIZE=10001;
	
	private static class Configurations	{
		private final boolean[][] winning;
		
		public Configurations(int size)	{
			winning=new boolean[size][size];
		}
		
		public boolean isWinning(int x,int y)	{
			return (x<y)?winning[x][y]:winning[y][x];
		}
		
		public void markAsWinning(int x,int y)	{
			winning[x][y]=true;
		}
	}
	
	public static void main(String[] args)	{
		try (PrintStream ps=new PrintStream("C:\\out325.txt"))	{
			Configurations confs=new Configurations(SIZE);
			long sum=0;
			for (int i=1;i<SIZE-1;++i) for (int j=i+1;j<SIZE;++j)	{
				if ((j%i)==0)	{
					// Winning configuration.
					confs.markAsWinning(i,j);
				}	else	{
					boolean isLosing=true;
					for (int k=i;k<j;k+=i) if (!confs.isWinning(i,j-k))	{
						isLosing=false;
						break;
					}
					if (!isLosing) confs.markAsWinning(i,j);
					else	{
						ps.println(""+i+", "+j);
						sum+=i+j;
					}
				}
			}
			ps.println(sum);
		}	catch (IOException exc)	{
			System.out.println("No puedo entender algo como esto.");
		}
	}
}
