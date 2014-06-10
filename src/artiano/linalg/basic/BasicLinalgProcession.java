package artiano.linalg.basic;

import artiano.core.structure.Matrix;

/**
 *	基本的线性代数操作，包含矩阵行列式的值的计算，方差计算等
 */
public class BasicLinalgProcession {
	/**
	 * 计算方阵的行列式的值
	 * @param mat 方阵
	 * @return
	 */
	public double det(Matrix mat) {
		if(mat.rows() != mat.columns()) {
			throw new IllegalArgumentException("矩阵的行数和列数不同，"
					+ "无法计算它的行列式的值");
		}
		
		Matrix cloneMat = mat.clone();
		elimination(cloneMat);  //化为上三角矩阵
		return calcDet(cloneMat);
	}

	/**
	 * 将上三角主对角线上的元素相乘，得到矩阵的行列式值
	 * @param mat 矩阵
	 * @return 矩阵行列式的值
	 */
	private double calcDet(Matrix mat) {
		double detValue = 1.0;
		for(int i=0; i<mat.rows(); i++) {
			detValue *= mat.at(i, i);
		}
		return detValue;
	}
	
	/**
	 * 将矩阵化为上三角矩阵
	 * @param a - 系数矩阵
	 */
	private void elimination(Matrix mat) {
		for(int j=0; j<mat.columns() - 1; j++) {	
			if(mat.at(j, j) == 0) {
				mat.set(j, j, 1e-20);
			}
			
			for(int i=j+1; i<mat.rows(); i++) {
				double mult = mat.at(i, j) / mat.at(j, j); 
				for(int k=j; k<mat.columns(); k++) {
					double newValue = mat.at(i, k) - mat.at(j, k) *  mult; 
					mat.set(i, k, newValue);
				}
			}
		}	
	}
	
}
