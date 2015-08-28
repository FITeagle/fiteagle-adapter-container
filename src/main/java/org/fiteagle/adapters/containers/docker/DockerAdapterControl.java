package org.fiteagle.adapters.containers.docker;

import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AdapterControl;
import org.fiteagle.abstractAdapter.dm.IAbstractAdapter;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.OntologyModelUtil;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

@Singleton
@Startup
public class DockerAdapterControl extends AdapterControl {
	public static final String CONF_HOSTNAME = "hostname";
	public static final String CONF_PORT = "port";

	public Property propAdapterHostname, propAdapterPort, propID, propImage, propCommand, propPortMap;

	private final Logger logger = Logger.getLogger(getClass().getName());

	@Inject
	protected DockerAdapterMDBSender adapterSender;

	@PostConstruct
	void init() {
		adapterModel = OntologyModelUtil.loadModel(
			"ontologies/docker.ttl",
			IMessageBus.SERIALIZATION_TURTLE
		);

		String dockerPrefix = adapterModel.getNsPrefixURI("docker");

		propAdapterHostname = adapterModel.getProperty(dockerPrefix, "hostname");
		propAdapterPort     = adapterModel.getProperty(dockerPrefix, "port");

		propID      = adapterModel.getProperty(dockerPrefix, "id");
		propImage   = adapterModel.getProperty(dockerPrefix, "image");
		propCommand = adapterModel.getProperty(dockerPrefix, "command");
		propPortMap = adapterModel.getProperty(dockerPrefix, "portMap");

		adapterInstancesConfig = readConfig("DockerAdapter");

		createAdapterInstances();
		publishInstances();
	}

	@Override
	protected void addAdapterProperties(Map<String, String> arg0) {}

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
		JsonElement instanceListElem =
		parsedElem.getAsJsonObject().get(IAbstractAdapter.ADAPTER_INSTANCES);

		if (instanceListElem == null || !instanceListElem.isJsonArray())
			return;

		// Iterate over list and create all instances
		for (JsonElement entry: instanceListElem.getAsJsonArray()) {
			if (!entry.isJsonObject())
				continue;

			try {
				createAdapterFromConf(entry.getAsJsonObject());
			} catch (DockerControlException e) {
				// No need to log exception, 'createAdapterFromConf' already does that
			}
		}
	}

	/**
	 * Create an adapter instance using a JSON configuration.
	 * @param comConf Configuration object
	 * @throws DockerControlException
	 */
	public AbstractAdapter createAdapterFromConf(JsonObject comConf) throws DockerControlException {
		logger.info("Using adapter configuration: ");
		logger.info(comConf.toString());

		// Validate configuration schema
		if (!comConf.has(IAbstractAdapter.COMPONENT_ID) || !comConf.has(CONF_HOSTNAME) || !comConf.has(CONF_PORT)) {
			logger.warning("Ignoring invalid adapter configuration");
			throw new DockerControlException("Ignoring invalid adapter configuration");
		}

		// Retrieve configuration elements
		JsonElement comIDElem = comConf.get(IAbstractAdapter.COMPONENT_ID);
		JsonElement comHostnameElem = comConf.get(CONF_HOSTNAME);
		JsonElement comPortElem = comConf.get(CONF_PORT);

		// Validate element types
		if (!comIDElem.isJsonPrimitive() ||
		    !comHostnameElem.isJsonPrimitive() ||
		    !comPortElem.isJsonPrimitive())
		{
			logger.warning("Ignoring configuration: invalid schema");
			throw new DockerControlException("Ignoring configuration: invalid schema");
		}

		String comID = comIDElem.getAsJsonPrimitive().getAsString();
		String comHostname = comHostnameElem.getAsJsonPrimitive().getAsString();
		int comPort = comPortElem.getAsJsonPrimitive().getAsInt();

		return createAdapterInstance(comID, comHostname, comPort);
	}

	/**
	 * Create a new adapter instance
	 * @param comID Resource identifier
	 * @param comHostname Docker API endpoint hostname
	 * @param comPort Docker API endpoint port
	 * @throws DockerControlException
	 */
	public AbstractAdapter createAdapterInstance(String comID, String comHostname, int comPort)
		throws DockerControlException
	{
		if (comID.isEmpty() || comHostname.isEmpty()) {
			logger.warning("Ignoring configuration: empty identifier or hostname");
			throw new DockerControlException("Ignoring configuration: empty identifier or hostname");
		}

		Resource configResource = ModelFactory.createDefaultModel().createResource(comID);

		configResource.addProperty(propAdapterHostname, comHostname);
		configResource.addProperty(propAdapterPort, String.valueOf(comPort));

		return createAdapterInstance(
			adapterModel,
			configResource
		);
	}

	@Override
	public AbstractAdapter createAdapterInstance(Model mod, Resource res) {
		String endpointHostname =
			res.getProperty(propAdapterHostname).getObject().asLiteral().getString();
		int endpointPort = res.getProperty(propAdapterPort).getObject().asLiteral().getInt();

		DockerAdapter adapter = new DockerAdapter(this, mod, res);
		adapter.connect(endpointHostname, endpointPort);

		adapterInstances.put(adapter.getId(), adapter);
		return adapter;
	}
}
