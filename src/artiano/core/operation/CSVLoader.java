/**
 * CSVLoader.java
 */
package artiano.core.operation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


import artiano.core.structure.Attribute;
import artiano.core.structure.IncrementVector;
import artiano.core.structure.NumericAttribute;
import artiano.core.structure.StringAttribute;
import artiano.core.structure.Table;
import artiano.core.structure.Table.TableRow;


/**
 * <p></p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-11-2
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class CSVLoader {
	/** 待读取的文件  */
	private File file = null;
	/** 文件读取器 */
	private BufferedReader reader = null;
	/** 文件后缀名 */
	public static final String FILE_EXTENSION = ".csv";
	/** 默认一次读取的行数 */
	public static final int DEFAULT_READ_STEP = 2000;
	/** 分隔符 */
	private String separator = ",";
	/** 文件行数 */
	private int rows = 0;
	/** 表的列数 */
	private int columns = 0;
	/** 文件所在的当前行 */
	private int currentRow = 0;
	public CSVLoader(){}
	/**
	 * 使用文件路径构造一个csv文件读取器
	 * @param path 声明的csv文件
	 * @throws IOException 当路径错误时发生
	 */
	public CSVLoader(String path) throws IOException{
		setPath(path);
	}
	/**
	 * 读取一次文件，获取其行数
	 * @throws IOException
	 */
	private void getAllRows() throws IOException{
		columns = readHeader().length;
		while (reader.readLine() != null)
			rows++;
		reader.close();
		reader = new BufferedReader(new FileReader(file));
	}
	/**
	 * 获取文件行数
	 * @return
	 */
	public int rows(){
		return this.rows;
	}
	/**
	 * 获取表列数
	 * @return
	 */
	public int columns(){
		return this.columns;
	}
	/**
	 * 设置分隔符
	 * @param separator 待设置的分隔符
	 */
	public void setSeparator(String separator){
		this.separator = separator;
	}
	/**
	 * 设置文件路径
	 * @param path
	 * @throws IOException
	 */
	public void setPath(String path) throws IOException{
		file = new File(path);
		reader = new BufferedReader(new FileReader(file));
		getAllRows();
	}
	/**
	 * 读取文件所有行
	 * @return
	 * @throws IOException
	 */
	public Table read() throws IOException{
		return read(-1);
	}
	/**
	 * 读取一行
	 * @return
	 */
	public TableRow readRow(){
		return null;
	}
	/**
	 * 读取指定行数
	 * @param rows 指定行数
	 * @return 读取后形成的表
	 * @throws IOException
	 */
	public Table read(int rows) throws IOException{
		String[] names = readHeader();
		int cols = names.length;
		String line;
		//attribute vectors
		IncrementVector[] vectors = new IncrementVector[cols];
		for (int i=0; i<cols; i++)
			vectors[i] = new IncrementVector();
		String[] values;
		int[] dvc = new int[cols];
		int[] svc = new int[cols];
		int r = 0;
		if (rows < 0)
			rows = DEFAULT_READ_STEP;
		while (r <= rows && (line = reader.readLine()) != null){
			values = line.split(separator);
			r++;
			currentRow++;
			for (int i=0; i<columns; i++){
				//if missing value
				if (i>=values.length || values[i] == null || values[i].equals("")){
					vectors[i].push(Attribute.MISSING_VALUE);
					continue;
				}
				//try parse double
				try {
					double x = Double.parseDouble(values[i]);
					vectors[i].push(x);
					dvc[i]++;
				} catch (Exception e) {
					vectors[i].push(values[i].trim());
					svc[i]++;
				}
			}
		}
		//construct attributes
		String tableName = this.file.getName().replaceAll("\\.[cC][sS][vV]$", "");
		Table table = new Table(tableName);
		for (int i=0; i<cols; i++){
			if (dvc[i] >= svc[i]){
				Attribute att = new NumericAttribute(names[i], vectors[i]);
				table.addAttribute(att);
			} else {
				Attribute att = new StringAttribute(names[i], vectors[i]);
				table.addAttribute(att);
			}
		}
		return table;
	}
	/**
	 * 是否还有更多行
	 * @return
	 */
	public boolean hasMore(){
		if (currentRow < rows)
			return true;
		return false;
	}
	/**
	 * 读取文件头
	 * @return
	 * @throws IOException
	 */
	private String[] readHeader() throws IOException{
		String line = reader.readLine();
		String[] names = line.split(separator);
		return names;
	}
	/**
	 * 关闭流
	 * @throws IOException
	 */
	public void close() throws IOException{
		this.reader.close();
	}
	
	public static void main(String[] args){
		CSVLoader loader = new CSVLoader();
		try {
			loader.setPath("f:\\Titanic.csv");
			System.out.println("data rows:"+loader.rows());
			Table table = loader.read();
			loader.close();
			System.out.println("read: ");
			table.print();
			System.out.println("delete with missing: ");
			table.deleteMissing();
			table.print();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}






