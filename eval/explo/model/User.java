package explo.model;

import java.io.Serializable;

/**
 * A user is represented by a set of continuous and nominal features.
 * @author Louis Dorard, University College London
 */
public class User implements Serializable{
	
	private static final long serialVersionUID = 1L;
	final public Number[] featuresCON;
	final public String[] featuresNOM; 

	public User(Number[] featuresCON, String[] featuresNOM) {
		this.featuresCON = featuresCON;
		this.featuresNOM = featuresNOM;
	}
	
	public String toString(){
		String s = featuresCON[0].toString();
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
