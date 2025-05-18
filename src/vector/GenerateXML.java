package vector;

import java.util.Locale;
import statistics.Function;


public class GenerateXML {
	
	public Function f;
	double[] functionArrX;
	double[] functionArrY;
	double[] errorArr;
	int start;
	int end;
	
	StringBuilder svg = new StringBuilder();
	public String restXML =  "\" stroke=\"black\" fill=\"none\" stroke-width=\"2\"/></svg>";
							
			        

	
	public GenerateXML(Function f) {
		this.f = f;
		this.functionArrY = f.yActualVal;
		this.functionArrX = f.xActualVal;
		
	}
	
	public void createXML(Function f, int start, int end) {
		ControlPoint cp = new ControlPoint(f, start, end);
//		cp.getValuesOfCurve();
		String points;
		int e = cp.getErrorIndex(f);
//		System.out.println("e = " + e);
		points = String.format(Locale.US,"M %.2f,%.2f C %.2f,%.2f %.2f,%.2f %.2f,%.2f",  functionArrX[start], functionArrY[start], cp.v1.x, cp.v1.y, cp.v2.x, cp.v2.y, functionArrX[end], functionArrY[end]);
//		System.out.println("RECURSIVE CALL!");
		if(cp.maxError > 1 ) {
			createXML(f, start, start + e);
//			System.out.println("left size = " + e);
			createXML(f, start + e, end);
//			System.out.println("subarray start = " + start  + " end  = " + end);
		}else {
			if(cp.v1.x == Double.NaN || cp.v1.y == Double.NaN || cp.v2.x == Double.NaN || cp.v2.y == Double.NaN){
//				System.out.println("one or more of the points was NaN");
				points = String.format(Locale.US,"M %.2f,%.2f L %.2f,%.2f",  functionArrX[start], functionArrY[start], functionArrX[end], functionArrY[end]);
				

			}
			svg.append(points);
//			System.out.println(points);
		}
	}
	
}
