/**
 * Test.java
 */
package artiano.ml.regression.test;

import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;
import org.math.plot.Plot3DPanel;
import org.math.plot.PlotPanel;

import artiano.core.structure.Matrix;
import artiano.ml.regression.LinearRegression;
import artiano.ml.regression.Regression;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-10-21
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class Test {
	
	final static double[] x =  { 2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0,11.0};
	final static double[] y = {56.27,41.32,31.45,30.05,24.69,19.78,20.94,16.73,14.21,12.44};
	
	public static void test(){
		LinearRegression regression = new LinearRegression();
		Matrix X = new Matrix(10, 1, x);
		Matrix Y = new Matrix(10, 1, y);
		Matrix c = regression.fit(X, Y);
		c.print();
		Plot2DPanel panel = new Plot2DPanel();
		panel.addScatterPlot("x-y", x, y);
		double y1 = c.at(0) - 1.*c.at(1);
		double y2 = c.at(0) + 12.*c.at(1);
		panel.addLinePlot("xxx", new double[]{-1,y1}, new double[]{12,y2});
		panel.setLegendOrientation(PlotPanel.SOUTH);
		JFrame frame = new JFrame("a plot panel");
        frame.setSize(600, 600);
        frame.setContentPane(panel);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void test3D(){
		double[] a ={ 
				2.0,1,
				3.0,2,
				4.0,3,
				5.0,4,
				6.0,2,
				7.0,6,
				8.0,7,
				9.0,8,
				10.0,9,
				11.0,10};
	    	double[] b = {56.27,41.32,31.45,30.05,24.69,19.78,20.94,16.73,14.21,12.44};
	    Matrix XY = new Matrix(a.length/2, 2, a);
	    Matrix Z = new Matrix(b.length, 1, b);
	    Regression regression = new LinearRegression();
	    Matrix C = regression.fit(XY, Z);
	    for(int i=0; i<C.rows(); i++) {
    		for(int j=0; j<C.columns(); j++) {
    			if(i == 0) {
    				System.out.print(C.at(i, j) + " ");
    			} else {
    				if(C.at(i, j) < 0) {
    					System.out.print("- " + C.at(i, j) + "*x" + i + " ");
    				} else {
    					System.out.print("+ " + C.at(i, j) + "*x" + i + " ");
    				}    				
    			}    			
    		}
    	}
	    Plot3DPanel panel = new Plot3DPanel();
	    double[] x = XY.column(0).toArray();
	    double[] y = XY.column(1).toArray();
	    double[] z = b;
	    panel.addScatterPlot("scatter", x, y, z);
	    double[] xx = {0,12};
	    double[] yy = {0,11};
	    double[] zz = new double[2];
	    zz[0] = C.at(0)+C.at(1)*0+C.at(2)*0;
	    zz[1] = C.at(0)+C.at(1)*12+C.at(2)*11;
	    panel.addLinePlot("fitted model", xx, yy, zz);
	    panel.setLegendOrientation(PlotPanel.SOUTH);
	    JFrame frame = new JFrame("a plot panel");
        frame.setSize(600, 600);
        frame.setContentPane(panel);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void testRegression() {
		double[] a = {
    	     //	1, 3, 4, 5, 6,
				0.4, 23, 163, 
				3.1, 19, 37,
				0.6, 34, 157,
				4.7, 24, 59,
				1.7, 65, 123,
				9.4, 44, 46,				
				10.1, 31, 117,
				10.9, 37, 111,
    	};
    	double[] b = {
    			//3, 2, 9, 7, 1
    			60, 71, 61, 54, 77, 81, 93, 76
    	};
    	Matrix X = new Matrix(a.length / 3, 3, a);
    	Matrix Y = new Matrix(b.length, 1, b);
    	Matrix dst = (new LinearRegression()).fit(X, Y);
    	for(int i=0; i<dst.rows(); i++) {
    		for(int j=0; j<dst.columns(); j++) {
    			if(i == 0) {
    				System.out.print(dst.at(i, j) + " ");
    			} else {
    				if(dst.at(i, j) < 0) {
    					System.out.print("- " + dst.at(i, j) + "*x" + i + " ");
    				} else {
    					System.out.print("+ " + dst.at(i, j) + "*x" + i + " ");
    				}    				
    			}    			
    		}
    	}
	}
	
	public static void main(String[] arg){
		test3D();
	}
	
}
