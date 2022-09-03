package com.euler;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.euler.common.EulerUtils;

public class Euler682 {
	private final static int N=10_000_000;
	private final static long MOD=1_000_000_007l;
	
	private final static long INV2=(MOD+1)/2;
	private final static long INV3=EulerUtils.modulusInverse(3l,MOD);
	
	// Numbers with the same value of omega and s.
	private static class NumberGroup	{
		public long n2;
		public long n3;
		public long n5;
		public long n23;
		public long n25;
		public long n35;
		public long n235;
		public void normalise()	{
			n23=(n23*INV2)%MOD;
			n25=(n25*INV2)%MOD;
			n35=(n35*INV2)%MOD;
			n235=(n235*INV3)%MOD;
		}
		public long countAll()	{
			return (n2+n3+n5+n23+n25+n35+n235)%MOD;
		}
	}
	
	// Numbers with the same value of omega.
	private static class NumberCluster	{
		// The key is s.
		private final NavigableMap<Integer,NumberGroup> groups;
		public NumberCluster()	{
			groups=new TreeMap<>();
		}
		private void incorporate2(NumberCluster other)	{
			for (Map.Entry<Integer,NumberGroup> entry:other.groups.entrySet())	{
				NumberGroup source=entry.getValue();
				NumberGroup target=groups.computeIfAbsent(entry.getKey()+2,(Integer unused)->new NumberGroup());
				target.n2+=source.n2;
				target.n23+=source.n3+source.n23;
				target.n25+=source.n5+source.n25;
				target.n235+=source.n35+source.n235;
			}
		}
		private void incorporate3(NumberCluster other)	{
			for (Map.Entry<Integer,NumberGroup> entry:other.groups.entrySet())	{
				NumberGroup source=entry.getValue();
				NumberGroup target=groups.computeIfAbsent(entry.getKey()+3,(Integer unused)->new NumberGroup());
				target.n3+=source.n3;
				target.n23+=source.n2+source.n23;
				target.n35+=source.n5+source.n35;
				target.n235+=source.n25+source.n235;
			}
		}
		private void incorporate5(NumberCluster other)	{
			for (Map.Entry<Integer,NumberGroup> entry:other.groups.entrySet())	{
				NumberGroup source=entry.getValue();
				NumberGroup target=groups.computeIfAbsent(entry.getKey()+5,(Integer unused)->new NumberGroup());
				target.n5+=source.n5;
				target.n25+=source.n2+source.n25;
				target.n35+=source.n3+source.n35;
				target.n235+=source.n23+source.n235;
			}
		}
		public void normaliseAll()	{
			groups.values().forEach(NumberGroup::normalise);
		}
		public static NumberCluster init()	{
			NumberCluster result=new NumberCluster();
			NumberGroup n2=new NumberGroup();
			n2.n2=1;
			NumberGroup n3=new NumberGroup();
			n3.n3=1;
			NumberGroup n5=new NumberGroup();
			n5.n5=1;
			result.groups.put(2,n2);
			result.groups.put(3,n3);
			result.groups.put(5,n5);
			return result;
		}
		public NumberCluster evolve()	{
			NumberCluster result=new NumberCluster();
			result.incorporate2(this);
			result.incorporate3(this);
			result.incorporate5(this);
			result.normaliseAll();
			return result;
		}
		public boolean isTooFar()	{
			return groups.firstKey()*2>N;
		}
		public long getResults()	{
			long result=0;
			if (2*groups.lastKey()<N) return 0;
			for (Map.Entry<Integer,NumberGroup> entry:groups.entrySet())	{
				int s=entry.getKey();
				//if (2*s>N) break;
				NumberGroup other=groups.get(N-s);
				if (other==null) continue;
				result+=entry.getValue().countAll()*other.countAll();
				result%=MOD;
			}
			return result;
		}
	}
	
	// Works, but its far too slow.
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		NumberCluster currentGen=NumberCluster.init();
		long result=0;
		int Ω=1;
		while (!currentGen.isTooFar())	{
			++Ω;
			System.out.println("Ω="+Ω+"...");
			result+=currentGen.getResults();
			result%=MOD;
			currentGen=currentGen.evolve();
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
