package artiano.ml.classifier;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import artiano.core.operation.Preservable;
import artiano.core.structure.Domain;
import artiano.core.structure.Matrix;

/***
 * 
 * @author BreezeDust
 * 
 */
public class NaiveBayesDiscreteClassifier extends Preservable{
	public String[] domainStr;
	public Map<Integer, Matrix> labelMap = new LinkedHashMap<Integer, Matrix>();
	public List<Integer> labeList = new LinkedList<Integer>();
	public List<Domain[]> domainList = new LinkedList<Domain[]>();
	public Matrix[] trainingResults;
	public double[] plabel;
	public Matrix trainData;
	
	public void group(Matrix trainData,int labelColIndex){
		Matrix labeMx=trainData.getSingerCol(labelColIndex);
		/***
		 * 整理训练矩阵
		 */
		for(int i=0;i<trainData.rows();i++){
			Matrix rowMx=new Matrix(1,trainData.columns()-1);
			/***
			 * 取得处类标的矩阵
			 */
			for(int j=0,con=0;j<trainData.columns();j++){
				if(j!=labelColIndex){
					rowMx.set(0,con++,trainData.at(i,j));
				}
			}
			int labelValue=(int)labeMx.at(i,0);
			//压入HASHMAP
			if(labelMap.get(labelValue)==null){
				labelMap.put(labelValue,rowMx);
				labeList.add(labelValue);
			}
			else{
				Matrix oldMx=labelMap.get(labelValue);
				oldMx.mergeAfterRow(rowMx);
			}
		}		
	}
	public Matrix laPlace(Matrix rowMx,int total){
		boolean flag=false;
		for(int i=1;i<rowMx.columns();i++){
			if(rowMx.at(0, i)==0){
				flag=true;
				break;
			} 
		}
		if(flag){
			for(int i=1;i<rowMx.columns();i++){
				int times=(int) (total*rowMx.at(0, 0)*rowMx.at(0, i));
				double newp=(double)(times+1)/(double)(total+rowMx.columns()-1)/rowMx.at(0, 0);
				rowMx.set(0, i, newp);
			}
		}
		return rowMx;
	}
	public Matrix trainWork(Matrix trainData,Domain[] domains,int con){
		/***
		 * 计算每个label的概率
		 */
		plabel=new double[labeList.size()];
		int rows=trainData.rows();
		Matrix result=null;
		for(int i=0;i<labeList.size();i++){
			Matrix rowMx=new Matrix(1,domains.length+1);
			Matrix tmpMx=labelMap.get(labeList.get(i));
//			tmpMx.print();
			if(plabel[i]==0)plabel[i]=(double)tmpMx.rows()/(double)rows;
			rowMx.set(0,0,plabel[i]);
			
			for(int j=0;j<domains.length;j++){
				int tmpCon=0;
				for(int x=0;x<tmpMx.rows();x++){
					if(domains[j].isIn(tmpMx.at(x,con))==0) tmpCon++;
				}
				double pAB=(double)tmpCon/(double)rows;
				double pA_Y=pAB/plabel[i];
				rowMx.set(0,j+1,pA_Y);
			}
			rowMx=laPlace(rowMx,rows);
//			rowMx.print();
			if(i==0) result=rowMx;
			if(i>0) result.mergeAfterRow(rowMx);
		}
		return result;
		
	}
	public void groupDomain(String[] domianStr){
		for(int i=0;i<domianStr.length;i++){
			Domain[] tmp=Domain.getArray(domianStr[i]);
			domainList.add(tmp);
		}
		this.domainStr=domianStr;
	}
	public void trainWorkBoot(Matrix trainData){
		trainingResults=new Matrix[domainList.size()];
		for(int i=0;i<domainList.size();i++){
			Matrix tmp=this.trainWork(trainData, domainList.get(i), i);
			System.out.println("["+i+"]"+"---------"+domainStr[i]);
			tmp.print();
			trainingResults[i]=tmp;
		}
		
	}
	public boolean train(Matrix trainData,String[] domainStr,int labelColIndex){
		this.trainData=trainData;
		this.groupDomain(domainStr);
		this.group(trainData, labelColIndex);
		this.trainWorkBoot(trainData);
		return false;
	}
	public double testResult(Matrix testMx,int labelIndex){
		int trueCon=0;
		for(int i=0;i<testMx.rows();i++){
			Matrix rowMx=new Matrix(1,testMx.columns()-1);
			for(int j=0,x=0;j<testMx.columns();j++){
				if(j!=labelIndex) rowMx.set(0, x++, testMx.at(i,j));
			}
			if((int)testMx.at(i,labelIndex)==this.classifier(rowMx)) trueCon++;
		}
		double p=(double)trueCon/(double)testMx.rows();
		System.out.println("trainData:"+this.trainData.rows()+"   testData:"+testMx.rows());
		System.out.println(p*100+"%");
		return p;
		
	}
	public int classifier(Matrix mx){
		Matrix labels=new Matrix(mx.rows(),1);
		double[][] p=new double[labeList.size()][mx.columns()];
		for(int con=0;con<labeList.size();con++){
				for(int j=0;j<mx.columns();j++){
					double ai=mx.at(0, j);
					Domain[] dm=domainList.get(j);
					int index=0;
					for(int y=0;y<dm.length;y++){
						if(dm[y].isIn(ai)==0){
							index=y;
							break;
						}
					}
					p[con][j]=trainingResults[j].at(con,index+1);
				}			
		}
		double[] maxs=new double[labeList.size()];
		for(int i=0;i<maxs.length;i++){
			maxs[i]=1;
			for(int j=0;j<mx.columns();j++){
				maxs[i]*=p[i][j];
			}
			maxs[i]*=trainingResults[0].at(i,0);
			Matrix ca=new Matrix(1,mx.columns(),p[i]);
			System.out.println("****p(ai | y))");
			ca.print();
		}
		Matrix ca=new Matrix(labeList.size(),1,maxs);
		ca.printAll();
		int index=getMaxIndex(maxs);
		System.out.println("==========Label is:"+labeList.get(index));
		return labeList.get(index);
		
	}
	private int getMaxIndex(double[] maxs){
		int max=0;
		for(int con=0;con<maxs.length;con++){
			if(maxs[con]>maxs[max]){
				max=con;
			}
		}
		return max;
	}
	
}
