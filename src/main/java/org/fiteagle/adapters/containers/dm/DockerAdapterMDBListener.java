package org.fiteagle.adapters.containers.dm;

import javax.jms.Message;
import javax.jms.MessageListener;

public class DockerAdapterMDBListener implements MessageListener {

	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		String messageType = MessageUtil.getMessageType(message);
	    String serialization = MessageUtil.getMessageSerialization(message);
	    String rdfString = MessageUtil.getStringBody(message);
	    
	    if (messageType != null && rdfString != null) {
	      Model messageModel = MessageUtil.parseSerializedModel(rdfString, serialization);
	      
	      for(DockerAdapter adapter : getAdapterInstances().values()){
	        if (adapter.isRecipient(messageModel)) {
	          LOGGER.log(Level.INFO, "Received a " + messageType + " message");
	          try{
	            if (messageType.equals(IMessageBus.TYPE_CREATE)) {
	              Model resultModel = adapter.createInstances(messageModel);
	              adapter.notifyListeners(resultModel, MessageUtil.getJMSCorrelationID(message), IMessageBus.TYPE_INFORM, null);
	              
	            } else if (messageType.equals(IMessageBus.TYPE_CONFIGURE)) {
	              Model resultModel = adapter.updateInstances(messageModel);
	              adapter.notifyListeners(resultModel, MessageUtil.getJMSCorrelationID(message), IMessageBus.TYPE_INFORM, null);
	              
	            } else if (messageType.equals(IMessageBus.TYPE_DELETE)) {
	              Model resultModel = adapter.deleteInstances(messageModel);
	              adapter.notifyListeners(resultModel, MessageUtil.getJMSCorrelationID(message), IMessageBus.TYPE_INFORM, null);
	              
	            } else if (messageType.equals(IMessageBus.TYPE_GET)) {
	              Model resultModel = adapter.getInstances(messageModel);
	              adapter.notifyListeners(resultModel, MessageUtil.getJMSCorrelationID(message), IMessageBus.TYPE_INFORM, null);
	            }
	          } catch(ProcessingException | InvalidRequestException | InstanceNotFoundException e){
	            Message errorMessage = MessageUtil.createErrorMessage(e.getMessage(), MessageUtil.getJMSCorrelationID(message), context);
	            context.createProducer().send(topic, errorMessage);
	          }
	        }
	      }
	    }
	}

}
