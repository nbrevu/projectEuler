package com.euler.common.alpertron;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.euler.common.EulerUtils.Pair;
import com.euler.common.Primes.PrimeDecomposer;
import com.euler.common.Primes.StandardPrimeDecomposer;
import com.google.common.collect.ImmutableSet;
import com.google.common.math.IntMath;

public class Diophantine2dSolverTest {
	private static PrimeDecomposer getDecomposer(int maxDigits)	{
		return new StandardPrimeDecomposer(IntMath.pow(10,maxDigits));
	}
	private Set<String> getSolutionStrings(Collection<? extends DiophantineSolution> solutions)	{
		return solutions.stream().map(Object::toString).collect(Collectors.toUnmodifiableSet());
	}
	
	/*-
	 * OK. Here we go.
	 * 
	 * Tests:
	 * OK	- Linear case, two variables.
	 * OK	- Linear case, actually a single variable.
	 * OK	- Linear case, non coprime but with solutions.
	 * OK	- Linear case, no solutions.
	 * OK - Simple hyperbolic case, decomposable, two solutions.
	 * OK - Simple hyperbolic case, decomposable, one solutions.
	 * OK - Simple hyperbolic case, decomposable, no solutions.
	 * OK - Simple hyperbolic case, non decomposable.
	 * OK - Parabolic case, alpha=0, beta=0.
	 * OK - Parabolic case, alpha=0, beta!=0.
	 * OK - Parabolic case, alpha=0, beta=k^2.
	 * OK - Parabolic case, alpha!=0.
	 * OK - Elliptical case, no Legendre transform, F=0.
	 * OK - Elliptical case, no Legendre transform, F!=0, no unimodular trasform.
	 * OK - Elliptical case, no Legendre transform, F!=0, unimodular transform.
	 * OK - Elliptical case, Legendre transform, F=0.
	 * OK - Elliptical case, Legendre transform, F!=0, no unimodular trasform.
	 * OK - Elliptical case, Legendre transform, F!=0, unimodular transform.
	 * OK - Hyperbolic case, no Legendre transform, det=k^2, F=0.
	 * OK - Hyperbolic case, no Legendre transform, det=k^2, F!=0.
	 * OK - Hyperbolic case, no Legendre transform, det!=k^2, F=0.
	 * OK - Hyperbolic case, no Legendre transform, det!=k^2, F!=0, no unimodular trasform.
	 * OK - Hyperbolic case, no Legendre transform, det!=k^2, F!=0, unimodular transform.
	 * OK - Hyperbolic case, Legendre transform, det=k^2, F=0.
	 * OK - Hyperbolic case, Legendre transform, det=k^2, F!=0.
	 * OK - Hyperbolic case, Legendre transform, det!=k^2, F=0.
	 * OK - Hyperbolic case, Legendre transform, det!=k^2, F!=0, no unimodular trasform.
	 * OK - Hyperbolic case, Legendre transform, det!=k^2, F!=0, unimodular transform  (ZUTUN! The VERY BIG case is failing...).
	 * OK - Also, for fun: x^2+y^2=<Some big pythagorean number>.
	 * OK - Also, for fun: some Pell equation. 661, maybe?
	 * OK - Also, for fun: some equation without solutions.
	 */
	@Test
	public void testLinearCaseSimple()	{
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(0l,0l,0l,3l,7l,13l,null);
		assertEquals(1,solutions.size());
		assertEquals("x=7t+26; y=-3t-13",solutions.get(0).toString());
	}
	@Test
	public void testLinearCaseNonCoprime()	{
 		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(0l,0l,0l,504l,636l,1728l,null);
		assertEquals(1,solutions.size());
		assertEquals("x=53t-3456; y=-42t+2736",solutions.get(0).toString());
	}
	@Test
	public void testLinearCaseNoSolutions()	{
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(0l,0l,0l,28l,35l,999l,null);
		assertEquals(0,solutions.size());
	}
	@Test
	public void testLinearCaseOneVariable()	{
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(0l,0l,0l,3l,0l,12l,null);
		assertEquals(1,solutions.size());
		assertEquals("x=-4; y=t",solutions.get(0).toString());
	}
	@Test
	public void testSimpleHyperbolicDecomposableTwoSolutions()	{
		// (3x+6)*(5y-15) = 15xy-45x+30y-90
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(0l,15l,0l,-45l,30l,-90l,null);
		assertEquals(2,solutions.size());
		Set<String> solStrings=getSolutionStrings(solutions);
		assertTrue(solStrings.contains("x=-2; y=t"));
		assertTrue(solStrings.contains("x=t; y=3"));
	}
	@Test
	public void testSimpleHyperbolicDecomposableOneSolution()	{
		// (8x-7)*(-2y+4)=-16xy+32x+14y-28
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(0l,-16l,0l,32l,14l,-28l,null);
		assertEquals(1,solutions.size());
		assertEquals("x=t; y=2",solutions.get(0).toString());
	}
	@Test
	public void testSimpleHyperbolicNonDecomposableWithSolutions()	{
		// Example taken straight from Alpertron.
		BigInteger b=BigInteger.TWO;
		BigInteger d=BigInteger.valueOf(5l);
		BigInteger e=BigInteger.valueOf(56l);
		BigInteger f=BigInteger.valueOf(7l);
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(BigInteger.ZERO,b,BigInteger.ZERO,d,e,f,getDecomposer(3));
		Set<String> solStrings=getSolutionStrings(solutions);
		Set<String> expectedSolutions=ImmutableSet.of("(-27,64)","(-29,-69)","(-21,7)","(-35,-12)","(-9,1)","(-47,-6)","(105,-2)","(-161,-3)");
		assertEquals(expectedSolutions,solStrings);
		for (DiophantineSolution sol:solutions)	{
			assertTrue(sol instanceof FixedSolution);
			FixedSolution fixed=(FixedSolution)sol;
			BigInteger x=fixed.x;
			BigInteger y=fixed.y;
			assertEquals(BigInteger.ZERO,b.multiply(x).multiply(y).add(d.multiply(x)).add(e.multiply(y)).add(f));
		}
	}
	@Test
	public void testParabolicAlphaZeroBetaZero()	{
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(1l,2l,1l,2l,2l,1l,null);
		assertEquals(1,solutions.size());
		assertEquals("x=t; y=-t-1",solutions.get(0).toString());
	}
	@Test
	public void testParabolicAlphaZeroBetaSquare()	{
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(1l,2l,1l,5l,5l,4l,null);
		assertEquals(2,solutions.size());
		Set<String> solStrings=getSolutionStrings(solutions);
		assertTrue(solStrings.contains("x=t; y=-t-4"));
		assertTrue(solStrings.contains("x=t; y=-t-1"));
	}
	@Test
	public void testParabolicAlphaZeroBetaSquare2()	{
		// Just another example. I'm not too happy with the previous one, it's too smooth and simple.
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(-3l,18l,-27l,1l,-3l,14l,null);
		assertEquals(1,solutions.size());
		assertEquals("x=3t-2; y=t",solutions.get(0).toString());
	}
	@Test
	public void testParabolicAlphaZeroBetaNegative()	{
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(-3l,18l,-27l,1l,-3l,-2l,null);
		assertEquals(0,solutions.size());
	}
	@Test
	public void testParabolicAlphaZeroBetaPositiveNonSquare()	{
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(-3l,18l,-27l,1l,-3l,7l,null);
		assertEquals(0,solutions.size());
	}
	@Test
	public void testParabolicAlphaNonZero()	{
		// I can't believe this worked on the first try. I'm about to cry. And I don't say that (just) because it rhymes.
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(7l,-70l,175l,-3l,12l,26l,getDecomposer(2));
		assertEquals(4,solutions.size());
		Set<String> solStrings=getSolutionStrings(solutions);
		assertTrue(solStrings.contains("x=420t^2+256t+82; y=84t^2+50t+16"));
		assertTrue(solStrings.contains("x=420t^2+536t+214; y=84t^2+106t+42"));
		assertTrue(solStrings.contains("x=420t^2+676t+315; y=84t^2+134t+62"));
		assertTrue(solStrings.contains("x=420t^2+116t+51; y=84t^2+22t+10"));
	}
	
	@Test
	public void testEllipticalNoLegendreHomogeneous()	{
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(1l,2l,8l,0l,0l,0l,null);
		assertEquals(1,solutions.size());
		assertEquals("(0,0)",solutions.get(0).toString());
	}
	
	@Test
	public void testEllipticalNoLegendreNonHomogeneousNoUnimodular()	{
		BigInteger a=BigInteger.ONE;
		BigInteger b=BigInteger.TWO;
		BigInteger c=BigInteger.valueOf(8l);
		BigInteger f=BigInteger.valueOf(-11l);
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(a,b,c,BigInteger.ZERO,BigInteger.ZERO,f,getDecomposer(5));
		assertEquals(4,solutions.size());
		Set<String> solStrings=getSolutionStrings(solutions);
		Set<String> expectedSolutions=ImmutableSet.of("(1,1)","(-1,-1)","(3,-1)","(-3,1)");
		assertEquals(expectedSolutions,solStrings);
		for (DiophantineSolution sol:solutions)	{
			assertTrue(sol instanceof FixedSolution);
			FixedSolution fixed=(FixedSolution)sol;
			BigInteger x=fixed.x;
			BigInteger y=fixed.y;
			assertEquals(BigInteger.ZERO,a.multiply(x.multiply(x)).add(b.multiply(x.multiply(y))).add(c.multiply(y.multiply(y))).add(f));
		}
	}
	
	@Test
	public void testEllipticalNoLegendreNonHomogeneousUnimodular()	{
		// Another one that, incredibly, worked on the first try. GO, GO, GO!
		BigInteger a=BigInteger.TWO;
		BigInteger b=BigInteger.valueOf(3);
		BigInteger c=BigInteger.TWO;
		BigInteger f=BigInteger.valueOf(-1108l);
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(a,b,c,BigInteger.ZERO,BigInteger.ZERO,f,getDecomposer(4));
		assertEquals(12,solutions.size());
		Set<String> solStrings=getSolutionStrings(solutions);
		Set<String> expectedSolutions=ImmutableSet.of("(-2,-22)","(2,22)","(22,2)","(-22,-2)","(22,-35)","(-22,35)","(2,-25)","(-2,25)","(25,-2)","(-25,2)","(35,-22)","(-35,22)");
		assertEquals(expectedSolutions,solStrings);
		for (DiophantineSolution sol:solutions)	{
			assertTrue(sol instanceof FixedSolution);
			FixedSolution fixed=(FixedSolution)sol;
			BigInteger x=fixed.x;
			BigInteger y=fixed.y;
			assertEquals(BigInteger.ZERO,a.multiply(x.multiply(x)).add(b.multiply(x.multiply(y))).add(c.multiply(y.multiply(y))).add(f));
		}
	}
	
	@Test
	public void testEllipticalLegendreHomogeneous()	{
		BigInteger a=BigInteger.ONE;
		BigInteger b=BigInteger.valueOf(-1l);
		BigInteger c=BigInteger.ONE;
		BigInteger d=BigInteger.valueOf(5l);
		BigInteger e=BigInteger.valueOf(14l);
		BigInteger f=BigInteger.valueOf(97l);
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(a,b,c,d,e,f,null);
		assertEquals(1,solutions.size());
		assertEquals("(-8,-11)",solutions.get(0).toString());
		for (DiophantineSolution sol:solutions)	{
			assertTrue(sol instanceof FixedSolution);
			FixedSolution fixed=(FixedSolution)sol;
			BigInteger x=fixed.x;
			BigInteger y=fixed.y;
			assertEquals(BigInteger.ZERO,a.multiply(x.multiply(x)).add(b.multiply(x.multiply(y))).add(c.multiply(y.multiply(y))).add(d.multiply(x)).add(e.multiply(y)).add(f));
		}
	}
	
	@Test
	public void testEllipticalLegendreNonHomogeneousNoUnimodular()	{
		// Oooh, very subtle bug in the Tonelli-Shanks maze of functions. But it's SOLVED now!
		BigInteger a=BigInteger.valueOf(-1l);
		BigInteger b=BigInteger.valueOf(3l);
		BigInteger c=BigInteger.valueOf(-7l);
		BigInteger d=BigInteger.ZERO;
		BigInteger e=BigInteger.valueOf(-12l);
		BigInteger f=BigInteger.valueOf(11l);
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(a,b,c,d,e,f,getDecomposer(4));
		assertEquals(2,solutions.size());
		Set<String> solStrings=getSolutionStrings(solutions);
		Set<String> expectedSolutions=ImmutableSet.of("(-7,-2)","(1,-2)");
		assertEquals(expectedSolutions,solStrings);
		for (DiophantineSolution sol:solutions)	{
			assertTrue(sol instanceof FixedSolution);
			FixedSolution fixed=(FixedSolution)sol;
			BigInteger x=fixed.x;
			BigInteger y=fixed.y;
			assertEquals(BigInteger.ZERO,a.multiply(x.multiply(x)).add(b.multiply(x.multiply(y))).add(c.multiply(y.multiply(y))).add(d.multiply(x)).add(e.multiply(y)).add(f));
		}
	}
	
	@Test
	public void testEllipticalLegendreNonHomogeneousUnimodular()	{
		BigInteger a=BigInteger.valueOf(12l);
		BigInteger b=BigInteger.valueOf(25l);
		BigInteger c=BigInteger.valueOf(37l);
		BigInteger d=BigInteger.valueOf(18l);
		BigInteger e=BigInteger.valueOf(94l);
		BigInteger f=BigInteger.valueOf(-106l);
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(a,b,c,d,e,f,getDecomposer(7));
		assertEquals(1,solutions.size());
		Set<String> solStrings=getSolutionStrings(solutions);
		Set<String> expectedSolutions=ImmutableSet.of("(5,-4)");
		assertEquals(expectedSolutions,solStrings);
		for (DiophantineSolution sol:solutions)	{
			assertTrue(sol instanceof FixedSolution);
			FixedSolution fixed=(FixedSolution)sol;
			BigInteger x=fixed.x;
			BigInteger y=fixed.y;
			assertEquals(BigInteger.ZERO,a.multiply(x.multiply(x)).add(b.multiply(x.multiply(y))).add(c.multiply(y.multiply(y))).add(d.multiply(x)).add(e.multiply(y)).add(f));
		}
	}
	
	@Test
	public void testEllipticalAnothor()	{
		BigInteger a=BigInteger.ONE;
		BigInteger b=BigInteger.ONE;
		BigInteger c=BigInteger.valueOf(3l);
		BigInteger d=BigInteger.valueOf(-15l);
		BigInteger e=BigInteger.valueOf(-6l);
		BigInteger f=BigInteger.valueOf(-54l);
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(a,b,c,d,e,f,getDecomposer(5));
		assertEquals(12,solutions.size());
		Set<String> solStrings=getSolutionStrings(solutions);
		Set<String> expectedSolutions=ImmutableSet.of("(18,-4)","(3,-5)","(17,-5)","(1,-4)","(3,6)","(18,0)","(-3,0)","(6,6)","(15,3)","(15,-6)","(6,-6)","(-3,3)");
		assertEquals(expectedSolutions,solStrings);
		for (DiophantineSolution sol:solutions)	{
			assertTrue(sol instanceof FixedSolution);
			FixedSolution fixed=(FixedSolution)sol;
			BigInteger x=fixed.x;
			BigInteger y=fixed.y;
			assertEquals(BigInteger.ZERO,a.multiply(x.multiply(x)).add(b.multiply(x.multiply(y))).add(c.multiply(y.multiply(y))).add(d.multiply(x)).add(e.multiply(y)).add(f));
		}
	}
	
	@Test
	public void testHyperbolicNoLegendreSquareDetHomogeneous()	{
		BigInteger a=BigInteger.valueOf(-7l);
		BigInteger b=BigInteger.valueOf(-36l);
		BigInteger c=BigInteger.valueOf(-5l);
		BigInteger d=BigInteger.ZERO;
		BigInteger e=BigInteger.ZERO;
		BigInteger f=BigInteger.ZERO;
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(a,b,c,d,e,f,null);
		assertEquals(2,solutions.size());
		Set<String> solStrings=getSolutionStrings(solutions);
		Set<String> expectedSolutions=ImmutableSet.of("x=-t; y=7t","x=-5t; y=t");
		assertEquals(expectedSolutions,solStrings);
	}
	
	@Test
	public void testHyperbolicNoLegendreSquareDetNonHomogeneous()	{
		BigInteger a=BigInteger.valueOf(10l);
		BigInteger b=BigInteger.valueOf(51l);
		BigInteger c=BigInteger.valueOf(56l);
		BigInteger d=BigInteger.ZERO;
		BigInteger e=BigInteger.ZERO;
		BigInteger f=BigInteger.valueOf(-620l);
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(a,b,c,d,e,f,getDecomposer(2));
		assertEquals(4,solutions.size());
		Set<String> solStrings=getSolutionStrings(solutions);
		Set<String> expectedSolutions=ImmutableSet.of("(228,-65)","(-228,65)","(3,2)","(-3,-2)");
		assertEquals(expectedSolutions,solStrings);
		for (DiophantineSolution sol:solutions)	{
			assertTrue(sol instanceof FixedSolution);
			FixedSolution fixed=(FixedSolution)sol;
			BigInteger x=fixed.x;
			BigInteger y=fixed.y;
			assertEquals(BigInteger.ZERO,a.multiply(x.multiply(x)).add(b.multiply(x.multiply(y))).add(c.multiply(y.multiply(y))).add(d.multiply(x)).add(e.multiply(y)).add(f));
		}
	}

	@Test
	public void testHyperbolicNoLegendreNoSquareDetHomogeneous()	{
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(8l,-7l,-14l,0l,0l,0l,null);
		assertEquals(1,solutions.size());
		assertEquals("(0,0)",solutions.get(0).toString());
	}

	@Test
	public void testHyperbolicNoLegendreConvergentsNoUnimodular()	{
		BigInteger a=BigInteger.valueOf(1l);
		BigInteger b=BigInteger.valueOf(4l);
		BigInteger c=BigInteger.valueOf(2l);
		BigInteger d=BigInteger.ZERO;
		BigInteger e=BigInteger.ZERO;
		BigInteger f=BigInteger.valueOf(7l);
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(a,b,c,d,e,f,getDecomposer(2));
		assertEquals(1,solutions.size());
		assertTrue(solutions.get(0) instanceof RecursiveSolution);
		Set<FixedSolution> baseSolutions=((RecursiveSolution)solutions.get(0)).getBaseSolutions();
		Set<String> solStrings=getSolutionStrings(baseSolutions);
		Set<String> expectedSolutions=ImmutableSet.of("(-5,2)","(5,-2)","(-3,2)","(3,-2)");
		assertTrue(solStrings.containsAll(expectedSolutions));
		Set<FixedSolution> iteratedOver=new HashSet<>();
		for (Pair<BigInteger,BigInteger> sol:solutions.get(0))	{
			BigInteger x=sol.first;
			BigInteger y=sol.second;
			FixedSolution fs=new FixedSolution(x,y);
			assertFalse(iteratedOver.contains(fs));
			assertEquals(BigInteger.ZERO,a.multiply(x.multiply(x)).add(b.multiply(x.multiply(y))).add(c.multiply(y.multiply(y))).add(d.multiply(x)).add(e.multiply(y)).add(f));
			iteratedOver.add(fs);
			if (iteratedOver.size()>=1000) break;
		}
	}

	@Test
	public void testHyperbolicNoLegendreConvergentsWithUnimodular()	{
		BigInteger a=BigInteger.valueOf(5l);
		BigInteger b=BigInteger.valueOf(23l);
		BigInteger c=BigInteger.valueOf(8l);
		BigInteger d=BigInteger.ZERO;
		BigInteger e=BigInteger.ZERO;
		BigInteger f=BigInteger.valueOf(-605l);
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(a,b,c,d,e,f,getDecomposer(3));
		assertEquals(1,solutions.size());
		assertTrue(solutions.get(0) instanceof RecursiveSolution);
		Set<FixedSolution> baseSolutions=((RecursiveSolution)solutions.get(0)).getBaseSolutions();
		Set<String> solStrings=getSolutionStrings(baseSolutions);
		Set<String> expectedSolutions=ImmutableSet.of("(-33,88)","(33,-88)","(-11,0)","(11,0)");
		assertTrue(solStrings.containsAll(expectedSolutions));
		Set<FixedSolution> iteratedOver=new HashSet<>();
		for (Pair<BigInteger,BigInteger> sol:solutions.get(0))	{
			BigInteger x=sol.first;
			BigInteger y=sol.second;
			FixedSolution fs=new FixedSolution(x,y);
			assertFalse(iteratedOver.contains(fs));
			assertEquals(BigInteger.ZERO,a.multiply(x.multiply(x)).add(b.multiply(x.multiply(y))).add(c.multiply(y.multiply(y))).add(d.multiply(x)).add(e.multiply(y)).add(f));
			iteratedOver.add(fs);
			if (iteratedOver.size()>=1000) break;
		}
	}

	@Test
	public void testHyperbolicLegendreSquareDetHomogeneous()	{
		BigInteger a=BigInteger.valueOf(2l);
		BigInteger b=BigInteger.valueOf(7l);
		BigInteger c=BigInteger.valueOf(5l);
		BigInteger d=BigInteger.valueOf(8l);
		BigInteger e=BigInteger.valueOf(5l);
		BigInteger f=BigInteger.valueOf(-10);
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(a,b,c,d,e,f,getDecomposer(6));
		assertEquals(2,solutions.size());
		Set<String> solStrings=getSolutionStrings(solutions);
		Set<String> expectedSolutions=ImmutableSet.of("x=5t+5; y=-2t-4","x=t+5; y=-t-4");
		assertEquals(expectedSolutions,solStrings);
	}

	@Test
	public void testHyperbolicLegendreSquareDetNonHomogeneous()	{
		BigInteger a=BigInteger.valueOf(18l);
		BigInteger b=BigInteger.valueOf(81l);
		BigInteger c=BigInteger.valueOf(25l);
		BigInteger d=BigInteger.valueOf(6l);
		BigInteger e=BigInteger.valueOf(5l);
		BigInteger f=BigInteger.valueOf(-218);
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(a,b,c,d,e,f,getDecomposer(6));
		assertEquals(1,solutions.size());
		assertEquals("(6,-1)",solutions.get(0).toString());
	}

	@Test
	public void testHyperbolicLegendreNoSquareDetHomogeneous()	{
		BigInteger a=BigInteger.valueOf(6l);
		BigInteger b=BigInteger.valueOf(12l);
		BigInteger c=BigInteger.valueOf(5l);
		BigInteger d=BigInteger.valueOf(12l);
		BigInteger e=BigInteger.valueOf(94l);
		BigInteger f=BigInteger.valueOf(-1675);
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(a,b,c,d,e,f,null);
		assertEquals(1,solutions.size());
		assertEquals("(-42,41)",solutions.get(0).toString());
	}
	
	@Test
	public void testHyperbolicLegendreConvergentsNoUnimodular()	{
		BigInteger a=BigInteger.valueOf(1l);
		BigInteger b=BigInteger.valueOf(15l);
		BigInteger c=BigInteger.valueOf(6l);
		BigInteger d=BigInteger.valueOf(12l);
		BigInteger e=BigInteger.valueOf(-13l);
		BigInteger f=BigInteger.valueOf(428l);
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(a,b,c,d,e,f,getDecomposer(2));
		assertEquals(1,solutions.size());
		assertTrue(solutions.get(0) instanceof RecursiveSolution);
		Set<FixedSolution> baseSolutions=((RecursiveSolution)solutions.get(0)).getBaseSolutions();
		Set<String> solStrings=getSolutionStrings(baseSolutions);
		Set<String> expectedSolutions=ImmutableSet.of("(24,-4)");
		assertTrue(solStrings.containsAll(expectedSolutions));
		Set<FixedSolution> iteratedOver=new HashSet<>();
		for (Pair<BigInteger,BigInteger> sol:solutions.get(0))	{
			BigInteger x=sol.first;
			BigInteger y=sol.second;
			FixedSolution fs=new FixedSolution(x,y);
			assertFalse(iteratedOver.contains(fs));
			assertEquals(BigInteger.ZERO,a.multiply(x.multiply(x)).add(b.multiply(x.multiply(y))).add(c.multiply(y.multiply(y))).add(d.multiply(x)).add(e.multiply(y)).add(f));
			iteratedOver.add(fs);
			if (iteratedOver.size()>=1000) break;
		}
	}

	@Test
	public void testHyperbolicLegendreConvergentsUnimodular()	{
		BigInteger a=BigInteger.valueOf(12l);
		BigInteger b=BigInteger.valueOf(55l);
		BigInteger c=BigInteger.valueOf(14l);
		BigInteger d=BigInteger.valueOf(-12l);
		BigInteger e=BigInteger.valueOf(-14l);
		BigInteger f=BigInteger.valueOf(306l);
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(a,b,c,d,e,f,getDecomposer(6));
		assertEquals(1,solutions.size());
		assertTrue(solutions.get(0) instanceof RecursiveSolution);
		Set<FixedSolution> baseSolutions=((RecursiveSolution)solutions.get(0)).getBaseSolutions();
		Set<String> solStrings=getSolutionStrings(baseSolutions);
		Set<String> expectedSolutions=ImmutableSet.of("(-2390635698438,554308900585)","(190529386,-44177427)","(4719,-1094)","(-5,18)");
		assertTrue(solStrings.containsAll(expectedSolutions));
		Set<FixedSolution> iteratedOver=new HashSet<>();
		for (Pair<BigInteger,BigInteger> sol:solutions.get(0))	{
			BigInteger x=sol.first;
			BigInteger y=sol.second;
			FixedSolution fs=new FixedSolution(x,y);
			assertFalse(iteratedOver.contains(fs));
			assertEquals(BigInteger.ZERO,a.multiply(x.multiply(x)).add(b.multiply(x.multiply(y))).add(c.multiply(y.multiply(y))).add(d.multiply(x)).add(e.multiply(y)).add(f));
			iteratedOver.add(fs);
			if (iteratedOver.size()>=1000) break;
		}
	}
	
	@Test
	public void testHugeCase()	{
		// IT WORKS. IT FUCKING WORKS.
		BigInteger a=BigInteger.valueOf(108l);
		BigInteger b=BigInteger.valueOf(915l);
		BigInteger c=BigInteger.valueOf(317l);
		BigInteger d=BigInteger.valueOf(-646l);
		BigInteger e=BigInteger.valueOf(-1009l);
		BigInteger f=BigInteger.valueOf(5578l);
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(a,b,c,d,e,f,getDecomposer(6));
		assertEquals(1,solutions.size());
		assertTrue(solutions.get(0) instanceof RecursiveSolution);
		Set<FixedSolution> baseSolutions=((RecursiveSolution)solutions.get(0)).getBaseSolutions();
		Set<String> solStrings=getSolutionStrings(baseSolutions);
		Set<String> expectedSolutions=ImmutableSet.of("(-395629465466373515358225850067895368159,1093177841034625177565640881557212576360)","(17587581710761,-48596872280290)","(-12652195254122003143344381016504615915205734,1560012894859934566739888231754873986147110)","(38341859180599776677944902390091312722000675908746548473593677555016815650944409055598661607086,-105943754191544670626705124828109232518531646355411790162335892695107772864550902735456987986915)");
		assertTrue(solStrings.containsAll(expectedSolutions));
		Set<FixedSolution> iteratedOver=new HashSet<>();
		for (Pair<BigInteger,BigInteger> sol:solutions.get(0))	{
			BigInteger x=sol.first;
			BigInteger y=sol.second;
			FixedSolution fs=new FixedSolution(x,y);
			assertFalse(iteratedOver.contains(fs));
			assertEquals(BigInteger.ZERO,a.multiply(x.multiply(x)).add(b.multiply(x.multiply(y))).add(c.multiply(y.multiply(y))).add(d.multiply(x)).add(e.multiply(y)).add(f));
			iteratedOver.add(fs);
			if (iteratedOver.size()>=1000) break;
		}
	}

	@Test
	public void testPythagorean()	{
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(1l,0l,1l,0l,0l,-25l,getDecomposer(2));
		assertEquals(12,solutions.size());
		Set<String> solStrings=getSolutionStrings(solutions);
		Set<String> expectedSolutions=ImmutableSet.of("(3,4)","(-3,4)","(3,-4)","(-3,-4)","(4,3)","(-4,3)","(4,-3)","(-4,-3)","(0,5)","(0,-5)","(5,0)","(-5,0)");
		assertEquals(expectedSolutions,solStrings);
	}

	@Test
	public void testPell()	{
		BigInteger a=BigInteger.ONE;
		BigInteger b=BigInteger.ZERO;
		BigInteger c=BigInteger.valueOf(-661l);
		BigInteger d=BigInteger.ZERO;
		BigInteger e=BigInteger.ZERO;
		BigInteger f=BigInteger.ONE.negate();
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(a,b,c,d,e,f,getDecomposer(6));
		assertEquals(1,solutions.size());
		assertTrue(solutions.get(0) instanceof RecursiveSolution);
		Set<FixedSolution> iteratedOver=new HashSet<>();
		for (Pair<BigInteger,BigInteger> sol:solutions.get(0))	{
			BigInteger x=sol.first;
			BigInteger y=sol.second;
			FixedSolution fs=new FixedSolution(x,y);
			assertFalse(iteratedOver.contains(fs));
			assertEquals(BigInteger.ZERO,a.multiply(x.multiply(x)).add(b.multiply(x.multiply(y))).add(c.multiply(y.multiply(y))).add(d.multiply(x)).add(e.multiply(y)).add(f));
			iteratedOver.add(fs);
			if (iteratedOver.size()>=1000) break;
		}
	}

	@Test
	public void testNoSolutions1()	{
		BigInteger a=BigInteger.valueOf(18l);
		BigInteger b=BigInteger.valueOf(81l);
		BigInteger c=BigInteger.valueOf(25l);
		BigInteger d=BigInteger.valueOf(6l);
		BigInteger e=BigInteger.valueOf(5l);
		BigInteger f=BigInteger.valueOf(-213l);
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(a,b,c,d,e,f,getDecomposer(6));
		assertTrue(solutions.isEmpty());
	}
}
