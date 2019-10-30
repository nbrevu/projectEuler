package com.euler;

import java.util.ArrayList;
import java.util.List;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.euler.common.Timing;

public class Euler152 {
	private static final int LIMIT=80;
	
	private static class SumFinder	{
		private final int[] primes;
		private final int[] primeBases;
		private final int[][][] splits;
		private final int[] order;
		public SumFinder()	{
			primes=Primes.listIntPrimesAsArray(LIMIT);
			primeBases=new int[1+LIMIT];	// 0 by default!
			for (int p:primes) for (int i=p;i<=LIMIT;i*=p) primeBases[i]=p;
			long[] tmp=new long[2];	// Allocated once, used many times. 
			splits=new int[1+LIMIT][][];
			for (int i=2;i<=LIMIT;++i) splits[i]=split(i,tmp);
			order=calculateIterationOrder();
		}
		private int[][] split(int in,long[] tmp)	{
			long m=1;
			List<int[]> listR=new ArrayList<int[]>();
			for (int p:primes)	{
				int pow=1;
				while ((in%p)==0)	{
					pow*=p;
					in/=p;
				}
				if (pow>1)	{
					long p2=pow*pow;
					long i2=in*in;
					EulerUtils.extendedGcd(p2,i2,tmp);
					tmp[0]*=m;
					tmp[1]*=m;
					long k=tmp[1]/p2;
					tmp[1]-=k*p2;
					tmp[0]+=k*i2;
					while (tmp[1]!=0)	{
						int pr2=p*p;
						if ((tmp[1]%pr2)!=0) listR.add(new int[] {(int)(tmp[1]%pr2),(int)pow});
						pow/=p;
						tmp[1]/=pr2;
					}
					m=tmp[0];
				}
			}
			int[][] result=new int[listR.size()][];
			for (int i=0;i<listR.size();++i) result[i]=listR.get(i);
			return result;
		}
		private void balanceAt(int pp,int[] have)	{
			int p=primeBases[pp];
			int s=p*p;
			do	{
				if ((have[pp]<s)&&(have[pp]>-s)) return;
				int up=pp/p;
				if (have[pp]>=s)	{
					have[pp]-=s;
					++have[up];
				}	else if (have[pp]<=-s)	{
					have[pp]+=s;
					--have[up];
				}
				pp=up;
			}	while (pp>1);
		}
		private int findSumsRecursive(int w,int[] have)	{
			if (w==0) return 1;
			--w;
			int n=order[w];
			int result=(have[n]==0)?findSumsRecursive(w,have):0;
			if ((have[n]!=0)||(primeBases[n]==0))	{
				for (int[] split:splits[n])	{
					int pp=split[1];
					have[pp]-=split[0];
					balanceAt(pp,have);
				}
				if (have[n]==0) result+=findSumsRecursive(w,have);
				for (int[] split:splits[n])	{
					int pp=split[1];
					have[pp]+=split[0];
					balanceAt(pp,have);
				}
			}
			return result;
		}
		private int getLocks(int n,boolean[] used)	{
			int result=0;
			for (int k=n+n;k<=LIMIT;k+=n) if (!used[k]) ++result;
			return result;
		}
		private int[] calculateIterationOrder()	{
			boolean[] used=new boolean[1+LIMIT];
			int w=LIMIT-1;
			int[] order=new int[w];
			while (w>0)	{
				for (int i=LIMIT;i>=2;--i) if (!used[i]&&(primeBases[i]>0)&&(getLocks(i,used)==0))	{
					used[i]=true;
					--w;
					order[w]=i;
				}
				int bestI=-1;
				long bestF=-1l;
				for (int i=LIMIT;i>=2;--i) if (!used[i])	{
					long f=0;
					for (int k=2;k<i;++k) if ((primeBases[k]>0)&&((i%k)==0)) f+=1l<<Math.max(0,55-getLocks(k,used));
					if (f>bestF)	{
						bestI=i;
						bestF=f;
					}
				}
				if (bestI>=0)	{
					used[bestI]=true;
					--w;
					order[w]=bestI;
				}
			}
			return order;
		}
		public int findSums()	{
			int[] have=new int[1+LIMIT];
			have[2]=2;	// 2/2^2 = 1/2.
			return findSumsRecursive(LIMIT-1,have);
		}
	}

	private static long solve()	{
		return new SumFinder().findSums();
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler152::solve);
	}
}