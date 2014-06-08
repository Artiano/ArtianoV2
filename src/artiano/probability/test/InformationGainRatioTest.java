package artiano.probability.test;

import artiano.core.structure.Attribute;
import artiano.core.structure.NominalAttribute;
import artiano.core.structure.Table;
import artiano.probability.splitAttrSelect.InformationGainRatio;

public class InformationGainRatioTest {

	public static void main(String[] args) {
		Attribute[] attrs = new Attribute[]{
				new NominalAttribute("Outlook"),
				new NominalAttribute("Temperature"),
				new NominalAttribute("Humidity"),
				new NominalAttribute("Windy"),
				new NominalAttribute("Play"),
			};
		Table dataset = new Table();
		dataset.addAttributes(attrs);
		dataset.setClassAttribute(attrs.length - 1);
		
		Object[][] dataArr = new Object[][]{
				{"sunny",  "hot",  "high",  "false",  "no"},
				{"sunny",  "hot",  "high",  "true",  "no"},
				{"overcast",  "hot",  "high",  "false",  "yes"},
				{"rain",  "mild",  "high",  "false",  "yes"},
				{"rain",  "cool",  "normal",  "false",  "yes"},
				{"rain",  "cool",  "normal",  "true",  "no"},
				{"overcast",  "cool",  "normal",  "true",  "yes"},
				{"sunny",  "mild",  "high",  "false",  "no"},
				{"sunny",  "cool",  "normal",  "false",  "yes"},
				{"rain",  "mild",  "normal",  "false",  "yes"},
				{"sunny",  "mild",  "normal",  "true",  "yes"},
				{"overcast",  "mild",  "high",  "true",  "yes"},
				{"overcast",  "hot",  "normal",  "false",  "yes"},
				{"rain",  "mild",  "high",  "true",  "no"}
		};
		for(int i=0; i<dataArr.length; i++) {
			dataset.push(dataArr[i]);
		}

		InformationGainRatio IGR = new InformationGainRatio();
		System.out.println("Information Gain Ratio: " + 
				IGR.calcInfoGainRatio(dataset, 3));
		
	}

}
