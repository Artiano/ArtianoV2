package artiano.ml.association.test;

import java.util.*;

import org.junit.Test;

import artiano.ml.association.*;
import artiano.ml.association.structure.Element;
import artiano.ml.association.structure.Sequence;

public class GSPTest {

	@Test
	public void testGetSequences() {
		List<Sequence<Integer>> sequenceList = 
			new ArrayList<Sequence<Integer>>();
		//<{1 5}{2}{3}{4}>
		Sequence<Integer> seq1 = new Sequence<Integer>();		
		seq1.addElement(new Element<Integer>(Arrays.asList(1, 5)));
		seq1.addElement(new Element<Integer>(Arrays.asList(2)));
		seq1.addElement(new Element<Integer>(Arrays.asList(3)));
		seq1.addElement(new Element<Integer>(Arrays.asList(4)));
		sequenceList.add(seq1);
		
		//<{1}{3}{4}{3 5}>
		Sequence<Integer> seq2 = new Sequence<Integer>();
		seq2.addElement(new Element<Integer>(Arrays.asList(1)));
		seq2.addElement(new Element<Integer>(Arrays.asList(3)));
		seq2.addElement(new Element<Integer>(Arrays.asList(4)));
		seq2.addElement(new Element<Integer>(Arrays.asList(3, 5)));
		sequenceList.add(seq2);
		
		//<{1}{2}{3}{4}>
		Sequence<Integer> seq3 = new Sequence<Integer>();
		seq3.addElement(new Element<Integer>(Arrays.asList(1)));
		seq3.addElement(new Element<Integer>(Arrays.asList(2)));
		seq3.addElement(new Element<Integer>(Arrays.asList(3)));
		seq3.addElement(new Element<Integer>(Arrays.asList(4)));
		sequenceList.add(seq3);	
		
		//<{1}{3}{5}>
		Sequence<Integer> seq4 = new Sequence<Integer>();
		seq4.addElement(new Element<Integer>(Arrays.asList(1)));
		seq4.addElement(new Element<Integer>(Arrays.asList(3)));
		seq4.addElement(new Element<Integer>(Arrays.asList(5)));
		sequenceList.add(seq4);	
		
		//<{4}{5}>
		Sequence<Integer> seq5 = new Sequence<Integer>();
		seq5.addElement(new Element<Integer>(Arrays.asList(4)));
		seq5.addElement(new Element<Integer>(Arrays.asList(5)));
		sequenceList.add(seq5);	
		
		int minSupport = 2;
		GSP<Integer> gsp = new GSP<Integer>(sequenceList, minSupport);
		List<Sequence<Integer>> sequencePattern = gsp.getSequences();
		System.out.println(sequencePattern.toString());
	}

}
