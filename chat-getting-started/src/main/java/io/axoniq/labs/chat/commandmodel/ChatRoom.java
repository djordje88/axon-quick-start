package io.axoniq.labs.chat.commandmodel;

import io.axoniq.labs.chat.coreapi.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.HashSet;
import java.util.Set;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
public class ChatRoom {

    @AggregateIdentifier
    private String id;
    private String name;
    private Set<String> participants;


    public ChatRoom() {}

    @CommandHandler
    public ChatRoom(CreateRoomCommand command) {
        apply(new RoomCreatedEvent(command.getRoomId(),command.getName()));
    }

    @EventSourcingHandler
    public void on(RoomCreatedEvent event){
        this.id = event.getRoomId();
        this.name=event.getName();
        this.participants=new HashSet<>();
    }

    @CommandHandler
    public void handle(JoinRoomCommand command){
        if(!participants.contains(command.getParticipant())) {
            apply(new ParticipantJoinedRoomEvent(command.getParticipant(), command.getRoomId()));
        }
    }

    @EventSourcingHandler
    public void on(ParticipantJoinedRoomEvent event){
        participants.add(event.getParticipant());
    }

    @CommandHandler
    public void handle(LeaveRoomCommand command){
        if(participants.contains(command.getParticipant())) {
            apply(new ParticipantLeftRoomEvent(command.getParticipant(), command.getRoomId()));
        }
    }

    @EventSourcingHandler
    public void on(ParticipantLeftRoomEvent event){
        participants.remove(event.getParticipant());
    }

    @CommandHandler
    public void handle(PostMessageCommand command){
        if(participants.contains(command.getParticipant())) {
            apply(new MessagePostedEvent(command.getParticipant(), command.getRoomId(),command.getMessage()));
        }else{
            throw new IllegalStateException("Participant is not in the room");
        }
    }
}
