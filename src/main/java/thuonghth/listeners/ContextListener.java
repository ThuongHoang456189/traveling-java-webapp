package thuonghth.listeners;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.PropertyConfigurator;

/**
 * Application Lifecycle Listener implementation class ContextListener
 *
 */
public class ContextListener implements ServletContextListener {

	/**
	 * Default constructor.
	 */
	public ContextListener() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	private final String IMAGE_DIR = "images";

	public void contextInitialized(ServletContextEvent sce) {
		// initialized log4j
		ServletContext context = sce.getServletContext();
		String log4jConfigFile = context.getInitParameter("log4j-config-location");
		String fullPath = context.getRealPath("/") + File.separator + log4jConfigFile;
		PropertyConfigurator.configure(fullPath);

		// initialized images directory
		String rootPath = sce.getServletContext().getRealPath("/");
		File imageDir = new File(rootPath + File.separator + IMAGE_DIR);
		if (!imageDir.exists()) {
			imageDir.mkdir();
		}
	}

}
