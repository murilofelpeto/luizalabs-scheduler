package br.com.murilo.luizalab.repository;

import br.com.murilo.luizalab.model.Notice;
import br.com.murilo.luizalab.types.MessageStatus;
import br.com.murilo.luizalab.types.MessageType;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;

@RunWith(SpringRunner.class)
@DataJpaTest
class NoticeRepositoryTest {

    @Autowired
    private NoticeRepository noticeRepository;

    private Notice notice;

    @BeforeEach
    void setUp() {
        final Faker faker = new Faker();
        final LocalDateTime sendDate = LocalDateTime.now().plusMinutes(5);
        final Integer senderId = faker.number().randomDigitNotZero();
        final Integer recipientId = faker.number().randomDigitNotZero();
        final String recipient = faker.phoneNumber().phoneNumber();
        final MessageType messageType = MessageType.WHATSAPP;
        final String message = faker.elderScrolls().dragon();
        final MessageStatus status = MessageStatus.SCHEDULED;
        notice = this.noticeRepository.save(Notice.of(null, sendDate, senderId, recipientId, recipient, messageType, message, status));
    }

    @Test
    void should_find_notice_by_sendDate_between() {
        final var startDateTime = LocalDateTime.now();
        final var endDateTime = LocalDateTime.now().plusHours(1);

        final var found = noticeRepository.findNoticeBySendDateBetween(startDateTime, endDateTime, Pageable.unpaged());

        assertThat(found.getContent(), hasItem(notice));
    }

    @Test
    void should_not_find_notice_by_by_sendDate_between(){
        final var startDateTime = LocalDateTime.now().plusHours(1);
        final var endDateTime = LocalDateTime.now().plusHours(2);

        final var found = noticeRepository.findNoticeBySendDateBetween(startDateTime, endDateTime, Pageable.unpaged());

        assertThat(found.getContent(), empty());
    }

    @Test
    void should_find_notice_by_sendDate_between_and_status() {
        final var startDateTime = LocalDateTime.now();
        final var endDateTime = LocalDateTime.now().plusHours(1);
        final var status = MessageStatus.SCHEDULED;

        final var found = noticeRepository.findNoticeBySendDateBetweenAndStatus(startDateTime, endDateTime, status);

        assertThat(found, hasItem(notice));
    }

    @Test
    void should_not_find_notice_by_sendDate_between_and_status() {
        final var startDateTime = LocalDateTime.now();
        final var endDateTime = LocalDateTime.now().plusHours(1);
        final var status = MessageStatus.SENT;

        final var found = noticeRepository.findNoticeBySendDateBetweenAndStatus(startDateTime, endDateTime, status);

        assertThat(found, empty());
    }
}