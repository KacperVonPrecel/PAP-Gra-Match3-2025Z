package pap.project.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

//XXX https://docs.spring.io/spring-security/reference/servlet/integrations/websocket.html
//@Configuration
//@EnableWebSocketSecurity
public class WebSocketSecurityConfig
{
//    @Bean
//    @NonNull AuthorizationManager<Message<?>> messageAuthorizationManager(@NonNull MessageMatcherDelegatingAuthorizationManager.Builder messages) {
////        messages.simpDestMatchers("/user/**").hasRole("USER")
////XXX
//        return messages.build();
//    }
}
