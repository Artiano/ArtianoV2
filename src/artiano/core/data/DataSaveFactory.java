package artiano.core.data;
/***
 * 
 * @author BreezeDust
 *
 */
public class DataSaveFactory {
	private static String saveMethod="TextFileData"; //以后通过配置文件来选择储存方式
	/***
	 * 
	 * @return DataSave
	 */
	public static DataSave createDataSave(){
		Class dataSaveClass=null;
		try {
			dataSaveClass = Class.forName("artiano.core.data."+saveMethod);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		DataSave dataSave=null;
		try {
			dataSave = (DataSave)dataSaveClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return dataSave;
	}
	/***
	 * 
	 * @param saveMethod 持久化保存的方法
	 * @return DataSave
	 */
	public static DataSave createDataSave(String saveMethod){
		DataSaveFactory.saveMethod=saveMethod;
		return DataSaveFactory.createDataSave();
	}

}
