package it.f2informatica.services.context;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
		"it.f2informatica.services.domain",
		"it.f2informatica.services.gateway"
})
public class ServicesApplicationContext {

}
