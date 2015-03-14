package transmit;

import java.io.Serializable;

public class ShowShared implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public int showID;
	public String movieName;
	public String price;
	public String hallNr;
	public long time;
	public byte[] image;

}
