package br.com.murilo.luizalab.converters;

import br.com.murilo.luizalab.dto.response.NoticeResponse;
import br.com.murilo.luizalab.model.Notice;
import org.springframework.core.convert.converter.Converter;

public class NoticeToNoticeResponseConverter implements Converter<Notice, NoticeResponse> {

    @Override
    public NoticeResponse convert(final Notice notice) {
        return NoticeResponse.of(notice.getId(),
                notice.getSendDate(),
                notice.getSenderId(),
                notice.getRecipientId(),
                notice.getRecipient(),
                notice.getMessageType(),
                notice.getMessage(),
                notice.getStatus());
    }
}
