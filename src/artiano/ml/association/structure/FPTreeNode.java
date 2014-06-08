package artiano.ml.association.structure;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Description: FPTreeNode for FPTree.</p>
 * @author JohnF Nash
 * reference: http://blog.csdn.net/abcjennifer/article/details/7928082
 * @version 1.0.0
 * @date 2013-10-17
 * @function 
 * @since 1.0.0
 */
public class FPTreeNode implements Comparable<FPTreeNode> {
	
	private String name;
	private int count;	
	private FPTreeNode parent;	 //parent node
	private List<FPTreeNode> children; //child node			
	private FPTreeNode nextHomonym; //下一个同名节点
	
	public FPTreeNode() {		
	}
	
	public FPTreeNode(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public FPTreeNode getParent() {
		return parent;
	}

	public void setParent(FPTreeNode parent) {
		this.parent = parent;
	}	
	
	public FPTreeNode getNextHomonym() {
		return nextHomonym;
	}

	public void setNextHomonym(FPTreeNode nextHomonym) {
		this.nextHomonym = nextHomonym;
	}

	public List<FPTreeNode> getChildren() {
		return children;
	}

	public void setChildren(List<FPTreeNode> chidren) {
		this.children = chidren;
	}

	public void countIncrement(int n) {
        this.count += n;
    }
	
	public void addChidren(FPTreeNode child) {
	 if (this.getChildren() == null) {
            List<FPTreeNode> list = new ArrayList<FPTreeNode>();
            list.add(child);
            this.setChildren(list);
        } else {
            this.getChildren().add(child);
        }
	}
	
	public FPTreeNode findChild(String name) {
		List<FPTreeNode> children = this.getChildren();
		if(children != null) {
			for(FPTreeNode child : children) {
				if(child.getName().equals(name)) {
					return child;
				}
			}			
		}
		return null;
	}
	
	@Override
	public int compareTo(FPTreeNode o) {
		return o.getCount() - this.getCount();  //reverse
	}
}