package br.com.murilo.luizalab.facade.implementation;

import br.com.murilo.luizalab.dto.request.NoticeRequest;
import br.com.murilo.luizalab.dto.response.NoticeResponse;
import br.com.murilo.luizalab.facade.NoticeFacade;
import br.com.murilo.luizalab.model.Notice;
import br.com.murilo.luizalab.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class NoticeFacadeImpl implements NoticeFacade {

    private final NoticeService noticeService;
    private final ConversionService conversionService;

    @Autowired
    public NoticeFacadeImpl(final NoticeService noticeService, final ConversionService conversionService) {
        this.noticeService = noticeService;
        this.conversionService = conversionService;
    }

    @Override
    public NoticeResponse save(final NoticeRequest noticeRequest) {
        final var notice = convertToNotice(noticeRequest);
        final var createdNotice = this.noticeService.save(notice);
        return convertToNoticeResponse(createdNotice);
    }



    @Override
    public NoticeResponse findById(final UUID id) {
        final var notice = this.noticeService.findById(id);
        return convertToNoticeResponse(notice);
    }

    @Override
    public Page<NoticeResponse> findNoticeBySendDateBetween(final LocalDateTime startDate, final LocalDateTime endDate, final Pageable page) {
        final var notices = this.noticeService.findNoticeBySendDateBetween(startDate, endDate, page);
        return notices.map(this::convertToNoticeResponse);
    }

    @Override
    public NoticeResponse update(final UUID id, final NoticeRequest noticeRequest) {
        final var notice = convertToNotice(noticeRequest);
        final var updatedNotice = this.noticeService.update(id, notice);
        return convertToNoticeResponse(updatedNotice);
    }

    @Override
    public void delete(final UUID id) {
        this.noticeService.delete(id);
    }

    private Notice convertToNotice(final NoticeRequest noticeRequest) {
        return this.conversionService.convert(noticeRequest, Notice.class);
    }

    private NoticeResponse convertToNoticeResponse(final Notice notice) {
        return this.conversionService.convert(notice, NoticeResponse.class);
    }

}
