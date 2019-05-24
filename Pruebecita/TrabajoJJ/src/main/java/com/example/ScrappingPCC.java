package com.example;

import java.io.BufferedReader;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;




public class ScrappingPCC implements FuenteDato{

	private List<String> cookies;
	private String firstquery  = "https://www.pccomponentes.com/buscar/?query=";
	
	
	public String query(String request)
	{
		return getNPages(request);
	}
	
	public String getNPages(String request) { 
		
		//////////////////////////////////////// Search has better results with just the 3 first words ////////
		String words[] = request.split(" ");
		if(words.length > 1) {
			words[0] += "+" + words[1];
			if(words.length > 2) {
				words[0] += "+" + words[2];
			}
		}
		request = words[0];
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		
		String res = "";
		String nuevo;
		int i = 1;
		
		nuevo = SendHTTP(firstquery + request, "GET", true, 0, request);
		
		while((nuevo = SendHTTP("https://www.pccomponentes.com/buscar/ajax", "POST", true, i, request)) != "" ) {
			res+=nuevo+"\n";
			i++;
			if(i>2)break;
		}

		return res;
	}
	
	
	private String ReadBuffer(BufferedReader buffer) {  /// Get all placed in a buffer and stores it inside String
		String read = "";
		String line = "";
		try {
			while((line = buffer.readLine()) != null) {
				read += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return read;
	}
	private String SendHTTP( String url, String method, boolean saveCokies, int page, String request) {
		
		HttpURLConnection connection;
		BufferedReader response;
		String res = "";
		try {
			
			connection = (HttpURLConnection) new URL( url ).openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestMethod(method);
			if(cookies != null) {
				int max = cookies.size();
				for(int i=0; i<max; i++)
					connection.setRequestProperty("Cookie", cookies.get(i));
			}
			if(method=="POST") {
				connection.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
				wr.writeBytes("page=" + page + "&query="+ request + "&order=search;");
				wr.flush();
				wr.close();
			}
			
		
			connection.connect();	
		    response = new BufferedReader(new InputStreamReader(connection.getInputStream()));		
			res = ReadBuffer(response);
			if(saveCokies) {
				cookies = connection.getHeaderFields().get("Set-Cookie");
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
}
