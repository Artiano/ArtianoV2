package artiano.probability.test;

import artiano.core.structure.Attribute;
import artiano.core.structure.NominalAttribute;
import artiano.core.structure.NumericAttribute;
import artiano.core.structure.Table;
import artiano.probability.splitAttrSelect.ShannonEntropy;

public class ShannonEntropyTest {

	public static void main(String[] args) {
		Attribute[] attrs = new Attribute[]{
			new NumericAttribute("no surfacing"),
			new NumericAttribute("flippers"),
			new NominalAttribute("isFish")
		};
		Table dataset = new Table();
		dataset.addAttributes(attrs);
		// 设置类标属性
		dataset.setClassAttribute(2);
		
		Object[][] dataArr = new Object[][]{
			{1, 1, "yes"},   {1, 1, "yes"},   
			{1, 0, "no"},    {0, 1, "no"},
			{0, 1, "no"}
		};
		for(int i=0; i<dataArr.length; i++) {
			dataset.push(dataArr[i]);
		}
		
		ShannonEntropy shannonEnt = new ShannonEntropy();
	 	System.out.println("Shannon Entropy: " + 
	 			shannonEnt.calcShannonEntropy(dataset));
		
	}

}
