package dma.restconnexion.jwtsecurity.config;

import dma.restconnexion.jwtsecurity.security.JwtAuthenticationProvider;
import dma.restconnexion.jwtsecurity.security.JwtSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import dma.restconnexion.jwtsecurity.security.JwtAuthenticationEntryPoint;
import dma.restconnexion.jwtsecurity.security.JwtAuthenticationTokenFilter;

import javax.servlet.Filter;
import java.util.Collections;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtAuthenticationProvider authenticationProvider;
	@Autowired
	private JwtAuthenticationEntryPoint entryPoint;
	
	@Bean
	public AuthenticationManager authenticationManager()
	{
		return new ProviderManager(Collections.singletonList(authenticationProvider));
	}
	
	public JwtAuthenticationTokenFilter authenticationTokenFilter()
	{
		JwtAuthenticationTokenFilter filter = new JwtAuthenticationTokenFilter();
		filter.setAuthenticationManager(authenticationManager());
		filter.setAuthenticationSuccessHandler(new JwtSuccessHandler());
		
		return filter;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
				.exceptionHandling().authenticationEntryPoint(entryPoint).and()
				
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

				.authorizeRequests()
				.antMatchers("/").permitAll()
				.antMatchers("/auth/**").permitAll() //  /auth/login
				.anyRequest().authenticated();
		
		http.addFilterBefore(authenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class); //
		http.headers().cacheControl();
	}
	
	//	@Autowired
//	private JwtAuthenticationEntryPoint unauthorizedHandler;
//
//	// Custom JWT based jwtsecurity filter
//	@Autowired
//	JwtAuthorizationTokenFilter authenticationTokenFilter;
//
//	@Value("${jwt.header}")
//	private String tokenHeader;
//
//	@Value("${jwt.route.authentication.path}")
//	private String authenticationPath;
//
//	@Bean
//	public PasswordEncoder passwordEncoderBean() {
//		return new BCryptPasswordEncoder();
//	}
//
//	@Bean
//	@Override
//	public AuthenticationManager authenticationManagerBean() throws Exception {
//		return super.authenticationManagerBean();
//	}
//
//	@Override
//	protected void configure(HttpSecurity httpSecurity) throws Exception {
//		httpSecurity
//				// we don't need CSRF because our token is invulnerable
//				.csrf().disable()
//
//				.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
//
//				// don't create session
//				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and() //a voir dans la java doc.
//
//				.authorizeRequests()
//
//				// Un-secure H2 Database
//				//.antMatchers("/h2-console/**/**").permitAll()
//
//				.antMatchers("/auth/**").permitAll()
//				.anyRequest().authenticated();
//
//		httpSecurity
//				.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
//
//		// disable page caching
//		httpSecurity
//				.headers()
//				.frameOptions().sameOrigin()  // required to set for H2 else H2 Console will be blank.
//				.cacheControl();
//	}
//
//	@Override
//	public void configure(WebSecurity web) throws Exception {
//		// AuthenticationTokenFilter will ignore the below paths
//		web
//				.ignoring()
//				.antMatchers(
//						HttpMethod.POST,
//						authenticationPath
//				)
//
//				// allow anonymous resource requests
//				.and()
//				.ignoring()
//				.antMatchers(
//						HttpMethod.GET,
//						"/",
//						"/*.html",
//						"/favicon.ico",
//						"/**/*.html",
//						"/**/*.css",
//						"/**/*.js"
//				);
//	}
}
