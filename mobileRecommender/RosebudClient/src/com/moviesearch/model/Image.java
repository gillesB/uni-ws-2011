/*
 * This class was not written by us. Credits go to nalinmello.
 * The code was found in the Google Code project p-nalin-android-moviessearchapp (Revision 14)
 * http://code.google.com/p/p-nalin-android-moviessearchapp/ (02.03.2012)
 * The file might have been modified by us.
 */
package com.moviesearch.model;

import java.io.Serializable;

public class Image implements Serializable {
	
	private static final long serialVersionUID = -2428562977284114465L;
	
	public static final String SIZE_ORIGINAL = "original";
	public static final String SIZE_MID = "mid";
	public static final String SIZE_COVER = "cover";
	public static final String SIZE_THUMB = "thumb";

	public static final String TYPE_PROFILE = "profile";
	public static final String TYPE_POSTER = "poster";
	
	public String type;
	public String url;
	public String size;
	public int width;
	public int height;
	
}
