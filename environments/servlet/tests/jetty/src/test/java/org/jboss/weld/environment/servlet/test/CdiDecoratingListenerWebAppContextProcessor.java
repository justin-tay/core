package org.jboss.weld.environment.servlet.test;

import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.jboss.arquillian.container.jetty.embedded_12_ee10.WebAppContextProcessor;
import org.jboss.shrinkwrap.api.Archive;

/**
 * {@link WebAppContextProcessor} that sets up the {@link WebAppContext} with
 * the ee10-cdi-decorate module.
 * <p>
 * The {@link org.eclipse.jetty.ee10.cdi.CdiServletContainerInitializer} is
 * configured using {@link org.eclipse.jetty.ee10.cdi.CdiConfiguration} in
 * arquillian.xml.
 */
public class CdiDecoratingListenerWebAppContextProcessor implements WebAppContextProcessor {

	@Override
	public void process(WebAppContext webAppContext, Archive<?> archive) {
		/*
		 * Set by the module ee10-cdi-decorate.
		 */
		webAppContext.setInitParameter("org.eclipse.jetty.ee10.cdi", "CdiDecoratingListener");

		/*
		 * For ServletContainerInitializer ordering.
		 * 
		 * @see org.eclipse.jetty.ee10.annotations.AnnotationConfiguration
		 */
		webAppContext.setAttribute("org.eclipse.jetty.containerInitializerOrder",
				"org.eclipse.jetty.ee10.cdi.CdiServletContainerInitializer,*");
	}
}
