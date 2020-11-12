package br.com.murilo.luizalab.dto.listener;

import br.com.murilo.luizalab.types.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@AllArgsConstructor(staticName = "of")
@Getter
@EqualsAndHashCode
@ToString
public class NoticeListener {

    private UUID id;
    private MessageStatus status;
}
