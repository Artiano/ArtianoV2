package artiano.ml.association.test;

import java.util.*;
import org.junit.Test;
import artiano.ml.association.PrefixSpan;
import artiano.ml.association.structure.*;

public class PrefixSpanTest {

	@Test
	public void testGetSeuqencePattern() {
		List<Sequence<Integer>> sequenceList = 
				new ArrayList<Sequence<Integer>>();
		//<{1 2}{1 3}>
		Sequence<Integer> seq1 = new Sequence<Integer>();		
		seq1.addElement(new Element<Integer>(Arrays.asList(1, 2)));
		seq1.addElement(new Element<Integer>(Arrays.asList(1, 3)));
		sequenceList.add(seq1);
		
		//<{3 4} {5 6 7}>
		Sequence<Integer> seq2 = new Sequence<Integer>();
		seq2.addElement(new Element<Integer>(Arrays.asList(3, 4)));	
		seq2.addElement(new Element<Integer>(Arrays.asList(5, 6, 7)));
		sequenceList.add(seq2);
		
		//<{1 3} {8} {7}>
		Sequence<Integer> seq3 = new Sequence<Integer>();
		seq3.addElement(new Element<Integer>(Arrays.asList(1, 3)));
		seq3.addElement(new Element<Integer>(Arrays.asList(8)));
		seq3.addElement(new Element<Integer>(Arrays.asList(7)));
		sequenceList.add(seq3);	
		
		//<{8}>
		Sequence<Integer> seq4 = new Sequence<Integer>();
		seq4.addElement(new Element<Integer>(Arrays.asList(8)));
		sequenceList.add(seq4);			
		
		PrefixSpan<Integer> prefixSpan = new PrefixSpan<>(sequenceList, 2);
		List<Sequence<Integer>> sequencePattern =
			prefixSpan.getSeuqencePattern();
		System.out.println(sequencePattern.toString());
	}

}
