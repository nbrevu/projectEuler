package com.euler;

import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.hash.HashLongIntMaps;

public class Euler375 {
	private final static long N=2*LongMath.pow(10l,9);
	
	/*
	 * S_1=S_6308949=629527.
	 * El ciclo es puro (sin prefijos) y contiene 6308948 elementos.
	 * 
	 * Se me ocurre una especie de estructura de árbol en la que los mínimos estén señalados.
	 * A lo mejor se puede almacenar, para cada número X, la posición del siguiente valor menor que X. Esto puede servir para calcular
	 * la cantidad de A(i,j) que son iguales a cada valor. Por lo pronto, A(i,i)=S_i así que cada valor se cuenta al menos una vez.
	 * 
	 * Por otro lado, 2e9=6308948*317 + 63484 -> 317 y pico ciclos.
	 * 
	 * "Minimum value: 3, found at 2633997", so the vast majority of the A(i,j) are equal to 3.
	 * In particular, if j>=i+6308948, A(i,j)=3, no question.
	 */
	private static class PseudoRandomGenerator	{
		private final static long MOD=50515093l;
		private final static long INITIAL=290797l;
		private long s;
		public PseudoRandomGenerator()	{
			s=INITIAL;
		}
		public long next()	{
			s=(s*s)%MOD;
			return s;
		}
	}
	
	public static void main(String[] args)	{
		LongIntMap appearances=HashLongIntMaps.newMutableMap();
		PseudoRandomGenerator generator=new PseudoRandomGenerator();
		long min=Long.MAX_VALUE;
		int minPos=-1;
		for (int i=1;;++i)	{
			long s=generator.next();
			if (s<min)	{
				min=s;
				minPos=i;
			}
			int pos=appearances.getOrDefault(s,-1);
			if (pos==-1) appearances.put(s,i);
			else	{
				System.out.println("ICH HABE DAS CYCLEN GEFUNDEN!!!!! ODER????? S_"+pos+"=S_"+i+"="+s+".");
				System.out.println("Minimum value: "+min+", found at "+minPos+".");
				break;
			}
		}
		System.out.println(N%appearances.size());
	}
}
