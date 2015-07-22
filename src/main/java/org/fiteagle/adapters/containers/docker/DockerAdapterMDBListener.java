package org.fiteagle.adapters.containers.docker;

import java.util.Collection;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBListener;
import org.fiteagle.api.core.IMessageBus;

@MessageDriven(
	name = "DockerAdapterMDBListener",
	activationConfig = {
		@ActivationConfigProperty(
			propertyName = "destinationType",
			propertyValue = "javax.jms.Topic"
		),
		@ActivationConfigProperty(
			propertyName = "destination",
			propertyValue = IMessageBus.TOPIC_CORE
		),
		@ActivationConfigProperty(
			propertyName = "messageSelector",
			propertyValue =
				IMessageBus.METHOD_TARGET
				+ " = 'http://docker.com/schema/docker#Adapter' AND ("
				+ IMessageBus.METHOD_TYPE + " = '" + IMessageBus.TYPE_CREATE
				+ "' OR "
				+ IMessageBus.METHOD_TYPE + " = '" + IMessageBus.TYPE_CONFIGURE
				+ "' OR "
				+ IMessageBus.METHOD_TYPE + " = '" + IMessageBus.TYPE_GET
				+ "' OR "
				+ IMessageBus.METHOD_TYPE + " = '" + IMessageBus.TYPE_DELETE
				+ "')"
		),
		@ActivationConfigProperty(
			propertyName = "acknowledgeMode",
			propertyValue = "Auto-acknowledge"
		)
	}
)
public class DockerAdapterMDBListener extends AbstractAdapterMDBListener {
	@EJB
    DockerAdapterControl adapterControl;

	@Override
	protected Collection<AbstractAdapter> getAdapterInstances() {
		return adapterControl.getAdapterInstances();
	}
}
