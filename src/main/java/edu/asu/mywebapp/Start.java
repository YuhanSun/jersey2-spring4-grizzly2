package edu.asu.mywebapp;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.servlet.ServletContainer;
import commons.Util;
import edu.asu.mywebapp.resources.TestResource;

/**
 * The sample demonstrates how to make Jersey2-Spring integration work on top of Grizzly 2,
 * including static pages served from a folder or from within a jar file.
 * 
 * @author Janus Dam Nielsen
 */
public class Start {

  public static void main(String[] args) throws Exception {
    startEmbeddedServer();
  }

  static void startEmbeddedServer() throws Exception {

    // Create test web application context.
    WebappContext webappContext = new WebappContext("Test Context");
    webappContext.addContextInitParameter("contextClass",
        "org.springframework.web.context.support.XmlWebApplicationContext");
    webappContext.addContextInitParameter("contextConfigLocation", "classpath*:spring-context.xml");
    webappContext.addListener("org.springframework.web.context.ContextLoaderListener");

    // Create a servlet registration for the web application in order to wire up Spring managed
    // collaborators to Jersey resources.
    ServletRegistration servletRegistration =
        webappContext.addServlet("jersey-servlet", ServletContainer.class);

    // The logging filters for server logging.
    servletRegistration.setInitParameter("com.sun.jersey.spi.container.ContainerResponseFilters",
        "com.sun.jersey.api.container.filter.LoggingFilter");
    servletRegistration.setInitParameter("com.sun.jersey.spi.container.ContainerRequestFilters",
        "com.sun.jersey.api.container.filter.LoggingFilter");

    servletRegistration.setInitParameter("javax.ws.rs.Application",
        MywebappResourceRegistrator.class.getName());

    servletRegistration.setInitParameter("com.sun.jersey.config.property.packages", "com.test");
    servletRegistration.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");
    servletRegistration.addMapping("/*");

    HttpServer server = new HttpServer();
    NetworkListener listener = new NetworkListener("grizzly2", "10.218.107.47", 3388);
    server.addListener(listener);

    webappContext.deploy(server);

    try {
      server.start();
      System.out.println("Press enter to stop the server...");
      System.in.read();
      Util.close(TestResource.dbserviceRisoTree);
      // TestResource.driver.close();
    } finally {
      server.shutdownNow();
    }
  }
}
