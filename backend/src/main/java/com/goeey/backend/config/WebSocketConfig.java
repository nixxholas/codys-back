package com.goeey.backend.config;

import com.goeey.backend.handler.ClientMessageHandler;
import com.goeey.backend.handler.SocketHandler;
import com.goeey.backend.service.PlayerService;
import com.goeey.backend.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebSocketConfig {
    @Autowired
    private TaskScheduler taskScheduler;

    @Bean
    public ClientMessageHandler clientMessageHandler() {
        return new ClientMessageHandler(roomService(), playerService());
    }

    @Bean
    public RoomService roomService() {
        return new RoomService(taskScheduler);
    }

    @Bean
    public PlayerService playerService() {
        return new PlayerService();
    }

    @Bean
    public HandlerMapping handlerMapping() {
        Map<String, SocketHandler> map = new HashMap<>();
        map.put("/ws", new SocketHandler(clientMessageHandler(), roomService(), playerService()));

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(map);
        mapping.setOrder(-1); // before annotated controllers
        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
