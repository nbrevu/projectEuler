package com.euler.common;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import com.euler.common.ConvergentInversion.QuadraticRational;
import com.euler.common.ConvergentInversion.SecondGradeEquation;
import com.euler.common.Convergents.ContinuedFraction;
import com.euler.common.Convergents.Convergent;
import com.euler.common.EulerUtils.Pair;
import com.euler.common.Primes.PrimeDecomposer;
import com.euler.common.QuadraticRationalContinuedFraction.IterationType;
import com.google.common.collect.Iterators;
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.set.LongSet;

/*
 * YES, THE NAME OF THIS CLASS IS ALPERTRON. Sorry not sorry.
 * See also: https://www.alpertron.com.ar/METHODS.HTM.
 * OH, OH, OH. http://mathafou.free.fr/themes_en/kpell.html, MAGIA DE ECUACIONES DE PELL. Igual me sirve para el 582. Mirar en detalle.
 * 331: ¿Eeeh? "Bresenham points", ¿qué coño es eso? (Se refiere simplemente al algoritmo de Bresenham para círculos, supongo).
 */
public class AlpertronOld {
	private final static BigInteger FOUR=BigInteger.valueOf(4);
	
	private static class EmptyIterator<T> implements Iterator<T>	{
		@Override
		public boolean hasNext() {
			return false;
		}
		@Override
		public T next() {
			throw new NoSuchElementException();
		}
	}
	public static interface Solution1D extends Iterable<BigInteger>	{}
	
	public static enum NoSolution1D implements Solution1D	{
		INSTANCE;
		@Override
		public Iterator<BigInteger> iterator() {
			return new EmptyIterator<>();
		}
	}
	public static class SingleSolution1D implements Solution1D	{
		private final BigInteger solution;
		public SingleSolution1D(BigInteger solution)	{
			this.solution=solution;
		}
		@Override
		public Iterator<BigInteger> iterator() {
			return Iterators.singletonIterator(solution);
		}
	}
	public static class FiniteListSolution1D implements Solution1D	{
		private final Collection<BigInteger> solutions;
		public FiniteListSolution1D(Collection<BigInteger> solutions)	{
			this.solutions=solutions;
		}
		@Override
		public Iterator<BigInteger> iterator() {
			return solutions.iterator();
		}
	}
	public static class RecursiveSolution1D implements Solution1D	{
		private final BigInteger initial;
		private final BigInteger increment;
		public RecursiveSolution1D(BigInteger initial,BigInteger increment)	{
			this.initial=initial;
			this.increment=increment;
		}
		@Override
		public Iterator<BigInteger> iterator() {
			return new Iterator<>()	{
				private BigInteger current=initial;
				@Override
				public boolean hasNext() {
					return true;
				}
				@Override
				public BigInteger next() {
					BigInteger result=current;
					current=current.add(increment);
					return result;
				}
			};
		}
	}
	public final static Solution1D ALL_INTEGERS=new RecursiveSolution1D(BigInteger.ZERO,BigInteger.ONE);
	
	public static interface Solution2D extends Iterable<Pair<BigInteger,BigInteger>>	{}
	
	public static enum NoSolution2D implements Solution2D	{
		INSTANCE;
		@Override
		public Iterator<Pair<BigInteger,BigInteger>> iterator() {
			return new EmptyIterator<>();
		}
	}
	public static class SingleSolution2D implements Solution2D	{
		private final Pair<BigInteger,BigInteger> solution;
		public SingleSolution2D(BigInteger solutionX,BigInteger solutionY)	{
			this.solution=new Pair<>(solutionX,solutionY);
		}
		@Override
		public Iterator<Pair<BigInteger,BigInteger>> iterator() {
			return Iterators.singletonIterator(solution);
		}
	}
	public static class FiniteListSolution2D implements Solution2D	{
		private final Collection<Pair<BigInteger,BigInteger>> solutions;
		public FiniteListSolution2D(Collection<Pair<BigInteger,BigInteger>> solutions)	{
			this.solutions=solutions;
		}
		@Override
		public Iterator<Pair<BigInteger,BigInteger>> iterator() {
			return solutions.iterator();
		}
	}
	public static class CrossedSolution2D implements Solution2D	{
		// ACHTUNG, infinite solutions are managed poorly.
		private final Solution1D x;
		private final Solution1D y;
		public CrossedSolution2D(Solution1D x,Solution1D y)	{
			this.x=x;
			this.y=y;
		}
		@Override
		public Iterator<Pair<BigInteger, BigInteger>> iterator() {
			if (!(x.iterator().hasNext()&&y.iterator().hasNext())) return new EmptyIterator<>();
			return new Iterator<>()	{
				private final Iterator<BigInteger> iterX=x.iterator();
				private Iterator<BigInteger> iterY=y.iterator();
				private BigInteger curX=iterX.next();
				private BigInteger curY=iterY.next();
				@Override
				public boolean hasNext() {
					return iterX.hasNext()||iterY.hasNext();
				}
				@Override
				public Pair<BigInteger, BigInteger> next() {
					Pair<BigInteger,BigInteger> result=new Pair<>(curX,curY);
					if (iterY.hasNext()) curY=iterY.next();
					else	{
						curX=iterX.next();
						iterY=y.iterator();
						curY=iterY.next();
					}
					return result;
				}
			};
		}
	}
	public static class LinearSolution2D implements Solution2D	{
		private final BigInteger x0;
		private final BigInteger coefX;
		private final BigInteger y0;
		private final BigInteger coefY;
		public LinearSolution2D(BigInteger x0,BigInteger coefX,BigInteger y0,BigInteger coefY)	{
			this.x0=x0;
			this.coefX=coefX;
			this.y0=y0;
			this.coefY=coefY;
		}
		@Override
		public Iterator<Pair<BigInteger, BigInteger>> iterator() {
			return new Iterator<>()	{
				private BigInteger currentX=x0;
				private BigInteger currentY=y0;
				@Override
				public boolean hasNext() {
					return true;
				}
				@Override
				public Pair<BigInteger,BigInteger> next() {
					Pair<BigInteger,BigInteger> result=new Pair<>(currentX,currentY);
					currentX=currentX.add(coefX);
					currentY=currentY.add(coefY);
					return result;
				}
			};
		}
	}
	public static class QuadraticSolution2D implements Solution2D	{
		private final BigInteger x0;
		private final BigInteger linX;
		private final BigInteger quadX;
		private final BigInteger y0;
		private final BigInteger linY;
		private final BigInteger quadY;
		public QuadraticSolution2D(BigInteger x0,BigInteger linX,BigInteger quadX,BigInteger y0,BigInteger linY,BigInteger quadY)	{
			this.x0=x0;
			this.linX=linX;
			this.quadX=quadX;
			this.y0=y0;
			this.linY=linY;
			this.quadY=quadY;
		}
		@Override
		public Iterator<Pair<BigInteger, BigInteger>> iterator() {
			return new Iterator<>()	{
				private BigInteger t=BigInteger.ZERO;
				@Override
				public boolean hasNext() {
					return true;
				}
				@Override
				public Pair<BigInteger,BigInteger> next() {
					BigInteger x=quadX.multiply(t).add(linX).multiply(t).add(x0);
					BigInteger y=quadY.multiply(t).add(linY).multiply(t).add(y0);
					t=t.add(BigInteger.ONE);
					return new Pair<>(x,y);
				}
			};
		}
	}
	public static class RecursiveSolution2D implements Solution2D	{
		private final BigInteger x0;
		private final BigInteger y0;
		private final BigInteger a;
		private final BigInteger b;
		private final BigInteger c;
		private final BigInteger d;
		public RecursiveSolution2D(BigInteger x0,BigInteger y0,BigInteger a,BigInteger b,BigInteger c,BigInteger d)	{
			this.x0=x0;
			this.y0=y0;
			this.a=a;
			this.b=b;
			this.c=c;
			this.d=d;
		}
		@Override
		public Iterator<Pair<BigInteger,BigInteger>> iterator() {
			return new Iterator<>()	{
				private BigInteger currentX=x0;
				private BigInteger currentY=y0;
				@Override
				public boolean hasNext() {
					return true;
				}
				@Override
				public Pair<BigInteger,BigInteger> next() {
					Pair<BigInteger,BigInteger> result=new Pair<>(currentX,currentY);
					currentX=result.first.multiply(a).add(result.second.multiply(b));
					currentY=result.first.multiply(c).add(result.second.multiply(d));
					return result;
				}
			};
		}
	}
	public static class DisplacedSolution2D implements Solution2D	{
		private final Solution2D baseSolution;
		private final BigInteger alpha;
		private final BigInteger beta;
		private final BigInteger det;
		public DisplacedSolution2D(Solution2D baseSolution,BigInteger alpha,BigInteger beta,BigInteger det)	{
			this.baseSolution=baseSolution;
			this.alpha=alpha;
			this.beta=beta;
			this.det=det;
		}
		private class InternalIterator implements Iterator<Pair<BigInteger,BigInteger>>	{
			private final Iterator<Pair<BigInteger,BigInteger>> baseIterator;
			private boolean hasNext;
			private Pair<BigInteger,BigInteger> currentValue;
			public InternalIterator()	{
				baseIterator=baseSolution.iterator();
				hasNext=true;
				currentValue=null;
				move();
			}
			private void move()	{
				if (hasNext) while (baseIterator.hasNext())	{
					Pair<BigInteger,BigInteger> baseResult=baseIterator.next();
					BigInteger[] x=baseResult.first.add(alpha).divideAndRemainder(det);
					if (x[1].signum()!=0) continue;
					BigInteger[] y=baseResult.second.add(beta).divideAndRemainder(det);
					if (y[1].signum()!=0) continue;
					currentValue=new Pair<>(x[0],y[0]);
					return;
				}
				hasNext=false;
			}
			@Override
			public boolean hasNext() {
				return hasNext;
			}
			@Override
			public Pair<BigInteger, BigInteger> next() {
				if (!hasNext) throw new NoSuchElementException();
				Pair<BigInteger,BigInteger> result=currentValue;
				move();
				return result;
			}
			
		}
		@Override
		public Iterator<Pair<BigInteger, BigInteger>> iterator() {
			return new InternalIterator();
		}
	}
	public static class RescaledSolution2D implements Solution2D	{
		private final Solution2D baseSolution;
		private final BigInteger g;
		private RescaledSolution2D(Solution2D baseSolution,BigInteger g)	{
			this.baseSolution=baseSolution;
			this.g=g;
		}
		@Override
		public Iterator<Pair<BigInteger, BigInteger>> iterator() {
			return new Iterator<>()	{
				private final Iterator<Pair<BigInteger,BigInteger>> baseIterator=baseSolution.iterator();
				@Override
				public boolean hasNext() {
					return baseIterator.hasNext();
				}
				@Override
				public Pair<BigInteger, BigInteger> next() {
					Pair<BigInteger,BigInteger> base=baseIterator.next();
					return new Pair<>(base.first.multiply(g),base.second.multiply(g));
				}
			};
		}
	}
	
	/*
	 * Gives the (possibly infinite) integer solutions to the following equations:
	 * Ax^2 + Bxy + Cy^2 + Dx + Ey + F = 0
	 */
	public static List<Solution2D> solveDiophantineEquation(long a,long b,long c,long d,long e,long f,PrimeDecomposer decomposer)	{
		if ((a==0)&&(c==0))	{
			return (b==0)?solveLinearCase(d,e,f):solveSimpleHyperbolicCase(b,d,e,f,decomposer);
		}
		BigInteger bigA=BigInteger.valueOf(a);
		BigInteger bigB=BigInteger.valueOf(b);
		BigInteger bigC=BigInteger.valueOf(c);
		BigInteger det=bigB.multiply(bigB).subtract(FOUR.multiply(bigA).multiply(bigC));
		int detSign=det.signum();
		if (detSign>0) return solveHyperbolicCase(a,b,c,d,e,f,decomposer);
		else if (detSign<0) return solveEllipticalCase(a,b,c,d,e,f);
		else return solveParabolicCase(a,b,c,d,e,f);
	}
	
	public static List<Solution2D> solveLinearCase(long d,long e,long f)	{
		if (d==0)	{
			if (e==0) throw new ArithmeticException("Malformed equation.");
			if ((f%e)!=0) return Collections.emptyList();
			BigInteger y=BigInteger.valueOf(-f/e);
			Solution1D solutionX=ALL_INTEGERS;
			Solution1D solutionY=new SingleSolution1D(y);
			return Collections.singletonList(new CrossedSolution2D(solutionX,solutionY));
		}	else if (e==0)	{
			if ((f%d)!=0) return Collections.emptyList();
			BigInteger x=BigInteger.valueOf(-f/d);
			Solution1D solutionX=new SingleSolution1D(x);
			Solution1D solutionY=ALL_INTEGERS;
			return Collections.singletonList(new CrossedSolution2D(solutionX,solutionY));
		}	else	{
			long g=EulerUtils.gcd(d,e);
			if ((f%g)!=0) return Collections.emptyList();
			d/=g;
			e/=g;
			f/=g;
			long[] coeffs=EulerUtils.extendedGcd(d,e).coeffs;
			long baseX=-coeffs[0]*f;
			long baseY=-coeffs[1]*f;
			return Collections.singletonList(new LinearSolution2D(BigInteger.valueOf(baseX),BigInteger.valueOf(baseY),BigInteger.valueOf(e),BigInteger.valueOf(-d)));
		}
	}
	
	// OK. I need an object to abstract prime decompositions.
	public static List<Solution2D> solveSimpleHyperbolicCase(long b,long d,long e,long f,PrimeDecomposer decomposer)	{
		BigInteger bigB=BigInteger.valueOf(b);
		BigInteger bigD=BigInteger.valueOf(d);
		BigInteger bigE=BigInteger.valueOf(e);
		BigInteger bigF=BigInteger.valueOf(f);
		BigInteger de_bf=bigD.multiply(bigE).subtract(bigB.multiply(bigF));
		if (de_bf.signum()==0)	{
			List<Solution2D> result=new ArrayList<>();
			if ((e%b)==0)	{
				Solution1D solutionX=new SingleSolution1D(BigInteger.valueOf(-e/b));
				Solution1D solutionY=ALL_INTEGERS;
				result.add(new CrossedSolution2D(solutionX,solutionY));
			}
			if ((d%b)==0)	{
				Solution1D solutionX=ALL_INTEGERS;
				Solution1D solutionY=new SingleSolution1D(BigInteger.valueOf(-d/b));
				result.add(new CrossedSolution2D(solutionX,solutionY));
			}
			return result;
		}
		LongSet divisors=decomposer.getPositiveAndNegativeDivisors(de_bf.abs());
		List<Solution2D> result=new ArrayList<>();
		for (LongCursor cursor=divisors.cursor();cursor.moveNext();)	{
			long di=cursor.elem();
			long n1=di-e;
			long n2=de_bf.divide(BigInteger.valueOf(di)).longValue()-d;
			if (((n1%b)==0)&&((n2%b)==0)) result.add(new SingleSolution2D(BigInteger.valueOf(n1/b),BigInteger.valueOf(n2/b)));
		}
		return result;
	}
	
	public static List<Solution2D> solveEllipticalCase(long a,long b,long c,long d,long e,long f)	{
		double dA=(double)a;
		double dB=(double)b;
		double dC=(double)c;
		double dD=(double)d;
		double dE=(double)e;
		double dF=(double)f;
		// Quadratic equation in X...
		double qA=dB*dB-4*dA*dC;
		double qB=2*(dB*dE-2*dC*dD);
		double qC=dE*dE-4*dC*dF;
		double det=qB*qB-4*qA*qC;
		if (det<0) return Collections.emptyList();
		double sDet=Math.sqrt(det);
		double sol1=(-qB-sDet)/(2*qA);
		double sol2=(-qB+sDet)/(2*qA);
		if (sol1>sol2)	{
			double swap=sol1;
			sol1=sol2;
			sol2=swap;
		}
		BigInteger minX=BigInteger.valueOf((long)Math.ceil(sol1));
		BigInteger maxX=BigInteger.valueOf((long)Math.floor(sol2));
		List<Solution2D> result=new ArrayList<>();
		BigInteger bA=BigInteger.valueOf(a);
		BigInteger bB=BigInteger.valueOf(b);
		BigInteger bC=BigInteger.valueOf(c);
		BigInteger bD=BigInteger.valueOf(d);
		BigInteger bE=BigInteger.valueOf(e);
		BigInteger bF=BigInteger.valueOf(f);
		for (BigInteger x=minX;x.compareTo(maxX)<=0;x=x.add(BigInteger.ONE))	{
			// Ax^2+Bxy+Cy^2+Dx+Ey+F=0 -> Cy^2+(B*x+E)y+(Ax^2+Dx+F)=0.
			BigInteger yB=bB.multiply(x).add(bE);
			BigInteger yC=bA.multiply(x).add(bD).multiply(x).add(bF);
			List<BigInteger> ys=solveQuadratic1D(bC,yB,yC);	// yA would be bC.
			for (BigInteger y:ys) result.add(new SingleSolution2D(x,y));
		}
		return result;
	}
	
	private static List<BigInteger> solveQuadratic1D(BigInteger a,BigInteger b,BigInteger c)	{
		if (a.signum()==0)	{
			// a=0, we just need to solve bx+c=0 -> x=-c/b.
			BigInteger[] c_b=c.divideAndRemainder(b);
			if (c_b[1].signum()==0) return Collections.singletonList(c_b[0].negate());
			else return Collections.emptyList();
		}
		BigInteger det=b.multiply(b).subtract(FOUR.multiply(a).multiply(c));
		if (det.signum()<0) return Collections.emptyList();
		BigInteger sDet=det.sqrt();
		if (sDet.multiply(sDet).equals(det))	{
			BigInteger b_=b.negate();
			BigInteger n1=b_.add(sDet);
			BigInteger n2=b_.subtract(sDet);
			BigInteger den=a.add(a);
			List<BigInteger> result=new ArrayList<>();
			BigInteger[] div1=n1.divideAndRemainder(den);
			if (div1[1].signum()==0) result.add(div1[0]);
			BigInteger[] div2=n2.divideAndRemainder(den);
			if (div2[1].signum()==0) result.add(div2[0]);
			return result;
		}	else return Collections.emptyList();
	}
	
	public static List<Solution2D> solveParabolicCase(long A,long B,long C,long d,long e,long f)	{
		long g=EulerUtils.gcd(Math.abs(A),Math.abs(C));
		if (A<0) g=-g;
		long a=A/g;
		long c=C/g;
		long sA=LongMath.sqrt(a,RoundingMode.UNNECESSARY);
		long sC=LongMath.sqrt(c,RoundingMode.UNNECESSARY);
		if (B/A<0) sC=-sC;
		List<Solution2D> result=new ArrayList<>();
		if (sC*d==sA*e)	{
			List<BigInteger> sols=solveQuadratic1D(BigInteger.valueOf(sA*g),BigInteger.valueOf(d),BigInteger.valueOf(sA*f));
			for (BigInteger u:sols) result.addAll(solveLinearCase(sA,sC,-u.longValueExact()));
		}	else	{
			long n=sC*d-sA*e;
			long absN=Math.abs(n);
			for (long u=0;u<absN;++u)	{
				/*
				 * There is a better way of finding valid "u" values (Tonelli-Shanks!!). But unless I specifically need it because of performance
				 * reasons, I'm not going to bother.
				 */
				long poly=(sA*g*u+d)*u+sA*f;
				if ((poly%n)==0)	{
					long poly2=(sC*g*u+e)*u+sC*f;
					assert poly2%n==0;
					BigInteger x0=BigInteger.valueOf(-poly2/n);
					BigInteger linX=BigInteger.valueOf(-(e+2*sC*g*u));
					BigInteger quadX=BigInteger.valueOf(sC*g*(-n));
					BigInteger y0=BigInteger.valueOf(poly/n);
					BigInteger linY=BigInteger.valueOf(d+2*sA*g*u);
					BigInteger quadY=BigInteger.valueOf(sA*g*n);
					result.add(new QuadraticSolution2D(x0,linX,quadX,y0,linY,quadY));
				}
			}
		}
		return result;
	}

	public static List<Solution2D> solveHyperbolicCase(long a,long b,long c,long d,long e,long f,PrimeDecomposer decomposer)	{
		BigInteger bA=BigInteger.valueOf(a);
		BigInteger bB=BigInteger.valueOf(b);
		BigInteger bC=BigInteger.valueOf(c);
		BigInteger bD=BigInteger.valueOf(d);
		BigInteger bE=BigInteger.valueOf(e);
		BigInteger bF=BigInteger.valueOf(f);
		BigInteger alpha=BigInteger.TWO.multiply(bC).multiply(bD).subtract(bB.multiply(bE));
		BigInteger beta=BigInteger.TWO.multiply(bA).multiply(bE).subtract(bB.multiply(bD));
		BigInteger det=bB.multiply(bB).subtract(FOUR.multiply(bA).multiply(bC));
		BigInteger ae2=bA.multiply(bE).multiply(bE);
		BigInteger bed=bB.multiply(bE).multiply(bD);
		BigInteger cd2=bC.multiply(bD).multiply(bD);
		BigInteger fD=bF.multiply(det);
		BigInteger newF=det.multiply(bed.subtract(ae2).subtract(cd2).subtract(fD));
		List<Solution2D> homogeneousSolution=solveHyperbolicHomogeneousCase(a,b,c,newF,decomposer);
		List<Solution2D> result=new ArrayList<>(homogeneousSolution.size());
		for (Solution2D sol:homogeneousSolution) result.add(new DisplacedSolution2D(sol,alpha,beta,det));
		return result;
	}
	
	private static List<Solution2D> solveHyperbolicHomogeneousCase(long a,long b,long c,BigInteger bF,PrimeDecomposer decomposer)	{
		BigInteger bA=BigInteger.valueOf(a);
		BigInteger bB=BigInteger.valueOf(b);
		BigInteger bC=BigInteger.valueOf(c);
		BigInteger det=bB.multiply(bB).subtract(FOUR.multiply(bA).multiply(bC));
		BigInteger[] sDet=det.sqrtAndRemainder();
		List<Solution2D> result=new ArrayList<>();
		if (bF.signum()==0)	{
			result.add(new SingleSolution2D(BigInteger.ZERO,BigInteger.ZERO));
			if (sDet[1].signum()==0)	{	// Additional solutions when B^2-4AC is integer.
				long k=sDet[0].longValueExact();
				/*
				 * This will (probably) result in (0,0) appearing more than once. Sorry!
				 */
				result.addAll(solveLinearCase(2*a,b+k,0));
				result.addAll(solveLinearCase(2*a,b-k,0));
			}
		}	else if (sDet[1].signum()==0)	{
			BigInteger k=sDet[0];
			BigInteger af4=FOUR.multiply(bA).multiply(bF);
			LongSet divs=decomposer.getPositiveAndNegativeDivisors(af4);
			BigInteger bk=bB.add(k);
			BigInteger k2=k.add(k);
			BigInteger a2=bA.add(bA);
			for (LongCursor cursor=divs.cursor();cursor.moveNext();)	{
				BigInteger u=BigInteger.valueOf(cursor.elem());
				BigInteger numY=u.add(af4.divide(u));
				BigInteger[] y=numY.divideAndRemainder(k2);
				if (y[1].signum()!=0) continue;
				BigInteger numX=u.subtract(bk.multiply(y[0]));
				BigInteger[] x=numX.divideAndRemainder(a2);
				if (x[1].signum()==0) result.add(new SingleSolution2D(x[0],y[0]));
			}
		}	else	{
			BigInteger g=EulerUtils.gcd(bA,EulerUtils.gcd(bB,bC));
			if (BigInteger.ONE.equals(g)) return solveHyperbolicImpl(bA,bB,bC,bF,decomposer);
			BigInteger[] div=bF.divideAndRemainder(g.multiply(g));
			if (div[1].signum()!=0) return Collections.emptyList();
			List<Solution2D> baseSols=solveHyperbolicImpl(bA,bB,bC,div[0],decomposer);
			return baseSols.stream().map((Solution2D sol)->new RescaledSolution2D(sol,g)).collect(Collectors.toUnmodifiableList());
		}
		return result;
	}
	// Gets the (x,y) solutions to an equation of the form Ax^2+Bxy+Cy^2=1.
	private static List<Pair<BigInteger,BigInteger>> solveSimplifiedQuadratic(BigInteger bA,BigInteger bB,BigInteger bC,boolean getAllRoots)	{
		List<Pair<BigInteger,BigInteger>> result=new ArrayList<>();
		List<QuadraticRational> realQuadRoots=new SecondGradeEquation(bA,bB,bC).solve();
		for (QuadraticRational root:realQuadRoots)	{
			ContinuedFraction continuedFraction=QuadraticRationalContinuedFraction.getFor(root,IterationType.SINGLE_PASS);
			for (Convergent conv:continuedFraction) if (bA.multiply(conv.p.multiply(conv.p)).add(bB.multiply(conv.p.multiply(conv.q))).add(bC.multiply(conv.q.multiply(conv.q))).equals(BigInteger.ONE))	{
				result.add(new Pair<>(conv.p,conv.q));
				if (!getAllRoots) break;	// In some cases we only want one solution per continued fraction.
			}
		}
		return result;
	}
	private static List<Solution2D> solveHyperbolicImpl(BigInteger bA,BigInteger bB,BigInteger bC,BigInteger bF,PrimeDecomposer decomposer)	{
		// THIS IS IT. The final frontier!
		List<Pair<BigInteger,BigInteger>> rawResult=new ArrayList<>();
		boolean isNegative=bF.signum()<0;
		SquareFactorsIterator squareFactors=new SquareFactorsIterator(bF,decomposer.decompose(bF.abs()));
		while (squareFactors.hasNext())	{
			boolean areXAndYSwapped=false;
			ObjIntMap<BigInteger> smallFDecomp=squareFactors.getQuotientDecomposition();
			Set<BigInteger> auxQuadraticSolutions=DiophantineUtils.quadraticPolynomialRootsModuloAnyNumber(bA,bB,bC,smallFDecomp);
			if (auxQuadraticSolutions.isEmpty())	{
				areXAndYSwapped=true;
				BigInteger swap=bA;
				bA=bC;
				bC=swap;
				auxQuadraticSolutions=DiophantineUtils.quadraticPolynomialRootsModuloAnyNumber(bA,bB,bC,smallFDecomp);
				if (auxQuadraticSolutions.isEmpty()) continue;
			}
			// Some solution, any one is good.
			BigInteger s=auxQuadraticSolutions.iterator().next();
			BigInteger smallF=squareFactors.getQuotient();
			BigInteger factor=BigInteger.valueOf(squareFactors.getRoot());
			if (isNegative) smallF=smallF.negate();
			BigInteger newA=bA.multiply(s).add(bB).multiply(s).add(bC).divide(smallF).negate();
			BigInteger newB=BigInteger.TWO.multiply(s).multiply(bA).add(bB);
			BigInteger newC=bA.multiply(smallF).negate();
			for (Pair<BigInteger,BigInteger> quadSolution:solveSimplifiedQuadratic(newA,newB,newC,true))	{
				BigInteger y=quadSolution.first;
				BigInteger x=s.multiply(y).subtract(smallF.multiply(quadSolution.second));
				y=y.multiply(factor);
				x=x.multiply(factor);
				Pair<BigInteger,BigInteger> rawSolution=(areXAndYSwapped?new Pair<>(y,x):new Pair<>(x,y));
				rawResult.add(rawSolution);
			}
			if (areXAndYSwapped)	{
				// Undo swap. There are better ways to do this... just use other variables!
				BigInteger swap=bA;
				bA=bC;
				bC=swap;
			}
		}
		if (rawResult.isEmpty()) return Collections.emptyList();
		List<Pair<BigInteger,BigInteger>> recursiveData=solveSimplifiedQuadratic(BigInteger.ONE,bB,bA.multiply(bC),false);
		if (recursiveData.isEmpty()) return rawResult.stream().map((Pair<BigInteger,BigInteger> solution)->new SingleSolution2D(solution.first,solution.second)).collect(Collectors.toUnmodifiableList());
		List<Solution2D> result=new ArrayList<>(rawResult.size()*recursiveData.size());
		for (Pair<BigInteger,BigInteger> recursiveSolution:recursiveData)	{
			BigInteger p=recursiveSolution.first;
			BigInteger q=bA.multiply(recursiveSolution.second);
			BigInteger r=bC.multiply(recursiveSolution.second).negate();
			BigInteger s=p.add(bB.multiply(recursiveSolution.second));
			for (Pair<BigInteger,BigInteger> baseSolution:rawResult) result.add(new RecursiveSolution2D(baseSolution.first,baseSolution.second,p,q,r,s));
		}
		return result;
	}
	
	/*
	 * ALMOST FINISHED!! NO, REALLY, FOR REAL!!!!!
	 * 
	 * Pending:
	 * 	1) Determine how many convergents do I need to use in that TODO from line 617.
	 *   The holy oracle of Alpertron says:
	 *   "The signs in the third column are alternated, so the numbers will repeat after an even number of convergents. Therefore two entire
	 *   periods should be considered if the period length is odd. If it is even, only one period should be considered. With these solutions and
	 *   the recurrence relation to be developed in the next section we can find all solutions of the homogeneous equation"
	 *   This should be easy to implement, although it does clash a little with my current architecture.
	 *   GEMACHT!!!!!
	 *  2) I'm willing to bet that there is a much better way to find elliptical solutions. Play with Alpertron until the "show steps" button
	 *  confesses all its secrets.
	 *  		WAIT, the method is just basically THE SAME as for hyperbolic, but without the recursion? This is AWESOME.
	 *  2') There is a SUPER SIMPLE way to solve parabolic equations!! This is even more AWESOME!
	 *  	Actually not so simple for cases D!=0, E!=0.
	 *  	Also I should experiment with Legendre's transform. It must not be equivalent to the actual method because the solutions look different,
	 *  	but what's the result of using it?
	 *  3) Run some experiments, solve the billion bugs.
	 *  4) Refactor, make the code easier to follow and so on.
	 */
}