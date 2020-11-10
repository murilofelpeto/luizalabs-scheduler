package br.com.murilo.luizalab.service;

import br.com.murilo.luizalab.model.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface NoticeService {

    Notice save(Notice notice);
    Notice findById(UUID id);
    Page<Notice> findNoticeBySendDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable page);
    Notice update(UUID id, Notice notice);
    void delete(UUID id);
}
