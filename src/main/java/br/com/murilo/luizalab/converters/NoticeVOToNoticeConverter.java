package br.com.murilo.luizalab.converters;

import br.com.murilo.luizalab.model.Notice;
import br.com.murilo.luizalab.vo.NoticeVO;
import org.springframework.core.convert.converter.Converter;

public class NoticeVOToNoticeConverter implements Converter<NoticeVO, Notice> {

    @Override
    public Notice convert(final NoticeVO noticeVO) {
        return null;
    }
}
