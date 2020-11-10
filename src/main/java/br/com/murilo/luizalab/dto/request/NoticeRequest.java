package br.com.murilo.luizalab.dto.request;

import br.com.murilo.luizalab.types.MessageStatus;
import br.com.murilo.luizalab.types.MessageType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class NoticeRequest {

    private UUID id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sendDate;
    private Integer senderId;
    private Integer recipientId;
    private String recipient;
    private MessageType messageType;
    private String message;
    private MessageStatus status;
}
