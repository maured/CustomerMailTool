package dma.restconnexion.hub;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.Client;
import org.restlet.Context;
import org.restlet.Response;
import org.restlet.data.Protocol;
import org.restlet.resource.ClientResource;

public class RestletClientHttpHandler {
	private static final Log logger = LogFactory.getLog(RestletClientHttpHandler.class);
	private Client client = null;
	private volatile int crCount;
	private static RestletClientHttpHandler _handler = null;

	public static RestletClientHttpHandler getHandler() {
		if (_handler == null) {
			_handler = new RestletClientHttpHandler();
		}

		return _handler;
	}

	private RestletClientHttpHandler() {
		this.client = new Client(new Context(), Protocol.HTTP);
		this.crCount = 0;
	}

	public synchronized ClientResource getClientResource(String url) {
		if (this.crCount == 0) {
			try {
				logger.debug("Client not open.... starting hub Client");
				this.client.start();
			} catch (Exception var3) {
				logger.error("Error starting Restlet/HTTPClient client", var3);
				return null;
			}
		}

		ClientResource cr = new ClientResource(url);
		cr.setNext(this.client);
		++this.crCount;
		return cr;
	}

	public synchronized void closeClientResource(ClientResource resource) {
		if (resource == null) {
			logger.warn("closeClientResource() called with null ClientResource. Possible connection leak");
		} else {
			--this.crCount;
			resource.release();
			Response response = resource.getResponse();
			if (response != null) {
				response.release();
			}

			if (this.crCount == 0) {
				logger.debug("All connections closed... closing client");

				try {
					this.client.stop();
				} catch (Exception var4) {
					logger.error("Error stopping Restlet/HTTPClient client", var4);
				}
			}

		}
	}

	protected void finalize() throws Throwable {
		try {
			logger.debug("RestletClientHttpHandler being destroyed... closing client");
			this.client.stop();
		} catch (Exception var2) {
			logger.error("Error stopping Restlet/HTTPClient client", var2);
		}

		super.finalize();
	}
}
