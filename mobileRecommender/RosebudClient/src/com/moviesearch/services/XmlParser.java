/*
 * This class was not written by us. Credits go to nalinmello.
 * The code was found in the Google Code project p-nalin-android-moviessearchapp (Revision 14)
 * http://code.google.com/p/p-nalin-android-moviessearchapp/ (02.03.2012)
 * The file might have been modified by us.
 */
package com.moviesearch.services;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.moviesearch.handlers.MovieHandler;
import com.moviesearch.model.Movie;

public class XmlParser {
	
	private XMLReader initializeReader() throws ParserConfigurationException, SAXException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		// create a parser
		SAXParser parser = factory.newSAXParser();
		// create the reader (scanner)
		XMLReader xmlreader = parser.getXMLReader();
		return xmlreader;
	}
	

	
	public ArrayList<Movie> parseMoviesResponse(String xml) {
		
		try {
			
			XMLReader xmlreader = initializeReader();
			
			MovieHandler movieHandler = new MovieHandler();

			// assign our handler
			xmlreader.setContentHandler(movieHandler);
			// perform the synchronous parse
			xmlreader.parse(new InputSource(new StringReader(xml)));
			
			return movieHandler.retrieveMoviesList();			
			
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

}
