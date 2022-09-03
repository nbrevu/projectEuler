package com.euler;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class Euler588_4 {
	private static class StringOfOnes	{
		public final long start;
		public final long length;
		public StringOfOnes(long start,long length)	{
			this.start=start;
			this.length=length;
		}
		public long getEnd()	{
			return start+length;
		}
		public boolean strictlyBeforeThan(StringOfOnes other)	{
			return other.start>getEnd();
		}
		public StringOfOnes displace(long amount)	{
			return new StringOfOnes(start+amount,length);
		}
		@Override
		public String toString()	{
			return "{"+start+"}: "+length;
		}
		public static List<StringOfOnes> displace(List<StringOfOnes> ss,long amount)	{
			List<StringOfOnes> result=new ArrayList<>();
			for (StringOfOnes s:ss) result.add(s.displace(amount));
			return result;
		}
	}

	private static class CondensedPolynomial	{
		private static Table<Long,Long,List<StringOfOnes>> PRODUCT_CACHE=HashBasedTable.create();		
		private List<StringOfOnes> representation;
		public CondensedPolynomial(long howManyOnes)	{
			representation=Arrays.asList(new StringOfOnes(0,howManyOnes));
		}
		public CondensedPolynomial(List<StringOfOnes> representation)	{
			this.representation=representation;
		}
		public long countOddCoeffs()	{
			long result=0;
			for (StringOfOnes s:representation) result+=s.length;
			return result;
		}
		@Override
		public String toString()	{
			return representation.toString();
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
		public static List<StringOfOnes> sum(List<StringOfOnes> ss1,List<StringOfOnes> ss2)	{
			for (StringOfOnes s2:ss2) ss1=sum(ss1,s2);
			return ss1;
		}
		private static List<StringOfOnes> getProductFromCache(StringOfOnes s1,StringOfOnes s2)	{
			long l1=s1.length;
			long l2=s2.length;
			List<StringOfOnes> result=PRODUCT_CACHE.get(l1,l2);
			if (result==null) return null;
			else return StringOfOnes.displace(result,s1.start+s2.start);
		}
		private static void storeProductInCache(StringOfOnes s1,StringOfOnes s2,List<StringOfOnes> result)	{
			long shift=s1.start+s2.start;
			List<StringOfOnes> normalized=StringOfOnes.displace(result,-shift);
			PRODUCT_CACHE.put(s1.length,s2.length,normalized);
		}
		private static List<StringOfOnes> product(StringOfOnes s1,StringOfOnes s2)	{
			if (s1.length<s2.length) return product(s2,s1);
			if (s2.length==1)	{
				StringOfOnes singleton=new StringOfOnes(s1.start+s2.start,s1.length);
				return Arrays.asList(singleton);
			}
			List<StringOfOnes> result=getProductFromCache(s1,s2);
			if (result!=null) return result;
			else result=new ArrayList<>();
			if (s1.length==s2.length)	{
				long basePos=s1.start+s2.start;
				for (long i=0;i<s1.length;++i)	{
					result.add(new StringOfOnes(basePos,1));
					basePos+=2;
				}
			}	else	{
				List<StringOfOnes> standardCase=product(new StringOfOnes(s1.start,s2.length),s2);
				List<StringOfOnes> remainder=product(new StringOfOnes(s1.start+s2.length,s1.length-s2.length),s2);
				result=sum(standardCase,remainder);
			}
			storeProductInCache(s1,s2,result);
			return result;
		}
		public static List<StringOfOnes> product(List<StringOfOnes> ss1,StringOfOnes s2)	{
			List<StringOfOnes> result=new ArrayList<>();
			for (StringOfOnes s1:ss1)	{
				List<StringOfOnes> tmpProduct=product(s1,s2);
				List<StringOfOnes> newResult=sum(result,tmpProduct);
				result=newResult;
			}
			return result;
		}
		public static List<StringOfOnes> product(List<StringOfOnes> ss1,List<StringOfOnes> ss2)	{
			List<StringOfOnes> result=new ArrayList<>();
			for (StringOfOnes s1:ss1) for (StringOfOnes s2:ss2)	{
				List<StringOfOnes> tmpProduct=product(s1,s2);
				List<StringOfOnes> newResult=sum(result,tmpProduct);
				result=newResult;
			}
			return result;
		}
	}
	
	private static class AdvancedPolynomialCalculator	{
		private static Map<Long,List<StringOfOnes>> FOURTH_POWER_CACHE=new HashMap<>();
		private static Map<Long,List<StringOfOnes>> FIFTH_POWER_CACHE=new HashMap<>();
		private static Table<Long,Long,List<StringOfOnes>> A_TIMES_B_4_CACHE=HashBasedTable.create();
		private static List<StringOfOnes> getFourthPowerFromCache(StringOfOnes s1)	{
			List<StringOfOnes> result=FOURTH_POWER_CACHE.get(s1.length);
			if (result==null) return null;
			else return StringOfOnes.displace(result,4*s1.start);
		}
		private static void storeFourthPowerInCache(StringOfOnes s1,List<StringOfOnes> result)	{
			List<StringOfOnes> normalized=StringOfOnes.displace(result,-4*s1.start);
			FOURTH_POWER_CACHE.put(s1.length,normalized);
		}
		private static List<StringOfOnes> getFifthPowerFromCache(StringOfOnes s1)	{
			List<StringOfOnes> result=FIFTH_POWER_CACHE.get(s1.length);
			if (result==null) return null;
			else return StringOfOnes.displace(result,5*s1.start);
		}
		private static void storeFifthPowerInCache(StringOfOnes s1,List<StringOfOnes> result)	{
			List<StringOfOnes> normalized=StringOfOnes.displace(result,-5*s1.start);
			FIFTH_POWER_CACHE.put(s1.length,normalized);
		}
		private static List<StringOfOnes> getATimesB4FromCache(StringOfOnes a,StringOfOnes b)	{
			List<StringOfOnes> result=A_TIMES_B_4_CACHE.get(a.length,b.length);
			if (result==null) return null;
			else return StringOfOnes.displace(result,a.start+4*b.start);
		}
		private static void storeATimesB4InCache(StringOfOnes a,StringOfOnes b,List<StringOfOnes> result)	{
			long shift=a.start+4*b.start;
			List<StringOfOnes> normalized=StringOfOnes.displace(result,-shift);
			A_TIMES_B_4_CACHE.put(a.length,b.length,normalized);
		}
		private static List<StringOfOnes> getFourthPower(StringOfOnes s1)	{
			if (s1.length==1) return Arrays.asList(new StringOfOnes(s1.start*4,1));
			List<StringOfOnes> result=getFourthPowerFromCache(s1);
			if (result!=null) return result;
			List<StringOfOnes> s2=CondensedPolynomial.product(s1,s1);
			result=CondensedPolynomial.product(s2,s2);
			storeFourthPowerInCache(s1,result);
			return result;
		}
		private static List<StringOfOnes> getFifthPower(StringOfOnes s1)	{
			if (s1.length==1) return Arrays.asList(new StringOfOnes(s1.start*5,1));
			List<StringOfOnes> result=getFifthPowerFromCache(s1);
			if (result!=null) return result;
			List<StringOfOnes> s4=getFourthPower(s1);
			result=CondensedPolynomial.product(s4,s1);
			storeFifthPowerInCache(s1,result);
			return result;
		}
		private static List<StringOfOnes> getATimesB4(StringOfOnes a,StringOfOnes b)	{
			List<StringOfOnes> result=getATimesB4FromCache(a,b);
			if (result!=null) return result;
			List<StringOfOnes> b4=getFourthPower(b);
			result=CondensedPolynomial.product(b4,a);
			storeATimesB4InCache(a,b,result);
			return result;
		}
		private static List<StringOfOnes> getFifthPower(List<StringOfOnes> in)	{
			List<StringOfOnes> result=new ArrayList<>();
			for (StringOfOnes s1:in)	{
				result=CondensedPolynomial.sum(result,getFifthPower(s1));
				for (StringOfOnes s2:in) if (s1!=s2) result=CondensedPolynomial.sum(result,getATimesB4(s1,s2));
			}
			return result;
		}
		public static CondensedPolynomial getFifthPower(CondensedPolynomial in)	{
			return new CondensedPolynomial(getFifthPower(in.representation));
		}
	}
	
	// VERY interesting finding: apparently, for non-powers of 2, odd(X) = odd(2*X), and even better,
	// odd(X) = odd((2^N)*X), for each N. This means that we will only need powers of 5.
	private final static int EXPONENT=8;
	
	public static void main(String[] args)	{
		try(PrintStream ps=new PrintStream(new File("C:\\out588_4.txt")))	{
			CondensedPolynomial initial=new CondensedPolynomial(5);
			CondensedPolynomial base=AdvancedPolynomialCalculator.getFifthPower(initial);
			ps.println("Case 1: "+base.toString()+".");
			ps.println("\t"+base.countOddCoeffs()+" odd digits.");
			CondensedPolynomial higherPower=base;
			for (int i=2;i<=EXPONENT;++i)	{
				higherPower=AdvancedPolynomialCalculator.getFifthPower(higherPower);
				long augend=higherPower.countOddCoeffs();
				ps.println("Case "+i+": "+higherPower.toString()+".");
				ps.println("\t"+augend+" odd digits.");
			}
		}	catch (IOException exc)	{
			System.out.println("D'oh!");
		}
	}
}
