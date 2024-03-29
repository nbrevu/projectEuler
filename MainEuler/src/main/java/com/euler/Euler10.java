package com.euler;

import com.euler.common.Primes;

public class Euler10 {
	/*
	 * This is an algorithm presented by Lucy_Hedgehog in PE 10. It sums all the primes up to certain bound. This is super useful!!!!!
	 * https://projecteuler.net/thread=10;page=5#111677 
	 */
	/*
def P10(n):
    r = int(n**0.5)
    assert r*r <= n and (r+1)**2 > n
    V = [n//i for i in range(1,r+1)]
    V += list(range(V[-1]-1,0,-1))
    S = {i:i*(i+1)//2-1 for i in V}
    for p in range(2,r+1):
        if S[p] > S[p-1]:  # p is prime
            sp = S[p-1]  # sum of primes smaller than p
            p2 = p*p
            for v in V:
                if v < p2: break
                S[v] -= p*(S[v//p] - sp)
    return S[n]
	 */
	private final static long LIMIT=2000000l;

	public static void main(String[] args)	{
		System.out.println(Primes.sumPrimes(LIMIT));
	}
}
