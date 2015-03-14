/*
 * This class was not written by us. Credits go to nalinmello.
 * The code was found in the Google Code project p-nalin-android-moviessearchapp (Revision 14)
 * http://code.google.com/p/p-nalin-android-moviessearchapp/ (02.03.2012)
 * The file might have been modified by us.
 */
package com.moviesearch.services;

import java.util.ArrayList;

import android.util.Log;

import com.moviesearch.model.Movie;

public class MovieSeeker extends GenericSeeker<Movie> {
		
	private static final String MOVIE_SEARCH_PATH = "Movie.search/";
	
	public ArrayList<Movie> find(String query) {
		ArrayList<Movie> moviesList = retrieveMoviesList(query);
		return moviesList;
	}
	
	public ArrayList<Movie> find(String query, int maxResults) {
		ArrayList<Movie> moviesList = retrieveMoviesList(query);
		return retrieveFirstResults(moviesList, maxResults);
	}
	
	private ArrayList<Movie> retrieveMoviesList(String query) {
		String url = constructSearchUrl(query);
		String response = httpRetriever.retrieve(url);
		Log.d(getClass().getSimpleName(), response);
		return xmlParser.parseMoviesResponse(response);
	}

	@Override
	public String retrieveSearchMethodPath() {
		return MOVIE_SEARCH_PATH;
	}

}
