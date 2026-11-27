
package com.manya.salesforce_crud;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            ClientRegistrationRepository clientRegistrationRepository)
            throws Exception {

        DefaultOAuth2AuthorizationRequestResolver delegate =
                new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository,
                        "/oauth2/authorization"
                );

        OAuth2AuthorizationRequestResolver resolver =
                new OAuth2AuthorizationRequestResolver() {

            @Override
            public OAuth2AuthorizationRequest resolve(
                    HttpServletRequest request) {
                return fixRedirectUri(delegate.resolve(request));
            }

            @Override
            public OAuth2AuthorizationRequest resolve(
                    HttpServletRequest request, String clientRegistrationId) {
                return fixRedirectUri(
                        delegate.resolve(request, clientRegistrationId)
                );
            }

            private OAuth2AuthorizationRequest fixRedirectUri(
                    OAuth2AuthorizationRequest request) {

                if (request == null) {
                    return null;
                }

                String uri = request.getAuthorizationRequestUri();

                uri = uri.replace(
                    "redirect_uri=https://salesforce-crud-61no.onrender.com/login/oauth2/code/salesforce",
                    "redirect_uri=https%3A%2F%2Fsalesforce-crud-61no.onrender.com%2Flogin%2Foauth2%2Fcode%2Fsalesforce"
                );

                return OAuth2AuthorizationRequest.from(request)
                        .authorizationRequestUri(uri)
                        .build();
            }
        };

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/index.html", "/favicon.ico"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
            )
            .oauth2Login(oauth -> oauth
                .authorizationEndpoint(endpoint ->
                    endpoint.authorizationRequestResolver(resolver)
                )
                .defaultSuccessUrl("/", true)
            );

        return http.build();
    }
}
