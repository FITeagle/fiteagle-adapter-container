package org.fiteagle.adapters.containers;

import java.util.Map.Entry;

import org.fiteagle.adapters.containers.docker.internal.CreateContainerInfo;

public class Playground {
	public static void main(String[] args) {
		CreateContainerInfo cci = new CreateContainerInfo("mycontainer", "root", "ubuntu:latest");

		cci.putEnvironment("hello", "world");
		cci.putLabel("how", "wonderful");
		cci.setCommandEasily("/bin/sh", "-c", "echo Hello World");

		for (Entry<String, String> entry: cci.getEnvironment().entrySet()) {
			System.out.println(entry.getKey() + " = " + entry.getValue());
		}

		System.out.println(cci.toJsonObject().toString());
	}
}
