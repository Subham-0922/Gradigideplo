package com.gradigi.LoginAndSecurity.SecurityConfiguratons;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.gradigi.LoginAndSecurity.JwtConfigurations.CustomJwtAuthenticationFilter;
import com.gradigi.LoginAndSecurity.JwtConfigurations.JwtAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CustomConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private CustomJwtAuthenticationFilter customJwtAuthenticationFilter;

	@Autowired
	private JwtAuthenticationEntryPoint unauthorizedHandler;

	@Bean
	public UserDetailsService getUserDetailService() {
		return new UserDetailServiceImpl();
	}

	@Bean
	public PasswordEncoder getPassEncoder() {
		return new BCryptPasswordEncoder(10);
	}

	@Bean
	public DaoAuthenticationProvider getDaoAuthprovider() {

		DaoAuthenticationProvider dao = new DaoAuthenticationProvider();

		dao.setUserDetailsService(getUserDetailService());
		dao.setPasswordEncoder(getPassEncoder());

		return dao;
	}

	@Bean
	public AuthenticationManager getAuthenticationManager() throws Exception {

		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.authenticationProvider(getDaoAuthprovider());

		super.configure(auth);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests()
		.antMatchers("/csv/**","/users/get", "/users/authenticate", "/users/save", "/otp/**")
		.permitAll()
		.antMatchers(HttpMethod.OPTIONS,"/**")
		.permitAll().and()
				.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and().sessionManagement()

				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		http.addFilterBefore(customJwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		http.csrf().disable();
		super.configure(http);
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {

		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(Arrays.asList("*"));

		configuration.setAllowedMethods(Arrays.asList("GET", "OPTIONS", "POST", "PUT", "DELETE"));

		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		source.registerCorsConfiguration("/**", configuration);

		return source;
	}

}