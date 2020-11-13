package br.com.murilo.luizalab.queue.publisher.implementation;

import br.com.murilo.luizalab.dto.publisher.NoticePublisher;
import br.com.murilo.luizalab.queue.publisher.MagaluPublisher;
import br.com.murilo.luizalab.utils.JsonConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static br.com.murilo.luizalab.configuration.RabbitMQListenerConfiguration.ROUTING_KEY_SENDER;
import static br.com.murilo.luizalab.configuration.RabbitMQSenderConfiguration.EXCHANGE_SENDER_NAME;

@Component
@Slf4j
public class MagaluPublisherImpl implements MagaluPublisher {

    private final RabbitTemplate magaluTemplate;

    @Autowired
    public MagaluPublisherImpl(final RabbitTemplate magaluTemplate) {
        this.magaluTemplate = magaluTemplate;
    }

    @Override
    public void publishNotice(final NoticePublisher noticePublisher) {
        log.info("Publishing message with id: {}", noticePublisher.getId());
        magaluTemplate.convertAndSend(EXCHANGE_SENDER_NAME, ROUTING_KEY_SENDER, JsonConverter.convert(noticePublisher));
        log.info("Published successfully message with id: {}", noticePublisher.getId());
    }
}
