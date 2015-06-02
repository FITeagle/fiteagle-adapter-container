package org.fiteagle.adapters.containers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.*;

public class Playground {
	public static void main(String[] args) throws IOException, URISyntaxException {
		CloseableHttpClient client = HttpClients.createDefault();
		
		URI uri = new URIBuilder().setScheme("http").setHost("localhost").setPort(8080).setPath("/version").build();
		HttpGet request = new HttpGet(uri);

		try {
			CloseableHttpResponse response = client.execute(request);
			BufferedReader breader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			String line = null;

			while ((line = breader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client.close();
		}
	}
}
