package uk.nominet.DDDS;

/**
 * The generic representation of a rule in a DDDS database
 * 
 * @author ray
 *
 */
public interface Rule {

	public int getPriority();
	public String getFlags();
	public String getService();
	public boolean isTerminal();
	
	public String evaluate();

}
