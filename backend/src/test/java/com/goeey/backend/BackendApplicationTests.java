package com.goeey.backend;

import static org.assertj.core.api.Assertions.assertThat;

import com.goeey.backend.model.entity.Player;
import com.goeey.backend.model.entity.Room;
import com.goeey.backend.service.PlayerService;
import com.goeey.backend.service.RoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BackendApplicationTests {
    @Autowired
    private RoomService roomService;
    @Autowired
    private PlayerService playerService;
    @Test
    void contextLoads() {
        assertThat(roomService).isNotNull();
        assertThat(playerService).isNotNull();
    }

    @Test
    void testCreatePlayer() {
        assertThat(playerService.createPlayer("TestPlayer")).isNotNull();
    }

    @Test
    void testJoinRoom() {
        Room room = roomService.getRoom("TestRoom");
        if (room == null) {
            room = roomService.createRoom();
        }
        Player player = playerService.createPlayer("TestPlayer");
        room.addPlayer(player, 1);

        assertThat(room.hasPlayerById(player.getId())).isTrue();
    }
}
