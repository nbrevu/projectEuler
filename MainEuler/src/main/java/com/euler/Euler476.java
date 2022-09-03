package com.euler;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

public class Euler476 {
	/*
	int sum=0;
	for (int a=1;a<=LIMIT;++a) for (int b=a;b<=LIMIT-a;++b)	{
		int minC=b;
		int maxC=a+b;
		int howMany=maxC-minC;
		if (howMany>0) sum+=howMany;
		else	{
			System.out.println("D'OH!");
			break;
		}
	}
	System.out.println(sum+".");	// 245845964
	*/
	/*
	 * a goes from 1 to LIMIT.
	 * b goes from a to LIMIT.
	 * c goes from b (inclusive) to a+b (not inclusive), therefore there are "a" possibilities for each c.
	 * So we have: sum(sum(a,b,a,LIMIT-a),a,1,LIMIT/2) = sum(a*(LIMIT+1-2*a),a,1,LIMIT/2) = 
	 *  = (LIMIT+1)*sum(a,a,1,LIMIT/2) - 2*sum(a^2,a,1,LIMIT/2) = 
	 *  = (LIMIT+1)*(LIMIT/2)*((LIMIT+2)/2)/2 - (2*(LIMIT/2)^3+3*(LIMIT/2)^2+(LIMIT/2))/3.
	 *  = (LIMIT^3+3*LIMIT^2+2*LIMIT)/8 - (LIMIT^3/12 + LIMIT^2/4 + LIMIT/6) = (LIMIT^3+3*LIMIT^2+2*LIMIT)/24;
	 */
	// long result2=LIMIT*(2+LIMIT*(3+LIMIT))/24;
	// System.out.println(result2);
	
	private final static long LIMIT=1803;
	
	private static class RadiusAndAngle implements Comparable<RadiusAndAngle>	{
		public final double radius;
		public final double angle;
		public RadiusAndAngle(double radius,double angle)	{
			this.radius=radius;
			this.angle=angle;
		}
		@Override
		public int compareTo(RadiusAndAngle o) {
			return Double.compare(radius,o.radius);
		}
		@Override
		public int hashCode()	{
			return Double.hashCode(radius)+Double.hashCode(angle);
		}
		@Override
		public boolean equals(Object other)	{
			RadiusAndAngle raOther=(RadiusAndAngle)other;
			return (radius==raOther.radius)&&(angle==raOther.angle);
		}
	}
	
	// This returns the angle opposite to "a".
	private static double getAlpha(int a,int b,int c)	{
		double num=b*b+c*c-a*a;
		double den=2*b*c;
		return Math.acos(num/den);
	}
	
	private static double circleArea(double radius)	{
		return Math.PI*radius*radius;
	}
	
	private static double getIncircleRadius(double a,double b,double c)	{
		double s=(a+b+c)/2.0;
		return Math.sqrt((s-a)*(s-b)*(s-c)/s);
	}
	
	// I'm getting the right result for LIMIT=2, but not for LIMIT=5. Something is wrong, most probably here.
	private static RadiusAndAngle getForIsoscelesTriangle(double angle,double radius)	{
		/*
		 * So we know that there is a "wedge" of a given angle, inside of which there is an inscribed circle whose radius we also know.
		 * This information is enough to know the radius of a circle which is tangent to both segments and the given circle. 
		 *
		 * The angle in the "wedge" is angle, so the angle formed by the two tangent radii of the circle is otherAngle = Math.PI-angle.
		 * 
		 * Using the cosine theorem, the chord between the two tangent points is D = 2R^2-2R^2(1-cos(otherAngle)).
		 * Also using the cosine theorem, the chord equals D = 2X^2-2X^2(1-cos(angle)), where X is the distance between the vertex and
		 * each tangent point.
		 * 
		 * So: 2R^2(1-cos(otherAngle)) = 2X^2(1-cos(angle)), and so, X=R*sqrt((1-cos(otherAngle))/(1-cos(angle))).
		 * 
		 * With some trigonometric magic we can quickly get that X=R*cot(angle/2) which is wonderfully simple.
		 * 
		 * And with this we get the "height" (distance between the vertex and the circle centre) which is sqrt(X^2+R^2) = R/cos(angle/2).
		 * With this we can start building the isosceles triangle, which is the juxtaposition of two rectangle triangles with an angle of
		 * angle/2 and a contiguous cathetus of H=R(1/cos(angle/2)-1), which is the height of the isosceles triangle. Therefore the
		 * hypotenuse is R(1/cos^2(angle/2) - 1/cos(angle/2)) and the unequal side equals the hypotenuse multiplied by 2sin(angle/2).
		 */
		double ca2=Math.cos(angle/2);
		double sa2=Math.sin(angle/2);
		double fullHeight=radius/sa2;
		double baseHeight=fullHeight-radius;
		double hypotenuse=baseHeight/ca2;
		double unequalSide=2*hypotenuse*sa2;
		double newInRadius=getIncircleRadius(hypotenuse,hypotenuse,unequalSide);
		return new RadiusAndAngle(newInRadius,angle);
	}
	
	private static double getForTwoCirclesAndALine(double r1,double r2) {
		double k1=1/r1;
		double k2=1/r2;
		double k4=k1+k2+2*Math.sqrt(k1*k2);
		return 1/k4;
	}
	
	private static double getTotalArea(int a,int b,int c)	{
		double inRadius=getIncircleRadius(a,b,c);
		// The first circle is always the inscribed one.
		double result=circleArea(inRadius);
		double alpha=getAlpha(a,b,c);
		double beta=getAlpha(b,c,a);
		double gamma=getAlpha(c,a,b);
		RadiusAndAngle forAlpha=getForIsoscelesTriangle(alpha,inRadius);
		RadiusAndAngle forBeta=getForIsoscelesTriangle(beta,inRadius);
		RadiusAndAngle forGamma=getForIsoscelesTriangle(gamma,inRadius);
		List<RadiusAndAngle> angles=Lists.newArrayList(forAlpha,forBeta,forGamma);
		angles.sort(null);
		RadiusAndAngle bestAngle=angles.get(2);
		// The second circle is the biggest of the "secondary" (tangent to the inscribed and two sides) ones.
		result+=circleArea(bestAngle.radius);
		double[] candidates=new double[4];
		candidates[0]=angles.get(0).radius;
		candidates[1]=angles.get(1).radius;
		candidates[2]=getForIsoscelesTriangle(bestAngle.angle,bestAngle.radius).radius;
		candidates[3]=getForTwoCirclesAndALine(inRadius,bestAngle.radius);
		Arrays.sort(candidates);
		/*
		 * The last one can be either one of the discarded "secondary" ones or one of the two "tertiary" ones associated to the one chosen in
		 * the previous step.
		 */
		result+=circleArea(candidates[3]);
		return result;
	}
	
	public static void main(String[] args)	{
		/*
		 * I spent a shameful amount of time trying to debug a perfectly fine algorithm until I reread the problem and I saw that it wasn't
		 * asking for the maximum, but for the average.
		 */
		long tic=System.nanoTime();
		double sum=0.0;
		int count=0;
		for (int a=1;a<=LIMIT;++a) for (int b=a;b<=LIMIT-a;++b) for (int c=b;c<a+b;++c)	{
			sum+=getTotalArea(a,b,c);
			++count;
		}
		double result=sum/count;
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Calculated for "+count+" triangles.");
		System.out.println(result+".");
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
