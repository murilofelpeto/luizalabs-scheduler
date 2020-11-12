package br.com.murilo.luizalab.queue.publisher;

import br.com.murilo.luizalab.dto.publisher.NoticePublisher;

public interface MagaluPublisher {

    void publishNotice(NoticePublisher noticeVO);
}
