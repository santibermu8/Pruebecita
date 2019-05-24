package com.example;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;




public class ScrappingCU implements FuenteDato{

	private String firstquery  = "https://www.computeruniverse.net/en/list.asp?searchname=";
	
	public String getPage(String request) {
		
		//////////////////////////////////////// Search has better results with just the 3 first words ////////
		String words[] = request.split(" ");
		if(words.length > 1) {
			words[0] += "+" + words[1];
			if(words.length > 2) {
				words[0] += "+" + words[2];
			}
		}
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		String nuevo;
		nuevo = SendHTTP(firstquery + words[0]);
		
		return nuevo;
	}
	
	
	private String ReadBuffer(BufferedReader buffer) {  /// Get all placed in a buffer and stores it inside String
		String read = "";
		String line = "";
		try {
			while((line = buffer.readLine()) != null) {
				read += line;
				//System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return read;
	}
	private String SendHTTP( String url) { 
		
		HttpURLConnection connection;
		BufferedReader response;
		String res = "";
		try {
			
			connection = (HttpURLConnection) new URL( url ).openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestMethod("GET");
			
			connection.connect();	
		    response = new BufferedReader(new InputStreamReader(connection.getInputStream()));		
			res = ReadBuffer(response);


		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return res;
	}


	@Override
	public String query(String request) {
		return getPage(request);
	}
}
