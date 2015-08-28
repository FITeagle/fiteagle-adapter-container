package org.fiteagle.adapters.containers.docker;

import com.hp.hpl.jena.rdf.model.*;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapter.InstanceNotFoundException;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import static org.fiteagle.abstractAdapter.AbstractAdapter.InvalidRequestException;

@ApplicationPath("/docker")
public class DockerApplication extends Application {
	/**
	 * Root API Handler
	 */
	@Path("/")
	public static class Handler {
		@EJB
		DockerAdapterControl adapterControl;

		/**
		 * Find an adapter with the given URI.
		 * @param uri URI identifier
		 */
		private AbstractAdapter findAdapterByURI(String uri) {
			for (AbstractAdapter aa: adapterControl.getAdapterInstances()) {
				if (aa.getAdapterABox().getURI().equals(uri))
					return aa;
			}

			return null;
		}

		/**
		 * Create a new Resource instance.
		 * @param lang Ontology model language (e.g. `turtle` or `rdfxml`)
		 * @param req Request context used to retrieve the POST body
		 * @throws IOException
		 * @throws InvalidRequestException
		 * @throws AbstractAdapter.ProcessingException
		 */
		@POST
		@Path("/create")
		public Response createInstance(
			@QueryParam("lang") String lang,
			@Context HttpServletRequest req
		)
			throws IOException, InvalidRequestException, AbstractAdapter.ProcessingException
		{
			// Retrieve the input model from the request body
			Model requestModel = ModelFactory.createDefaultModel();
			requestModel.read(req.getReader(), null, lang.toUpperCase());

			Model responseModel = ModelFactory.createDefaultModel();

			// Iterate over resources with a `omn-lifecycle:implementedBy` predicate
			ResIterator resources =
				requestModel.listResourcesWithProperty(Omn_lifecycle.implementedBy);

			while (resources.hasNext()) {
				Resource res = resources.next();

				// Use the `omn-lifecycle:implementedBy` predicate to find the responsible adapter
				RDFNode implementedBy = res.getProperty(Omn_lifecycle.implementedBy).getObject();
				String adapterUri = implementedBy.asResource().getURI();
				AbstractAdapter adapter = findAdapterByURI(adapterUri);

				// Resources which are not attached to the adapters we manage, are neglected
				if (adapter == null)
					continue;

				// Response model needs to be merged
				Model createModel = adapter.createInstance(res.getURI(), requestModel);
				responseModel.add(createModel);
			}

			// Generate a response body
			StringWriter modelOutput = new StringWriter();
			responseModel.write(modelOutput, lang.toUpperCase(), null);

			return
				Response
					.ok(modelOutput.toString())
					.status(201)                                // 201 Created
					.header("Access-Control-Allow-Origin", "*") // Needed for our web application
					.build();
		}

		/**
		 * Update an existing Resource
		 * @param lang Ontology model language (e.g. `turtle` or `rdfxml`)
		 * @param req Request context used to retrieve the POST body
		 * @throws IOException
		 * @throws InvalidRequestException
		 * @throws AbstractAdapter.ProcessingException
		 */
		@POST
		@Path("/update")
		public Response updateInstance(
			@QueryParam("lang") String lang,
			@Context HttpServletRequest req
		)
			throws IOException, InvalidRequestException, AbstractAdapter.ProcessingException
		{
			// Retrieve the input model from the request body
			Model requestModel = ModelFactory.createDefaultModel();
			requestModel.read(req.getReader(), null, lang.toUpperCase());

			Model responseModel = ModelFactory.createDefaultModel();

			// Iterate over resources with a `omn-lifecycle:implementedBy` predicate
			ResIterator resources =
				requestModel.listResourcesWithProperty(Omn_lifecycle.implementedBy);

			while (resources.hasNext()) {
				Resource res = resources.next();

				// Use the `omn-lifecycle:implementedBy` predicate to find the responsible adapter
				RDFNode implementedBy = res.getProperty(Omn_lifecycle.implementedBy).getObject();
				String adapterUri = implementedBy.asResource().getURI();
				AbstractAdapter adapter = findAdapterByURI(adapterUri);

				// Resources which are not attached to the adapters we manage, are neglected
				if (adapter == null)
					continue;

				// Response model needs to be merged
				Model createModel = adapter.updateInstance(res.getURI(), requestModel);
				responseModel.add(createModel);
			}

			// Generate a response body
			StringWriter modelOutput = new StringWriter();
			responseModel.write(modelOutput, lang.toUpperCase(), null);

			return
				Response
					.ok(modelOutput.toString())
					.status(201)                                // 201 Created
						.header("Access-Control-Allow-Origin", "*") // Needed for web application
					.build();
		}

		/**
		 * Delete a resource
		 * @param uri Resource URI
		 * @throws IOException
		 * @throws AbstractAdapter.ProcessingException
		 * @throws InstanceNotFoundException
		 * @throws InvalidRequestException
		 */
		@GET
		@Path("/delete")
		public Response deleteInstance(
			@QueryParam("uri") String uri
		)
			throws IOException, AbstractAdapter.ProcessingException, InstanceNotFoundException,
			       InvalidRequestException
		{
			// Delete each instance with the given URI
			for (AbstractAdapter aa: adapterControl.getAdapterInstances())
				aa.deleteInstance(uri);

			return
				Response
					.ok()
					.status(200)
					.header("Access-Control-Allow-Origin", "*") // Needed for web application
					.build();
		}
	}

	@Override
	public Set<Class<?>> getClasses() {
		HashSet<Class<?>> directingClasses = new HashSet<Class<?>>();

		// Parent web framework needs to know which handlers there are
		directingClasses.add(Handler.class);

		return directingClasses;
	}
}
