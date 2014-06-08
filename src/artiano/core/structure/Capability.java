/**
 * Capability.java
 */
package artiano.core.structure;

import java.util.HashSet;
import java.util.Iterator;


/**
 * 表示处理能力的类。
 * <p>
 * 这种“能力”表示仅限于对数据实例的处理。默认情况下，构造的本类实例
 * 不具备任何实例处理能力，你可能需要手工添加。</p>
 * 
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-12-16
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class Capability {
	/** 属性处理能力 */
	protected HashSet<Class<?>> attCapabilities = new HashSet<>();
	/** 类属性处理能力 */
	protected HashSet<Class<?>> classCapabilities = new HashSet<>();
	/** 是否允许属性值有缺失 */
	protected boolean allowAttMissing = false;
	/** 是否允许类属性值有缺失 */
	protected boolean allowClsMissing = false;
	/** 最少实例数 ，未设定为负数*/
	protected int minInstaces = -1;
	/** 不能处理的原因 */
	protected String failReason = null;
	/**
	 * 构造一个不具备任何“能力”的实例
	 */
	public Capability() {
		disableAll();
	}
	/**
	 * 使用给定的“能力”构造一个实例
	 * @param c 指定实例
	 */
	public Capability(Capability c) {
		this();
		or(c);
	}
	/**
	 * 设定是否允许属性值有缺失
	 * @param allowMissing 是否能处理
	 */
	public void allowAttributeMissing(boolean allowMissing) {
		this.allowAttMissing = allowMissing;
	}
	/**
	 * 设定是否允许类属性值有缺失
	 * @param allowMissing
	 */
	public void allowClassMissing(boolean allowMissing) {
		this.allowClsMissing = allowMissing;
	}
	/**
	 * 设置最小能处理实例数
	 * @param min 最小实例数
	 */
	public void setMinimumInstances(int min) {
		this.minInstaces = min;
	}
	/**
	 * 获取能处理的最小实例数
	 * @return 能处理的最小实例数
	 */
	public int minimumInstacnes() {
		return this.minInstaces;
	}
	/**
	 * 当使用方法{@link #handles(Table)}对表数据集进行测试失败的原因，如果当期“能力”具备
	 * 处理待测试的表，则此方法返回{@code null}
	 * @return 测试失败原因
	 */
	public String failReason() {
		return failReason.toString();
	}
	/**
	 * 属性处理能力
	 * @return
	 */
	public Iterator<Class<?>> attributeCapabilities() {
		return attCapabilities.iterator();
	}
	/**
	 * 类属性处理能力
	 * @return
	 */
	public Iterator<Class<?>> classCapabilities() {
		return classCapabilities.iterator();
	}
	/**
	 * 开启对某个属性的“能力”<br>
	 * <b><i>NOTICE:</b></i> 指定属性不包括类属性
	 * @param att 属性类型
	 */
	public void enableAttribute(Class<?> att) {
		attCapabilities.add(att);
	}
	/**
	 * 禁用对某个属性的“能力”<br>
	 * <b><i>NOTICE:</b></i>如果本类实例中并不包含这个类的“能力”，将不做任何处理
	 * @param att 属性类型
	 */
	public void disableAttribute(Class<?> att) {
		attCapabilities.remove(att);
	}
	/**
	 * 开启对指定类属性的“能力”
	 * @param att 属性类型
	 */
	public void enableClass(Class<?> att) {
		classCapabilities.add(att);
	}
	/**
	 * 禁用对指定类属性的能力
	 * @param att 指定属性类型
	 */
	public void disableClass(Class<?> att) {
		classCapabilities.remove(att);
	}
	/**
	 * 禁用所有能力
	 */
	public void disableAll() {
		attCapabilities.clear();
		classCapabilities.clear();
		allowAttMissing = false;
		allowClsMissing = false;
	}
	/**
	 * 判断当前能力是否支持指定能力
	 * @param cap 指定能力
	 * @return 若支持返回{@code true}
	 */
	public boolean supports(Capability cap) {
		// attribute capabilities
		for (Iterator<Class<?>> it=cap.attributeCapabilities(); it.hasNext();)
			if (!handlesAttribute(it.next())) return false;
		// class capabilities
		for (Iterator<Class<?>> it=cap.classCapabilities(); it.hasNext();)
			if (!handlesClass(it.next())) return false;
		// allow attribute missing ?
		if (!allowAttMissing && cap.allowAttMissing) return false;
		// allow class attribute missing ?
		if (!allowClsMissing && cap.allowClsMissing) return false;
		// minimum instances
		if (minInstaces < cap.minInstaces) return false;
		// pass all tests
		return true;
	}
	
	/**
	 * 判断是否具有处理某个类的处理能力
	 * @param att 指定的类
	 * @return 若具备指定类的处理能力则返回{@code true}
	 */
	public boolean handlesAttribute(Class<?> att) {
		return attCapabilities.contains(att);
	}
	/**
	 * 判断是否具有处理指定类属性的能力
	 * @param att 指定属性类型
	 * @return 若具备指定属性的能力则返回{@code true}
	 */
	public boolean handlesClass(Class<?> att) {
		return classCapabilities.contains(att);
	}
	/**
	 * 判断是否具备处理某个表的能力
	 * @param t 待判断的表
	 * @return 如果能处理则返回{@code true}
	 */
	public boolean handles(Table t) {
		// attributes capabilities
		for (Iterator<Attribute> it=t.attributes(); it.hasNext();) {
			Attribute att = it.next();
			// not the class attribute
			if (!t.isClassAttribute(att)) {
				// can handle ?
				if (!handlesAttribute(att.getClass())) {
					failReason = "Can't handle <"+ att.getType() + "> attributes" +
							" in the table "+t.getName() + ".";
					return false;
				}
				// allow missing ?
				if (!allowAttMissing && att.hasMissing()) {
					failReason = "Missing values in attributes " + att.getName() +
							" is not allowed.";
					return false;
				}
			}
		}
		// class capabilities
		if (t.hasClass()) {
			Attribute att = t.classAttribute();
			// can handle the class attribute ?
			if (!handlesClass(att.getClass())) {
				failReason = "Can't handle <" + att.getType() + "> class.\n";
				return false;
			}
			// allow class value missing ?
			if (!allowClsMissing & att.hasMissing()) {
				failReason = "Missing value in class attribute " + att.getName() +
						" is not allowed.";
				return false;
			}
		}
		// minimum instances
		if (t.rows() < minInstaces) {
			failReason = "Number of instances in the table " + t.getName() +
					" < minimum instances (="+minInstaces+") needed.\n";
			return false;
		}
		// pass all tests
		return true;
	}
	/**
	 * 与某个能力进行“或”运算
	 * @param c 指定的“能力”
	 */
	public void or(Capability c) {
		// attribute capabilities
		attCapabilities.addAll(c.attCapabilities);
		// class attribute capabilities
		classCapabilities.addAll(c.classCapabilities);
		// can handle missing values in attributes ?
		allowAttMissing = allowAttMissing || c.allowAttMissing;
		// can handle missing values in class ?
		allowClsMissing = allowClsMissing || c.allowClsMissing;
		// minimum instances
		minInstaces = minInstaces<c.minInstaces?c.minInstaces:minInstaces;
	}
	/**
	 * 与某个能力进行“与”运算
	 * @param c 指定“能力”
	 */
	public void and(Capability c) {
		// attribute capabilities
		for (Class<?> cs: attCapabilities) 
			if (!(handlesAttribute(cs) && c.handlesAttribute(cs)))
				attCapabilities.remove(cs);
		// class attribute capabilities
		for (Class<?> cs: classCapabilities)
			if (!(handlesClass(cs) && c.handlesClass(cs)))
				classCapabilities.remove(cs);
		// can handle missing values in attributes ?
		allowAttMissing = allowAttMissing && c.allowAttMissing;
		// can handle missing values in class ?
		allowClsMissing = allowClsMissing && c.allowClsMissing;
		minInstaces = minInstaces<c.minInstaces?minInstaces:c.minInstaces;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		// header
		sb.append("Capabilities:\n");
		// attribute capabilities
		sb.append("#Attributes can handles:\n");
		if (attCapabilities.isEmpty())
			sb.append("\tNONE\n");
		else {
			for (Iterator<Class<?>> it = attributeCapabilities(); it.hasNext();) {
				sb.append("\t<");
				sb.append(it.next()+">\n");
			}
		}
		
		// class attribute capabilities
		sb.append("\n#Class can handles:\n");
		if (classCapabilities.isEmpty())
			sb.append("\tNONE\n");
		else {
			for (Iterator<?> it = classCapabilities(); it.hasNext();) {
				sb.append("\t<");
				sb.append(it.next()+">\n");
			}
		}
		
		sb.append("\n#Addtional:\n");
		// allow attribute value missing ?
		sb.append("\tAllow missing values in attributes: " + allowAttMissing + "\n");
		sb.append("\tAllow mssing values in class: " + allowClsMissing + "\n");
		// minimum instances needed
		sb.append("\tMinimum instances needed: " + (minInstaces<0?"Unrestricted":minInstaces));
		return sb.toString();
	}
	
	public static void main(String[] args) {
		Capability capability = new Capability();
		capability.enableAttribute(NumericAttribute.class);
		capability.enableAttribute(NominalAttribute.class);
		capability.enableClass(NominalAttribute.class);
		capability.allowAttributeMissing(true);
		capability.allowClassMissing(true);
		System.out.println(capability);
	}
}







