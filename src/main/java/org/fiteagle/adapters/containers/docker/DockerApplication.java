package org.fiteagle.adapters.containers.docker;

import java.io.BufferedReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;
import org.fiteagle.abstractAdapter.AbstractAdapter;

import com.google.gson.JsonObject;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.OntologyModelUtil;

@ApplicationPath("/docker")
public class DockerApplication extends Application {
//	public static class AdapterConfigWrapper {
//		private String id, hostname;
//		private int port;
//
//		public String getId() {
//			return id;
//		}
//
//		public void setId(String id) {
//			this.id = id;
//		}
//
//		public String getHostname() {
//			return hostname;
//		}
//
//		public void setHostname(String hostname) {
//			this.hostname = hostname;
//		}
//
//		public int getPort() {
//			return port;
//		}
//
//		public void setPort(int p) {
//			this.port = p;
//		}
//	}

	public static class ContainerConfig {
		private String uri;
		private String image;
		private String command;
		private List<String> portMaps;

		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}

		public String getImage() {
			return image;
		}

		public void setImage(String image) {
			this.image = image;
		}

		public String getCommand() {
			return command;
		}

		public void setCommand(String command) {
			this.command = command;
		}

		public List<String> getPortMaps() {
			return portMaps;
		}

		public void setPortMaps(List<String> portMaps) {
			this.portMaps = portMaps;
		}
	}

	@Path("/")
	public static class Handler {
		@EJB
		DockerAdapterControl adapterControl;

		@GET
		@Path("/adapters")
		public Response listAdapters() {
			JsonArray resultArray = new JsonArray();

			for (AbstractAdapter aa: adapterControl.getAdapterInstances()) {
				JsonObject adapterObject = new JsonObject();
				adapterObject.addProperty("id", aa.getId());
				adapterObject.addProperty("uri", aa.getAdapterABox().getURI());

				resultArray.add(adapterObject);
			}

			return Response.ok(resultArray.toString()).status(200).build();
		}

//		@POST
//		@Path("/adapters")
//		@Consumes(MediaType.APPLICATION_JSON)
//		public Response createAdapter(AdapterConfigWrapper data) {
//			JsonObject resultJSON = new JsonObject();
//
//			try {
//				AbstractAdapter aa = adapterControl.createAdapterInstance(
//					data.getId(), data.getHostname(), data.getPort()
//				);
//
//				return Response.ok(aa.getId()).status(200).build();
//			} catch (DockerControlException e) {
//				return Response.ok(e.getMessage()).status(500).build();
//			}
//		}

//		@POST
//		@Path("/adapters/{id}/delete")
//		public Response deleteAdapter(@PathParam("id") String id) {
//			return Response.ok().build();
//		}

		@GET
		@Path("/adapters/{id}/instances")
		public Response findAdapterInstances(@PathParam("id") String id) {
			try {
				for (AbstractAdapter aa: adapterControl.getAdapterInstances()) {
					if (!aa.getId().equals(id))
						continue;

					JsonArray returnArray = new JsonArray();
					ResIterator iter = aa.getAllInstances().listSubjectsWithProperty(Omn_lifecycle.hasState);

					while (iter.hasNext()) {
						returnArray.add(new JsonPrimitive(iter.next().getURI()));
					}

					return Response.ok(returnArray.toString()).build();
				}
			} catch (AbstractAdapter.ProcessingException e) {
				e.printStackTrace();
			} catch (AbstractAdapter.InstanceNotFoundException e) {
				e.printStackTrace();
			}

			return Response.status(404).build();
		}

		@POST
		@Path("/adapters/{id}/create")
		public Response createAdapterInstance(@PathParam("id") String id, @QueryParam("uri") String uri,
		                                      @QueryParam("lang") String lang, @Context HttpServletRequest req)
				throws Exception
		{
			for (AbstractAdapter aa: adapterControl.getAdapterInstances()) {
				if (!aa.getId().equals(id))
					continue;

				Model inputModel = ModelFactory.createDefaultModel();
				inputModel.read(req.getReader(), null, lang);

				Model mResponse = aa.createInstance(uri, inputModel);

				StringWriter mOut = new StringWriter();
				mResponse.write(mOut, null, lang);

				return Response.ok(mOut.toString()).build();
			}

			return Response.status(404).build();
		}

		@POST
		@Path("/adapters/{id}/update")
		public Response updateAdapterInstance(@PathParam("id") String id, @QueryParam("uri") String uri,
		                                      @QueryParam("lang") String lang, @Context HttpServletRequest req)
				throws Exception
		{
			for (AbstractAdapter aa: adapterControl.getAdapterInstances()) {
				if (!aa.getId().equals(id))
					continue;

				Model inputModel = ModelFactory.createDefaultModel();
				inputModel.read(req.getReader(), null, lang);

				Model mResponse = aa.createInstance(uri, inputModel);

				StringWriter mOut = new StringWriter();
				mResponse.write(mOut, null, lang);

				return Response.ok(mOut.toString()).build();
			}

			return Response.status(404).build();
		}

		@POST
		@Path("/adapters/{id}/describe")
		public Response updateAdapterInstance(@PathParam("id") String id, @QueryParam("uri") String uri)
				throws Exception
		{
			for (AbstractAdapter aa: adapterControl.getAdapterInstances()) {
				if (!aa.getId().equals(id))
					continue;

				aa.deleteInstance(uri);
				return Response.ok().build();
			}

			return Response.status(404).build();
		}

		@POST
		@Path("/adapters/{id}/describe")
		public Response updateAdapterInstance(@PathParam("id") String id, @QueryParam("uri") String uri,
		                                      @QueryParam("lang") String lang)
				throws Exception
		{
			for (AbstractAdapter aa: adapterControl.getAdapterInstances()) {
				if (!aa.getId().equals(id))
					continue;

				Model mResponse = aa.getInstance(uri);

				StringWriter mOut = new StringWriter();
				mResponse.write(mOut, null, lang);

				return Response.ok(mOut.toString()).build();
			}

			return Response.status(404).build();
		}
	}

	@Override
	public Set<Class<?>> getClasses() {
		HashSet<Class<?>> directingClasses = new HashSet<Class<?>>();

		directingClasses.add(Handler.class);

		return directingClasses;
	}
}
