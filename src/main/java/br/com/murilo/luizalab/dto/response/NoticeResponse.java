package br.com.murilo.luizalab.dto.response;

import br.com.murilo.luizalab.types.MessageStatus;
import br.com.murilo.luizalab.types.MessageType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class NoticeResponse extends RepresentationModel<NoticeResponse> {

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
