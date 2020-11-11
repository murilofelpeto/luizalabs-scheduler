package br.com.murilo.luizalab.converters;

import br.com.murilo.luizalab.model.Notice;
import br.com.murilo.luizalab.vo.NoticeVO;
import org.springframework.core.convert.converter.Converter;

public class NoticeToNoticeVOConverter implements Converter<Notice, NoticeVO> {

    @Override
    public NoticeVO convert(final Notice notice) {
        return NoticeVO.of(notice.getId(),
                notice.getSendDate(),
                notice.getRecipient(),
                notice.getMessage(),
                notice.getMessageType(),
                notice.getStatus());
    }
}
