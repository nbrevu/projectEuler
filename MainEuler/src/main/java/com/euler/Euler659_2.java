package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler659_2 {
	private final static int LIMIT=IntMath.pow(10,7);
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int primeLimit=IntMath.pow(10,9);
		boolean[] composites=Primes.sieve(primeLimit);
		IntSet pending=HashIntSets.newMutableSet();
		for (int i=1;i<=LIMIT;++i) pending.add(i);
		int[] results=new int[1+LIMIT];
		for (int x=2;;x+=2)	{
			int prime=2*x+1;
			if (prime>primeLimit) break;
			if (composites[prime]) continue;
			long lx=x;
			long residue=(-lx*lx)%prime;
			residue=(prime+residue)%prime;
			LongSet roots=EulerUtils.squareRootModuloPrime(residue,prime);
			LongCursor cursor=roots.cursor();
			while (cursor.moveNext())	{
				long elem=cursor.elem();
				if (elem<=LIMIT)	{
					int intElem=(int)elem;
					pending.removeInt(intElem);
					results[intElem]=prime;
				}
			}
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		// "Quedan 6901156 elementos por encontrar". Esto no funciona :(.
		System.out.println("Quedan "+pending.size()+" elementos por encontrar.");
		for (int i=1;;++i) if (results[i]==0) {
			System.out.println("El primero que se ha quedado sin asignar es "+i+".");
			break;
		}
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
