package com.euler;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import com.euler.common.Primes;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler552 {
	private final static long LIMIT=300000l;
	
	private static BigInteger solveChineseRemainder(BigInteger a,BigInteger x,BigInteger b,BigInteger y)	{
		BigInteger y_x=modulusInverse(y.mod(x),x);
		BigInteger x_y=modulusInverse(x.mod(y),y);
		BigInteger prod=x.multiply(y);
		BigInteger m=y.multiply(y_x).mod(prod);
		BigInteger n=x.multiply(x_y).mod(prod);
		return a.multiply(m).add(b.multiply(n)).mod(prod);
	}
	
	private static BigInteger modulusInverse(BigInteger operand,BigInteger mod)	{
		BigInteger store=mod;
		BigInteger x=BigInteger.ZERO;
		BigInteger y=BigInteger.ONE;
		BigInteger lastX=BigInteger.ONE;
		BigInteger lastY=BigInteger.ZERO;
		BigInteger q,tmp;
		while (!operand.equals(BigInteger.ZERO))	{
			BigInteger[] division=mod.divideAndRemainder(operand);
			q=division[0];
			tmp=division[1];
			mod=operand;
			operand=tmp;
			tmp=lastX.subtract(q.multiply(x));
			lastX=x;
			x=tmp;
			tmp=lastY.subtract(q.multiply(y));
			lastY=y;
			y=tmp;
		}
		while (lastY.compareTo(BigInteger.ZERO)<0) lastY=lastY.add(store);
		return lastY.mod(store);
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		List<BigInteger> primes=Primes.listLongPrimes(LIMIT).stream().map(BigInteger::valueOf).collect(Collectors.toUnmodifiableList());
		BigInteger currentNumber=BigInteger.ONE;
		BigInteger currentMod=BigInteger.TWO;
		LongSet validPrimes=HashLongSets.newMutableSet();
		for (int i=1;i<primes.size();++i)	{
			currentNumber=solveChineseRemainder(currentNumber,currentMod,BigInteger.valueOf(i+1),primes.get(i));
			currentMod=currentMod.multiply(primes.get(i));
			for (int j=i+1;j<primes.size();++j) if (currentNumber.mod(primes.get(j)).equals(BigInteger.ZERO)) validPrimes.add(primes.get(j).longValue());
		}
		long result=0;
		for (LongCursor cursor=validPrimes.cursor();cursor.moveNext();) result+=cursor.elem();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		/*
		 * Elapsed 20436.423169738002 seconds.
		 * JAJA SI.
		 * 
		 * Bueno, y ahora a ver el hilo del problema, a ver cómo se resuelve *de verdad*.
		 */
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
