package org.openmrs.module.serialization.xstream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.BaseModuleActivator;

public class ModuleActivator extends BaseModuleActivator {

	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see org.openmrs.module.ModuleActivator#started()
	 */
	@Override
	public void started() {
		log.info("Started Serialization XStream Module ...");
	}

	/**
	 * @see org.openmrs.module.ModuleActivator#stopped()
	 */
	@Override
	public void stopped() {
		log.info("Stopped Serialization XStream Module ...");
	}
}
