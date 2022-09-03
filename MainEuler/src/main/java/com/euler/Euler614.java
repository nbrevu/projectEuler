package com.euler;

public class Euler614 {
	/*
	 *  Se me ocurre una forma de hacerlo, pero no es eficiente.
	 *  Para cada número, montamos dos contadores, I(n) y T(n). I(n) contiene la cantidad
	 *  de formas en las que n se puede expresar como suma de números IMPARES.
	 *  
	 *  T(n) será igual a la suma, para i=0..n, de I(i)*T((n-i)/4), contando sólo los casos en
	 *  los que (n-i)/4 es entero.
	 *  
	 *  Para I(n) consideramos un caso recursivo. I(n) será igual a la suma, para i=0..n, de
	 *  la cantidad de formas en las que I(n) se expresa como suma de i números impares, que
	 *  es justamente la cantidad de formas de particionar (n-i)/2 en "i" formas distintas.
	 *  Para ello tengo un PDF que me leeré en algún momento del año 2144, calculo.
	 *  
	 *  Incluso aunque pudiera obtener I(n) en orden constante, el orden de este algoritmo es
	 *  O(N) donde N=10^7, así que no va a haber forma de hacer esto eficientemente.
	 *  
	 *  P.D.: todo son sumas y multiplicaciones, así que se pueden usar longs, con módulos,
	 *  todo el rato.
	 */
}
