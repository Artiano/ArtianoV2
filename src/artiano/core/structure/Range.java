/**
 * Range.java
 */
package artiano.core.structure;

import java.io.Serializable;

/**
 * <p>Description: To describe an ultra-tail range. It is very useful while some structure has a range in 
 * most case. For example: when you want to get a sub-matrix from a matrix, you can write code like:
 * <code><br>Matrix x = y.at(new Range(1,3), Range.all());<br></code>
 * That means matrix x hold the 1st row to 3rd row (but not including the 3rd row) and all columns of the matrix y.</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-23
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class Range implements Serializable{
	
	private static final long serialVersionUID = 1L;
	/**
	 * range start
	 */
	protected int start = 0;
	/**
	 * range end
	 */
	protected int end = 0;
	
	/**
	 * private constructor
	 */
	private Range(){ 
		start = 0;
		end = 0;
	}
	
	/**
	 * Construct a new range with specified start and end.
	 * @param start Range start
	 * @param end Range end
	 */
	public Range(int start, int end){
		if (start >= end)
			throw new IllegalArgumentException("Range, range end must greater than range begin.");
		this.start = start;
		this.end = end;
	}
	
	/**
	 * Get the range length
	 * @return Range length
	 */
	public int length(){
		return end - start;
	}
	
	/**
	 * Range all.
	 * <li>This method will create a special range that represent whole range. It depends on the specific 
	 * application. When you decide to use it in your application, you should convert it to absolute range.</li>
	 * @return A special range express the whole range
	 */
	public static Range all(){
		return new Range();
	}
	
	/**
	 * Range begin
	 * @return The start of the range
	 */
	public int begin(){
		return start;
	}
	
	/**
	 * Range end
	 * @return End of the range
	 */
	public int end(){
		return end;
	}
	
	/**
	 * judge if two range is crossover
	 * @param range
	 * @return
	 */
	public boolean isCross(Range range){
		return (this.start > range.end || range.start > this.end);
	}
	
	/**
	 * Judge if x is in the range
	 * @param x An integer
	 * @return True if x is in the range or false otherwise
	 */
	public boolean isContain(int x){
		return (x >= start && x <= end);
	}
	
	/**
	 * Judge if another range is in the range
	 * @param x Another range
	 * @return True if x is in the range or false otherwise
	 */
	public boolean isContain(Range x){
		return (x.start >= start && x.end <= end);
	}
	
	/**
	 * Judge if another range is equals to the range
	 * @param x Another range
	 * @return True if x is equals to the range or false otherwise
	 */
	public boolean equals(Range x){
		return (x.start == start && x.end == end);
	}
}
