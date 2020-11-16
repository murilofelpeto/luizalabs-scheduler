package br.com.murilo.luizalab.service.implementation;

import br.com.murilo.luizalab.exception.InvalidDateException;
import br.com.murilo.luizalab.exception.ResourceNotFoundException;
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

    private static final String INVALID_DATE_MESSAGE = "A data precisa ser maior que agora";
    private static final String MESSAGE_NOT_FOUND = "Mensagem não encontrada";

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
        throw new InvalidDateException(INVALID_DATE_MESSAGE);
    }

    @Override
    public Notice findById(final UUID id) {
        return this.noticeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(MESSAGE_NOT_FOUND));
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
            throw new ResourceNotFoundException(MESSAGE_NOT_FOUND);
        }
        throw new InvalidDateException(INVALID_DATE_MESSAGE);
    }

    @Override
    public void delete(final UUID id) {
        if(noticeExists(id)){
            this.noticeRepository.deleteById(id);
            return;
        }
        throw new ResourceNotFoundException(MESSAGE_NOT_FOUND);
    }

    private boolean noticeExists(final UUID id) {
        return this.noticeRepository.findById(id).isPresent();
    }

    private boolean isValidDate(final LocalDateTime sendDate) {
        final var now = LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).withSecond(0);
        return sendDate.isAfter(now);
    }
}
