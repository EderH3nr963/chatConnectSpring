package com.example.chatConnectSpring.chat.infrastructure.adapters.out.events;

import com.example.chatConnectSpring.chat.domain.ports.out.EventPublisherPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SpringEventPublisherAdapter implements EventPublisherPort {
    private final ApplicationEventPublisher publisher;
    
    public SpringEventPublisherAdapter(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }
    
    @Override
    public void publishEvent(Object event) {
        publisher.publishEvent(event);
    }
}
