package artiano.ml.classifier.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import artiano.core.structure.Attribute;
import artiano.core.structure.NominalAttribute;
import artiano.core.structure.NumericAttribute;
import artiano.core.structure.Table;
import artiano.core.structure.Table.TableRow;
import artiano.ml.classifier.DTreeClassifier;

public class DTreeClassifierTest {

	public static void main(String[] args) {
		testDTreeClasifier();
	}

	public static void testDTreeClasifier() {										
		Table dataSet = new Table();
		//Load training data
		//File that store the training data
		String dataFilePath = "src\\artiano\\ml\\classifier\\test\\data.txt";
		loadTrainingData(dataSet, dataFilePath); 										
		
		DTreeClassifier classifier = new DTreeClassifier();
		classifier.train(dataSet); //Train data
		try {
			classifier.save("D:\\decisionTree.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		 try {
			artiano.ml.classifier.DTreeClassifier dTreeClassifier = 
				(artiano.ml.classifier.DTreeClassifier)artiano.ml.classifier.DTreeClassifier.load("D:\\decisionTree.txt");
			System.out.println(dTreeClassifier.toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 		 
		//-------------------------predict-------------------------------//
		Table samples = new Table();
		loadTrainingData(samples, dataFilePath);
		samples.removeAttribute(samples.classIndex());
		NominalAttribute classificationList = classifier.predict(samples);
		for(int i=0; i<classificationList.size(); i++) {
			System.out.println("Classification: " + classificationList.get(i));
		}
			
	}
	
	//Load the training data
	//Return index of the target attribute	
	private static void loadTrainingData(Table dataSet,String dataFilePath) {
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(dataFilePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		int targetAttrIdx = -1;  //IncrementIndex of target attribute index 
		try {
			//Target attribute index
			String targetAttrIdxStr = 
					(input.readLine().trim()).split("[\t]")[1];
			targetAttrIdx = Integer.parseInt(targetAttrIdxStr);
			System.out.println("traget index: " + targetAttrIdx);
			//Attributes
			String[] attributes = 
					(input.readLine().trim()).split("[\t]");  //Attributes			
			//Training data
			String item = input.readLine();
			String[] firstRowAttrValues = (item.trim()).split("[\t]");
			for(int i=0; i<firstRowAttrValues.length; i++) {		
				String value = firstRowAttrValues[i].trim();
				Attribute attr;
				if(value.matches("\\d+")) {
					attr = new NumericAttribute(attributes[i]);
				} else {
					attr = new NominalAttribute(attributes[i]);
				}
				dataSet.addAttribute(attr);				
			}
			while(! "".equals(item) && ! (null == item) ) {
				String[] attrValues = (item.trim()).split("[\t]");				
				TableRow tableRow = dataSet.new TableRow();				
				for(int i=0; i<attrValues.length; i++) {
					String value = attrValues[i].trim();
					tableRow.set(i, value);								
				}
				dataSet.push(tableRow);
				item = input.readLine();
			}								
			input.close();						
			dataSet.setClassAttribute(targetAttrIdx);
		} catch (IOException e) {			
			e.printStackTrace();
		}		
	}	
		
}
