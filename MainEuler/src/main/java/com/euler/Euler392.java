package com.euler;

public class Euler392 {
	// Yes. This is exactly what it looks. Even if it doesn't work, I don't regret it.
	private final static int VARIABLES_IN_QUADRANT=200;
	
	private static class MathematicaCallGenerator	{
		private final int vars;
		private final String[] x;
		public MathematicaCallGenerator(int vars)	{
			this.vars=vars;
			x=new String[vars];
			for (int i=0;i<vars;++i) x[i]="x"+i;
		}
		public String generateFunction()	{
			StringBuilder sb=new StringBuilder();
			sb.append("4*(").append(x[0]).append('+');
			for (int i=0;i<vars-1;++i) sb.append('(').append(x[i+1]).append('-').append(x[i]).append(")*").append(x[vars-1-i]).append('+');
			sb.append("(1-").append(x[vars-1]).append(")*").append(x[0]).append(')');
			return sb.toString();
		}
		public String generateQuadraticConstraints()	{
			StringBuilder sb=new StringBuilder();
			int limit=(vars+1)/2;
			for (int i=0;i<limit;++i)	{
				if (i>0) sb.append(',');
				sb.append(x[i]).append("^2+").append(x[vars-1-i]).append("^2==1");
			}
			return sb.toString();
		}
		public String generateOrderConstraint()	{
			StringBuilder sb=new StringBuilder();
			sb.append("0<");
			for (String xi:x) sb.append(xi).append('<');
			sb.append('1');
			return sb.toString();
		}
		public String generateVariableList()	{
			StringBuilder sb=new StringBuilder();
			sb.append('{');
			boolean first=true;
			for (String xi:x)	{
				if (first) first=false;
				else sb.append(',');
				sb.append(xi);
			}
			sb.append('}');
			return sb.toString();
		}
		public String generateAllConstraints()	{
			StringBuilder sb=new StringBuilder();
			sb.append('{').append(generateQuadraticConstraints()).append(',').append(generateOrderConstraint()).append('}');
			return sb.toString();
		}
		public String generateFunctionCall()	{
			StringBuilder sb=new StringBuilder();
			sb.append("NMinimize[{").append(generateFunction()).append(',').append(generateAllConstraints()).append("},").append(generateVariableList()).append(']');
			return sb.toString();
		}
	}
	
	public static void main(String[] args)	{
		System.out.println(new MathematicaCallGenerator(VARIABLES_IN_QUADRANT).generateFunctionCall());
	}
}
