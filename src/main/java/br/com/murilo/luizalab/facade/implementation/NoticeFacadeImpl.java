package br.com.murilo.luizalab.facade.implementation;

import br.com.murilo.luizalab.dto.request.NoticeRequest;
import br.com.murilo.luizalab.dto.response.NoticeResponse;
import br.com.murilo.luizalab.facade.NoticeFacade;
import br.com.murilo.luizalab.model.Notice;
import br.com.murilo.luizalab.queue.publisher.MagaluPublisher;
import br.com.murilo.luizalab.service.NoticeService;
import br.com.murilo.luizalab.dto.publisher.NoticePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Component
public class NoticeFacadeImpl implements NoticeFacade {

    private final NoticeService noticeService;
    private final ConversionService conversionService;
    private final MagaluPublisher magaluPublisher;

    @Autowired
    public NoticeFacadeImpl(final NoticeService noticeService, final ConversionService conversionService, final MagaluPublisher magaluPublisher) {
        this.noticeService = noticeService;
        this.conversionService = conversionService;
        this.magaluPublisher = magaluPublisher;
    }

    @Override
    public NoticeResponse save(final NoticeRequest noticeRequest) {
        final var notice = convertToNotice(noticeRequest);
        final var createdNotice = this.noticeService.save(notice);
        noticeCanBePublished(createdNotice);
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
        noticeCanBePublished(updatedNotice);
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

    private void noticeCanBePublished(final Notice notice) {
        final var now = LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).withSecond(0);
        final var sendDate = notice.getSendDate();
        if(sendDate.isAfter(now) && sendDate.isBefore(now.plusMinutes(1))) {
            final var noticeVO = this.conversionService.convert(notice, NoticePublisher.class);
            magaluPublisher.publishNotice(noticeVO);
        }
    }

}
