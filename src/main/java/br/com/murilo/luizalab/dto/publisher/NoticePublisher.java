package br.com.murilo.luizalab.dto.publisher;

import br.com.murilo.luizalab.types.MessageStatus;
import br.com.murilo.luizalab.types.MessageType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor(staticName = "of")
@Getter
@EqualsAndHashCode
@ToString
public class NoticePublisher {

    private UUID id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sendDate;
    private String recipient;
    private String message;
    private MessageType messageType;
    private MessageStatus status;
}
