package vector;

import java.util.Locale;
import statistics.Function;
import java.util.*;


public class GenerateXML {
	
	public Function f;
	double[] functionArrX;
	double[] functionArrY;
	double[] errorArr;
	int start;
	int end;
	Node root;
	ArrayList<ControlPoint> controlPoints = new ArrayList<ControlPoint>();
	
	
	StringBuilder svg = new StringBuilder();
	public String restXML =  "\" stroke=\"black\" fill=\"none\" stroke-width=\"2\"/></svg>";
							
			        

	
	public GenerateXML(Function f) {
		this.f = f;
		this.functionArrY = f.yActualVal;
		this.functionArrX = f.xActualVal;
		root = new Node();
		
		root.cp = new ControlPoint(f, start, end);
		System.out.println(root.toString());
		createXML(f, 0, f.yActualVal.length - 1, root);

		
		
	}
	
	
	
	
	public void createXML(Function f, int start, int end, Node node) {
		
		node.cp = new ControlPoint(f, start, end);
		
		int e = node.cp.getErrorIndex(f);
		String points;
		
		if(node.cp.maxError > 1) {
			
			Node left = new Node();
			node.left  = left;
			
			createXML(f, start, start + e, node.left);
			
			Node right = new Node();
			node.right = right;
			createXML(f, start + e, end, node.right);
		}else {
	//		setMiddleToZero(node);
//			points = String.format(Locale.US,"M %.2f,%.2f C %.2f,%.2f %.2f,%.2f %.2f,%.2f",  functionArrX[start], functionArrY[start], node.cp.v1.x, node.cp.v1.y, node.cp.v2.x, node.cp.v2.y, functionArrX[end], functionArrY[end]);
//			svg.append(points);
		}

	}
	

	
	//ADD TO ARRAY LIST DURING THE CREATION OF SUBCURVES IN createXML1() method!!!!!!!!!!!!!!!!!!!!!!!!!!!!! NOT here
	public void iterate(Node node) {


		if(node != null) {
			
			if(node.left == null && node.right == null) {
				
				String points = String.format(Locale.US,"M %.2f,%.2f C %.2f,%.2f %.2f,%.2f %.2f,%.2f",  functionArrX[node.cp.start], functionArrY[node.cp.start], node.cp.v1.x, node.cp.v1.y, node.cp.v2.x, node.cp.v2.y, functionArrX[node.cp.end], functionArrY[node.cp.end]);

				controlPoints.add(node.cp);
				
			//	svg.append(points);
			}
	//		System.out.println(String.format(Locale.US,"\t  M %.2f,%.2f C %.2f,%.2f %.2f,%.2f %.2f,%.2f",  functionArrX[node.cp.start], functionArrY[node.cp.start], node.cp.v1.x, node.cp.v1.y, node.cp.v2.x, node.cp.v2.y, functionArrX[node.cp.end], functionArrY[node.cp.end]));
			
		}
		if(node.left != null) {
			iterate(node.left);
		}
		if(node.right != null) {
			iterate(node.right);
		}
	}
	
	public void continuityConstraintG1() {
		int size = controlPoints.size();
		int counter = 0;
		
		
		for(ControlPoint cp: controlPoints) {
			
			
			if(counter + 1 < size && counter > 0) {
				Vector t = Vector.subtract2Vectors(cp.v0, cp.v3);

				double distCurr = Vector.vecMagnitude(t);
				ControlPoint nextCP = controlPoints.get(counter + 1);
				ControlPoint lastCP = controlPoints.get(counter - 1);
				double distNext = nextCP.getMagnitude();
				
				if(distCurr <= 20 && distNext <= 20) {
					cp.setV1fromPrev(lastCP);

				}
				if(distCurr <=20 && distNext > 20) {

					cp.setV1fromPrev(lastCP);
					cp.setV2fromNext(nextCP);

				}
			}
			
			String points = String.format(Locale.US,"M %.2f,%.2f C %.2f,%.2f %.2f,%.2f %.2f,%.2f",  functionArrX[cp.start], functionArrY[cp.start], cp.v1.x, cp.v1.y, cp.v2.x, cp.v2.y, functionArrX[cp.end], functionArrY[cp.end]);
			counter ++;
			
			svg.append(points);

			
			
		}
	}
	
	
	
}
