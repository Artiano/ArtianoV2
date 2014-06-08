package artiano.linalg.basic;

import artiano.core.structure.Matrix;

public class Normalization {	
	/**
	 * 将数据集按列归一化到[0,1]
	 * @param dataset 待归一化数据集
	 * @return 归一化后得到的新的数据集矩阵
	 */
	public Matrix colNorm0_1(Matrix dataset) {
		int rows = dataset.rows();
		int cols = dataset.columns();
		Matrix colMax = dataset.colMax();
		Matrix colMin = dataset.colMin();
		
		Matrix dataAfterNorm = new Matrix(rows, cols);
		for(int j=0; j<cols; j++) {
			double gap = colMax.at(j) - colMin.at(j);
			if(gap < 1e-20) {  //该列的值都相同时，该列归一到1
				for(int i=0; i<rows; i++) {
					dataAfterNorm.set(i, j, 1);
				}
				continue;
			}
			
			for(int i=0; i<rows; i++) {
				double value = (dataset.at(i, j) - colMin.at(j))/gap;
				dataAfterNorm.set(i, j, value);
			}
		}
		return dataAfterNorm;
	}

	/**
	 * 将数据集按列归一化到[-1,1]
	 * @param dataset 待归一化数据集
	 * @return 归一化后得到的新的数据集矩阵
	 */
	public Matrix colNormNeg1_1(Matrix dataset) {
		int rows = dataset.rows();
		int cols = dataset.columns();
		Matrix colMax = dataset.colMax();
		Matrix colMin = dataset.colMin();
		
		Matrix dataAfterNorm = new Matrix(rows, cols);
		for(int j=0; j<cols; j++) {
			double gap = colMax.at(j) - colMin.at(j);
			if(gap < 1e-20) {  //该列的值都相同时，该列归一到1
				for(int i=0; i<rows; i++) {
					dataAfterNorm.set(i, j, 1);
				}
				continue;
			}
			
			for(int i=0; i<rows; i++) {
				double value = 2*((dataset.at(i, j) - colMin.at(j))/gap) - 1;
				dataAfterNorm.set(i, j, value);
			}
		}
		return dataAfterNorm;
	}

	/**
	 * 将数据集按行归一化到[0,1]
	 * @param dataset 待归一化数据集
	 * @return 归一化后得到的新的数据集矩阵
	 */
	public Matrix rowNorm0_1(Matrix dataset) {
		int rows = dataset.rows();
		int cols = dataset.columns();
		Matrix rowMax = dataset.rowMax();
		Matrix rowMin = dataset.rowMin();
		
		Matrix dataAfterNorm = new Matrix(rows, cols);
		for(int i=0; i<rows; i++) {
			double gap = rowMax.at(i) - rowMin.at(i);
			if(gap < 1e-20) {  //该行的值都相同时，该行归一到1
				for(int j=0; j<rows; j++) {
					dataAfterNorm.set(i, j, 1);
				}
				continue;
			}
			
			for(int j=0; j<rows; j++) {
				double value = (dataset.at(i, j) - rowMin.at(i))/gap;
				dataAfterNorm.set(i, j, value);
			}
		}
		return dataAfterNorm;
	}	

	/**
	 * 将数据集按行归一化到[-1,1]
	 * @param dataset 待归一化数据集
	 * @return 归一化后得到的新的数据集矩阵
	 */
	public Matrix rowNormNeg1_1(Matrix dataset) {
		int rows = dataset.rows();
		int cols = dataset.columns();
		Matrix rowMax = dataset.rowMax();
		Matrix rowMin = dataset.rowMin();
		
		Matrix dataAfterNorm = new Matrix(rows, cols);
		for(int i=0; i<rows; i++) {
			double gap = rowMax.at(i) - rowMin.at(i);
			if(gap < 1e-20) {  //该行的值都相同时，该行归一到1
				for(int j=0; j<rows; j++) {
					dataAfterNorm.set(i, j, 1);
				}
				continue;
			}
			
			for(int j=0; j<rows; j++) {
				double value = 2*((dataset.at(i, j) - rowMin.at(i))/gap) - 1;
				dataAfterNorm.set(i, j, value);
			}
		}
		return dataAfterNorm;
	}	
	
}
