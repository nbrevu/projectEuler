package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Euler588_2 {
	private static class CondensedPolynomial	{
		private static class StringOfOnes	{
			public final long start;
			public final long length;
			public StringOfOnes(long position,long length)	{
				this.start=position;
				this.length=length;
			}
			public long getEnd()	{
				return start+length;
			}
			public boolean strictlyBeforeThan(StringOfOnes other)	{
				return other.start>getEnd();
			}
			@Override
			public String toString()	{
				return "From x^"+start+" to x^"+getEnd();
			}
		}
		private List<StringOfOnes> representation;
		public CondensedPolynomial(long howManyOnes)	{
			representation=Arrays.asList(new StringOfOnes(0,howManyOnes));
		}
		public long countOddCoeffs()	{
			long result=0;
			for (StringOfOnes s:representation) result+=s.length;
			return result;
		}
		public static CondensedPolynomial product(CondensedPolynomial p1,CondensedPolynomial p2)	{
			return new CondensedPolynomial(product(p1.representation,p2.representation));
		}
		@Override
		public String toString()	{
			return representation.toString();
		}
		private CondensedPolynomial(List<StringOfOnes> representation)	{
			this.representation=representation;
		}
		/*
		 *  Black magic fuckery incoming. It's 1 o'clock and I just had 50€+ worth of sushi.
		 *  I don't guarantee any results or readability.
		 */
		private static List<StringOfOnes> sum(StringOfOnes s1,StringOfOnes s2)	{
			if (s1.start>s2.start) return sum(s2,s1);
			else if (s1.start==s2.start)	{
				if (s1.length==s2.length) return Collections.emptyList();
				long newLength;
				long newPosition;
				if (s1.length>s2.length)	{
					newLength=s1.length-s2.length;
					newPosition=s1.start+s2.length;
				}	else	{
					newLength=s2.length-s1.length;
					newPosition=s2.start+s1.length;
				}
				return Arrays.asList(new StringOfOnes(newPosition,newLength));
			}
			// Code from this point is based on s1.start < s2.start.
			long e1=s1.getEnd();
			long e2=s2.getEnd();
			if (e1==e2)	{
				long newLength=s2.start-s1.start;
				return Arrays.asList(new StringOfOnes(s1.start,newLength));
			}	else if (e1==s2.start) return Arrays.asList(new StringOfOnes(s1.start,e2-s1.start));
			else if (e1<s2.start) return Arrays.asList(s1,s2);
			// And up to this point, there is an intersection.
			long start1=s1.start;
			long end1=s2.start;
			long start2,end2;
			if (e1>e2)	{
				start2=e2;
				end2=e1;
			}	else	{
				start2=e1;
				end2=e2;
			}
			StringOfOnes result1=new StringOfOnes(start1,end1-start1);
			StringOfOnes result2=new StringOfOnes(start2,end2-start2);
			return Arrays.asList(result1,result2);
		}
		private static List<StringOfOnes> sum(List<StringOfOnes> ss1,StringOfOnes s2)	{
			List<StringOfOnes> result=new ArrayList<>();
			for (int i=0;i<ss1.size();++i)	{
				StringOfOnes s1=ss1.get(i);
				if (s1.strictlyBeforeThan(s2))	{
					result.add(s1);
					continue;
				}
				if (s2.strictlyBeforeThan(s1))	{
					result.add(s2);
					result.addAll(ss1.subList(i,ss1.size()));
					return result;
				}	else	{
					List<StringOfOnes> intersection=sum(s1,s2);
					List<StringOfOnes> remaining=ss1.subList(i+1,ss1.size());
					switch (intersection.size())	{
						case 0:	{
							result.addAll(remaining);
							break;
						}	case 1:	{
							result.addAll(sum(remaining,intersection.get(0)));
							break;
						}	default:	{
							result.addAll(sum(remaining,intersection));
						}
					}
					return result;
				}
			}
			// If we got here, s2 is further than the end of ss1.
			result.add(s2);
			return result;
		}
		private static List<StringOfOnes> sum(List<StringOfOnes> ss1,List<StringOfOnes> ss2)	{
			for (StringOfOnes s2:ss2) ss1=sum(ss1,s2);
			return ss1;
		}
		private static List<StringOfOnes> product(StringOfOnes s1,StringOfOnes s2)	{
			// Next stop: store temporal results.
			if (s1.length<s2.length) return product(s2,s1);
			if (s2.length==1)	{
				StringOfOnes singleton=new StringOfOnes(s1.start+s2.start,s1.length);
				return Arrays.asList(singleton);
			}
			if (s1.length==s2.length)	{
				List<StringOfOnes> result=new ArrayList<>();
				long basePos=s1.start+s2.start;
				for (long i=0;i<s1.length;++i)	{
					result.add(new StringOfOnes(basePos,1));
					basePos+=2;
				}
				return result;
			}	else	{
				List<StringOfOnes> standardCase=product(new StringOfOnes(s1.start,s2.length),s2);
				List<StringOfOnes> remainder=product(new StringOfOnes(s1.start+s2.length,s1.length-s2.length),s2);
				List<StringOfOnes> result=sum(standardCase,remainder);
				return result;
			}
		}
		private static List<StringOfOnes> product(List<StringOfOnes> ss1,List<StringOfOnes> ss2)	{
			List<StringOfOnes> result=new ArrayList<>();
			for (StringOfOnes s1:ss1) for (StringOfOnes s2:ss2)	{
				List<StringOfOnes> tmpProduct=product(s1,s2);
				List<StringOfOnes> newResult=sum(result,tmpProduct);
				result=newResult;
			}
			return result;
		}
	}
	
	// VERY interesting finding: apparently, for non-powers of 2, odd(X) = odd(2*X), and even better,
	// odd(X) = odd((2^N)*X), for each N. This means that we will only need powers of 5.
	private final static int BASE=5;
	private final static int EXPONENT=18;
	private final static int INITIAL_POLYNOMIAL_DEGREE=5;
	
	public static void main(String[] args)	{
		// Y ahora es el momento de repasar el álgebra de 7º de EGB.
		// El producto de polinomios no multiplica grados; los SUMA :|.
		CondensedPolynomial initial=new CondensedPolynomial(INITIAL_POLYNOMIAL_DEGREE);
		CondensedPolynomial base=CondensedPolynomial.product(initial,initial);
		for (int i=3;i<=BASE;++i) base=CondensedPolynomial.product(initial,base);
		System.out.println("\tBase: "+base.toString()+".");
		System.out.println("For 5, there are "+base.countOddCoeffs()+" odd digits.");
		CondensedPolynomial higherPower=base;
		long sum=base.countOddCoeffs();
		for (int i=2;i<=EXPONENT;++i)	{
			higherPower=CondensedPolynomial.product(base,higherPower);
			long augend=higherPower.countOddCoeffs();
			System.out.println("\tCase "+i+": "+higherPower.toString()+".");
			System.out.println("For 5^"+i+", there are "+augend+" odd digits.");
			sum+=augend;
		}
		System.out.println(sum);
	}
}
