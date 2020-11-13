package br.com.murilo.luizalab.service.implementation;

import br.com.murilo.luizalab.dto.publisher.NoticePublisher;
import br.com.murilo.luizalab.queue.publisher.MagaluPublisher;
import br.com.murilo.luizalab.repository.NoticeRepository;
import br.com.murilo.luizalab.service.Scheduler;
import br.com.murilo.luizalab.types.MessageStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SchedulerImpl implements Scheduler {

    private final NoticeRepository noticeRepository;
    private final MagaluPublisher magaluPublisher;
    private final ConversionService conversionService;

    @Autowired
    public SchedulerImpl(final NoticeRepository noticeRepository, final MagaluPublisher magaluPublisher, final ConversionService conversionService) {
        this.noticeRepository = noticeRepository;
        this.magaluPublisher = magaluPublisher;
        this.conversionService = conversionService;
    }

    @Scheduled(zone = "GMT-3:00", fixedRate = 60000)
    @Override
    public void publishMessage() {
        final var now = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
        final var notices = this.noticeRepository.findNoticeBySendDateBetweenAndStatus(now, now.plusMinutes(1), MessageStatus.SCHEDULED);
        final var noticesPublisher = notices.stream().map(notice -> this.conversionService.convert(notice, NoticePublisher.class)).collect(Collectors.toList());
        noticesPublisher.forEach(noticePublisher -> this.magaluPublisher.publishNotice(noticePublisher));
    }
}
