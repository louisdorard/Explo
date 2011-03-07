package explo.model;

import java.io.Serializable;

/**
 * A user is represented by a set of continuous and nominal features.
 * @author Louis Dorard, University College London
 */
public class User implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	final private Number[] featuresCON; // continuous features.
	final private String[] featuresNOM; // nominal features 

	/**
	 * Constructor.
	 * @param featuresCON
	 * @param featuresNOM
	 */
	public User(Number[] featuresCON, String[] featuresNOM) {
		this.featuresCON = featuresCON;
		this.featuresNOM = featuresNOM;
	}
	
	/**
	 * Gets the continuous features.
	 * @return continuous features
	 */
	public Number[] getFeaturesCON() {
		return featuresCON;
	}

	/**
	 * Gets the nominal features.
	 * @return nominal features
	 */
	public String[] getFeaturesNOM() {
		return featuresNOM;
	}
	
	/**
	 * Prints the feature values on a single line, separated by commas.
	 */
	public String toString(){
		String s = printNull(featuresCON[0]);
		for(int i=1; i<featuresCON.length; i++){
			s = s + " , " + printNull(featuresCON[i]);
		}
		for(int i=0; i<featuresNOM.length; i++){
			s = s + " , " + printNull(featuresNOM[i]);
		}
		return s;
	}

	private String printNull(Object o) {
		if (o==null)
			return "null";
		else
			return o.toString();
	}
}
