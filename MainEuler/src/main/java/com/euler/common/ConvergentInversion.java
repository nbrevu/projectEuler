package com.euler.common;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Collections2;

public class ConvergentInversion {
	/*
	 * Represents a number of the form (A+sqrt(B))/C.
	 */
	public static class QuadraticRational	{
		private final static BigInteger MINUS_ONE=BigInteger.ONE.negate();
		public final BigInteger a;
		public final BigInteger b;
		public final BigInteger c;
		public QuadraticRational(BigInteger a,BigInteger b,BigInteger c)	{
			this.a=a;
			this.b=b;
			this.c=c;
		}
		public QuadraticRational simplify()	{
			BigInteger g=EulerUtils.gcd(a,c);
			if (g.abs().equals(BigInteger.ONE)) return this;
			/*
			 * Let's suppose that we can simplify. So, b=x*y^2 where y divides g.
			 * We can do the following:
			 * 1) calculate gcd(b,g)=g'.
			 * 2) then, b'=b/g' is an integer.
			 * 3) Unfortunately shit happens when g is not squarefree.
			 */
			BigInteger g2=EulerUtils.gcd(g,b);
			if (g2.abs().equals(BigInteger.ONE)) return this;
			BigInteger b2=b.divide(g2);
			BigInteger g3=EulerUtils.gcd(b2,g2);
			if (g3.abs().equals(BigInteger.ONE)) return this;
			else return new QuadraticRational(a.divide(g3),b.divide(g3.multiply(g3)),c.divide(g3));
		}
		public boolean isPositive()	{
			boolean numSign=isPositive(a,b);
			return (c.signum()>0)?numSign:(!numSign);
		}
		private static boolean isPositive(BigInteger a,BigInteger b)	{
			/*
			 * Returns true if a+sqrt(b)>0. If a>0, it's always the case. If not, then it must be |a|<sqrt(b) -> a^2<b.
			 */
			if (a.signum()>0) return true;
			else return b.subtract(a.multiply(a)).signum()>0;
		}
		@Override
		public String toString()	{
			StringBuilder result=new StringBuilder();
			if (MINUS_ONE.equals(c)) appendNum(result,a,b,true);
			else if (BigInteger.ONE.equals(c)) appendNum(result,a,b,false);
			else if (c.signum()<0)	{
				result.append('(');
				appendNum(result,a,b,true);
				result.append(")/").append(c.negate().toString());
			}	else	{
				result.append('(');
				appendNum(result,a,b,false);
				result.append(")/").append(c.toString());
			}
			return result.toString();
		}
		private static void appendNum(StringBuilder builder,BigInteger a,BigInteger b,boolean changeSign)	{
			if (changeSign)	{
				if (a.signum()<0) builder.append(a.negate().toString());
				else if (a.signum()>0) builder.append('-').append(a.toString());
				builder.append('-');
			}	else if (a.signum()!=0) builder.append(a.toString()).append('+');
			appendSqrt(builder,b);
		}
		private static void appendSqrt(StringBuilder builder,BigInteger sqrt)	{
			builder.append("S(").append(sqrt.toString()).append(')');
		}
	}
	
	public static class SecondGradeEquation	{
		private final static BigInteger FOUR=BigInteger.valueOf(4);
		public final BigInteger a;
		public final BigInteger b;
		public final BigInteger c;
		public SecondGradeEquation(BigInteger a,BigInteger b,BigInteger c)	{
			this.a=a;
			this.b=b;
			this.c=c;
		}
		public List<QuadraticRational> solve()	{
			BigInteger det=b.multiply(b).subtract(FOUR.multiply(a.multiply(c)));
			if (det.signum()<0) return Collections.emptyList();
			else if (det.signum()==0) return Collections.singletonList(new QuadraticRational(b.negate(),BigInteger.ZERO,a.add(a)));
			else	{
				BigInteger den=a.add(a);
				QuadraticRational sol1=new QuadraticRational(b.negate(),det,den).simplify();
				QuadraticRational sol2=new QuadraticRational(b,det,den.negate()).simplify();
				return Arrays.asList(sol1,sol2);
			}
		}
	}
	
	public static QuadraticRational reverseConvergent(long... convergents)	{
		return reverseConvergent(Arrays.stream(convergents).mapToObj(BigInteger::valueOf).collect(Collectors.toUnmodifiableList()));
	}
	
	public static QuadraticRational reverseConvergent(BigInteger... convergents)	{
		return reverseConvergent(Arrays.asList(convergents));
	}
	
	public static QuadraticRational reverseConvergent(List<BigInteger> convergents)	{
		/*
		 * The fraction is represented as {A,B,C,D}, meaning (Ax+B)/(Cx+D).
		 * 
		 * The starting fraction is x-a0: A=1, B=-a0, C=0, D=1.
		 */
		BigInteger a0=convergents.get(0);
		BigInteger a=BigInteger.ONE;
		BigInteger b=a0.negate();
		BigInteger c=BigInteger.ZERO;
		BigInteger d=BigInteger.ONE;
		for (int i=convergents.size()-1;i>0;--i)	{
			/*
			 * If the current fraction is F=(Ax+B)/(Cx+D), the next one comes from 1/(F+an) = 1/{[(A+an*C)x+(B+an*D)]/[Cx+D]}.
			 * So, C'=an*A+C; D'=an*B+D; A'=C; B'=D.
			 */
			BigInteger conv=convergents.get(i);
			BigInteger cc=conv.multiply(c).add(a);
			BigInteger dd=conv.multiply(d).add(b);
			a=c;
			b=d;
			c=cc;
			d=dd;
		}
		/*
		 * At the end we have x-a0=(Ax+B)/(Cx+D) -> Cx^2-a0*Cx+Dx-a0*D=Ax+B -> quadratic equation with coefficients:
		 * a=C
		 * b=-a0*C+D-A
		 * c=-B-a0*D.
		 */
		BigInteger qB=d.subtract(a).subtract(a0.multiply(c));
		BigInteger qC=b.add(a0.multiply(d)).negate();
		List<QuadraticRational> eqSols=new SecondGradeEquation(c,qB,qC).solve();
		Collection<QuadraticRational> positiveSols=Collections2.filter(eqSols,QuadraticRational::isPositive);
		if (positiveSols.size()!=1) throw new ArithmeticException("No solution could be picked.");
		return positiveSols.iterator().next();
	}
}
