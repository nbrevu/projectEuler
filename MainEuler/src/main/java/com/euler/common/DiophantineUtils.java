package com.euler.common;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.ImmutableSet;
import com.koloboke.collect.map.ObjIntCursor;
import com.koloboke.collect.map.ObjIntMap;

public class DiophantineUtils {
	private final static BigInteger FOUR=BigInteger.valueOf(4l);
	
	private static BigInteger findFirstNonResidue(BigInteger prime)	{
		BigInteger exp=prime.subtract(BigInteger.ONE).divide(BigInteger.TWO);
		for (BigInteger q=BigInteger.TWO;;q=q.add(BigInteger.ONE)) if (!EulerUtils.expMod(q,exp,prime).equals(BigInteger.ONE)) return q;
	}
	private static boolean isQuadraticResidue(BigInteger n,BigInteger prime)	{
		return EulerUtils.expMod(n,prime.subtract(BigInteger.ONE).divide(BigInteger.TWO),prime).equals(BigInteger.ONE);
	}
	public static Set<BigInteger> tonelliShanks(BigInteger n,BigInteger prime)	{
		// Results undefined if "prime" is not an ODD prime.
		if (n.signum()==0) return Collections.singleton(BigInteger.ZERO);
		// There are solutions if an only if n^((prime-1)/2)==1 mod prime.
		if (!isQuadraticResidue(n,prime)) return Collections.emptySet();
		BigInteger q=prime.subtract(BigInteger.ONE);
		int s=q.getLowestSetBit();
		q=q.divide(BigInteger.ONE.shiftLeft(s));
		BigInteger z=findFirstNonResidue(prime);
		int m=s;
		BigInteger c=EulerUtils.expMod(z,q,prime);
		BigInteger t=EulerUtils.expMod(n,q,prime);
		BigInteger r=EulerUtils.expMod(n,q.add(BigInteger.ONE).divide(BigInteger.TWO),prime);
		for (;;)	{
			if (t.equals(BigInteger.ONE)) return ImmutableSet.of(r,prime.subtract(r));
			int i=1;
			BigInteger t2i=t;
			for (;i<m;++i)	{
				t2i=t2i.multiply(t2i).mod(prime);
				if (t2i.equals(BigInteger.ONE)) break;
			}
			if (i>=m) return Collections.emptySet();	// Ooooh, no solutions.
			BigInteger b=c;
			for (int j=0;j<m-i-1;++j) b=b.multiply(b).mod(prime);
			m=i;
			c=b.multiply(b).mod(prime);
			t=t.multiply(c).mod(prime);
			r=r.multiply(b).mod(prime);
		}
	}
	private static Set<BigInteger> manualLifting(Function<BigInteger,BigInteger> poly,BigInteger prime,int degree,BigInteger primeSol)	{
		int p=prime.intValueExact();
		BigInteger primePow=prime;
		Set<BigInteger> solutions=ImmutableSet.of(primeSol);
		for (int i=2;i<=degree;++i)	{
			BigInteger previousPow=primePow;
			primePow=primePow.multiply(prime);
			Set<BigInteger> nextSet=new HashSet<>();
			for (BigInteger x:solutions)	{
				BigInteger mod=poly.apply(x).mod(primePow);
				if (mod.signum()==0) for (int j=0;j<p;++j)	{
					nextSet.add(x);
					x=x.add(previousPow);
				}
			}
			solutions=nextSet;
			if (solutions.isEmpty()) break;
		}
		return solutions;
	}
	public static Set<BigInteger> quadraticPolynomialHenselLifting(Set<BigInteger> baseSolutions,BigInteger a,BigInteger b,BigInteger c,BigInteger prime,int degree)	{
		// As seen in https://www.geeksforgeeks.org/hensels-lemma/.
		if (baseSolutions.isEmpty()) return Collections.emptySet();
		else if (degree==1) return baseSolutions;
		else if (degree<1) throw new ArithmeticException("Lo que me habéis dao pa papear me roe las tripas.");
		Set<BigInteger> result=new HashSet<>();
		Function<BigInteger,BigInteger> f=(BigInteger x)->a.multiply(x).add(b).multiply(x).add(c);
		BigInteger a2=a.add(a);
		Function<BigInteger,BigInteger> dF=(BigInteger x)->a2.multiply(x).add(b);
		BigInteger primePower=prime.pow(degree);
		for (BigInteger x:baseSolutions)	{
			BigInteger dFx=dF.apply(x);
			// if (dFx.mod(prime).signum()==0) continue;	// Ooooh, can't lift. But this is WRONG! This means that I need to do MANUAL LIFTING!
			if (dFx.mod(prime).signum()==0) result.addAll(manualLifting(f,prime,degree,x));
			else	{
				BigInteger invFx=EulerUtils.modulusInverse(dFx,prime);
				BigInteger ak=x;
				for (int i=1;i<degree;++i) ak=ak.subtract(f.apply(ak).multiply(invFx));
				result.add(ak.mod(primePower));
			}
		}
		return result;
	}
	public static Set<BigInteger> squareRootHenselLifting(Set<BigInteger> baseSolutions,BigInteger radicand,BigInteger prime,int degree)	{
		return quadraticPolynomialHenselLifting(baseSolutions,BigInteger.ONE,BigInteger.ZERO,radicand.negate(),prime,degree);
	}
	public static Set<BigInteger> quadraticPolynomialRootsMod2n(BigInteger a,BigInteger b,BigInteger c,int degree)	{
		Set<BigInteger> result=new HashSet<>();
		if (!c.abs().testBit(0)) result.add(BigInteger.ZERO);
		if (!a.add(b).add(c).abs().testBit(0)) result.add(BigInteger.ONE);
		BigInteger pow2Prev=BigInteger.TWO;
		Function<BigInteger,BigInteger> f=(BigInteger x)->a.multiply(x).add(b).multiply(x).add(c);
		for (int i=1;i<degree;++i)	{
			BigInteger pow2Curr=pow2Prev.add(pow2Prev);
			if (result.isEmpty()) return result;
			Set<BigInteger> tmpResult=new HashSet<>();
			for (BigInteger prevPowerSolution:result) {
				if (f.apply(prevPowerSolution).mod(pow2Curr).signum()==0) tmpResult.add(prevPowerSolution);
				BigInteger newAttempt=prevPowerSolution.add(pow2Prev);
				if (f.apply(newAttempt).mod(pow2Curr).signum()==0) tmpResult.add(newAttempt);
			}
			pow2Prev=pow2Curr;
			result=tmpResult;
		}
		return result;
	}
	public static Set<BigInteger> squareRootModuloPrimePower(BigInteger radicand,BigInteger prime,int power)	{
		Set<BigInteger> baseSolutions=tonelliShanks(radicand,prime);
		return squareRootHenselLifting(baseSolutions,radicand,prime,power);
	}
	public static BigInteger solveChineseRemainder(BigInteger a,BigInteger x,BigInteger b,BigInteger y)	{
		// Assumes x and y coprime. Undefined results if this doesn't hold.
		BigInteger y_x=EulerUtils.modulusInverse(y.mod(x),x);
		BigInteger x_y=EulerUtils.modulusInverse(x.mod(y),y);
		BigInteger prod=x.multiply(y);
		BigInteger m=y.multiply(y_x).mod(prod);
		BigInteger n=x.multiply(x_y).mod(prod);
		return a.multiply(m).add(b.multiply(n)).mod(prod);
	}
	public static Optional<BigInteger> solveChineseRemainderNonCoprime(BigInteger a,BigInteger x,BigInteger b,BigInteger y)	{
		BigInteger g=EulerUtils.gcd(x,y);
		if (a.subtract(b).mod(g).signum()!=0) return Optional.empty();
		BigInteger m1=x.divide(g);
		BigInteger m2=y.divide(g);
		BigInteger[] coeffs=EulerUtils.extendedGcd(m1,m2).coeffs;
		BigInteger result=a.multiply(coeffs[1]).multiply(m2).add(b.multiply(coeffs[0]).multiply(m1));
		return Optional.of(result.mod(m1.multiply(m2).multiply(g)));
	}
	public static Set<BigInteger> solveChineseRemainder(Set<BigInteger> as,BigInteger x,Set<BigInteger> bs,BigInteger y)	{
		Set<BigInteger> result=new HashSet<>(as.size()*bs.size());
		for (BigInteger a:as) for (BigInteger b:bs) result.add(solveChineseRemainder(a,x,b,y));
		return result;
	}
	public static Set<BigInteger> quadraticPolynomialModuloPrime(BigInteger a,BigInteger b,BigInteger c,BigInteger prime)	{
		if (a.mod(prime).signum()==0)	{
			if (b.mod(prime).signum()==0) return Collections.emptySet();
			return Collections.singleton(c.multiply(EulerUtils.modulusInverse(b,prime)).negate().mod(prime));
		}	else	{
			Set<BigInteger> sqrts=tonelliShanks(b.multiply(b).subtract(FOUR.multiply(a).multiply(c)).mod(prime),prime);
			if (sqrts.isEmpty()) return Collections.emptySet();
			BigInteger denom=EulerUtils.modulusInverse(a.add(a),prime);
			Set<BigInteger> result=new HashSet<>();
			BigInteger b_=b.negate();
			for (BigInteger s:sqrts)	{
				result.add(b_.add(s).multiply(denom).mod(prime));
				result.add(b_.subtract(s).multiply(denom).mod(prime));
			}
			return result;
		}
	}
	public static Set<BigInteger> quadraticPolynomialRootsModuloPrimePower(BigInteger a,BigInteger b,BigInteger c,BigInteger prime,int degree)	{
		if (BigInteger.TWO.equals(prime)) return quadraticPolynomialRootsMod2n(a,b,c,degree);
		Set<BigInteger> baseSolutions=quadraticPolynomialModuloPrime(a,b,c,prime);
		return quadraticPolynomialHenselLifting(baseSolutions,a,b,c,prime,degree);
	}
	// And now, the ACTUAL function I'm going to call over and over; the reason I've been doing all this shit.
	public static Set<BigInteger> quadraticPolynomialRootsModuloAnyNumber(BigInteger a,BigInteger b,BigInteger c,ObjIntMap<BigInteger> primePowers)	{
		ObjIntCursor<BigInteger> cursor=primePowers.cursor();
		if (!cursor.moveNext()) return ImmutableSet.of(BigInteger.ZERO);
		BigInteger prime=cursor.key();
		int degree=cursor.value();
		Set<BigInteger> result=quadraticPolynomialRootsModuloPrimePower(a,b,c,prime,degree);
		if (result.isEmpty()) return result;
		BigInteger totalProduct=prime.pow(degree);
		while (cursor.moveNext())	{
			prime=cursor.key();
			degree=cursor.value();
			Set<BigInteger> tmpResult=quadraticPolynomialRootsModuloPrimePower(a,b,c,prime,degree);
			if (tmpResult.isEmpty()) return tmpResult;
			BigInteger primePower=prime.pow(degree);
			result=solveChineseRemainder(result,totalProduct,tmpResult,primePower);
			totalProduct=totalProduct.multiply(primePower);
		}
		return result;
	}
}
