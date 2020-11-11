package br.com.murilo.luizalab.controller;

import br.com.murilo.luizalab.dto.request.NoticeRequest;
import br.com.murilo.luizalab.dto.response.NoticeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public interface NoticeController {

    ResponseEntity<NoticeResponse> save(NoticeRequest noticeRequest);
    ResponseEntity<NoticeResponse> findById(UUID id);
    ResponseEntity<Page<NoticeResponse>>findNoticeBySendDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable page);
    ResponseEntity<NoticeResponse> update(UUID id, NoticeRequest noticeRequest);
    ResponseEntity<Void> delete(UUID id);
}
