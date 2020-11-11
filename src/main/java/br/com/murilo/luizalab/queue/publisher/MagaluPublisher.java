package br.com.murilo.luizalab.queue.publisher;

import br.com.murilo.luizalab.vo.NoticeVO;

public interface MagaluPublisher {

    void publishNotice(NoticeVO noticeVO);
}
