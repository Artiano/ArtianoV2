package artiano.ml.association.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import artiano.ml.association.FPGrowth;

public class FPGrowthTest {

	public void testReadTransRecord() {
		List<List<String>> transactions =
			FPGrowthTest.readTransactionRecord("src\\artiano\\ml\\association\\test\\data2.txt");
		for(List<String> transaction: transactions) {
			for(String item : transaction) {
				System.out.print(item + "\t");
			}
			System.out.println();
		}
		
	}

/*	
	public void testFindFrequentOneItemset() {
		FPGrowth tree = new FPGrowth();
		int minSupport = 3;
		tree.setMinSupport(minSupport);
		List<List<String>> transactions =
			tree.readTransactionRecord("src\\artiano\\ml\\association\\test\\data2.txt");
		List<FPTreeNode> frequent1Itemset =
			tree.buildHeaderTable(transactions);
		for(FPTreeNode node : frequent1Itemset) {
			System.out.print(node.getName() + " : " + node.getCount() + "; ");
		}
		System.out.println();
	}
*/
/*	
	public void testSortByFrequent1Itemset() {
		FPGrowth tree = new FPGrowth();
		int minSupport = 3;
		tree.setMinSupport(minSupport);
		List<List<String>> transactions =
			tree.readTransactionRecord("src\\artiano\\ml\\association\\test\\data2.txt");
		List<FPTreeNode> frequent1Itemset =
			tree.buildHeaderTable(transactions);
		for (List<String> transRecord : transactions) {
            LinkedList<String> record = 
            	tree.sortByFrequent1Itemset(transRecord, frequent1Itemset);
            for(String item : record) {
            	System.out.print(item + " ");
            }
            System.out.println();
		}
	}
*/	
/*	
	public void testBuildFPTree() {
		FPGrowth tree = new FPGrowth();
		int minSupport = 3;
		tree.setMinSupport(minSupport);
		List<List<String>> transactions =
			tree.readTransactionRecord("src\\artiano\\ml\\association\\test\\data2.txt");
	 	// 构建项头表，同时也是频繁1项集
        List<FPTreeNode> HeaderTable = tree.buildHeaderTable(transactions);
        // 构建FP-Tree
        FPTreeNode treeRoot = tree.buildFPTree(transactions, HeaderTable);
        Queue<FPTreeNode> nodeQueue = new LinkedList<FPTreeNode>();
        nodeQueue.add(treeRoot);
        while(!nodeQueue.isEmpty()) {
        	FPTreeNode node = nodeQueue.remove();
        	System.out.println(node.getName());
        	List<FPTreeNode> chidren = node.getChildren();
        	if(chidren == null) {
        		continue;
        	}
        	for(FPTreeNode child : chidren) {
        		nodeQueue.add(child);
        	}
         }
	}
*/
	
	@org.junit.Test
	public void testFPTree() {
		FPGrowth fptree = new FPGrowth();
        fptree.setMinSupport(3);
        List<List<String>> transRecords = 
        	FPGrowthTest.readTransactionRecord("src\\artiano\\ml\\association\\test\\data2.txt");
        Map<String, Integer> frequentPatterns = 
        	fptree.fpGrowth(transRecords, null);
        Set<Entry<String, Integer>> entrySet =  frequentPatterns.entrySet();
        for(Entry<String, Integer> entry : entrySet) {
        	System.out.println(entry.getValue() + "\t" + entry.getKey());
        }
	}

	public static List<List<String>> readTransactionRecord(String fileName) {
		List<List<String>> transaction = 
			new ArrayList<List<String>>();
		try {
			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);
			String line;
			List<String> record = new ArrayList<String>();			
			while((line = br.readLine()) != null) {
				if(line.trim().length() > 0) {
					String[] str = line.split("[,，]");
					record = new LinkedList<String>();
					for(String w : str) {
						record.add(w.trim());
					}
					transaction.add(record);
				}
			}		
			br.close();
			
		} catch (IOException e) {
			System.out.println("Read transaction records failed."
	              + e.getMessage());
		}		
		return transaction;
	}
}
