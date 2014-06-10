package artiano.ml.classifier.test;

import artiano.core.structure.Matrix;
import artiano.core.structure.NominalAttribute;
import artiano.core.structure.Table;
import artiano.ml.classifier.KNearest;

public class KNearestTest {
	static double[] trainDataArr_2 = {			
		1, 2, 10,
		3, 2, 5,
		2, 8, 4,
		1, 5, 8,
		2, 7, 5,
		2, 6, 4, 
		3, 1, 2,
		1, 4, 9
	};  //第一列为类标号
	
	public static void main(String[] args) {
		testKNearest();
	}

	public static void testKNearest() {
		int attrNum = 3;
		//Train data
		Matrix trainData = 
			new Matrix(trainDataArr_2.length / attrNum, attrNum, trainDataArr_2);
		Table trainSet = new Table(trainData);
		trainSet.setClassAttribute(0);    //指定类标号属性的下标
		KNearest kNearest = new KNearest();
		kNearest.train(trainSet);  //Train data
		
		double[] sampleArr = {		
				2, 8,
				6, 9,
				2, 4,
				4, 8,
				4, 5
		};	//1, 1, 3, 1, 2 
		Matrix samplesMat = 
			new Matrix(sampleArr.length / (attrNum-1), attrNum-1, sampleArr);
		Table samples = new Table(samplesMat);
		//Find k-nearest		
		kNearest.setK(4);
		NominalAttribute results = kNearest.predict(samples);		
		if(results != null) {
			for(int i=0; i<results.size(); i++) {
				System.out.println("classification " + 
						Math.round((double)results.get(i)));
			}
		}
	}

	
}
