package br.com.murilo.luizalab.queue.listener;

import org.springframework.amqp.core.Message;

public interface MagaluListener {

    void process(Message message);
}
