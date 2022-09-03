package com.euler;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import com.euler.common.EulerUtils;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

public class Euler325 {
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
	
	private static class CondensedSubList	{
		public final int start;
		public final int end;
		public CondensedSubList(int start,int end)	{
			this.start=start;
			this.end=end;
		}
		@Override
		public String toString()	{
			if (start==end) return "["+start+"]";
			else return "["+start+"->"+end+"] {"+getSize()+"}";
		}
		public int getSize()	{
			return end+1-start;
		}
	}

	private static class CondensedList	{
		private final List<CondensedSubList> allLists;
		public CondensedList(Collection<Integer> numbers)	{
			allLists=condense(numbers);
		}
		@Override
		public String toString()	{
			switch (allLists.size())	{
			case 0:return "[]";
			case 1:return allLists.get(0).toString();
			default:return allLists.toString();
			}
		}
		private static List<CondensedSubList> condense(Collection<Integer> numbers)	{
			// "numbers" is assumed to be sorted.
			List<CondensedSubList> result=new ArrayList<>();
			Integer previous=null;
			int currentStart=0;
			int currentEnd=0;
			for (Integer n:numbers)	{
				if (previous==null)	{
					currentStart=n;
					currentEnd=n;
				}	else if (previous==(n-1)) currentEnd=n;
				else	{
					result.add(new CondensedSubList(currentStart,currentEnd));
					currentStart=n;
					currentEnd=n;
				}
				previous=n;
			}
			if (previous!=null) result.add(new CondensedSubList(currentStart,currentEnd));
			return result;
		}
		public CondensedSubList getSingleSubList()	{
			if (allLists.size()!=1) throw new NoSuchElementException();
			else return allLists.get(0);
		}
	}
	
	public static void main(String[] args)	{
		Multimap<Integer,Integer> results=TreeMultimap.create();
		try (PrintStream ps=new PrintStream("C:\\out325_2.txt","UTF-8"))	{
			Configurations confs=new Configurations(SIZE);
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
					else results.put(i,j);
				}
			}
			Map<Integer,CondensedList> processed=new TreeMap<>();
			for (Integer k:results.keySet())	{
				Collection<Integer> associated=results.get(k);
				CondensedList list=new CondensedList(associated);
				processed.put(k,list);
				ps.println(""+k+" => "+list.toString());
			}
			// Postprocesamiento...
			ps.println();
			Map<Integer,Integer> counter=new TreeMap<>();
			for (Map.Entry<Integer,CondensedList> entry:processed.entrySet())	{
				CondensedSubList subList=entry.getValue().getSingleSubList();
				if (subList.end>=SIZE-1) break;
				EulerUtils.increaseCounter(counter,subList.getSize());
			}
			List<Integer> positions=new ArrayList<>(); 
			for (Map.Entry<Integer,Integer> entry:counter.entrySet()) if (entry.getValue()==1)	{
				ps.println(""+entry.getKey()+" sólo aparece una vez.");
				positions.add(entry.getKey());
			}	else if (entry.getValue()!=2) throw new RuntimeException("Algo has hecho mal.");
			ps.println();
			ps.println("{"+positions.get(0)+"}");
			List<Integer> differences=new ArrayList<>();
			for (int i=1;i<positions.size();++i)	{
				int diff=positions.get(i)-positions.get(i-1);
				differences.add(diff);
				ps.println("{"+positions.get(i)+"} D="+diff+".");
			}
			ps.println();
			ps.println("Incoming ryhthm of threes and twos...");
			int threes=0;
			for (int diff:differences) if (diff==3) ++threes;
			else if (diff==2)	{
				ps.println(threes);
				threes=0;
			}	else throw new RuntimeException("BALLA NO ME LO EXPERABA.");
		}	catch (IOException exc)	{
			System.out.println("No puedo entender algo como esto.");
		}
	}
}
