package br.com.murilo.luizalab.queue.publisher.implementation;

import br.com.murilo.luizalab.queue.publisher.MagaluPublisher;
import br.com.murilo.luizalab.vo.NoticeVO;
import com.tradeshift.amqp.rabbit.handlers.RabbitTemplateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MagaluPublisherImpl implements MagaluPublisher {

    @Value("${spring.rabbitmq.custom.luizalab-sender.exchange}")
    private String exchangeLuizaLabSender;

    @Value("${spring.rabbitmq.custom.luizalab-sender.queueRoutingKey}")
    private String routingKeyLuizaLabSender;

    private final RabbitTemplateHandler magaluTemplateHandler;

    @Autowired
    public MagaluPublisherImpl(final RabbitTemplateHandler magaluTemplateHandler) {
        this.magaluTemplateHandler = magaluTemplateHandler;
    }

    @Override
    public void publishNotice(final NoticeVO noticeVO) {
        log.info("Publishing message with id: {}", noticeVO.getId());
        magaluTemplateHandler.getRabbitTemplate("luizalab-sender").convertAndSend(exchangeLuizaLabSender, routingKeyLuizaLabSender, noticeVO);
        log.info("Published successfully message with id: {}", noticeVO.getId());
    }
}
