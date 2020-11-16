package br.com.murilo.luizalab.facade.implementation;

import br.com.murilo.luizalab.LuizalabApplication;
import br.com.murilo.luizalab.dto.publisher.NoticePublisher;
import br.com.murilo.luizalab.dto.request.NoticeRequest;
import br.com.murilo.luizalab.dto.response.NoticeResponse;
import br.com.murilo.luizalab.facade.NoticeFacade;
import br.com.murilo.luizalab.model.Notice;
import br.com.murilo.luizalab.queue.publisher.MagaluPublisher;
import br.com.murilo.luizalab.service.NoticeService;
import br.com.murilo.luizalab.types.MessageStatus;
import br.com.murilo.luizalab.types.MessageType;
import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LuizalabApplication.class)
public class NoticeFacadeImplTest {

    @Autowired
    private ConversionService conversionService;

    @Mock
    private NoticeService noticeService;

    @Mock
    private MagaluPublisher magaluPublisher;

    private NoticeFacade noticeFacade;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        noticeFacade = new NoticeFacadeImpl(noticeService, conversionService, magaluPublisher);
    }

    @Test
    public void should_save_notice_without_publishing() {
        final var id = UUID.randomUUID();
        final var noticeRequest = createNoticeRequest(id);
        final var notice = this.conversionService.convert(noticeRequest, Notice.class);

        when(noticeService.save(notice)).thenReturn(notice);
        final var noticeResponse = this.noticeFacade.save(noticeRequest);

        assertThat(noticeResponse.getId(), equalTo(id));
        assertThat(noticeResponse.getMessage(), equalTo(noticeRequest.getMessage()));
        verify(magaluPublisher, times(0)).publishNotice(any(NoticePublisher.class));
    }

    @Test
    public void should_save_notice_publishing_message() {
        final var id = UUID.randomUUID();
        final var noticeRequest = createNoticeRequest(id, LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        final var notice = this.conversionService.convert(noticeRequest, Notice.class);

        when(noticeService.save(notice)).thenReturn(notice);
        doNothing().when(magaluPublisher).publishNotice(any(NoticePublisher.class));
        final var noticeResponse = this.noticeFacade.save(noticeRequest);

        assertThat(noticeResponse.getId(), equalTo(id));
        assertThat(noticeResponse.getMessage(), equalTo(noticeRequest.getMessage()));
        verify(magaluPublisher, times(1)).publishNotice(any(NoticePublisher.class));
    }

    @Test
    public void should_find_notice_by_id() {
        final var id = UUID.randomUUID();
        final var notice = createNotice(id);
        final var noticeResponse = this.conversionService.convert(notice, NoticeResponse.class);

        when(noticeService.findById(id)).thenReturn(notice);
        final var foundNotice = noticeFacade.findById(id);

        assertThat(foundNotice, equalTo(noticeResponse));
    }

    @Test
    public void should_find_notice_by_sendDate_between() {
        final var startDateTime = LocalDateTime.now();
        final var endDateTime = LocalDateTime.now().plusHours(1);
        final var noticeCreated = createNotice(UUID.randomUUID());
        final var notices = new PageImpl<Notice>(Arrays.asList(noticeCreated));
        final var noticeResponse = this.conversionService.convert(noticeCreated, NoticeResponse.class);

        when(noticeService.findNoticeBySendDateBetween(startDateTime, endDateTime, Pageable.unpaged())).thenReturn(notices);
        final var found = noticeFacade.findNoticeBySendDateBetween(startDateTime, endDateTime, Pageable.unpaged());

        assertThat(found.getContent(), hasItem(noticeResponse));
    }

    @Test
    public void should_update_notice_without_publishing() {
        final var id = UUID.randomUUID();
        final var noticeRequest = createNoticeRequest(id);
        final var notice = this.conversionService.convert(noticeRequest, Notice.class);

        when(noticeService.update(id, notice)).thenReturn(notice);
        final var noticeResponse = this.noticeFacade.update(id, noticeRequest);

        assertThat(noticeResponse.getId(), equalTo(id));
        assertThat(noticeResponse.getMessage(), equalTo(noticeRequest.getMessage()));
        verify(magaluPublisher, times(0)).publishNotice(any(NoticePublisher.class));
    }

    @Test
    public void should_update_notice_publishing_message() {
        final var id = UUID.randomUUID();
        final var noticeRequest = createNoticeRequest(id, LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        final var notice = this.conversionService.convert(noticeRequest, Notice.class);

        when(noticeService.update(id, notice)).thenReturn(notice);
        doNothing().when(magaluPublisher).publishNotice(any(NoticePublisher.class));
        final var noticeResponse = this.noticeFacade.update(id, noticeRequest);

        assertThat(noticeResponse.getId(), equalTo(id));
        assertThat(noticeResponse.getMessage(), equalTo(noticeRequest.getMessage()));
        verify(magaluPublisher, times(1)).publishNotice(any(NoticePublisher.class));
    }

    @Test
    public void delete() {
        final var id = UUID.randomUUID();
        doNothing().when(noticeService).delete(id);
        noticeFacade.delete(id);
        verify(noticeService, times(1)).delete(id);
    }

    private NoticeRequest createNoticeRequest(final UUID id) {
        final Faker faker = new Faker();
        final LocalDateTime sendDate = LocalDateTime.now().plusMinutes(5);
        final Integer senderId = faker.number().randomDigitNotZero();
        final Integer recipientId = faker.number().randomDigitNotZero();
        final String recipient = faker.phoneNumber().phoneNumber();
        final MessageType messageType = MessageType.WHATSAPP;
        final String message = faker.elderScrolls().dragon();
        final MessageStatus status = MessageStatus.SCHEDULED;
        return NoticeRequest.of(id, sendDate, senderId, recipientId, recipient, messageType, message, status);
    }

    private NoticeRequest createNoticeRequest(final UUID id, final LocalDateTime sendDate) {
        final var noticeRequest = createNoticeRequest(id);
        noticeRequest.setSendDate(sendDate);
        return noticeRequest;
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

}