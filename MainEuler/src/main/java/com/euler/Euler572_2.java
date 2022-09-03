package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Euler572_2 {
	private final static int N=200;	
	private static class Product	{
		public final int a,b;
		public Product(int a,int b)	{
			this.a=a;
			this.b=b;
		}
		@Override
		public String toString()	{
			return "("+a+","+b+")";
		}
	}
	
	private static void addProduct(Map<Integer,List<Product>> map,int a,int b)	{
		int pr=a*b;
		List<Product> prev=map.get(pr);
		if (prev==null)	{
			prev=new ArrayList<>();
			map.put(pr,prev);
		}
		prev.add(new Product(a,b));
		if (a!=b) prev.add(new Product(b,a));
	}
	
	private static Map<Integer,List<Product>> getAllProducts(int max)	{
		Map<Integer,List<Product>> res=new HashMap<>();
		for (int i=-max;i<=max;++i) for (int j=i;j<=max;++j) addProduct(res,i,j);
		return res;
	}
	
	private static List<Integer> getAcceptableFactors(int product,int factor1)	{
		if (factor1==0)	{
			if (product==0)	{
				List<Integer> result=new ArrayList<>(2*N+1);
				for (int i=-N;i<=N;++i) result.add(i);
				return result;
			}
			return Collections.emptyList();
		}
		if ((product%factor1)==0)	{
			int q=product/factor1;
			if (Math.abs(q)<=N) return Arrays.asList(product/factor1);
			return Collections.emptyList();
		}
		return Collections.emptyList();
	}
	
	private static int howManyDifferent(int a,int b,int c)	{
		int res=3;
		if (a==b) --res;
		if (a==c) --res;
		if (b==c) --res;
		return res;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int counter=2;	// Including (0,0,0) and (1,1,1).
		Map<Integer,List<Product>> allFactors=getAllProducts(N);
		for (int aa=-N;aa<=0;++aa) for (int bb=aa;bb<=N;++bb)	{
			int limInfC=Math.max(Math.max(1-aa-bb,-N),bb);
			int limSupC=Math.min(2-aa-bb,N);
			if (limInfC<=limSupC) for (int cc=limInfC;cc<=limSupC;++cc)	{
				int diff=howManyDifferent(aa,bb,cc);
				int toAdd;
				if (diff==3) toAdd=6;
				else if (diff==2) toAdd=3;
				else throw new RuntimeException("Developer error!");
				for (int ab=-N;ab<=N;++ab) for (int ba=-N;ba<=N;++ba)	{
					int acca=aa-(aa*aa+ab*ba);
					// Products checked now: aa.
					List<Product> factors=allFactors.get(acca);
					if (factors==null) continue;
					for (Product p:factors)	{
						int ac=p.a;
						int ca=p.b;
						int abbc=ac-(aa*ac+ac*cc);
						for (int bc:getAcceptableFactors(abbc,ab))	{
							// Products checked now: aa, ac.
							if (ba*ac+bb*bc+bc*cc!=bc) continue;
							// Products checked now: aa, ac, bc.
							if (ba*aa+bb*ba+bc*ca!=ba) continue;
							// Products checked now: aa, ac, ba, bc.
							int cbba=ca-(ca*aa+cc*ca);
							for (int cb:getAcceptableFactors(cbba,ba))	{
								// Products checked now: aa, ac, ba, bc, ca.
								if (aa*ab+ab*bb+ac*cb!=ab) continue;	// ab...
								if (ba*ab+bb*bb+bc*cb!=bb) continue;	// bb...
								if (ca*ab+cb*bb+cc*cb!=cb) continue;	// cb...
								if (ca*ac+cb*bc+cc*cc!=cc) continue;	// and cc.
								counter+=toAdd;
							}
						}
					}
				}
			}
		}
		long toc=System.nanoTime();
		System.out.println(counter);
		System.out.println("Time: "+((toc-tic)/1000000000)+"s.");
	}
}
