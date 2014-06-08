package artiano.core.data;

import artiano.core.structure.Matrix;
/***
 * 
 * @author BreezeDust
 * 
 *
 */
public interface DataSave{
	/***
	 * 
	 * @param key 字符串，可以是文件地址，可以是唯一标识
	 * @param cache 字节数组
	 * @return
	 */
	boolean save(String key,byte[] cache);
	/***
	 * 
	 * @param key 字符串，可以是文件地址，可以是唯一标识
	 * @return
	 */
	byte[] load(String key);

}
