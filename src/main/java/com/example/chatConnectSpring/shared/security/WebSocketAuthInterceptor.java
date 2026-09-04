package com.example.chatConnectSpring.shared.security;

import com.clerk.backend_api.helpers.security.AuthenticateRequest;
import com.clerk.backend_api.helpers.security.models.AuthenticateRequestOptions;
import com.clerk.backend_api.helpers.security.models.RequestState;
import com.example.chatConnectSpring.user.domain.model.User;
import com.example.chatConnectSpring.user.domain.port.in.UserIdentityProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    
    @Value("${CLERK_SECRET_KEY}")
    private String clerkSecret;
    
    private final UserIdentityProvider userIdentityProvider;
    
    public WebSocketAuthInterceptor(
            UserIdentityProvider userIdentityProvider
    ) {
        this.userIdentityProvider = userIdentityProvider;
    }
    
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }
        
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Map<String, List<String>> headers = accessor.toNativeHeaderMap();
            
            RequestState requestState;
            try {
                requestState = AuthenticateRequest.authenticateRequest(
                        headers,
                        AuthenticateRequestOptions.secretKey(clerkSecret).build()
                );
            } catch (Exception e) {
                throw new BadCredentialsException("Falha ao processar token de autenticação WebSocket", e);
            }
            
            if (!requestState.isSignedIn()) {
                throw new BadCredentialsException("Sessão inválida ou token do Clerk expirado");
            }
            
            String clerkId = requestState.claims()
                    .map(claims -> (String) claims.get("user_id"))
                    .orElseThrow(() -> new BadCredentialsException("Claim 'user_id' ausente no token do Clerk"));
            
            User user = userIdentityProvider.findByClerkUserId(clerkId);
            
            if (user == null) {
                throw new UsernameNotFoundException("Usuário não encontrado para o Clerk ID: " + clerkId);
            }
            
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken
                    .authenticated(userDetails, null, userDetails.getAuthorities());
            
            accessor.setUser(authentication);
        }
        
        return message;
    }
}