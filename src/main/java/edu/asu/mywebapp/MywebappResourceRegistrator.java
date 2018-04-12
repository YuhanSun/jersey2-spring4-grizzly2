/* Copyright 2013-2014 (c) Sepior Aps, all rights reserved. */

package edu.asu.mywebapp;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

import edu.asu.mywebapp.resources.TestResource;

/**
 * MywebappResourceRegistrator
 * @author Janus Dam Nielsen
 */
public class MywebappResourceRegistrator extends ResourceConfig {

	/**
	 * Register JAX-RS application components.
	 */
	public MywebappResourceRegistrator() {
		this.register(RequestContextFilter.class);
		this.register(TestResource.class);
		this.register(JacksonFeature.class);
		// Use this for registering a full set of resources.
		// this.packages("org.foo.rest;org.bar.rest");
	}
}
