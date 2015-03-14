package transmit;

import java.io.Serializable;

public class TicketShared implements Serializable{

	private static final long serialVersionUID = 1L;

	public int showID;
	public String movieName;
	public String hallNr;
	public int amount;
	public long time;
	public byte[] image;

}
