package com.euler;

public class Euler590_3 {
	/*
[2,3]:
	Sin añadir nada: multiplicamos por 2^N-1 = 3. [2,15],[3,10],[10,15]
	Añadiendo un cinco: multiplicamos por 2^(N-1) = 3.
	Sin añadir cinco, pero añadiendo un término multiplicado: multiplicamos por (N sobre 1)*2^(N-1) = 4. [2,3,10],[2,10,15],[2,3,15],[3,10,15]
	Añadiendo cinco y un término multiplicado: AGAIN 4
	Añadiendo dos términos multiplicados: (N sobre 2)*2^(N-2) = 1. [2,3,10,15]
	Añadiendo 5 y dos términos: otra vez 1
	Total: 16.
	La fórmula general sería: 2*(2^N-1) +suma, desde i=1 hasta N, de 2*(N sobre i)*2^(N-i)

[2,3,6]: 
	2^3-1 + suma, desde i=1 hasta 3, de 2*(3 sobre i)*2^(3-i)
		2*(2^3-1)=14
			[3,6,10],[2,6,15],[2,3,30],[6,10,15],[3,10,30],[2,15,30],[10,15,30]
			Los mismos con el 5
		2*(3*4)=24
			[2,3,6,10],[2,6,10,15],[2,3,10,30],[2,10,15,30],[2,3,6,15],[3,6,10,15],[2,3,15,30],[3,10,15,30],[2,3,6,30],[3,6,10,30],[2,6,15,30], [6,10,15,30]
			Los mismos con el 5
		2*(3*2)=12
			[2,3,6,10,15],[2,3,10,15,30],[2,3,6,10,30],[2,
		2*1=2
	Total: 52

[6]
2^1-1+suma, desde i=1 hasta 1, de 2*(1 sobre i)*2^(1-i)
	1+1 = 2 -> salen 4

Total:
	de [6] salen 4
	de [2,3],[2,6],[3,6] salen 3*16=48
	de [2,3,6] salen 52
	Y a�adimos los cinco originales, a los que a�adimos el 5 sin ninguna multiplicaci�n m�s.
	PROFIT!!!!
		Con esto parece que est� medio listo.
	 */
}
