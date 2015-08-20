package org.fiteagle.adapters.containers.docker;

import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import java.util.logging.Logger;

import org.fiteagle.adapters.containers.docker.internal.ContainerConfiguration;
import org.fiteagle.adapters.containers.docker.internal.DockerClient;
import org.fiteagle.adapters.containers.docker.internal.DockerException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class DockerContainer {
	private final Logger logger = Logger.getLogger(getClass().getName());

	private DockerAdapter adapter;
	private DockerAdapterControl adapterControl;

	private Resource instanceResource;
	private String instanceIdentifier;

	private enum State {
		Dead,       // Empty
		Configured, // Configuration created
		Failed,     // Create/Start failed
		Active      // Create/Start succeeded
	}

	private State containerState = State.Dead;
	private String containerID = null;
	private ContainerConfiguration containerConf = null;

	public DockerContainer(
		DockerAdapter parent,
		String uri,
		Resource resource
	) {
		adapter = parent;
		adapterControl = parent.parent;

		instanceIdentifier = uri;
		instanceResource = resource;
	}

	public void update(Resource newState) {
		logger.info("Received update request");

		if (newState == null || !newState.hasProperty(Omn_lifecycle.hasState)) {
			logger.severe("Invalid update model");
			return;
		}

		// Check resource state
		Resource stateResource =
			newState.getProperty(Omn_lifecycle.hasState).getObject().asResource();

		if (stateResource.equals(Omn_lifecycle.Ready)) {
			configureFromResource(newState);
		} else if (stateResource.equals(Omn_lifecycle.Uncompleted)) {
			configureFromResource(newState);
		} else {
			logger.severe("Unsupported lifecycle state: " + stateResource.toString());
		}
	}

	private void handleUncompleted(Resource newState) {
		if (containerState == State.Active)
			delete();

		configureFromResource(newState);
	}

	private void handleReady(Resource newState) {
		handleUncompleted(newState);

		if (containerState == State.Configured && containerConf != null) {
			try {
				logger.info("Starting with configuration " + containerConf.toJsonObject().toString());
				containerID = adapter.getDockerClient().create(containerConf);

				if (containerID != null && adapter.getDockerClient().start(containerID))
					containerState = State.Active;
				else
					containerState = State.Failed;
			} catch (DockerException e) {
				logger.throwing(DockerClient.class.getName(), "create/start", e);
				containerState = State.Failed;
			}
		}

		logger.info("State = " + containerState.toString());
	}

	public void delete() {
		switch (containerState) {
			case Dead:
				break;

			case Configured:
				if (containerConf == null)
					containerState = State.Dead;

				break;

			case Failed:
				if (containerConf == null)
					// An unconfigured container which failed to start? No shit.
					containerState = State.Dead;
				else
					// Start failure might be caused by invalid configuration, we'll keep it anyway
					containerState = State.Configured;

				break;

			case Active:
				if (containerID != null) {
					try {
						adapter.getDockerClient().delete(containerID, true, true);
					} catch (DockerException e) {
						logger.throwing(DockerClient.class.getName(), "stop", e);
					}
				}

				// Figure out if the container may stay configured
				if (containerConf != null)
					containerState = State.Configured;
				else
					containerState = State.Dead;

				break;
		}

		containerID = null;
		logger.info("State = " + containerState.toString());
	}

	private void configureFromResource(Resource newState) {
		containerConf = new ContainerConfiguration(null, null);

		logger.info("newState = " + (newState == null) + ", adapterControl = " + (adapterControl == null));

		// Image
		if (newState.hasProperty(adapterControl.propImage)) {
			Statement stmtImage = newState.getProperty(adapterControl.propImage);
			containerConf.image = stmtImage.getObject().asLiteral().getString();

			logger.info("Image = " + containerConf.image);
		}

		// Command
		if (newState.hasProperty(adapterControl.propCommand)) {
			Statement stmtImage = newState.getProperty(adapterControl.propCommand);
			String command = stmtImage.getObject().asLiteral().getString();

			logger.info("Command = " + command);
			containerConf.setCommandEasily(command);
		}

		// Port mappings
		StmtIterator portMapIter = newState.listProperties(adapterControl.propPortMap);
		while (portMapIter.hasNext()) {
			String value = portMapIter.next().getObject().asLiteral().getString();
			String[] segments = value.split(":");

			if (segments.length > 2) {
				try {
					int guestPort = Integer.parseInt(segments[1]);
					int hostPort = Integer.parseInt(segments[2]);
					containerConf.bindPort(segments[0], hostPort, guestPort);

					logger.info("Bound port " + value);
				} catch (NumberFormatException e) {
					logger.warning("Ignoring port map '" + value + "'");
				}
			} else {
				logger.warning("Ignoring port map '" + value + "'");
			}
		}

		containerState = State.Configured;
	}

	public Model serializeModel() {
		Model responseModel = ModelFactory.createDefaultModel();
		Resource resource = responseModel.createResource(instanceIdentifier);

        resource.addProperty(RDF.type, instanceResource);
        resource.addProperty(RDF.type, Omn.Resource);
        resource.addProperty(RDFS.label, resource.getLocalName());

        // Add Omn-related property
        Property property = resource.getModel().createProperty(
    		Omn_lifecycle.hasState.getNameSpace(),
    		Omn_lifecycle.hasState.getLocalName()
    	);
        property.addProperty(RDF.type, OWL.FunctionalProperty);

//        switch (containerState) {
//	        case Dead:
//	    		resource.addProperty(property, Omn_lifecycle.NotReady);
//	    		break;
//
//	        case Failed:
//	    		resource.addProperty(property, Omn_lifecycle.Failure);
//	    		break;
//
//	        case Configured:
//	    		resource.addProperty(property, Omn_lifecycle.Provisioned);
//	    		break;
//
//	        case Active:
//	        	resource.addProperty(property, Omn_lifecycle.Active);
//	        	break;
//        }

        resource.addProperty(property, Omn_lifecycle.Ready);

        // TODO: Serialize properties

		return responseModel;
	}
}
