package br.com.murilo.luizalab.converters;

import br.com.murilo.luizalab.dto.request.NoticeRequest;
import br.com.murilo.luizalab.model.Notice;
import org.springframework.core.convert.converter.Converter;

public class NoticeRequestToNoticeConverter implements Converter<NoticeRequest, Notice> {

    @Override
    public Notice convert(final NoticeRequest noticeRequest) {
        return Notice.of(noticeRequest.getId(),
                noticeRequest.getSendDate(),
                noticeRequest.getSenderId(),
                noticeRequest.getRecipientId(),
                noticeRequest.getRecipient(),
                noticeRequest.getMessageType(),
                noticeRequest.getMessage(),
                noticeRequest.getStatus());
    }
}
