package br.com.murilo.luizalab.model;

import br.com.murilo.luizalab.types.MessageStatus;
import br.com.murilo.luizalab.types.MessageType;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity(name = "notice")
public class Notice {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false)
    private UUID id;

    @NonNull
    @Column(name = "send_date", nullable = false)
    private LocalDateTime sendDate;

    @NonNull
    @Column(name = "sender_id", nullable = false)
    private Integer senderId;

    @NonNull
    @Column(name = "recipient_id", nullable = false)
    private Integer recipientId;

    @NonNull
    @Column(name = "recipient", nullable = false)
    private String recipient;

    @NonNull
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @NonNull
    @Column(name = "message", nullable = false)
    private String message;

    @NonNull
    @Enumerated(EnumType.STRING)
    private MessageStatus status;
}
