package com.euler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.euler.common.EulerUtils;
import com.google.common.base.Splitter;

public class Euler241_3 {
	// This is basically cheating. http://www.numericana.com/answer/numbers.htm#hemiperfect
	private final static List<String> MONSTER_STRINGS=Arrays.asList("2","24, 91963648, 10200236032","4320, 4680, 26208, 20427264, 197064960, 21857648640, 57575890944, 88898072401645056, 301183421949935616, 9083288595228991885541376, 22290964134962716779872256, 230361837156847526055247872, 3551746147589248994873004392448, 8716209461184471402733217906688, 90076051101488582786918337478656, 275517471462331149989751161880576, 8319263987369391948455878608398843904, 20415999472827819113761327282781159424, 3081634264657305632386843579602306072576, 93050102500349677040144591462063024482811904, 228350830852095014942603539620449439316967424","8910720, 17428320, 8583644160, 57629644800, 206166804480, 1416963251404800, 15338300494970880, 6275163455171297280, 200286975596707184640, 215594611071909888000, 5997579964837140234240, 39887491844324122951680, 189478877946949032837120, 464993138593758319902720, 4577250484712348791603200, 314220801442981320248524800, 14048146725436554258960875520, 20270811496597107858493931520, 81703797123392614369698250752, 612078178502919543930287114158080, 939834592031480161274941547741184, 1502078523847443989273473166868480, 2306413471743588373372911017263104, 157127060125322787706213898932715520, 954799029953763034837845432097308672, 2343137147924117580221226004651180032, 11528505172715763556107234109992568639979520000","17116004505600, 75462255348480000, 6219051710415667200, 14031414189615513600, 352444116692828160000, 835095457414213632000, 59485231752222033838080, 64031599488357236736000, 564178061132326319357952000, 1208818605469519237939200000, 1384528609279142174195712000, 3101020675856435565821952000, 3333576337140514195596902400");
	
	private static class HalfAbundantInfo implements Comparable<HalfAbundantInfo>	{
		private long number;
		private Map<Long,Integer> factors;
		private long sigma;
		private long div;
		public HalfAbundantInfo(long in)	{
			number=in;
			factors=getFactors(in);
			sigma=getSigma(factors);
			assert ((2*sigma)%in)==0;
			assert (((2*sigma)/in)%2)==1;
			div=sigma/in;
		}
		@Override
		public int compareTo(HalfAbundantInfo other)	{
			return Long.valueOf(number).compareTo(Long.valueOf(other.number));
		}
		@Override
		public String toString()	{
			StringBuilder sb=new StringBuilder();
			sb.append(number).append(" = ");
			boolean first=true;
			for (Map.Entry<Long,Integer> entry:factors.entrySet())	{
				if (first) first=false;
				else sb.append(" · ");
				sb.append(entry.getKey());
				int pow=entry.getValue();
				if (pow>1) sb.append('^').append(pow);
			}
			sb.append(". Sigma=").append(sigma).append("; ").append(sigma).append('/').append(number).append('=').append(div).append(".5.");
			return sb.toString();
		}
		public long getNumber()	{
			return number;
		}
		private static long addDivisor(long in,long p,Map<Long,Integer> divs)	{
			while ((in%p)==0)	{
				in/=p;
				EulerUtils.increaseCounter(divs,p);
			}
			return in;
		}
		private static Map<Long,Integer> getFactors(long in)	{
			Map<Long,Integer> result=new TreeMap<>();
			in=addDivisor(in,2l,result);
			in=addDivisor(in,3l,result);
			long p=5;
			boolean add4=false;
			while (in>1)	{
				in=addDivisor(in,p,result);
				p+=(add4?4:2);
				add4=!add4;
			}
			return result;
		}
		private static long getSigma(Map<Long,Integer> factors)	{
			long prod=1l;
			for (Map.Entry<Long,Integer> entry:factors.entrySet())	{
				long prime=entry.getKey();
				int power=entry.getValue();
				long highPow=prime;
				for (int i=0;i<power;++i) highPow*=prime;
				long factor=(highPow-1)/(prime-1);
				prod*=factor;
			}
			return prod;
		}
	}
	
	public static void main(String[] args)	{
		SortedSet<HalfAbundantInfo> infos=new TreeSet<>();
		Splitter sp=Splitter.on(",").omitEmptyStrings();
		for (String numberList:MONSTER_STRINGS)	{
			for (String substring:sp.split(numberList))	{
				substring=substring.trim();
				// System.out.println("<"+substring+">");
				assert substring.matches("^\\d+$");
				if (substring.length()<=18) infos.add(new HalfAbundantInfo(Long.parseLong(substring)));
			}
		}
		System.out.println("Found "+infos.size()+" numbers!");
		long sum=0;
		for (HalfAbundantInfo info:infos)	{
			System.out.println(info.toString());
			sum+=info.getNumber();
		}
		System.out.println();
		System.out.println(""+sum+".");
	}
}
