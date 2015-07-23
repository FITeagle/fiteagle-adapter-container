package org.fiteagle.adapters.containers.docker;

import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.adapters.containers.docker.internal.DockerClient;
import org.fiteagle.adapters.containers.docker.internal.DockerException;
import org.fiteagle.adapters.containers.docker.internal.VersionInformation;
import org.fiteagle.api.core.Config;
import org.fiteagle.api.core.MessageBusOntologyModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class DockerAdapter extends AbstractAdapter {
	public final Property propImage, propCommand, propPortMap;

	private final Logger logger = Logger.getLogger(getClass().getName());
	private final HashMap<String, DockerContainer> instances;

	private DockerClient client;

	public DockerAdapter(Model model, Resource res) {
		instances = new HashMap<String, DockerContainer>();

		uuid = UUID.randomUUID().toString();

		// Configure description
		adapterTBox = model;
		adapterABox = res;

		adapterABox.addProperty(RDF.type, getAdapterClass());
		adapterABox.addProperty(RDFS.label, adapterABox.getLocalName());
		adapterABox.addProperty(RDFS.comment, "Docker Adapter");

		adapterABox.addLiteral(MessageBusOntologyModel.maxInstances, 10);

		adapterABox.addProperty(
			adapterTBox.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#lat"),
			"52.555539"
		);

		adapterABox.addProperty(
			adapterTBox.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#long"),
			"13.209729"
		);

		// Iterate over implemented resources
		NodeIterator resourceIterator = this.adapterTBox.listObjectsOfProperty(Omn_lifecycle.implements_);
		if (resourceIterator.hasNext()) {
			Resource resource = resourceIterator.next().asResource();
			adapterABox.addProperty(Omn_lifecycle.canImplement, resource);
		}

		// Find properties
		String dockerPrefix = model.getNsPrefixURI("docker");

		propImage   = model.getProperty(dockerPrefix, "image");
		propCommand = model.getProperty(dockerPrefix, "command");
		propPortMap = model.getProperty(dockerPrefix, "portMap");

		// Instantiate docker client
		client = new DockerClient("localhost", 1337);

		try {
			VersionInformation verInfo = client.version();
			logger.info(verInfo.toString());
		} catch (DockerException e) {
			logger.severe("Failed to get version information");
			e.printStackTrace();
		}
	}

	public DockerClient getDockerClient() {
		return client;
	}

	@Override
	public Model createInstance(String instanceURI, Model resourceModel)
		throws ProcessingException, InvalidRequestException
	{
		DockerContainer container = new DockerContainer(
			this,
			instanceURI,
			getAdapterManagedResources().get(0)
		);
		instances.put(instanceURI, container);

		container.update(resourceModel);

		return container.serializeModel();
	}

	@Override
	public Model updateInstance(String instanceURI, Model resourceModel)
		throws InvalidRequestException, ProcessingException
	{
		DockerContainer container = instances.get(instanceURI);

		if (container == null)
			return ModelFactory.createDefaultModel();

		container.update(resourceModel);
		return container.serializeModel();
	}

	@Override
	public void deleteInstance(String instanceURI)
		throws InstanceNotFoundException, InvalidRequestException, ProcessingException
	{
		DockerContainer container = instances.get(instanceURI);

		if (container == null)
			return;

		container.delete();
		instances.remove(instanceURI);
	}

	@Override
	public Model getAllInstances()
		throws InstanceNotFoundException, ProcessingException
	{
		Model modelInstances = ModelFactory.createDefaultModel();

		for (DockerContainer docker: instances.values())
			modelInstances.add(docker.serializeModel());

		return modelInstances;
	}

	@Override
	public Model getInstance(String instanceURI)
		throws InstanceNotFoundException, ProcessingException, InvalidRequestException
	{
		DockerContainer docker = instances.get(instanceURI);

		if (docker == null)
			throw new InstanceNotFoundException("Instance '" + instanceURI + "' not found");

		return docker.serializeModel();
	}

	@Override
	public Resource getAdapterABox() {
		return adapterABox;
	}

	@Override
	public Model getAdapterDescriptionModel() {
		return adapterTBox;
	}

	@Override
	public void updateAdapterDescription() throws ProcessingException {}

	@Override
	public void configure(Config arg0) {}

	@Override
	public String getId() {
		return uuid;
	}

	@Override
	public void refreshConfig() throws ProcessingException {}

	@Override
	public void shutdown() {}
}
