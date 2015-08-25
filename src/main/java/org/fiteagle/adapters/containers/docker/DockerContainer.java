package org.fiteagle.adapters.containers.docker;

import com.hp.hpl.jena.rdf.model.*;
import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.fiteagle.adapters.containers.docker.internal.ContainerConfiguration;
import org.fiteagle.adapters.containers.docker.internal.DockerClient;
import org.fiteagle.adapters.containers.docker.internal.DockerException;

import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class DockerContainer {
	private final Logger logger = Logger.getLogger(getClass().getName());

	private DockerAdapter adapter;
	private DockerAdapterControl adapterControl;

	private Resource adapterResource;
	private String instanceIdentifier;

	private enum State {
		Dead,       // Empty
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
		adapterResource = resource;
	}

	public void update(Resource newState) {
		logger.info("Received update request");
		configureFromResource(newState);
		handleState();
	}

	private void handleState() {
		if (containerID != null) {
			delete();
		}

		if (containerConf == null) {
			containerState = State.Dead;
			return;
		}

		try {
			logger.info("Creating container with configuration: "
			            + containerConf.toJsonObject().toString());

			// TODO: Check if requested container image is available on the host

			containerID = adapter.getDockerClient().create(containerConf);
			containerState =
				adapter.getDockerClient().start(containerID)
					? State.Active
					: State.Dead;
		} catch (DockerException e) {
			logger.throwing(DockerClient.class.getName(), "create/start", e);
			containerState = State.Failed;
		}
	}

	public void delete() {
		if (containerID != null) {
			try {
				adapter.getDockerClient().delete(containerID, true, true);
			} catch (DockerException e) {
				logger.throwing(DockerClient.class.getName(), "delete", e);
			}
		}

		containerID = null;
		containerConf = null;
		containerState = State.Dead;
	}

	private static void extractStringList(List<String> output, Resource head) {
		if (head == null)
			return;

		if (head.hasProperty(RDF.first))
			output.add(head.getProperty(RDF.first).getObject().asLiteral().getString());

		if (head.hasProperty(RDF.rest))
			extractStringList(output, head.getProperty(RDF.rest).getObject().asResource());
	}

	private static Resource makeStringList(Model model, int i, String[] list) {
		if (i == list.length - 1) {
			Resource res = model.createResource(AnonId.create());

			res.addProperty(RDF.first, list[i]);
			res.addProperty(RDF.rest, RDF.nil);

			return res;
		} else if (i >= list.length) {
			return null;
		} else {
			Resource res = model.createResource(AnonId.create());

			res.addProperty(RDF.first, list[i]);
			res.addProperty(RDF.rest, makeStringList(model, i + 1, list));

			return res;
		}
	}

	private void configureFromResource(Resource newState) {
		containerConf = new ContainerConfiguration(null, null);

		// Image
		if (newState.hasProperty(adapterControl.propImage)) {
			Statement stmtImage = newState.getProperty(adapterControl.propImage);
			containerConf.image = stmtImage.getObject().asLiteral().getString();

			logger.info("Image = " + containerConf.image);
		}

		// Command
		if (newState.hasProperty(adapterControl.propCommand)) {
			Statement stmtCommand = newState.getProperty(adapterControl.propCommand);
			RDFNode commandHead = stmtCommand.getObject();

			if (commandHead.isResource()) {
				LinkedList<String> listOut = new LinkedList<>();
				extractStringList(listOut, commandHead.asResource());

				String[] commands = new String[listOut.size()];
				listOut.toArray(commands);

				containerConf.setCommand(commands);
			} else if (commandHead.isLiteral()) {
				containerConf.setCommandEasily(commandHead.asLiteral().getString());
			}
		}

		// Port mappings
		StmtIterator portMapIter = newState.listProperties(adapterControl.propPortMap);
		while (portMapIter.hasNext()) {
			String value = portMapIter.next().getObject().asLiteral().getString();
			String[] segments = value.split(":");

			if (segments.length > 2) {
				try {
					int hostPort = Integer.parseInt(segments[1]);
					int guestPort = Integer.parseInt(segments[2]);
					containerConf.bindPort(segments[0], hostPort, guestPort);

					logger.info("Bound port " + value);
				} catch (NumberFormatException e) {
					logger.warning("Ignoring port map '" + value + "'");
				}
			} else {
				logger.warning("Ignoring port map '" + value + "'");
			}
		}
	}

	public Model serializeModel() {
		Model responseModel = ModelFactory.createDefaultModel();
		Resource resource = responseModel.createResource(instanceIdentifier);

		resource.addProperty(RDF.type, adapterResource);
		resource.addProperty(RDF.type, Omn.Resource);
		resource.addProperty(RDFS.label, resource.getLocalName());
		resource.addProperty(Omn_lifecycle.implementedBy, adapterResource);

		switch (containerState) {
			case Dead:
				resource.addProperty(Omn_lifecycle.hasState, Omn_lifecycle.Uncompleted);
				break;

			case Failed:
				// TODO: Attach error message
				resource.addProperty(Omn_lifecycle.hasState, Omn_lifecycle.Failure);
				break;

			case Active:
				resource.addProperty(Omn_lifecycle.hasState, Omn_lifecycle.Active);
				break;
		}

		if (containerConf != null) {
			if (containerConf.image != null)
				resource.addProperty(adapterControl.propImage, containerConf.image);

			String[] commands = containerConf.getCommand();
			if (commands != null && commands.length > 0)
				resource.addProperty(adapterControl.propCommand, makeStringList(responseModel, 0, commands));
		}

		return responseModel;
	}
}
