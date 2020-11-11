package br.com.murilo.luizalab.queue.listener.implementation;

import br.com.murilo.luizalab.queue.listener.MagaluListener;
import br.com.murilo.luizalab.service.NoticeService;
import br.com.murilo.luizalab.utils.JsonConverter;
import br.com.murilo.luizalab.vo.NoticeVO;
import com.tradeshift.amqp.annotation.EnableRabbitRetryAndDlq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

@Component
@Slf4j
public class MagaluListenerImpl implements MagaluListener {

    private final NoticeService noticeService;

    @Autowired
    public MagaluListenerImpl(final NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @RabbitListener(containerFactory = "luizalab-listener", queues = "${spring.rabbitmq.custom.luizalab-listener.queue}")
    @EnableRabbitRetryAndDlq(event = "luizalab-listener")
    @Override
    public void process(final Message message) {
        log.info("Retrieving message");
        final String messageStr = new String(message.getBody(), Charset.defaultCharset());
        final var noticeVO = (NoticeVO)JsonConverter.convert(messageStr, NoticeVO.class);

        final var notice = this.noticeService.findById(noticeVO.getId());
        notice.setStatus(noticeVO.getStatus());
        this.noticeService.update(notice.getId(), notice);
        log.info("Message with id: {} updated status to -> {}", notice.getId(), notice.getStatus());
    }
}
