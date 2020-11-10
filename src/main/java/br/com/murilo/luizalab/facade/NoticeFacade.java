package br.com.murilo.luizalab.facade;

import br.com.murilo.luizalab.dto.request.NoticeRequest;
import br.com.murilo.luizalab.dto.response.NoticeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface NoticeFacade {

    NoticeResponse save(NoticeRequest noticeRequest);
    NoticeResponse findById(UUID id);
    Page<NoticeResponse> findNoticeBySendDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable page);
    NoticeResponse update(UUID id, NoticeRequest noticeRequest);
    void delete(UUID id);
}
