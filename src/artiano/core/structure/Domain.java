package artiano.core.structure;

import java.io.Serializable;

/***
 * 
 * @author BreezeDust
 *
 */
public class Domain implements Serializable{
	public double min=0;
	public double max=0;
	public String minCD="";
	public String maxCD="";
	/***
	 * 以定义域的形式构造划分，如(1.2,3.0],其中B代表无穷,如(B,1]
	 * @param condition (1.2,3.0]或者(B,1]的形式  
	 */
	public Domain(String condition){
		minCD=condition.substring(0, 1);
		maxCD=condition.substring(condition.length()-1,condition.length());
		condition=condition.replaceAll("[\\(\\)\\[\\]]", "");
		String[] strs=condition.split(",");
		if(strs[0].equals("B")){
			min=-1*Double.MAX_VALUE;
		}
		else{
			min=Double.parseDouble(strs[0]);
		}
		if(strs[1].equals("B")){
			max=Double.MAX_VALUE;
		}
		else{
			max=Double.parseDouble(strs[1]);	
		}
	}
	/***
	 * 判断是否落在划分内，
	 * @param value
	 * @return 0为落在区域内，-1在区域左边，1在区域右边
	 */
	public int isIn(double value){
		boolean[] flag=new boolean[2];
		flag[0]=false;
		flag[1]=false;
		int con=0;
		if(minCD.equals("(")) if(value>min) flag[con++]=true;
		if(minCD.equals("[")) if(value>=min) flag[con++]=true;
		if(con==0) con++;
		if(maxCD.equals(")")) if(value<max) flag[con++]=true;
		if(maxCD.equals("]")) if(value<=max) flag[con++]=true;
		if(flag[0] && flag[1]) return 0;
		if(!flag[0]) return -1;
		if(!flag[1]) return 1;
		return -2;
	}
	/***
	 * 根据以空格隔开的这样的字符串实例化一个划分数组 "(1,2] (b,100] [1,5]"
	 * @param conditions  "(1,2] (b,100] [1,5]"
	 * @return
	 */
	public static Domain[] getArray(String conditions){
		String[] strs=conditions.split(" ");
		Domain[] domains=new Domain[strs.length];
		for(int con=0;con<strs.length;con++){
			domains[con]=new Domain(strs[con]);
		}
		return domains;
		
	}

}
