package br.com.murilo.luizalab.service.implementation;

import br.com.murilo.luizalab.model.Notice;
import br.com.murilo.luizalab.repository.NoticeRepository;
import br.com.murilo.luizalab.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    @Autowired
    public NoticeServiceImpl(final NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    @Override
    public Notice save(final Notice notice) {
        if(isValidDate(notice.getSendDate())) {
            return this.noticeRepository.save(notice);
        }
        throw new RuntimeException("A data precisa ser maior que agora");
    }

    @Override
    public Notice findById(final UUID id) {
        return this.noticeRepository.findById(id).orElseThrow(() -> new RuntimeException("Mensagem não encontrada"));
    }

    @Override
    public Page<Notice> findNoticeBySendDateBetween(final LocalDateTime startDate, final LocalDateTime endDate, final Pageable page) {
        return this.noticeRepository.findNoticeBySendDateBetween(startDate, endDate, page);
    }

    @Override
    public Notice update(final UUID id, final Notice notice) {
        if(isValidDate(notice.getSendDate())){
            if(noticeExists(id)) {
                notice.setId(id);
                return this.noticeRepository.save(notice);
            }
            throw new RuntimeException("Mensagem não encontrada!");
        }
        throw new RuntimeException("A data precisa ser maior que agora");
    }

    @Override
    public void delete(final UUID id) {
        if(noticeExists(id)){
            this.noticeRepository.deleteById(id);
            return;
        }
        throw new RuntimeException("Mensagem não encontrada");
    }

    private boolean noticeExists(final UUID id) {
        return this.noticeRepository.findById(id).isPresent();
    }

    private boolean isValidDate(final LocalDateTime sendDate) {
        final var now = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
        return sendDate.isAfter(now);
    }
}
