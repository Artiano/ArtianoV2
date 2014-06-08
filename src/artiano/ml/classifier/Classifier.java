package artiano.ml.classifier;

import artiano.core.operation.Preservable;
import artiano.core.structure.*;

public abstract class Classifier extends Preservable {	
	private static final long serialVersionUID = 5186515619281612199L;

	/**
	 * Train data
	 * @param trainSet data to train	 
	 * @return whether the training successes or not
	 */
	public abstract boolean train(Table trainSet);
	
	/**
	 * Predict label of each sample case
	 * @param samples samples to test
	 * @return predications of label of each sample case   
	 */
	public abstract NominalAttribute predict(Table samples);	
}
