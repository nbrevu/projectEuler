package com.other;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.euler.common.EulerUtils.Pair;

public class Cycles1001 {
	private static class CyclicNumber	{
		private final LinkedList<Character> digits;
		private CyclicNumber(LinkedList<Character> digits)	{
			this.digits=digits;
		}
		public int size()	{
			return digits.size();
		}
		public List<CyclicNumber> fullCycle()	{
			List<CyclicNumber> result=new LinkedList<>();
			CyclicNumber previous=this;
			for (;;)	{
				CyclicNumber next=previous.nextElement();
				result.add(next);
				previous=next;
				if (next.equals(this)) return result;
			}
		}
		public CyclicNumber nextElement()	{
			if (size()==0) throw new IllegalStateException("Can't cycle over zero-sized lists.");
			LinkedList<Character> result=new LinkedList<>(digits);
			char first=result.pollFirst();
			if (first=='0') result.add(first);
			else	{
				Character newLastChar=subtract((char)(10+'0'),first).first;
				result.add(newLastChar);
				int i=result.size()-2;
				do	{
					Pair<Character,Boolean> nextDigit=subtract(result.get(i),'1');
					result.set(i,nextDigit.first);
					if (nextDigit.second) --i;
					else break;
				}	while (i>=0);
				if (i<0)	{
					i=result.size()-1;
					do	{
						Pair<Character,Boolean> nextDigit=increase(result.get(i));
						result.set(i,nextDigit.first);
						if (nextDigit.second) --i;
						else break;
					}	while (i>=0);
					if (i<0) throw new IllegalStateException("ACHTUNG. El resultado es 10^n, pero no cabe en n cifras. Qu� mala suerte, chatos.");
				}
			}
			return new CyclicNumber(result);
		}
		public static CyclicNumber randomNumber(int digits)	{
			Random rand=new Random();
			LinkedList<Character> list=new LinkedList<>();
			for (int i=0;i<digits;++i) list.add((char)('0'+rand.nextInt(10)));
			return new CyclicNumber(list);
		}
		@Override
		public boolean equals(Object other)	{
			CyclicNumber cnOther=(CyclicNumber)other;
			return digits.equals(cnOther.digits);
		}
		@Override
		public int hashCode()	{
			return digits.hashCode();
		}
		@Override
		public String toString()	{
			StringBuilder sb=new StringBuilder();
			for (char c:digits) sb.append(c);
			return sb.toString();
		}
		private static Pair<Character,Boolean> subtract(char minuend,char subtrahend)	{
			int dig1=minuend-'0';
			int dig2=subtrahend-'0';
			int diff=dig1-dig2;
			boolean carry=false;
			if (diff<0)	{
				diff+=10;
				carry=true;
			}
			return new Pair<>((char)('0'+diff),carry);
		}
		private static Pair<Character,Boolean> increase(char value)	{
			if (value=='9') return new Pair<>('0',true);
			else return new Pair<>((char)(1+value),false);
		}
	}
	
	public static void main(String[] args)	{
		/*
		// Con el 100 falla, porque EL RESULTADO ES 1000, que tiene 4 cifras y no 3.
		List<String> numbers=Arrays.asList("00010","100","2452","23789230452352","20");
		for (String n:numbers)	{
			CyclicNumber cn=new CyclicNumber(n);
			CyclicNumber next=cn.nextElement();
			System.out.println(n+": "+cn+" => "+next+".");
		}
		*/
		Random r=new Random();
		for (int i=0;i<20000;++i)	{
			int n=3+r.nextInt(97);
			CyclicNumber num=CyclicNumber.randomNumber(n);
			try	{
				// System.out.println(""+n+" digits: "+num);
				List<CyclicNumber> cycles=num.fullCycle();
				int expected=2*n;
				int found=cycles.size();
				if (expected!=found)	{
					System.out.println("Expected "+expected+", found "+found+".");
					System.out.println("\t"+cycles);
				}
			}	catch (IllegalStateException exc)	{
				System.out.println(exc.getMessage());
				System.out.println("n="+n+"; num="+num+".");
			}
		}
		/*
		CyclicNumber prueba=new CyclicNumber("42290");
		for (int i=0;i<20;++i)	{
			CyclicNumber next=prueba.nextElement();
			System.out.println(""+prueba+" => "+next);
			prueba=next;
		}
		*/
	}
}
