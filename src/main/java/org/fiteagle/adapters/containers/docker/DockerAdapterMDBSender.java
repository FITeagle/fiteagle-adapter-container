package org.fiteagle.adapters.containers.docker;

import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;

@Singleton
public class DockerAdapterMDBSender extends AbstractAdapterMDBSender {
	@EJB
	DockerAdapterControl adapterControl;
}
