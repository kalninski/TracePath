package vector;

import java.util.Arrays;
import java.util.Locale;

import statistics.Function;
import java.io.*;


public class ControlPoint {
	
	Vector v0;
	Vector v1;
	Vector v2;
	Vector v3;
	double[] functionY;//array of all the values
	double[] functionX;
	double[] polynomialY;
	double[] polynomialX;
	double[] parameterT;
	double[] error;
	double maxError;
	int start;
	int end;
	Function f;
	String points;
	
	
	public ControlPoint(Function f, int start, int end) {
		this.f = f;
		this.functionX = f.xActualVal;
		this.functionY = f.yActualVal;
		this.polynomialX = new double[end - start];
		this.polynomialY = new double[end - start];
		this.error = new double[end - start];
		this.start = start;
		this.end = end;
		this.v0 = new Vector(functionX[start], functionY[start]);
		this.v3 = new Vector(functionX[end], functionY[end]);
		initialParameterT();
		//System.out.println("T parameter array = " + Arrays.toString(parameterT));
		parameterTXT("NEWTON_RAPSON");
		setV1();
		setV2();
//		setV1_SecondVersion();
//		setV2_SecondVersion();
//		getValuesOfCurve();
		correctParameter();
//		setV1_SecondVersion();
//		setV2_SecondVersion();
		setV1();
		setV2();
		getValuesOfCurve();
		this.points = String.format(Locale.US,"M %.2f,%.2f C %.2f,%.2f %.2f,%.2f %.2f,%.2f",  functionX[start], functionY[start], this.v1.x, this.v1.y, this.v2.x, this.v2.y, functionX[end], functionY[end]);
//		System.out.println("COSNTRUCTOR polynomialY " + Arrays.toString(polynomialY));
//		System.out.println("COSNTRUCTOR polynomialX " + Arrays.toString(polynomialX));
		
	}
	
	
	public void initialParameterT() {
		double n = end - start;
		parameterT = new double[end - start];
		double increment = 1/n;
		double t = 0;
		for(int i = 0; i < n; i++) {
			parameterT[i] = t;
			t += increment;
		}
//		System.out.println("intitial paramter T arr = " + Arrays.toString(parameterT));
	}
	
	public void initialParameterT1() {
		double n = end - start;
		double totalDist = 0;
		parameterT = new double[end - start];
		double[] distances = new double[(int) (n-1)];
		double cumulativeDist = 0;
		if(n <= 1) {
			parameterT[0] = 0;
			return;
		}
		for(int i = 0; i < n - 1; i++) {
			double dy = functionY[start + i + 1] - functionY[start + i];
			double dx = functionX[start + i + 1] - functionX[start + i];
			double dyPow2 = Math.pow(dy, 2);
			double dxPow2 = Math.pow(dx, 2);
			totalDist += Math.sqrt(dyPow2 + dxPow2);
			distances[i] = Math.sqrt(dyPow2 + dxPow2);
			
		}
		if(totalDist <= 1E-10) {
			for(int k = 0; k < n; k++) {
				parameterT[k] = ((double) k) / n;

			}
		}else {
		parameterT[0] = 0;
		for(int j = 1; j < n; j++) {
			cumulativeDist += distances[j - 1];
			parameterT[j] = cumulativeDist/totalDist;

			
		}
		}
//		System.out.println("intitial paramter T arr = " + Arrays.toString(parameterT));
	}
	
	public int getErrorIndex(Function f) {
		int errorIndex = 0;
		maxError = 0;
		double errorX = 0;
		double errorY = 0;
		error = new double[end - start];
		for(int i = 0; i < error.length; i++) {
			errorY = Math.abs(f.yActualVal[start + i] - polynomialY[i]);
			errorX = Math.abs(f.xActualVal[start + i] - polynomialX[i]);
//			System.out.println("errorY = " + errorY + " errorX = " + errorX + " polynomialY[i] = " + polynomialY[i] + " polynomialX[i]" + polynomialX[i]);
			errorY = Math.pow(errorY, 2);
			errorX = Math.pow(errorX, 2);
			error[i] = Math.sqrt((errorX + errorY));
//			System.out.println("error[i] = " + error[i]);
			if(error[i] > error[errorIndex]) {
				maxError = error[i];
				errorIndex = i;
//				System.out.println(i + " INDEX ERROR");
				
				
			}
		
		}
		
//		System.out.println("error = " + Arrays.toString(error));
//		System.out.println("actualVal Y = " + Arrays.toString(f.yActualVal));
//		System.out.println("polynomial Y = " + Arrays.toString(polynomialY));
		return errorIndex;
	}
	
	public Vector getTangent1() {
		double count = Math.min(5, end - start - 1);
		if(count <= 1) {
			System.out.println("Count is less than 1 " + (start + 1 < functionY.length));
			return (start + 1 < functionY.length) ? new Vector(functionX[start + 1] - functionX[start], functionY[start + 1] - functionY[start]) : new Vector(1, 0);
		}
		Vector tg = new Vector(0, 0);
		double x = 0;
		double y = 0;
		double trueCount = 0;
		for(int i = 1; i < 5; i++) {
			
			if((start + i) < functionY.length) {
				
				double dx = functionX[start + i] - functionX[start];
				double dy = functionY[start + i] - functionY[start];
					
					if(Math.abs(dx) > 1e-10 || Math.abs(dy) > 1e-10) {
						tg.x += dx;
						tg.y += dy;
						trueCount++;
					}
					
				
			}
			
		}
		
		if(trueCount == 0) {
			System.out.println("trueCount = 0 so vector is (1, 0)");
			return new Vector(1, 0);
		}
		tg.x = tg.x/count;
		tg.y = tg.y/count;
		
		if((tg.x == 0 && tg.y == 0) || tg.x == Double.NaN || tg.y == Double.NaN) {
			System.out.println(tg.toString());
			return new Vector(1, 0);
		}
	
		return tg.normalize();
	}
		

	
	public Vector getTangent2() {
		Vector tg = new Vector(0, 0);
		double count = Math.min(5, end - start - 1);
		double x = 0;
		double y = 0;
		double trueCount = 0;
		if(count <= 1) {
			//if the subarray is of size 1, yet not start, return vector is the difference between current and next vecotr
			return (end - 1 > 0) ? new Vector(functionX[end - 1] - functionX[end], functionY[end - 1] - functionY[end]) : new Vector (-1, 0);
		}
		for(int i = 1; i < 5; i++) {
			
		if((end - i) > 0) {
			double dx = functionX[end - i] - functionX[end];
			double dy = functionY[end - i] - functionY[end];
			if(Math.abs(dx) > 1e-10 || Math.abs(dy) > 1e-10) {
				tg.x += dx;
				tg.y += dy;
				trueCount++;
			}
			
		}
		
		}
		if(trueCount == 0) {
			return new Vector(-1, 0);
		}
		tg.x = tg.x/count;
		tg.y = tg.y/count;
		if((tg.x == 0 && tg.y == 0) || tg.x == Double.NaN || tg.y == Double.NaN) {
			return new Vector(-1, 0);
		}
		
		return tg.normalize();
	}
	

	
	public double getC11() {
		int sizeParamT = parameterT.length;
		double sum = 0;
		for(int i = 0; i < sizeParamT; i++) {
			
			double t = parameterT[i];
			sum += Math.pow(1-t, 4) * Math.pow(t, 2) * 9;
		}
		return sum;
	}
	
	public double getC12C21() {
		int sizeParamT = parameterT.length;
		double sum = 0;
		Vector tan1 = getTangent1();
		Vector tan2 = getTangent2();
		double tan1DotTan2 = tan1.dot(tan2);
		for(int i = 0; i< sizeParamT; i++) {
			
			double t = parameterT[i];
			sum += Math.pow((1-t), 3) * Math.pow(t, 3) * 9 * tan1DotTan2;
		}
		
		return sum;
	}
	
	public double getC22() {
		int sizeParamT = parameterT.length;
		double sum = 0;
		for(int i = 0; i < sizeParamT; i++) {
			
			double t = parameterT[i];
			sum += Math.pow(t, 4) * Math.pow((1-t), 2) * 9;
		}
		
		return sum;
	}
	
	//One bernstein term specifically for getX1(), getX2() method
	public Vector bernstein(double t, Vector v0, Vector v1, Vector v2, Vector v3 ) {
		double coeff0 = Math.pow(1-t, 3);
		double coeff1 = Math.pow((1-t), 2) * t * 3;
		double coeff2 = Math.pow(t, 2) * (1-t) * 3;
		double coeff3 = Math.pow(t, 3);
		Vector newV0 = Vector.multiplyByScaler(coeff0, v0);
		Vector newV1 = Vector.multiplyByScaler(coeff1, v1);
		Vector newV2 = Vector.multiplyByScaler(coeff2, v2);
		Vector newV3 = Vector.multiplyByScaler(coeff3, v3);
		Vector sum = Vector.add4Vectors(newV0, newV1, newV2, newV3);
//		System.out.println("Bernestein T =  " + t + " coeff0 = " + coeff0 + " newV0 = " + newV0.toString() + " v0 = " + v0 );
		return sum;
	}
	
	public double getX1() {
		Vector tan1 = getTangent1();
		int size = parameterT.length;
		double sum = 0;
		
		for(int i = 0; i < size; i ++) {
			
			double t = parameterT[i];
			Vector dI = new Vector(functionX[start + i], functionY[start + i]);
			Vector bernstein = bernstein(t, v0, v0, v3, v3);
			Vector subtraction = Vector.subtract2Vectors(dI, bernstein);
			double coeffA1 = Math.pow((1-t), 2) * t * 3;
			Vector a1 = Vector.multiplyByScaler(coeffA1, tan1);
			sum += subtraction.dot(a1);
		}
		
		return sum;
	}
	
	public double getX2() {
		Vector tan2 = getTangent2();
		int size = parameterT.length;
		double sum = 0;
		
		for(int i = 0; i < size; i ++) {
			
			double t = parameterT[i];
			Vector dI = new Vector(functionX[start + i], functionY[start + i]);
			Vector bernstein = bernstein(t, v0, v0, v3, v3);
			Vector subtraction = Vector.subtract2Vectors(dI, bernstein);
			double coeffA2 = Math.pow(t, 2) * (1-t) * 3;
			Vector a2 = Vector.multiplyByScaler(coeffA2, tan2);
			sum += subtraction.dot(a2);
			
		}
		
		return sum;
	}
	
	public void setV1() {
		Matrix matrix = new Matrix();
		matrix.c11 = getC11();
		matrix.c12 = getC12C21();
		matrix.c21 = matrix.c12;
		matrix.c22 = getC22();
		double x1 = getX1();
		double x2 = getX2();
		double alpha1;
		double epsilon = 1e-15;
		Vector t1 = getTangent1();
		Matrix num = new Matrix(x1, matrix.c12, x2, matrix.c22);
		double numerator = num.determinant();
		double denominator = matrix.determinant();
		if(Math.abs(denominator) < epsilon) {
			

			t1 = Vector.multiplyByScaler(1, t1);
			
		}else {
			
			alpha1 = numerator/denominator;
			t1 = Vector.multiplyByScaler(alpha1, t1);
		
		}
		v1 = Vector.add2Vectors(v0, t1);
	//	System.out.println("set V1 = " + v1.toString());
	}
	//Second version!!!!!!
	public void setV1_SecondVersion() {
		Matrix matrix = new Matrix();
		matrix.c11 = getC11();
		matrix.c12 = getC12C21();
		matrix.c21 = matrix.c12;
		matrix.c22 = getC22();
		double x1 = getX1();
		double x2 = getX2();
		double alpha1;
		double epsilon = 1e-10;
		Vector t1 = getTangent1();
		Matrix num = new Matrix(x1, matrix.c12, x2, matrix.c22);
		double numerator = num.determinant();
		double denominator = matrix.determinant();
		if(Math.abs(denominator) < epsilon) {
			
		//	alpha1 = 0;
			double dy = f.getDerivative(start);
			Vector ddx = new Vector(1, dy);
			t1 = ddx.normalize();
//			Vector tOther = getOneValueOfCurveFirstDerivative(0);
//			t1 = tOther.normalize();
//			t1 = Vector.multiplyByScaler(1, t1);
			
		}else {
			
			alpha1 = numerator/denominator;
			t1 = Vector.multiplyByScaler(alpha1, t1);
		
		}
		v1 = Vector.add2Vectors(v0, t1);
	//	System.out.println("set V1 = " + v1.toString());
	}
	
	public void setV2() {
		Matrix matrix = new Matrix();
		matrix.c11 = getC11();
		matrix.c12 = getC12C21();
		matrix.c21 = matrix.c12;
		matrix.c22 = getC22();
		double x1 = getX1();
		double x2 = getX2();
		double alpha1;
		double epsilon = 1e-15;
		Vector t2 = getTangent2();
		Matrix num = new Matrix(matrix.c11, x1, matrix.c21, x2);
		double numerator = num.determinant();
		double denominator = matrix.determinant();
		if(Math.abs(denominator) < epsilon) {
			
		//	alpha1 = 0;
		
			
			t2 = Vector.multiplyByScaler(1, t2);
			
		}else {
			
			alpha1 = numerator/denominator;
			t2 = Vector.multiplyByScaler(alpha1, t2);
		}
		
		v2 = Vector.add2Vectors(v3, t2);
	}
	
	//SECOND_VERSION!!!!!
	public void setV2_SecondVersion() {
		Matrix matrix = new Matrix();
		matrix.c11 = getC11();
		matrix.c12 = getC12C21();
		matrix.c21 = matrix.c12;
		matrix.c22 = getC22();
		double x1 = getX1();
		double x2 = getX2();
		double alpha1;
		double epsilon = 1e-10;
		Vector t2 = getTangent2();
		Matrix num = new Matrix(matrix.c11, x1, matrix.c21, x2);
		double numerator = num.determinant();
		double denominator = matrix.determinant();
		if(Math.abs(denominator) < epsilon) {
			
		//	alpha1 = 0;
			double dy = f.getDerivative(end);
			Vector ddx = new Vector(-1, (-1 * dy));
			t2 = ddx.normalize();
			
//			Vector tOther = getOneValueOfCurveFirstDerivative(1);
//			t2 = tOther.normalize();
//			t2 = Vector.multiplyByScaler(1, t2);
			
		}else {
			
			alpha1 = numerator/denominator;
			t2 = Vector.multiplyByScaler(alpha1, t2);
		}
		
		v2 = Vector.add2Vectors(v3, t2);
	}
	
	
	public void getValuesOfCurve() {
		int size = parameterT.length;
		for(int i = 0; i < size; i++) {
			double t = parameterT[i];
			Vector lerpVec = bernstein(t, v0, v1, v2, v3);
			polynomialY[i] = lerpVec.y;
			polynomialX[i] = lerpVec.x;
	//	System.out.println("lerped vec with bernstein = " + lerpVec.toString() );
		}
	}
	
	public void parameterTXT(String name) {
		String sep = File.separator;
		File f = new File("C:" + sep + "Users" + sep + "Toms" + sep + "Desktop" + sep + "ImageEXPERIMENTS" + sep  + name + ".txt");
		
		try {
			if(!f.equals(f)) {
				FileWriter w = new FileWriter(f);
				w.write("Parameter arrays 1");
			}
			BufferedWriter write = new BufferedWriter(new FileWriter(f, true));
			if(parameterT.length < 5) {
				write.write("!!!!!!!!!!!!!" + Arrays.toString(this.parameterT));
			}else {
			write.write(Arrays.toString(this.parameterT));
			}
			write.newLine();
			write.close();
			
		}catch(IOException ex) {
			ex.printStackTrace();
		}
		
	}
	
	public Vector getOneValueOfCurve(double t) {
		double coeff0 = Math.pow((1-t), 3);
		double coeff1 = Math.pow((1-t), 2) * t * 3;
		double coeff2 = Math.pow(t, 2) * (1-t) * 3;
		double coeff3 = Math.pow(t, 3);
		Vector v0New = Vector.multiplyByScaler(coeff0, v0);
		Vector v1New = Vector.multiplyByScaler( coeff1, v1);
		Vector v2New = Vector.multiplyByScaler(coeff2, v2);
		Vector v3New = Vector.multiplyByScaler(coeff3, v3);
		return Vector.add4Vectors(v0New, v1New, v2New, v3New);
	}
	
	public Vector getOneValueOfCurveFirstDerivative(double t) {
		double coeff0 = Math.pow((1-t), 2);
		double coeff1 = (1-t) * t * 2;
		double coeff2 = Math.pow(t, 2);
		Vector deltaV0 = Vector.subtract2Vectors(v1, v0);
		Vector deltaV1 = Vector.subtract2Vectors(v2, v1);
		Vector deltaV2 = Vector.subtract2Vectors(v3, v2);
		Vector v0New = Vector.multiplyByScaler(coeff0, deltaV0);
		Vector v1New = Vector.multiplyByScaler(coeff1, deltaV1);
		Vector v2New = Vector.multiplyByScaler(coeff2, deltaV2);
		Vector sum = Vector.add3Vectors(v0New, v1New, v2New);
		return Vector.multiplyByScaler(3, sum);
	}
	
	public Vector getOneValueOfCurveSecondDerivative(double t) {
		double coeff0 = 1-t;
		double coeff1 = t;
		Vector delta1V0 = Vector.subtract2Vectors(v1, v0);
		Vector delta1V1 = Vector.subtract2Vectors(v2, v1);
		Vector delta1V2 = Vector.subtract2Vectors(v3, v2);
		Vector delta2V0 = Vector.subtract2Vectors(delta1V1, delta1V0);
		Vector delta2V1 = Vector.subtract2Vectors(delta1V2, delta1V1);
		delta2V0 = Vector.multiplyByScaler(coeff0, delta2V0);
		delta2V1 = Vector.multiplyByScaler(coeff1, delta2V1);
		Vector sum = Vector.add2Vectors(delta2V1, delta2V0);
		return Vector.multiplyByScaler(6 ,sum);
	}
	
	public double updateTParameter(double t, int index) {
		
		Vector qT = getOneValueOfCurve(t);
		Vector dI = new Vector(functionX[start + index], functionY[start + index]);
		Vector subtraction = Vector.subtract2Vectors(qT, dI);
		Vector qPrimeT = getOneValueOfCurveFirstDerivative(t);
		double numerator = subtraction.dot(qPrimeT);
		double productRule1 = qPrimeT.dot(qPrimeT);
		Vector qDoublePrimeT = getOneValueOfCurveSecondDerivative(t);
		double productRule2 = subtraction.dot(qDoublePrimeT);
		double denominator = productRule1 + productRule2;
		double fraction;
		if(Math.abs(denominator) > 1e-10) {
		fraction = numerator/denominator;
		}else {
			fraction = 0;
		}
		t = t - fraction;
		t = Math.max(0, Math.min(1, t));
//		System.out.println("the new t = " + t + " old t = "  + (t + fraction));
		return t;
	}
	
	public void correctParameter() {
		int size = parameterT.length;
		for(int i = 0; i < size; i++) {
			double t = parameterT[i];
			double oldT;
			double epsilon = 1e-6;
			for(int j = 0; j < 5; j++) {
				oldT = t;
				
				t = updateTParameter(t, i);
				if(Math.abs(t - oldT) <= epsilon) {
					return;
				}
				
			}
			parameterT[i] = t;
		}
	}
	
	//Use tangents of another control point
	public void setInnerPointsLeft(ControlPoint cp) {
		Vector t1 = new Vector();

		t1 = Vector.subtract2Vectors(cp.v1, cp.v0);
//		t2 = Vector.subtract2Vectors(cp.v3, cp.v2);
		t1 = Vector.multiplyByScaler(-1, t1);
//		t2 = Vector.multiplyByScaler(-1, t2);
		t1 = t1.normalize();
//		t2 = t2.normalize();
		this.v1 = Vector.add2Vectors(this.v0, t1);
//		this.v2 = Vector.add2Vectors(this.v3, t2);
		this.points = String.format(Locale.US,"M %.2f,%.2f C %.2f,%.2f %.2f,%.2f %.2f,%.2f",  functionX[start], functionY[start], this.v1.x, this.v1.y, this.v2.x, this.v2.y, functionX[end], functionY[end]);
	}
	
	//Use tangents of another control point
	public void setInnerPoints(ControlPoint cp) {
		Vector t1 = new Vector();
		Vector t2 = new Vector();
		t1 = Vector.subtract2Vectors(cp.v1, cp.v0);
		t2 = Vector.subtract2Vectors(cp.v3, cp.v2);
		t1 = Vector.multiplyByScaler(-1, t1);
		t2 = Vector.multiplyByScaler(-1, t2);
		t1 = t1.normalize();
		t2 = t2.normalize();
		this.v1 = Vector.add2Vectors(this.v0, t1);
		this.v2 = Vector.add2Vectors(this.v3, t2);
		this.points = String.format(Locale.US,"M %.2f,%.2f C %.2f,%.2f %.2f,%.2f %.2f,%.2f",  functionX[start], functionY[start], this.v1.x, this.v1.y, this.v2.x, this.v2.y, functionX[end], functionY[end]);
	}
	
	public void changeControlPoint(ControlPoint cp, Vector v) {
		Vector t = Vector.subtract2Vectors(v, cp.v0);
	}

	
	
}
