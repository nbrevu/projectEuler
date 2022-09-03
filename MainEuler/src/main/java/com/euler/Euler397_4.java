package com.euler;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.LongPair;

public class Euler397_4 {
	private final static long K=120;
	private final static long X=1000;
	
	private static enum RatioType	{
		NORMAL("=k") {
			@Override
			protected boolean verifies(long prod, long k) {
				return prod==k;
			}
		},HALF("*2=k") {
			@Override
			protected boolean verifies(long prod, long k) {
				return prod*2==k;
			}
		},DOUBLE("/2=k") {
			@Override
			protected boolean verifies(long prod, long k) {
				return prod/2==k;
			}
		};
		
		private final String id;
		private RatioType(String id)	{
			this.id=id;
		}
		@Override
		public String toString()	{
			return "abs(qba*qca*qcb)"+id;
		}
		protected abstract boolean verifies(long prod,long k);
		private final static RatioType[] ELEMENTS=values();
		public static RatioType identify(long prod,long k)	{
			for (RatioType type:ELEMENTS) if (type.verifies(prod,k)) return type;
			throw new IllegalArgumentException();
		}
	}
	
	private static class InternalId	implements Comparable<InternalId>	{
		public final RatioType ratio;
		public final char corner;
		public InternalId(RatioType ratio,char corner)	{
			this.ratio=ratio;
			this.corner=corner;
		}
		@Override
		public int hashCode()	{
			return ratio.hashCode()+corner;
		}
		@Override
		public boolean equals(Object other)	{
			InternalId iiOther=(InternalId)other;
			return (ratio==iiOther.ratio)&&(corner==iiOther.corner);
		}
		@Override
		public int compareTo(InternalId o) {
			int result=ratio.compareTo(o.ratio);
			if (result!=0) return result;
			else return Character.compare(corner,o.corner);
		}
	}
	
	private static class Summaries	{
		private final static Comparator<LongPair> PAIR_COMPARATOR=(LongPair o1,LongPair o2)->Long.compare(o1.x,o2.x);
		private final SortedMap<Long,SortedMap<LongPair,SortedMap<InternalId,Integer>>> dataMap;
		private long innerCounter;
		public Summaries()	{
			dataMap=new TreeMap<>();
			innerCounter=0;
		}
		public void displayDoubleTriangle(char type,long k,long a,long b,long c)	{
			long expected=K/k;
			System.out.println(String.format("Double triangle (type %c): {%d,%d,%d} (k=%d, expected %d multiples).",type,a,b,c,k,expected));
			innerCounter+=expected;
			long dba=b-a;
			long sba=b+a;
			long dca=c-a;
			long sca=c+a;
			long dcb=c-b;
			long scb=c+b;
			long valAb=dba*dba*(k*k+(sba*sba));
			long valAc=dca*dca*(k*k+(sca*sca));
			long valBc=dcb*dcb*(k*k+(scb*scb));
			long gcd=EulerUtils.gcd(Math.abs(dba),EulerUtils.gcd(Math.abs(dca),Math.abs(dcb)));
			long qba=dba/gcd;
			long qca=dca/gcd;
			long qcb=dcb/gcd;
			System.out.println(String.format("\tAB^2=%d^2(%d^2+%d^2)=%d.",dba,k,sba,valAb));
			System.out.println(String.format("\tAC^2=%d^2(%d^2+%d^2)=%d.",dca,k,sca,valAc));
			System.out.println(String.format("\tBC^2=%d^2(%d^2+%d^2)=%d.",dcb,k,scb,valBc));
			System.out.println(String.format("\tQuotients: %d, %d, %d.",qba,qca,qcb));
			long prod=Math.abs(qba*qca*qcb);
			RatioType ratioType=RatioType.identify(prod,k);
			if ((scb*sba)!=-k*k) throw new IllegalStateException(":(");
			if (((scb-sba)%2)!=0) throw new IllegalStateException(":(");
			SortedMap<LongPair,SortedMap<InternalId,Integer>> innerMap=dataMap.computeIfAbsent(k,(Long unused)->new TreeMap<>(PAIR_COMPARATOR));
			LongPair pairId=LongPair.sorted(Math.abs(sba),Math.abs(scb));
			SortedMap<InternalId,Integer> evenInnerMap=innerMap.computeIfAbsent(pairId,(LongPair unused)->new TreeMap<>());
			InternalId id=new InternalId(ratioType,type);
			EulerUtils.increaseCounter(evenInnerMap,id);
		}
		public void displaySummary()	{
			for (Map.Entry<Long,SortedMap<LongPair,SortedMap<InternalId,Integer>>> entry:dataMap.entrySet())	{
				long k=entry.getKey();
				SortedMap<LongPair,SortedMap<InternalId,Integer>> innerMap=entry.getValue();
				System.out.println(String.format("Cases for k=%d:",k));
				for (Map.Entry<LongPair,SortedMap<InternalId,Integer>> entry2:innerMap.entrySet())	{
					LongPair p=entry2.getKey();
					long x=p.x;
					long y=p.y;
					long gcd=EulerUtils.gcd(x,y);
					System.out.println(String.format("\tk=%d, inner keys=<%d,%d> (gcd=%d):",k,x,y,gcd));
					for (Map.Entry<InternalId,Integer> entry3:entry2.getValue().entrySet())	{
						InternalId id=entry3.getKey();
						int counter=entry3.getValue();
						System.out.println(String.format("\t\tk=%d, inner keys=<%d,%d>, %s, corner type %c: %d cases.",k,x,y,id.ratio.toString(),id.corner,counter));
					}
				}
			}
			System.out.println(String.format("Expected count after rescaling base cases: %d.",innerCounter));
		}
	}
	
	public static void main(String[] args)	{
		int result=0;
		int countA=0;
		int countB=0;
		int countC=0;
		int doubleResult=0;
		Summaries s=new Summaries();
		for (long k=1;k<=K;++k) for (long a=-X;a<=X;++a) for (long b=a+1;b<=X;++b) for (long c=b+1;c<=X;++c)	{
			long ax=k*a;
			long ay=a*a;
			long bx=k*b;
			long by=b*b;
			long cx=k*c;
			long cy=c*c;
			long abx=bx-ax;
			long bcx=cx-bx;
			long acx=cx-ax;
			long aby=by-ay;
			long bcy=cy-by;
			long acy=cy-ay;
			long ab2=abx*abx+aby*aby;
			long bc2=bcx*bcx+bcy*bcy;
			long ac2=acx*acx+acy*acy;
			long abbc=abx*bcx+aby*bcy;
			long bcac=bcx*acx+bcy*acy;
			long abac=abx*acx+aby*acy;
			long abbc2=abbc*abbc;
			long bcac2=bcac*bcac;
			long abac2=abac*abac;
			int counts=0;
			boolean isA=false;
			boolean isB=false;
			boolean isC=false;
			if ((abbc<0)&&(abbc2*2==ab2*bc2))	{	// Note the sign!
				isB=true;
				++countB;
				++counts;
			}
			if ((bcac>0)&&(bcac2*2==bc2*ac2))	{
				isC=true;
				++countC;
				++counts;
			}
			if ((abac>0)&&(abac2*2==ab2*ac2))	{
				isA=true;
				++countA;
				++counts;
			}
			if (counts>2) throw new IllegalArgumentException("Mira, no sé qué has hecho, pero está MAL.");
			if (counts>1)	{
				if (EulerUtils.areCoprime(Math.abs(a),Math.abs(b),Math.abs(c)))	{
					long aa,bb,cc;
					char type;
					if (!isA)	{
						aa=b;
						bb=a;
						cc=c;
						type='A';
					}	else if (!isB)	{
						aa=a;
						bb=b;
						cc=c;
						type='B';
					}	else if (!isC)	{
						aa=a;
						bb=c;
						cc=b;
						type='C';
					}	else throw new IllegalArgumentException("Es gefällt mir überhaupt nicht!!!!!");
					s.displayDoubleTriangle(type,k,aa,bb,cc);
				}
				++doubleResult;
			}
			if (counts>0) ++result;
		}
		System.out.println(result);
		System.out.println(String.format("Double results: %d.",doubleResult));
		System.out.println(String.format("Counts for: A=%d, B=%d, C=%d.",countA,countB,countC));
		s.displaySummary();
	}
}
