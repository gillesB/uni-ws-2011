package transmit;

import java.io.Serializable;

public class MovieShared implements Serializable {

	private static final long serialVersionUID = 1L;

	public int movieID;
	public String moviename;
	public String description;
	public byte[] image;

}
