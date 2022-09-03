package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.euler.common.EulerUtils.LongPair;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongLongCursor;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;

public class Euler289 {
	private final static int N=10;
	private final static int M=6;
	private final static long MOD=LongMath.pow(10l,10);
	
	private final static List<int[]> CONNECTIONS=List.of(
			new int[] {0,0,0,0},
			new int[] {0,0,0,3},
			new int[] {0,0,2,2},
			new int[] {0,0,2,3},
			new int[] {0,0,2,0},
			new int[] {0,1,0,0},
			new int[] {0,1,0,3},
			new int[] {0,1,1,0},
			new int[] {0,1,1,1},
			new int[] {0,1,1,3},
			new int[] {0,1,2,0},
			new int[] {0,1,2,1},
			new int[] {0,1,2,2},
			new int[] {0,1,2,3}
	);
	
	private static class State	{
		private final int[] data;
		private final int[] colours;
		private final long[] z;
		public State(int m)	{
			data=new int[m+3];
			colours=new int[4];
			z=new long[16];
		}
		private void getColours(int y,int newColour)	{
			System.arraycopy(data,y,colours,0,3);
			colours[3]=newColour;
		}
		private void decode(long s)	{
			for (int i=0;i<data.length;++i)	{
				data[i]=(int)(s&0xF);
				s>>=4;
			}
		}
		private long merge(long s,int x,int y)	{
			if (s<0) return s;
		    long xored=x^y;
		    long w=s;
		    for (int i=0;;++i) {
		        if (w==0) break;
		        if ((w&0xF)==x) s^=(xored<<(i*4));
		        w>>=4;
		    }
		    return s;
		}
		private int find(long s,int k)	{
			int result=0;
			for (int i=0;i<data.length;++i)	{
				long w=(s&0xF);
				if (w==k) ++result;
				s>>=4;
			}
			return result;
		}
		private long rep(long s)	{
			long result=0;
			int t=0;
			Arrays.fill(z,0);
			long w=s;
			for (int i=0;w>0;++i)	{
				int x=(int)(w&0xF);
				if (z[x]==0)	{
					++t;
					z[x]=t;
				}
				result|=(z[x]-1)<<(4*i);
				w>>=4;
			}
			return result;
		}
		public List<Long> initCache(long k,int x,int y)	{
			List<Long> result=new ArrayList<>();
			decode(k);
			for (int[] indices:CONNECTIONS)	{
				int newColour=((y==M)||(x==N))?0:15;
				getColours(y,newColour);
				long kk=(k<<4)|newColour;
				for (int i=0;i<4;++i) for (int j=0;j<4;++j) if ((colours[i]==0)&&((colours[j]==0)!=(indices[i]==indices[j]))) kk=-1;
				for (int i=0;(i<4)&&(kk!=-1);++i)	{
					if (i==indices[i]) continue;
					int src=(int)(kk>>(4*(y+i+1))&0xF);
					int dst=(int)(kk>>(4*(y+indices[i]+1))&0xF);
					if (i==3) src=newColour;
					if (src==0) continue;
					if ((src==dst)||(src==0)||(dst==0)) kk=-1;
					kk=merge(kk,src,dst);
				}
				if (kk!=-1)	{
					long cur=kk&15;
					kk=kk>>4;
					long old=(kk>>((y+1)*4))&0xF;
					if ((find(kk,(int)old)>1)||(old==cur)||(x==N&&y==M))	{
						kk^=(cur^old)<<(4*(y+1));
						if (y==M) kk=kk<<4;
						result.add(rep(kk));
					}
				}
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		LongLongMap f=HashLongLongMaps.newMutableMap();
		LongLongMap g=HashLongLongMaps.newMutableMap();
		f.put(0l,1l);
		Map<LongPair,List<Long>> cache=new HashMap<>();
		State s=new State(M);
		for (int x=0;x<=N;++x) for (int y=0;y<=M;++y)	{
			LongLongMap swap=f;
			f=g;
			g=swap;
			f.clear();
			for (LongLongCursor cursor=g.cursor();cursor.moveNext();)	{
				long k=cursor.key();
				long v=cursor.value();
				if (x==N) for (long kk:s.initCache(k,x,y))	{
					long res=f.getOrDefault(kk,0l);
					res+=v;
					res%=MOD;
					f.put(kk,res);
				}	else	{
					LongPair p=new LongPair(k,y);
					int xx=x;	// Stupid Java scoping rules.
					int yy=y;
					List<Long> kks=cache.computeIfAbsent(p,(LongPair unused)->s.initCache(k,xx,yy));
					for (long kk:kks)	{
						long res=f.getOrDefault(kk,0l);
						res+=v;
						res%=MOD;
						f.put(kk,res);
					}
				}
			}
		}
		long result=f.get(0l);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
