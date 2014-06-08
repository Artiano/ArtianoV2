package artiano.probability.test;

import artiano.core.structure.Attribute;
import artiano.core.structure.NominalAttribute;
import artiano.core.structure.Table;
import artiano.probability.splitAttrSelect.GiniIndex;

public class GiniIndexTest {

	public static void main(String[] args) {
		Table dataset = new Table();
		Attribute[] attrs = new Attribute[]{
			new NominalAttribute("体温"),   new NominalAttribute("表面覆盖"),
			new NominalAttribute("胎生"),   new NominalAttribute("产蛋"),
			new NominalAttribute("能飞"),   new NominalAttribute("水生"),
			new NominalAttribute("有腿"),   new NominalAttribute("冬眠"),
			new NominalAttribute("类标记")
		};
		dataset.addAttributes(attrs);
		dataset.setClassAttribute(attrs.length - 1);
		
		Object[][] dataArr = new Object[][]{
			{"恒温", "毛发", "是", "否", "否", "否", "是", "否", "哺乳类"},   //人
			{"冷血", "鳞片", "否", "是", "否", "否", "否", "是", "爬行类"},   //巨蟒
			{"冷血", "鳞片", "否", "是", "否", "是", "否", "否", "鱼类"},     //鲑鱼
			{"恒温", "毛发", "是", "否", "否", "是", "否", "否", "哺乳类"},   //鲸
			{"冷血", "无", "否", "是", "否", "有时", "是", "是", "两栖类"},	//蛙
			{"冷血", "鳞片", "否", "是", "否", "否", "是", "否", "爬行类"},	//巨蜥
			{"恒温", "毛发", "是", "否", "是", "否", "是", "否", "哺乳类"},	//蝙蝠
			{"恒温", "皮", "是", "否", "否", "否", "是", "否", "哺乳类"},		//猫
			{"冷血", "鳞片", "是", "否", "否", "是", "否", "否", "鱼类"},		//豹纹鲨
			{"冷血", "鳞片", "否", "是", "否", "有时", "是", "否", "爬行类"},	//海龟
			{"恒温", "刚毛", "是", "否", "否", "否", "是", "是", "哺乳类"},	//豪猪
			{"冷血", "鳞片", "否", "是", "否", "是", "否", "否", "鱼类"},		//鳗
			{"冷血", "无", "否", "是", "否", "有时", "是", "是", "两栖类"},	//蝾螈
			{"恒温", "刚毛", "否", "是", "是", "否", "是", "是", "鸟类"},		//啄木鸟
			{"恒温", "刚毛", "否", "是", "是", "否", "是", "是", "鸟类"}		//大雁
		};
		for(int i=0; i<dataArr.length; i++) {
			dataset.push(dataArr[i]);
		}
		
		GiniIndex gini = new GiniIndex();
		System.out.println("Gini index:  " + gini.calcGiniIndex(dataset, 0));
		
	}

}
