package com.euler;

import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.CombinationIterator;
import com.euler.common.EulerUtils;

public class Euler635_3 {
	/*
	 * The combination iterator includes 0 instead of 2*n, but that's good enough for us.
	 */
	public static void main(String[] args)	{
		/*
		 * OEIS to the rescue!
		 * For p=2, the value is 2.
		 * For any other prime, the value is [nchoosek(2p,p)+2(p-1)]/p
		 * 
		 * https://oeis.org/A128893
		 * See also:
		 * https://oeis.org/A123611
		 * https://oeis.org/A169888
		 * https://oeis.org/A304482
		 * 
		 * Hmmmm...
		 * "T(n, k)=if(n==0, 1, (-1)^n*sumdiv(n, d, binomial(k*d, d) * (-1)^d * eulerphi(n/d))/n)".
		Summary for p=2:
			Total combinations: 6.
			Valid combinations: 2.
			Elements present: [1, 1, 1, 1].
			Sum distribution: {2=1, 4=1}.
		Summary for p=3:
			Total combinations: 20.
			Valid combinations: 8.
			Elements present: [4, 4, 4, 4, 4, 4].
			Sum distribution: {3=1, 6=3, 9=3, 12=1}.
		Summary for p=5:
			Total combinations: 252.
			Valid combinations: 52.
			Elements present: [26, 26, 26, 26, 26, 26, 26, 26, 26, 26].
			Sum distribution: {10=1, 15=7, 20=18, 25=18, 30=7, 35=1}.
		Summary for p=7:
			Total combinations: 3432.
			Valid combinations: 492.
			Elements present: [246, 246, 246, 246, 246, 246, 246, 246, 246, 246, 246, 246, 246, 246].
			Sum distribution: {21=1, 28=15, 35=75, 42=155, 49=155, 56=75, 63=15, 70=1}.
		Summary for p=11:
			Total combinations: 705432.
			Valid combinations: 64132.
			Elements present: [32066, 32066, 32066, 32066, 32066, 32066, 32066, 32066, 32066, 32066, 32066, 32066, 32066, 32066, 32066, 32066, 32066, 32066, 32066, 32066, 32066, 32066].
			Sum distribution: {55=1, 66=56, 77=724, 88=3782, 99=10465, 110=17038, 121=17038, 132=10465, 143=3782, 154=724, 165=56, 176=1}.
		Summary for p=13:
			Total combinations: 10400600.
			Valid combinations: 800048.
			Elements present: [400024, 400024, 400024, 400024, 400024, 400024, 400024, 400024, 400024, 400024, 400024, 400024, 400024, 400024, 400024, 400024, 400024, 400024, 400024, 400024, 400024, 400024, 400024, 400024, 400024, 400024].
			Sum distribution: {78=1, 91=101, 104=1892, 117=13989, 130=55038, 143=130532, 156=198471, 169=198471, 182=130532, 195=55038, 208=13989, 221=1892, 234=101, 247=1}.
		Summary for p=17:
			Total combinations: 2333606220.
			Valid combinations: 137270956.
			Elements present: [68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478, 68635478].
			Sum distribution: {136=1, 153=297, 170=10480, 187=137453, 204=949446, 221=4013258, 238=11250201, 255=21899154, 272=30375188, 289=30375188, 306=21899154, 323=11250201, 340=4013258, 357=949446, 374=137453, 391=10480, 408=297, 425=1}.
		 Summary for p=19:
			Total combinations: 35345263800.
			Valid combinations: 1860277044.
			Elements present: [930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522, 930138522].
			Sum distribution: {171=1, 190=490, 209=22821, 228=382689, 247=3346632, 266=17910077, 285=63965053, 304=160494985, 323=291939145, 342=392076629, 361=392076629, 380=291939145, 399=160494985, 418=63965053, 437=17910077, 456=3346632, 475=382689, 494=22821, 513=490, 532=1}.
		 */
		int[] primes=new int[] {2,3,5,7,11,13,17,19};
		for (int p:primes)	{
			long totalCombinations=0;
			long validCombinations=0;
			long[] includes=new long[2*p];
			SortedMap<Integer,Long> actualSums=new TreeMap<>();
			for (int[] combi:new CombinationIterator(p,2*p,false))	{
				++totalCombinations;
				int sum=0;
				for (int c:combi) sum+=c;
				if ((sum%p)!=0) continue;
				++validCombinations;
				for (int c:combi) ++includes[c];
				EulerUtils.increaseCounter(actualSums,sum,1l);
			}
			System.out.println(String.format("Summary for p=%d:",p));
			System.out.println(String.format("\tTotal combinations: %d.",totalCombinations));
			System.out.println(String.format("\tValid combinations: %d.",validCombinations));
			System.out.println(String.format("\tElements present: %s.",Arrays.toString(includes)));
			System.out.println(String.format("\tSum distribution: %s.",actualSums.toString()));
		}
	}
}
