package br.com.murilo.luizalab.service.implementation;

import br.com.murilo.luizalab.exception.ResourceNotFoundException;
import br.com.murilo.luizalab.model.Notice;
import br.com.murilo.luizalab.repository.NoticeRepository;
import br.com.murilo.luizalab.service.NoticeService;
import br.com.murilo.luizalab.types.MessageStatus;
import br.com.murilo.luizalab.types.MessageType;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
class NoticeServiceImplTest {

    @Mock
    private NoticeRepository noticeRepository;
    private NoticeService noticeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        noticeService = new NoticeServiceImpl(noticeRepository);
    }

    @Test
    void should_save_notice() {
        final var noticeToSave = createNotice(null);
        final var notice = createNotice(UUID.randomUUID());

        when(noticeRepository.save(noticeToSave)).thenReturn(notice);
        final var saved = noticeService.save(noticeToSave);

        assertThat(saved, equalTo(notice));
    }

    @Test
    void should_not_save_notice_because_of_invalid_date() {
        final var invalidNotice = createNotice(null, LocalDateTime.now().minusDays(1));
        final var exception = assertThrows(RuntimeException.class, () -> {
            noticeService.save(invalidNotice);
        });

        assertThat(exception.getMessage(), equalTo("A data precisa ser maior que agora"));
    }

    @Test
    void should_find_notice_by_id() {
        final var notice = createNotice(UUID.randomUUID());
        final var id = notice.getId();

        when(noticeRepository.findById(id)).thenReturn(Optional.of(notice));
        final var found = noticeService.findById(id);

        assertThat(found, equalTo(notice));
    }

    @Test
    void should_not_find_notice_because_of_id_not_found() {
        final var exception = assertThrows(RuntimeException.class, () -> {
            noticeService.findById(UUID.randomUUID());
        });

        assertThat(exception.getMessage(), equalTo("Mensagem não encontrada"));
    }

    @Test
    void should_find_notice_by_sendDate_between() {
        final var startDateTime = LocalDateTime.now();
        final var endDateTime = LocalDateTime.now().plusHours(1);
        final var notice = createNotice(UUID.randomUUID());
        final var notices = new PageImpl<Notice>(Arrays.asList(notice));

        when(noticeRepository.findNoticeBySendDateBetween(startDateTime, endDateTime, Pageable.unpaged())).thenReturn(notices);
        final var found = noticeService.findNoticeBySendDateBetween(startDateTime, endDateTime, Pageable.unpaged());

        assertThat(found.getContent(), hasItem(notice));
    }

    @Test
    void should_update_notice() {
        final var id = UUID.randomUUID();
        final var savedNotice = createNotice(id);

        final var noticeToUpdate = savedNotice;
        noticeToUpdate.setMessage("Teste de update");

        when(noticeRepository.findById(id)).thenReturn(Optional.of(savedNotice));
        when(noticeRepository.save(noticeToUpdate)).thenReturn(noticeToUpdate);
        final var updated = noticeService.update(id, noticeToUpdate);

        assertThat(noticeToUpdate, equalTo(updated));
    }

    @Test
    void should_not_update_notice_because_of_invalid_date() {
        final var id = UUID.randomUUID();
        final var invalidNotice = createNotice(id, LocalDateTime.now().minusDays(1));
        final var exception = assertThrows(RuntimeException.class, () -> {
            noticeService.update(id, invalidNotice);
        });

        assertThat(exception.getMessage(), equalTo("A data precisa ser maior que agora"));
    }

    @Test
    void should_not_update_notice_because_of_id_not_found() {
        final var id = UUID.randomUUID();
        final var invalidNotice = createNotice(id);
        final var exception = assertThrows(ResourceNotFoundException.class, () -> {
            noticeService.update(id, invalidNotice);
        });

        assertThat(exception.getMessage(), equalTo("Mensagem não encontrada"));
    }

    @Test
    void should_delete_notice_by_id() {
        final var id = UUID.randomUUID();
        final var notice = createNotice(id);

        when(noticeRepository.findById(id)).thenReturn(Optional.of(notice));
        noticeService.delete(id);

        verify(noticeRepository, times(1)).deleteById(id);
    }

    @Test
    void should_not_delete_notice_because_of_id_not_found() {
        final var id = UUID.randomUUID();
        final var exception = assertThrows(ResourceNotFoundException.class, () -> {
            noticeService.delete(id);
        });

        assertThat(exception.getMessage(), equalTo("Mensagem não encontrada"));
    }

    private Notice createNotice(final UUID id) {
        final Faker faker = new Faker();
        final LocalDateTime sendDate = LocalDateTime.now().plusMinutes(5);
        final Integer senderId = faker.number().randomDigitNotZero();
        final Integer recipientId = faker.number().randomDigitNotZero();
        final String recipient = faker.phoneNumber().phoneNumber();
        final MessageType messageType = MessageType.WHATSAPP;
        final String message = faker.elderScrolls().dragon();
        final MessageStatus status = MessageStatus.SCHEDULED;
        return Notice.of(id, sendDate, senderId, recipientId, recipient, messageType, message, status);
    }

    private Notice createNotice(final UUID id, LocalDateTime sendDate) {
        final var notice = createNotice(id);
        notice.setSendDate(sendDate);
        return notice;
    }
}