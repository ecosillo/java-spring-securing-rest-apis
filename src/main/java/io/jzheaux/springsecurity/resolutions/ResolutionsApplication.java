package io.jzheaux.springsecurity.resolutions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.core.authority.*;


import javax.sql.DataSource;

import static org.springframework.http.HttpMethod.GET;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;


@EnableGlobalMethodSecurity(prePostEnabled = true)
@SpringBootApplication
public class ResolutionsApplication extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.authorizeRequests(authz -> authz
        			.anyRequest().authenticated())
    				.httpBasic(basic -> {});
			// .authorizeRequests(authz -> authz
			// 	.mvcMatchers(GET, "/resolutions", "/resolution/**").hasAuthority("resolution:read")
			// 	.anyRequest().hasAuthority("resolution:write"))
			// .httpBasic(basic -> {});
	}
	
	//.password("{bcrypt}$2a$10$MywQEqdZFNIYnx.Ro/VQ0ulanQAl34B5xVjK2I/SDZNVGS5tHQ08W")
	@Bean
	UserDetailsService userDetailsService(UserRepository users) {
		return new UserRepositoryUserDetailsService(users);

		//return new JdbcUserDetailsManager(dataSource);

		// return new JdbcUserDetailsManager(dataSource) {
		// 	@Override
		// 	protected List<GrantedAuthority> loadUserAuthorities(String username) {
		// 		return AuthorityUtils.createAuthorityList("resolution:read");
		// 	}
		// };
		
	}
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