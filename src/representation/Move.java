package representation;

public interface Move {

	public boolean validOn(Conf input);

	/**
	 * Actually apply this Action and return the resultant State
	 * 
	 * @param input The State we are applying this Action on.
	 * @return The resultant State from the application of this Action.
	 * @throws InvalidActionException Some Actions can't be applied to some States.
	 */
	public Conf applyTo(Conf input) throws InvalidActionException;
	
	/**
	 * 
	 * 
	 * 
	 * @return
	 */
	public int getValue();

	/**
	 * For traces, we need to be able to print out a String representation of this
	 * Action.
	 * 
	 * @return A String representation of this Action.
	 */
	@Override
	public String toString();

	/**
	 * Implementers must be able to compare two actions to see if they are the same.
	 * 
	 * @param obj
	 * @return
	 */
	@Override
	public boolean equals(Object obj);
}
