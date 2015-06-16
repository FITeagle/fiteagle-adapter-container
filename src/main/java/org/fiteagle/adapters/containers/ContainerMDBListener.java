package org.fiteagle.adapters.containers;

import java.util.HashMap;
import java.util.Map;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBListener;

public class ContainerMDBListener extends AbstractAdapterMDBListener {
	@Override
	protected Map<String, AbstractAdapter> getAdapterInstances() {
		HashMap<String, AbstractAdapter> instances = new HashMap<String, AbstractAdapter>();

		instances.put("docker", new DockerAdapter());

		return instances;
	}
}
