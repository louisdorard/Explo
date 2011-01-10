package explo.model;

import java.io.Serializable;

/**
 * As in the bandit terminology, an Arm can be seen as a possible action presented by the Environment.
 * Here, an arm is a (User,Option) couple, such that playing an arm corresponds to presenting a given option to a given user, which can result in a click (reward of 1, otherwise 0).
 * @author Louis Dorard, University College London
 */
public class Arm implements Serializable{
	private static final long serialVersionUID = 1L;
	final public User u;
	final public Option o;
	public Arm(User u, Option o) {
		this.u = u;
		this.o = o;
	}
	public String toString(){
		return "User: " + u + " \t Option: " + o;
	}
}
