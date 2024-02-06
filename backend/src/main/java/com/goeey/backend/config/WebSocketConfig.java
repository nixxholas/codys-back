package com.goeey.backend.config;

import com.goeey.backend.handler.SocketHandler;
import com.goeey.backend.service.PlayerService;
import com.goeey.backend.service.RoomService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebSocketConfig {
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5); // Adjust pool size as necessary
        scheduler.setThreadNamePrefix("scheduled-task-");
        scheduler.initialize();
        return scheduler;
    }

    @Bean
    public RoomService roomService() {
        return new RoomService(taskScheduler());
    }

    @Bean
    public PlayerService playerService() {
        return new PlayerService();
    }

    @Bean
    public HandlerMapping handlerMapping() {
        Map<String, SocketHandler> map = new HashMap<>();
        map.put("/ws", new SocketHandler(roomService(), playerService()));

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
