package org.openmrs.module.serialization.xstream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.Activator;

public class ModuleActivator implements Activator {

	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see org.openmrs.module.Activator#startup()
	 */
	public void shutdown() {
		log.info("Starting Serialization XStream Module ...");
	}

	/**
	 *  @see org.openmrs.module.Activator#shutdown()
	 */
	public void startup() {
		log.info("Shutting down Serialization XStream Module ...");
	}

}
