/**
 * Preservable.java
 */
package artiano.core.operation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import artiano.core.data.DataSave;
import artiano.core.data.DataSaveFactory;

/**
 * <p>Description: Abstract class of every preservable class. Every preservable class should extends this
 * class.</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-9-7
 * @author (latest modification by BreezeDust)
 * @since 1.0.0
 */
public abstract class Preservable implements Serializable {
	private static final long serialVersionUID = -4129767820227253645L;
	
	/***
	 * 以序列化的形式保存数据，并被适配器转换成任意数据
	 * @param key 字符串，可以是文件地址，可以是唯一标识
	 * @throws IOException
	 */
	public boolean save(String key) throws IOException{
		ByteArrayOutputStream cache=new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(cache);
		oos.writeObject(this);
		DataSave dataSave=DataSaveFactory.createDataSave();
		boolean flag=dataSave.save(key,cache.toByteArray());
		oos.flush();
		oos.close();
		cache.close();
		if(flag) return true;
		return false;
	}
	/***
	 * 加载保存的数据
	 * @param key 字符串，可以是文件地址，可以是唯一标识
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public  static Object load(String key) throws IOException, ClassNotFoundException{
		DataSave dataSave=DataSaveFactory.createDataSave();
		byte[] cache=dataSave.load(key);
		ByteArrayInputStream bIn=new ByteArrayInputStream(cache);
		ObjectInputStream ois = new ObjectInputStream(bIn);
		Object obj = ois.readObject();
		bIn.close();
		ois.close();
		return obj;
		
	}
	
}
