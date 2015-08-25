package org.fiteagle.adapters.containers.docker;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
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
import java.util.List;
import java.util.Set;

import static org.fiteagle.abstractAdapter.AbstractAdapter.*;

@ApplicationPath("/docker")
public class DockerApplication extends Application {
	@Path("/")
	public static class Handler {
		@EJB
		DockerAdapterControl adapterControl;

		private AbstractAdapter findAdapterByURI(String uri) {
			for (AbstractAdapter aa: adapterControl.getAdapterInstances()) {
				if (aa.getAdapterABox().getURI().equals(uri))
					return aa;
			}

			return null;
		}

		@POST
		@Path("/create")
		public Response createInstance(@QueryParam("lang") String lang, @Context HttpServletRequest req)
			throws IOException, InvalidRequestException, AbstractAdapter.ProcessingException
		{
			Model requestModel = ModelFactory.createDefaultModel();
			requestModel.read(req.getReader(), null, lang.toUpperCase());

			Model responseModel = ModelFactory.createDefaultModel();

			ResIterator resources = requestModel.listResourcesWithProperty(Omn_lifecycle.implementedBy);
			while (resources.hasNext()) {
				Resource res = resources.next();

				RDFNode implementedBy = res.getProperty(Omn_lifecycle.implementedBy).getObject();
				String adapterUri = implementedBy.asResource().getURI();
				AbstractAdapter adapter = findAdapterByURI(adapterUri);

				if (adapter == null)
					continue;

				Model createModel = adapter.createInstance(res.getURI(), requestModel);
				responseModel.add(createModel);
			}

			StringWriter modelOutput = new StringWriter();
			responseModel.write(modelOutput, lang.toUpperCase(), null);

			return Response.ok(modelOutput.toString()).status(201).build();
		}

		@POST
		@Path("/update")
		public Response updateInstance(@QueryParam("lang") String lang, @Context HttpServletRequest req)
				throws IOException, InvalidRequestException, AbstractAdapter.ProcessingException
		{
			Model requestModel = ModelFactory.createDefaultModel();
			requestModel.read(req.getReader(), null, lang.toUpperCase());

			Model responseModel = ModelFactory.createDefaultModel();

			ResIterator resources = requestModel.listResourcesWithProperty(Omn_lifecycle.implementedBy);
			while (resources.hasNext()) {
				Resource res = resources.next();

				RDFNode implementedBy = res.getProperty(Omn_lifecycle.implementedBy).getObject();
				String adapterUri = implementedBy.asResource().getURI();
				AbstractAdapter adapter = findAdapterByURI(adapterUri);

				if (adapter == null)
					continue;

				Model createModel = adapter.updateInstance(res.getURI(), requestModel);
				responseModel.add(createModel);
			}

			StringWriter modelOutput = new StringWriter();
			responseModel.write(modelOutput, lang.toUpperCase(), null);

			return Response.ok(modelOutput.toString()).status(201).build();
		}

		@GET
		@Path("/delete")
		public Response deleteInstance(@QueryParam("uri") String uri,
		                               @Context HttpServletRequest req)
			throws IOException, AbstractAdapter.ProcessingException, InstanceNotFoundException, InvalidRequestException
		{
			for (AbstractAdapter aa: adapterControl.getAdapterInstances()) {
				aa.deleteInstance(uri);
			}

			return Response.ok().status(200).build();
		}
	}

	@Override
	public Set<Class<?>> getClasses() {
		HashSet<Class<?>> directingClasses = new HashSet<Class<?>>();

		directingClasses.add(Handler.class);

		return directingClasses;
	}
}
