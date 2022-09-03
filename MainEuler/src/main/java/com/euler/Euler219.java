package com.euler;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Euler219 {
	private final static int SIZE=1000000000;
	private final static int CHEAP=1;
	private final static int EXPENSIVE=4;
	private static class CodePriceSet	{
		private NavigableMap<Integer,Long> prices;
		public CodePriceSet()	{
			prices=new TreeMap<>();
			prices.put(0,1l);
		}
		public int removeCheapestCode()	{
			Map.Entry<Integer,Long> cheapest=prices.firstEntry();
			int cheapestPrice=cheapest.getKey();
			long howMany=cheapest.getValue();
			if (howMany==1) prices.remove(cheapestPrice);
			else prices.put(cheapestPrice,howMany-1);
			return cheapestPrice;
		}
		public void add(int price)	{
			if (prices.containsKey(price)) prices.put(price,1+prices.get(price));
			else prices.put(price,1l);
		}
		public int size()	{
			int count=0;
			for (Map.Entry<Integer,Long> entry:prices.entrySet()) count+=entry.getValue();
			return count;
		}
		public long getTotalPrice()	{
			long total=0l;
			for (Map.Entry<Integer,Long> entry:prices.entrySet()) total+=((long)entry.getKey())*entry.getValue();
			return total;
		}
	}
	
	public static void main(String[] args)	{
		CodePriceSet prices=new CodePriceSet();
		for (int i=1;i<SIZE;++i)	{
			int cheapest=prices.removeCheapestCode();
			prices.add(cheapest+CHEAP);
			prices.add(cheapest+EXPENSIVE);
		}
		assert(prices.size()==SIZE);
		System.out.println(prices.getTotalPrice());
	}
}
