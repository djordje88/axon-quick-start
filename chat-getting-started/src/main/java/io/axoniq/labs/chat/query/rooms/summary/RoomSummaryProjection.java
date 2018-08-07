package io.axoniq.labs.chat.query.rooms.summary;

import io.axoniq.labs.chat.coreapi.CreateRoomCommand;
import io.axoniq.labs.chat.coreapi.ParticipantJoinedRoomEvent;
import io.axoniq.labs.chat.coreapi.ParticipantLeftRoomEvent;
import io.axoniq.labs.chat.coreapi.RoomCreatedEvent;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomSummaryProjection {

    private final RoomSummaryRepository roomSummaryRepository;

    public RoomSummaryProjection(RoomSummaryRepository roomSummaryRepository) {
        this.roomSummaryRepository = roomSummaryRepository;
    }

    @GetMapping
    public List<RoomSummary> listRooms() {
        return roomSummaryRepository.findAll();
    }

    @EventHandler
    public void handle(RoomCreatedEvent event){
        roomSummaryRepository.save(new RoomSummary(event.getRoomId(),event.getName()));
    }

    @EventHandler
    public void handle(ParticipantJoinedRoomEvent event){
        RoomSummary room = roomSummaryRepository.findOne(event.getRoomId());
        room.addParticipant();
        roomSummaryRepository.save(room);
    }

    @EventHandler
    public void handle(ParticipantLeftRoomEvent event){
        RoomSummary room = roomSummaryRepository.findOne(event.getRoomId());
        room.removeParticipant();
        roomSummaryRepository.save(room);
    }

}
