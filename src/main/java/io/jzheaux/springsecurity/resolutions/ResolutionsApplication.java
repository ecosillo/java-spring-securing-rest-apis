package io.jzheaux.springsecurity.resolutions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.http.HttpMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;


// @SpringBootApplication(exclude = SecurityAutoConfiguration.class)

@EnableGlobalMethodSecurity(prePostEnabled = true)
@SpringBootApplication
public class ResolutionsApplication extends WebSecurityConfigurerAdapter {
    @Autowired
    UserRepositoryJwtAuthenticationConverter authenticationConverter;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests(authz -> authz
				.mvcMatchers(GET, "/resolutions", "/resolution/**").hasAuthority("resolution:read")
				.anyRequest().hasAuthority("resolution:write"))
			.httpBasic(basic -> {})
			.oauth2ResourceServer(oauth2 -> oauth2.jwt().jwtAuthenticationConverter(this.authenticationConverter))
			.cors(cors -> {});
	}

			// @Override
			// protected void configure(HttpSecurity http) throws Exception {
			// 	http
			// 			.authorizeRequests(authz -> authz
			//     			.anyRequest().authenticated())
			// 				.httpBasic(basic -> {});

			// @Override
			// protected void configure(HttpSecurity http) throws Exception {
			// 	http			
			// 	.mvcMatchers(GET, "/resolutions", "/resolution/**").hasAuthority("resolution:read")
			// 	.anyRequest().hasAuthority("resolution:write"))
			// .httpBasic(basic -> {});
			//}
	
	//.password("{bcrypt}$2a$10$MywQEqdZFNIYnx.Ro/VQ0ulanQAl34B5xVjK2I/SDZNVGS5tHQ08W")
	
	@Bean
	public UserDetailsService userDetailsService(UserRepository users) {
		return new UserRepositoryUserDetailsService(users);

		//return new JdbcUserDetailsManager(dataSource);

		// return new JdbcUserDetailsManager(dataSource) {
		// 	@Override
		// 	protected List<GrantedAuthority> loadUserAuthorities(String username) {
		// 		return AuthorityUtils.createAuthorityList("resolution:read");
		// 	}
		// };
		
	}

	@Bean
	public WebMvcConfigurer webMvcConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
				    // .maxAge(0) // .maxAge(0) // if using local verification
					.allowedOrigins("http://localhost:4000")
					.allowedMethods("HEAD")
					.allowedHeaders("Authorization");
			}
		};
	}

	// @Bean
	// JwtAuthenticationConverter jwtAuthenticationConverter() {
    // 	JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
    // 	JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
    // 	authoritiesConverter.setAuthorityPrefix("");
    // 	authenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
    // 	return authenticationConverter;
	// }

	// @Bean
	// public UserDetailsService userDetailsService() {
	// 	return new InMemoryUserDetailsManager(
	// 		org.springframework.security.core.userdetails.User
	// 			.withUsername("user")
	// 			.password("{bcrypt}$2a$10$fkYoT0rToCLS7e8TJP2JCOTBtcjWCwhuEs6cDfqhiyGrOTU9Dq77m")
	// 			.authorities("resolution:read")
	// 			.build());
	// }

	public static void main(String[] args) {
		SpringApplication.run(ResolutionsApplication.class, args);
	}

}