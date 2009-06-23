package uk.nominet.DDDS;

public interface Rule {

	public int getPriority();
	public String getFlags();
	public String getService();
	public String getResult();
	public boolean isTerminal();

}
