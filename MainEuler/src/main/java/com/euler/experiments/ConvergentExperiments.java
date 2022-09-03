package com.euler.experiments;

import java.math.BigInteger;
import java.util.Arrays;

import com.euler.common.Convergents.Convergent;
import com.euler.common.Convergents.InfiniteContinuedFraction;
import com.euler.common.EulerUtils;
import com.euler.common.FiniteContinuedFraction;
import com.euler.common.QuadraticRationalContinuedFraction;
import com.euler.common.QuadraticRationalContinuedFraction.IterationType;
import com.koloboke.collect.LongCursor;

public class ConvergentExperiments {
	/*-
	 * ZUTUN! TODO! TEHDÄ!!!!!
	 * Empollarse esto: https://sites.millersville.edu/bikenaga/number-theory/periodic-continued-fractions/periodic-continued-fractions.html.
	 * Mirarse la parte de "Then xi=xj if and only if..." (buscar la segunda aparición de "if and only if"). ¿Me podrá servir POR FIN para encontrar
	 * los putos ciclos en general para cualquier racional cuadrático? (Narrador: yo de ti me lo pensaría antes de responder que sí).
	 */
	public static void main(String[] args)	{
		/*-
		QuadraticRational result=ConvergentInversion.reverseConvergent(0,14);
		System.out.println(result);
		*/
		/*-
		InfiniteContinuedFraction convergentSource=InfiniteContinuedFraction.getForQuadraticRational(2357,4675280,126749);
		int counter=10;
		for (LongCursor cursor=convergentSource.getTermsAsCursor();cursor.moveNext();)	{
			System.out.print(cursor.elem()+"...");
			--counter;
			if (counter<0) break;
		}
		System.out.println();
		Iterator<Convergent> iterator=InfiniteContinuedFraction.getForQuadraticRational(2357,4675280,126749).iterator();
		for (int i=0;i<10;++i)	{
			Convergent cosa=iterator.next();
			System.out.println(cosa.toString());
		}
		*/
		/*
		BigInteger mod=BigInteger.valueOf(8191l);
		BigInteger radicand=BigInteger.valueOf(6889l);
		System.out.println(DiophantineUtils.tonelliShanks(radicand, mod));
		mod=BigInteger.valueOf(127l);
		radicand=BigInteger.valueOf(15l);
		System.out.println(DiophantineUtils.tonelliShanks(radicand, mod));
		System.out.println(EulerUtils.modulusInverse(BigInteger.valueOf(29),BigInteger.valueOf(5)));
		System.out.println(DiophantineUtils.squareRootModuloPrimePower(BigInteger.valueOf(4),BigInteger.valueOf(3),2));
		BigInteger someMod=BigInteger.valueOf((4567*4567)%8191);
		System.out.println(DiophantineUtils.squareRootModuloPrimePower(someMod,BigInteger.valueOf(8191),2));
		System.out.println(DiophantineUtils.squareRootModuloPrimePower(someMod,BigInteger.valueOf(8191),3));
		System.out.println(DiophantineUtils.quadraticPolynomialRootsMod2n(BigInteger.ONE,BigInteger.valueOf(3l),BigInteger.valueOf(-12),5));
		System.out.println(DiophantineUtils.quadraticPolynomialModuloPrime(BigInteger.ONE,BigInteger.valueOf(5l),BigInteger.ZERO,BigInteger.valueOf(13)));
		System.out.println(DiophantineUtils.quadraticPolynomialRootsModuloPrimePower(BigInteger.TWO,BigInteger.valueOf(5l),BigInteger.valueOf(28),BigInteger.valueOf(13),2));
		ObjIntMap<BigInteger> decomposition=HashObjIntMaps.newMutableMap();
		decomposition.put(BigInteger.valueOf(7l),3);
		decomposition.put(BigInteger.valueOf(29l),2);
		decomposition.put(BigInteger.valueOf(2l),6);
		System.out.println(DiophantineUtils.quadraticPolynomialRootsModuloAnyNumber(BigInteger.valueOf(3l),BigInteger.valueOf(5l),BigInteger.valueOf(2l),decomposition));
		Set<BigInteger> attempt=DiophantineUtils.quadraticPolynomialRootsModuloAnyNumber(BigInteger.valueOf(3l),BigInteger.valueOf(5l),BigInteger.valueOf(2l),decomposition);
		BigInteger bigN=BigInteger.valueOf(343l*841l*64l);
		Function<BigInteger,BigInteger> f=(BigInteger x)->BigInteger.valueOf(3).multiply(x).add(BigInteger.valueOf(5l)).multiply(x).add(BigInteger.valueOf(2l)).mod(bigN);
		for (BigInteger x:attempt) System.out.println(f.apply(x));
		*/
		InfiniteContinuedFraction convergentSource=InfiniteContinuedFraction.getForQuadraticRational(-725,313,608);
		int counter=50;
		for (LongCursor cursor=convergentSource.getTermsAsCursor();cursor.moveNext();)	{
			System.out.print(cursor.elem()+"...");
			--counter;
			if (counter<0) break;
		}
		System.out.println();
		BigInteger n1=BigInteger.valueOf(5*5*5*7*7*7);
		BigInteger n2=BigInteger.valueOf(2*2*2*3*3*3);
		BigInteger q=EulerUtils.modulusInverse(n2,n1);
		System.out.println(q);
		System.out.println(q.multiply(n2).mod(n1));
		{
			FiniteContinuedFraction frac=FiniteContinuedFraction.getFor(7,3);
			System.out.println(Arrays.toString(frac.array));
			FiniteContinuedFraction frac2=FiniteContinuedFraction.getFor(0,5);
			System.out.println(Arrays.toString(frac2.array));
			FiniteContinuedFraction frac3=FiniteContinuedFraction.getFor(-12,-13);
			System.out.println(Arrays.toString(frac3.array));
			FiniteContinuedFraction frac4=FiniteContinuedFraction.getFor(9,4);
			System.out.println(Arrays.toString(frac4.array));
			FiniteContinuedFraction frac5=FiniteContinuedFraction.getFor(299,23);
			System.out.println(Arrays.toString(frac5.array));
			FiniteContinuedFraction frac6=FiniteContinuedFraction.getFor(-676,48);
			System.out.println(Arrays.toString(frac6.array));
			FiniteContinuedFraction frac7=FiniteContinuedFraction.getFor(115,-99);
			System.out.println(Arrays.toString(frac7.array));
		}
		for (Convergent conv:QuadraticRationalContinuedFraction.getForGenericQuadraticRational(-2, 2, 1, IterationType.DOUBLE_PASS))	{
			System.out.println(""+conv.p+'/'+conv.q);
		}
	}
}
