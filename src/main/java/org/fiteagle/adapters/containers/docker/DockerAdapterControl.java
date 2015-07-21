package org.fiteagle.adapters.containers.docker;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AdapterControl;
import org.fiteagle.abstractAdapter.dm.IAbstractAdapter;
import org.fiteagle.api.core.Config;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.OntologyModelUtil;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

@Singleton
@Startup
public class DockerAdapterControl extends AdapterControl {
//@Inject
//protected OpenstackAdapterMDBSender mdbSender;

	@PostConstruct
	void init() {
		adapterModel = OntologyModelUtil.loadModel(
			"ontologies/docker.ttl",
			IMessageBus.SERIALIZATION_TURTLE
		);

		adapterInstancesConfig = new Config();

		createAdapterInstances();
		publishInstances();
	}

	@Override
	protected void addAdapterProperties(Map<String, String> arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public AbstractAdapter createAdapterInstance(Model mod, Resource res) {
		return new DockerAdapter(mod, res);
	}

	@Override
	protected void parseConfig() {
        String jsonProperties = adapterInstancesConfig.readJsonProperties();

        if (jsonProperties.isEmpty())
        	return;

        // Retrieve root object
    	JsonElement parsedElem = new JsonParser().parse(jsonProperties);

    	if (!parsedElem.isJsonObject())
    		return;

    	// Retrieve list of adapter instances
    	JsonElement instanceListElem = parsedElem.getAsJsonObject().get(IAbstractAdapter.ADAPTER_INSTANCES);

    	if (instanceListElem == null || !instanceListElem.isJsonArray())
    		return;

    	// Iterate over list and create all instances
    	for (JsonElement entry: instanceListElem.getAsJsonArray()) {
    		if (!entry.isJsonObject())
    			continue;

    		JsonElement comIDElem = entry.getAsJsonObject().get(IAbstractAdapter.COMPONENT_ID);

    		if (!comIDElem.isJsonPrimitive())
    			continue;

    		String comID = comIDElem.getAsJsonPrimitive().getAsString();

    		if (!comID.isEmpty()) {
    			createAdapterInstance(
					adapterModel,
					ModelFactory.createDefaultModel().createResource(comID)
				);
    		}
    	}
	}
}
