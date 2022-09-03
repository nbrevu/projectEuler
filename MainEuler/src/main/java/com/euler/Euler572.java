package com.euler;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Euler572 {
	// 164, 848, 1928, 3752
	private final static int N=3;
	
	private static void square(int[][] orig,int[][] res)	{
		for (int i=0;i<3;++i) for (int j=0;j<3;++j)	{
			res[i][j]=0;
			for (int k=0;k<3;++k) res[i][j]+=orig[i][k]*orig[k][j];
		}
	}
	
	private static boolean equals(int[][] m1,int[][] m2)	{
		for (int i=0;i<3;++i) for (int j=0;j<3;++j) if (m1[i][j]!=m2[i][j]) return false;
		return true;
	}
	
	private static void printMat(int[][] mat,PrintStream out)	{
		for (int i=0;i<3;++i)	{
			out.print('[');
			boolean first=true;
			for (int j=0;j<3;++j)	{
				if (first) first=false;
				else out.print(' ');
				if (mat[i][j]>=0) out.print(' ');
				out.print(mat[i][j]);
			}
			out.println(']');
		}
		out.println();
	}
	
	private static class Trace	{
		private final int a,b,c;
		public Trace(int a,int b,int c)	{
			this.a=a;
			this.b=b;
			this.c=c;
		}
		@Override
		public int hashCode()	{
			return 7*a+11*b+13*c;
		}
		@Override
		public boolean equals(Object other)	{
			Trace tOther=(Trace)other;
			return (tOther.a==a)&&(tOther.b==b)&&(tOther.c==c);
		}
		@Override
		public String toString()	{
			return "["+a+","+b+","+c+"]";
		}
	}
	
	public static void main(String[] args)	{
		Map<Trace,Integer> traceCounter=new HashMap<>();
		try (PrintStream ps=new PrintStream("D:\\out572_"+N+".txt"))	{
			int count=0;
			int[][] mat=new int[3][3];
			int[][] second=new int[3][3];
			for (mat[0][0]=-N;mat[0][0]<=N;++mat[0][0]) for (mat[0][1]=-N;mat[0][1]<=N;++mat[0][1]) for (mat[0][2]=-N;mat[0][2]<=N;++mat[0][2]) for (mat[1][0]=-N;mat[1][0]<=N;++mat[1][0]) for (mat[1][1]=-N;mat[1][1]<=N;++mat[1][1]) for (mat[1][2]=-N;mat[1][2]<=N;++mat[1][2]) for (mat[2][0]=-N;mat[2][0]<=N;++mat[2][0]) for (mat[2][1]=-N;mat[2][1]<=N;++mat[2][1]) for (mat[2][2]=-N;mat[2][2]<=N;++mat[2][2])	{
				square(mat,second);
				if (equals(mat,second))	{
					printMat(mat,ps);
					Trace tr=new Trace(mat[0][0],mat[1][1],mat[2][2]);
					Integer oldValue=traceCounter.get(tr);
					int newValue=1+((oldValue==null)?0:oldValue.intValue());
					traceCounter.put(tr,newValue);
					++count;
				}
			}
			ps.println("Traces: ");
			Map<Integer,List<Trace>> grouped=new HashMap<>();
			for (Map.Entry<Trace,Integer> entry:traceCounter.entrySet())	{
				Trace tr=entry.getKey();
				Integer howMany=entry.getValue();
				ps.println(tr.toString()+" => "+howMany);
				List<Trace> group=grouped.get(howMany);
				if (group==null)	{
					group=new ArrayList<>();
					grouped.put(howMany,group);
				}
				group.add(tr);
			}
			ps.println("Grouped traces: ");
			for (Map.Entry<Integer,List<Trace>> entry:grouped.entrySet()) ps.println(entry.getKey().toString()+" => "+entry.getValue().toString());
			System.out.println("Found "+count+" matrices.");
		}	catch (IOException exc)	{
			System.out.println("D'oh! I/O Error: "+exc.getMessage());
		}
	}
}
