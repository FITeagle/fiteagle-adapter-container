package org.fiteagle.adapters.containers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.client.utils.URIBuilder;


public class Playground {
	public static void main(String[] args) throws IOException {
//		URL url = new URL("http://localhost:8080/version");
		URL url = new URL("http", "localhost", 8080, "/containers/json?all=1");
		
		URLConnection connection = url.openConnection();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		
		String line;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}
		
//		CloseableHttpClient client = HttpClients.createDefault();
//			
//		URI uri = new URIBuilder().setScheme("http").setHost("localhost").setPort(8080).setPath("/version").build();
//		HttpGet request = new HttpGet(uri);
//
//		try {
//			CloseableHttpResponse response = client.execute(request);
//			BufferedReader breader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//
//			String line = null;
//
//			while ((line = breader.readLine()) != null) {
//				System.out.println(line);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			client.close();
//		}
	}
}
