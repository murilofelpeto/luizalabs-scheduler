package br.com.murilo.luizalab.converters;

import br.com.murilo.luizalab.model.Notice;
import br.com.murilo.luizalab.dto.publisher.NoticePublisher;
import org.springframework.core.convert.converter.Converter;

public class NoticeToNoticePublisherConverter implements Converter<Notice, NoticePublisher> {

    @Override
    public NoticePublisher convert(final Notice notice) {
        return NoticePublisher.of(notice.getId(),
                notice.getSendDate(),
                notice.getRecipient(),
                notice.getMessage(),
                notice.getMessageType(),
                notice.getStatus());
    }
}
