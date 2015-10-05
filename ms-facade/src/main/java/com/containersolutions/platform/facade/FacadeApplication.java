package com.containersolutions.platform.facade;

import com.containersolutions.platform.facade.exceptions.RuntimeExceptionMapper;
import com.containersolutions.platform.facade.resources.TickResource;
import com.containersolutions.platform.filter.CharsetResponseFilter;
import com.containersolutions.platform.service.discovery.ConsulServiceDnsSRVResolver;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

@Slf4j
public class FacadeApplication extends Application<FacadeConfiguration> {
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			args = new String[]{"server", "src/main/resources/config.yml"};
		}
		new FacadeApplication().run(args);
	}

	@Override
	public String getName() {
		return "facade";
	}

	@Override
	public final void run(com.containersolutions.platform.facade.FacadeConfiguration configuration, Environment environment) throws Exception {
		environment.jersey().register(new CharsetResponseFilter());
		environment.jersey().register(new TickResource(new ConsulServiceDnsSRVResolver()));
		environment.jersey().register(new RuntimeExceptionMapper());

		setupCORSFilter(environment);
	}

	private void setupCORSFilter(Environment environment) {
		FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
		filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
		filter.setInitParameter("allowedOrigins", "*");
		filter.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,referer");
		filter.setInitParameter("allowedMethods", "GET,PUT,POST,DELETE,OPTIONS");
		filter.setInitParameter("allowCredentials", "true");
	}
}
