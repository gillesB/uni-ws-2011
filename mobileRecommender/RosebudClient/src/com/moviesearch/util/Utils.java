/*
 * This class was not written by us. Credits go to nalinmello.
 * The code was found in the Google Code project p-nalin-android-moviessearchapp (Revision 14)
 * http://code.google.com/p/p-nalin-android-moviessearchapp/ (02.03.2012)
 * The file might have been modified by us.
 */
package com.moviesearch.util;

import java.io.IOException;
import java.io.InputStream;

public class Utils {
	
	public static void closeStreamQuietly(InputStream inputStream) {
		 try {
			if (inputStream != null) {
			     inputStream.close();  
			 }
		} catch (IOException e) {
			// ignore exception
		}
	}
	
	public static boolean isMissing(String s){		
    	if (s==null||s.trim().equals("")) {
    		return true;
    	}
    	return false;    		
    }

}
