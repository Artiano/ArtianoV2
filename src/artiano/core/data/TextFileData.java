package artiano.core.data;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import artiano.core.structure.Matrix;
/***
 * 
 * @author BreezeDust
 *
 */
public class TextFileData implements DataSave{

	@Override
	public boolean save(String key, byte[] cache) {
		try {
			FileOutputStream out=new FileOutputStream(new File(key));
			out.write(cache);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public byte[] load(String key) {
		ByteArrayOutputStream outs=new ByteArrayOutputStream();
		try {
			FileInputStream in=new FileInputStream(new File(key));
			BufferedInputStream bIn=new BufferedInputStream(in);
			
			byte[] cache=new byte[1000];
			int length=0;
			while((length=bIn.read(cache))!=-1){
				outs.write(cache,0,length);
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outs.toByteArray();
	}
	
}
