package org.fiteagle.adapters.containers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;

public class Playground {
	public static void main(String[] args) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet request = new HttpGet("http://localhost:8080/version");

		try {
			CloseableHttpResponse response = client.execute(request);
			BufferedReader breader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			String line = null;

			while ((line = breader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (Exception e) {
			System.out.println("Exception while performing HTTP request:");
			System.out.println(e);
		} finally {
			client.close();
		}
	}
}
