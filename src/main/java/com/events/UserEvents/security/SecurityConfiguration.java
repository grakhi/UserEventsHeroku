package com.events.UserEvents.security;

/* 

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.context.annotation.Bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
 

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
 
	
	 
	
	 @Autowired
	 private DataSource securityDataSource;
	
	 
	
	 private final String USERS_QUERY = "select email , password, 1 from user where email = ? ";
	 
	 private final String ROLES_QUERY = "select email , 'USER' from user where email = ? ";
	 
	//Clear browser cache after lgoin , authentication does not work
	 
	  
	    @Override
	    protected void configure(HttpSecurity http) throws Exception  {
	    	
	    	 
	    	
	    	
	    	
	    	
	    	 http.authorizeRequests()
	    	   .antMatchers("/").permitAll()
	    	   .antMatchers("/login").permitAll()
	    	   .antMatchers("/signup").permitAll()
	    	   .antMatchers("/home/**").hasAuthority("ADMIN").anyRequest()
	    	   .authenticated().and().csrf().disable()
	    	   .formLogin().loginPage("/login").failureUrl("/login?error=true")
	    	   .defaultSuccessUrl("/events", true)
	    	   .usernameParameter("email")
	    	   .passwordParameter("password")
	    	   .and().logout()
	    	   .logoutUrl("/logout")
	    	   .logoutSuccessUrl("/login")
	    	   .and().rememberMe()
	    	   .tokenRepository(persistentTokenRepository())
	    	   .tokenValiditySeconds(60*60)
	    	   .and().exceptionHandling().accessDeniedPage("/access_denied");
	    	
	    	
	    	
	    	
	    }
	    
	    public PersistentTokenRepository persistentTokenRepository() {
	    	
	    	JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
	    	db.setDataSource(securityDataSource);
	    	
	    	return db;
	    }
	
	
	    @Override
	    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	    	
	    	
	    	auth.jdbcAuthentication().usersByUsernameQuery(USERS_QUERY)
	    	.authoritiesByUsernameQuery(ROLES_QUERY)
			.dataSource(securityDataSource)
			.passwordEncoder(NoOpPasswordEncoder.getInstance());   //phase 2 encrypt the password user enters	    	
	    	
	    	
	    }
 
}




 */