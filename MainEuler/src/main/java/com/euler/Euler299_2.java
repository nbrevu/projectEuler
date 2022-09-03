package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.PythagoreanTriples.PrimitiveTriplesIterator;

public class Euler299_2 {
	private final static long LIMIT=100000000;
	/*
	 * So. There are two kinds of triangle sets that verify the conditions.
	 * 
	 * The first stage of the preanalysis is to realise that the two smaller "hypotenuses" are part of the big triangle, which is similar to
	 * the smaller ones. Therefore the relation between the two "hypotenuses", which is by definition the relation between the two "catheti",
	 * is also the relation between the smaller triangles. In other words, the longer "cathetus" of the smallest triangle has the same length as
	 * the shortest "cathetus" of the middle triangle. Now these "catheti" are either straight (vertical/horizontal) or diagonal. There are three
	 * cases for these two sides that have the same length:
	 * 
	 * 1) One of these sides is diagonal and the other one is straight. This forces that both angles at P are the same, so they must equal 22,5
	 * degrees since they supplement the 135 degrees one from the big triangle. This implies that all the triangles are isosceles, but this
	 * can't happen since in the smaller triangles one of the "catheti" is always an integer and the other one is K*sqrt(2) with integer K,
	 * therefore they can't be the same.
	 * 
	 * 2) Both sides converging on P are the same. There will always be two symmetric cases, because b and d can be interchanged. Without loss
	 * of generality, assume b<d (we'll see later that b==d is not possible). Let m be the horizontal (smallest) "cathetus" and n the vertical
	 * (biggest) one. Both diagonal sides have length p=(a/2)*sqrt(2). So, let r=p/m be the ratio between the two "catheti" (this holds for the
	 * three triangles because they are similar). It can be proven that r must be of the form r=q*sqrt(2) with integer q. In order to show this,
	 * one can see that if the smallest "cathetus" is m, then the biggest "hypotenuse", which happens to be sqrt(b^2+d^2), is also equal to
	 * m*(1+2*q+2*q^2). This must be both the square root of an integer number and a rational number (since q is rational no matter what),
	 * therefore the only probability is that it's an integer AND it's part of a Pythagorean triple with b and d. This is also the reason why
	 * it's not possible that b=d. Also, by the relationships we have determined, we have that (a^2/2)=(d-a)*(b-a), that is, a^2-2(b+d)a+2bd=0. 
	 * So, for each (b,d) that are the smallest catheti of a Pythagorean triple, we solve that equation and every integer value of a between 1
	 * and b-1 that satisfies the equation gives us a case of (a,b,d). It happens that the solution is (b+d)\pm 1/2sqrt(4(b+d)^2-8bd) = 
	 * (b+d)\pm sqrt(b^2+d^2), i.e. b+d\pm c where c is the third component of the Pythagorean triple. Obviously b+d-c will be between the valid
	 * bounds, while b+d+c will be not, so each Pythagorean triple gives exactly one solution.
	 * 
	 *  a) Iterate over primitive Pythagorean triples between the given limit. Call b the smallest cathetus and d the biggest one.
	 *  b) Increase a counter once per triple.
	 *  c) After finishing, multiply the counter times 2.
	 *  d) ENDUT! HOCH HECH!
	 * 
	 * 3) Both straight sides are the same. This forces automatically b=d, meaning that this set of solutions will be disjoint with the previous
	 * one. Now we have m=n and the diagonal sides are p*sqrt(2) and q*sqrt(2) with q=p-a. Without loss of generality, let q<p. It's not
	 * possible that q=p, because then the two smaller triangles would be the same, so the biggest one would be isosceles, and then it can't be
	 * similar to the smaller ones. Then the four small sides are: q*sqrt(2)<m=n<p*sqrt(2). Also, 2pq=mn=n^2, which forces n even because p and
	 * q are integer. We can generate the tuples directly, but there are two separate cases.
	 *  3.1) q even, p odd. Then, with x and y coprime and y odd, let q=sqrt(2)*2*x^2, m=n=2*x*y, p=sqrt(2)*y^2.
	 *  	3.1.1) y must be greater than sqrt(2)*x for this to make sense without counting twice.
	 *  	3.1.2) a=2*x^2+y^2; b=d=a+m=2*x^2+y^2+2*x*y.
	 *  	3.1.3) For each of these, count one value of b=d. It shouldn't be generated twice if x and y are coprime. Consider scales!
	 *  3.2) q odd, p even. Again, let x and y be coprime, this time with x odd. q=sqrt(2)*x^2, m=n=2*x*y, p=sqrt(2)*2*y^2.
	 *  	3.2.1) x must be smaller than sqrt(2)*y, but it could be that x>y in principle.
	 *  	3.2.2) a=x^2+2*y^2; b=d=a+m=x^2+2*y^2+2*x*y.
	 *  	3.1.3) Just as before, count one for each case and then consider scaling.
	 */
	private final static double S2=Math.sqrt(2.0);
	private final static double S2R=Math.sqrt(0.5);
	
	private static long getPythagoreanCases(long in)	{
		long result=0;
		PrimitiveTriplesIterator iterator=new PrimitiveTriplesIterator();
		for (;;)	{
			iterator.next();
			long sum=iterator.a()+iterator.b();
			if (sum<=in) result+=in/sum;
			else if (iterator.isSmallestN()) break;	// n=1 or 2 and we are already out of bounds -> stop iterating.
		}
		return 2*result;
	}
	
	private static long getNonPythagoreanCasesXEven(long in)	{
		long result=0;
		for (long x=1;;++x)	{
			long x2=x*x;
			long minY=(long)Math.ceil(S2*x);
			for (long y=minY;;++y) if (EulerUtils.areCoprime(x,y))	{
				// a=2*x^2+y^2; b=d=a+m=2*x^2+y^2+2*x*y.
				long a=2*x2+y*y;
				long b=a+2*x*y;
				long sum=2*b;
				if (sum>in)	{
					if (y==minY) return result;
					else break;
				}
				if (EulerUtils.areCoprime(a,b)) result+=in/sum;
			}
		}
	}
	
	private static long getNonPythagoreanCasesXOdd(long in)	{
		long result=0;
		for (long x=1;;++x)	{
			long x2=x*x;
			long minY=(long)Math.ceil(S2R*x);
			for (long y=minY;;++y) if (EulerUtils.areCoprime(x,y))	{
				// a=x^2+2*y^2; b=d=a+m=x^2+2*y^2+2*x*y.
				long a=x2+2*y*y;
				long b=a+2*x*y;
				long sum=2*b;
				if (sum>in)	{
					if (y==minY) return result;
					else break;
				}
				if (EulerUtils.areCoprime(a,b)) result+=in/sum;
			}
		}
	}
	
	private static long getNonPythagoreanCases(long in)	{
		return getNonPythagoreanCasesXEven(in)+getNonPythagoreanCasesXOdd(in);
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=getPythagoreanCases(LIMIT-1)+getNonPythagoreanCases(LIMIT-1);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result+".");
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
