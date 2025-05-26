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
	Node root;
	
	StringBuilder svg = new StringBuilder();
	public String restXML =  "\" stroke=\"black\" fill=\"none\" stroke-width=\"2\"/></svg>";
							
			        

	
	public GenerateXML(Function f) {
		this.f = f;
		this.functionArrY = f.yActualVal;
		this.functionArrX = f.xActualVal;
		root = new Node();
		
		root.cp = new ControlPoint(f, start, end);
		System.out.println(root.toString());
		createXML1(f, start, end, root);
		iterateTree(root);
		
		
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
	
	
	
	public void createXML1(Function f, int start, int end, Node node) {
		
		node.cp = new ControlPoint(f, start, end);
		
		int e = node.cp.getErrorIndex(f);
		String points;
		
		if(node.cp.maxError > 1) {
			
			Node left = new Node();
			node.left  = left;
			
			createXML1(f, start, start + e, node.left);
			
			Node right = new Node();
			node.right = right;
			createXML1(f, start + e, end, node.right);
		}

	}
	
	

	
	public void setMiddleToZero(Node node) {
		if(node.left != null && node.right != null && (node.left.cp.end - node.left.cp.start <= 5) && (node.right.cp.end - node.right.cp.start) <= 5) {
			Vector leftV2 = node.left.cp.v2;
			Vector leftV3 = node.left.cp.v3;
			double dXl = leftV2.x - leftV3.x;
			double dYl = leftV2.y - leftV3.y;
			double distL = Math.sqrt(Math.pow(dXl, 2) + Math.pow(dYl, 2));
			Vector t1 = new Vector((-1) * distL, 0);
			node.left.cp.v2 = Vector.add2Vectors(node.left.cp.v3, t1);
			
			Vector rightV1 = node.right.cp.v1;
			Vector rightV0 = node.right.cp.v0;
			double dXr = rightV1.x - rightV0.x;
			double dYr = rightV1.y - rightV0.y;
			double distR = Math.sqrt(Math.pow(dXr, 2) + Math.pow(dYr, 2));
			Vector t2 = new Vector(distR, 0);
			node.right.cp.v1 = Vector.add2Vectors(node.right.cp.v0, t2);
			
			
		}
	}
	
}
