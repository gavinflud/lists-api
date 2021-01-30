package com.gavinflood.lists.api.config

import com.gavinflood.lists.api.auth.JwtAuthenticationEntryPoint
import com.gavinflood.lists.api.auth.JwtRequestFilter
import com.gavinflood.lists.api.service.AppUserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.util.Collections.singletonList

/**
 * HTTP security configuration for the api.
 *
 * The application should be stateless and use JWT for authentication.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig(

    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val jwtRequestFilter: JwtRequestFilter,
    private val userService: AppUserService,
    private val passwordEncoder: PasswordEncoder

) : WebSecurityConfigurerAdapter() {

    /**
     * Configuring [HttpSecurity] to have a stateless application that uses JWT for authentication.
     *
     * @param httpSecurity the [HttpSecurity] to modify
     */
    override fun configure(httpSecurity: HttpSecurity) {
        httpSecurity
            .cors().and()
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/api/authenticate/**").permitAll()
            .anyRequest().authenticated().and()
            .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)
    }

    /**
     * Configure the authentication manager to use the custom [AppUserService] and [BCryptPasswordEncoder].
     *
     * @param auth manager to configure
     */
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder)
    }

    /**
     * Expose the authentication manager as a bean.
     *
     * @return the default authentication manager
     */
    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    /**
     * Create the default CORS configuration source to allow only specific origins but all methods and headers.
     *
     * TODO: Restrict origins
     *
     * @return A source wrapper that provides a Cors configuration instance
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = singletonList(CorsConfiguration.ALL)
        configuration.allowedMethods = singletonList(CorsConfiguration.ALL)
        configuration.allowedHeaders = singletonList(CorsConfiguration.ALL)
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

}