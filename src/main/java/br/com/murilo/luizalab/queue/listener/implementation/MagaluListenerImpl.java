package br.com.murilo.luizalab.queue.listener.implementation;

import static br.com.murilo.luizalab.configuration.RabbitMQListenerConfiguration.QUEUE_LISTENER;
import br.com.murilo.luizalab.dto.listener.NoticeListener;
import br.com.murilo.luizalab.queue.listener.MagaluListener;
import br.com.murilo.luizalab.repository.NoticeRepository;
import br.com.murilo.luizalab.utils.JsonConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

@Component
@Slf4j
public class MagaluListenerImpl implements MagaluListener {

    private final NoticeRepository noticeRepository;

    @Autowired
    public MagaluListenerImpl(final NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    @RabbitListener(queues = QUEUE_LISTENER)
    @Override
    public void onMessage(final Message message) {
        log.info("Retrieving message");
        final String messageStr = new String(message.getBody(), Charset.defaultCharset());
        final var noticeListener = (NoticeListener) JsonConverter.convert(messageStr, NoticeListener.class);

        final var notice = this.noticeRepository.findById(noticeListener.getId()).orElseThrow(() -> new RuntimeException("Mensagem não encontrada"));
        notice.setStatus(noticeListener.getStatus());
        notice.setId((noticeListener.getId()));
        this.noticeRepository.save(notice);
        log.info("Message with id: {} updated status to -> {}", notice.getId(), notice.getStatus());
    }
}
