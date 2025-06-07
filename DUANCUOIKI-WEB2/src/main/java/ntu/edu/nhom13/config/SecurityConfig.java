package ntu.edu.nhom13.config;

import java.io.IOException;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ntu.edu.nhom13.entity.Account;
import ntu.edu.nhom13.services.AccountService;
import ntu.edu.nhom13.services.AdminService;
import ntu.edu.nhom13.services.CustomUserDetailsService;
import ntu.edu.nhom13.services.ScientistService;



@Configuration
@EnableWebSecurity
public class SecurityConfig {
//	@Value("${jwt.signerKey}")
//	private String signerKey;
//	
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//       http.authorizeHttpRequests(request->
//       		request.requestMatchers(HttpMethod.GET,"/loginTemplate").permitAll()
//       			.anyRequest().authenticated()
//       );
////       http.oauth2ResourceServer(oauth2->oauth2.jwt(jwtConfigure->jwtConfigure.decoder(null)));
////       http.csrf(AbstractHttpConfigurer::disable);
//        return http.build();
//    }
//    
//    @Bean
//    JwtDecoder jwtDecoder() {
//    	SecretKeySpec secretKeySpec=new SecretKeySpec(signerKey.getBytes(), "HS512");
//    	return NimbusJwtDecoder.withSecretKey(secretKeySpec)
//    			.macAlgorithm(MacAlgorithm.HS512)
//    			.build();
//    }
	@Autowired
    private CustomUserDetailsService customUserDetailsService;

	private final String[] ALL_PEOPLE= {"/user/**"};
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/","/scientists/list","/statistics").permitAll()
                .requestMatchers("/admin/**").hasRole("Admin")
                .requestMatchers("/scientist/**").hasRole("Scientist")
                .requestMatchers(ALL_PEOPLE).hasAnyRole("Scientist", "Admin")
                .anyRequest().authenticated()
            )
	            .formLogin(form -> form
	                .loginPage("/loginTemplate") 
	                .loginProcessingUrl("/login") 
	                .defaultSuccessUrl("/", true)
	                .failureUrl("/user/loginTemplateFailed")
	                .permitAll()
	            )
            .logout(logout -> logout.permitAll())
            .userDetailsService(customUserDetailsService)
            .build();
    }

}