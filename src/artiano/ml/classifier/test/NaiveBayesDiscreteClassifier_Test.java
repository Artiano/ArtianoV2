package artiano.ml.classifier.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import artiano.core.structure.Matrix;
import artiano.ml.classifier.NaiveBayesDiscreteClassifier;

public class NaiveBayesDiscreteClassifier_Test {
	public static void main(String[] arg) throws FileNotFoundException{
		NaiveBayesDiscreteClassifier cs=new NaiveBayesDiscreteClassifier();
		double[] in=new double[129*14];
		Scanner cin=new Scanner(new File("testData/NaiveBayesDiscreteClassifier_data.txt"));
		int con=0;
		while(cin.hasNext()){
			in[con++]=cin.nextDouble();
		}
		Matrix mx=new Matrix(129,14,in);
		mx.print();
		String[] strs=new String[13];
		strs[0]="[11,12) [12,13) [13,14) [14,B)";
		strs[1]="(0,1] (1,2] (2,3] (3,4] (4,B]";
		strs[2]="(0,2] (2,2.20] (2.20,2.40] (2.40,2.60] (2.60,2.80] (2.80,B]";
		strs[3]="(0,10] (10,12] (12,14] (14,16] (16,18] (18,20] (20,B]";
		strs[4]="(0,70] (70,80] (80,100] (100,120] (120,B]";
		strs[5]="(0,2] (2,2.5] (2.5,3] (3,4] (4,B]";
		strs[6]="(0,2] (2,2.5] (2.5,3] (3,4] (4,B]";
		strs[7]="(0,0.2] (0.2,0.4] (0.4,0.7] (0.7,1] (1,B]";
		strs[8]="(0,1] (1,1.5] (1.5,2] (2,3] (3,B]";
		strs[9]="(0,2] (2,3] (3,4] (4,4.5] (4.5,5] (5,B]";
		strs[10]="(0,1] (1,2] (2,3] (3,B]";
		strs[11]="(0,1] (1,1.5] (1.5,2] (2,2.5] (2.5,3] (3,3.5] [3.5,B]";
		strs[12]="(0,400] (400,800] (800,1000] (1000,1200] (1200,B]";
		cs.train(mx,strs, 0);
		cin=new Scanner(new File("testData/NaiveBayesDiscreteClassifier_test.txt"));
		con=0;
		while(cin.hasNext()){
			in[con++]=cin.nextDouble();
		}
		Matrix ts=new Matrix(49,14,in);
		cs.testResult(ts, 0);
		/***
		 * 序列化接口测试
		 */
		try {
			cs.save("cs.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			NaiveBayesDiscreteClassifier loadcs=(NaiveBayesDiscreteClassifier)NaiveBayesDiscreteClassifier.load("cs.txt");
			loadcs.trainingResults[0].print();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		double[] lpa={0.5,0,0.2,0.4,0.4};
//		Matrix lp=new Matrix(1,5,lpa);
//		Matrix ps=cs.laPlace(lp, 20);
//		ps.printAll();
	}

}
