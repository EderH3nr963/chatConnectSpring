package com.example.chatConnectSpring.chat.domain.ports.out;

import com.example.chatConnectSpring.chat.application.events.MessageSentEvent;

public interface EventPublisherPort {
    void publishEvent(Object event);
}
