package br.com.murilo.luizalab.repository;

import br.com.murilo.luizalab.model.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, UUID> {

    Page<Notice> findNoticeBySendDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable page);
}
