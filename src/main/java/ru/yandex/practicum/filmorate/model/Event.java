package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Positive;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Event {
    @Positive
    Long eventId;
    String eventType;
    String operation;
    Long timestamp;
    Long userId;
    Long entityId;

    public Map<String, Object> toMap() {
        Map<String, Object> eventDto = new HashMap<>();
        eventDto.put("event_id", eventId);
        eventDto.put("event_type", eventType);
        eventDto.put("operation", operation);
        eventDto.put("event_timestamp", timestamp);
        eventDto.put("user_id", userId);
        eventDto.put("entity_id", entityId);
        return eventDto;
    }
}
