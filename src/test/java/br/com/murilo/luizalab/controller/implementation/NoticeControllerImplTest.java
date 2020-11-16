package br.com.murilo.luizalab.controller.implementation;

import br.com.murilo.luizalab.LuizalabApplication;
import br.com.murilo.luizalab.configuration.RabbitMQListenerConfiguration;
import br.com.murilo.luizalab.configuration.RabbitMQSenderConfiguration;
import br.com.murilo.luizalab.configuration.WebConfig;
import br.com.murilo.luizalab.dto.request.NoticeRequest;
import br.com.murilo.luizalab.dto.response.NoticeResponse;
import br.com.murilo.luizalab.exception.InvalidDateException;
import br.com.murilo.luizalab.exception.ResourceNotFoundException;
import br.com.murilo.luizalab.types.MessageStatus;
import br.com.murilo.luizalab.types.MessageType;
import br.com.murilo.luizalab.utils.JsonConverter;
import com.github.javafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RabbitMQSenderConfiguration.class, RabbitMQListenerConfiguration.class, WebConfig.class})
@SpringBootTest(classes = LuizalabApplication.class)
@AutoConfigureMockMvc
public class NoticeControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String BASE_PATH = "/api/v1/notice/";

    private NoticeResponse createdNotice;

    @Before
    public void setUp() throws Exception {
        final var noticeRequest = createNoticeRequest(null, null);
        final var requestBody = JsonConverter.convert(noticeRequest);
        final var responseBody = this.mockMvc.perform(post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();
        createdNotice = (NoticeResponse) JsonConverter.convert(responseBody.getResponse().getContentAsString(), NoticeResponse.class);
    }

    @After
    public void afterTests() throws Exception {
        this.mockMvc.perform(delete(BASE_PATH + "/" + createdNotice.getId().toString())).andExpect(status().isOk());
    }

    @Test
    public void should_save_notice_without_publishing() throws Exception {
        final var noticeRequest = createNoticeRequest(null, null, LocalDateTime.now().plusHours(1));
        final var requestBody = JsonConverter.convert(noticeRequest);
        final var responseBody = this.mockMvc.perform(post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();
        final var noticeResponse = (NoticeResponse) JsonConverter.convert(responseBody.getResponse().getContentAsString(), NoticeResponse.class);
        assertThat(noticeResponse.getMessage(), equalTo(noticeRequest.getMessage()));
        assertThat(noticeResponse.getStatus(), equalTo(MessageStatus.SCHEDULED));

        final var link = noticeResponse.getLink("self").get().getHref();
        assertThat(link, containsString(noticeResponse.getId().toString()));
    }

    @Test
    public void should_not_save_notice_because_of_invalid_date() throws Exception {
        final var expiredSendDate = LocalDateTime.now().minusHours(1);
        final var noticeRequest = createNoticeRequest(null, null, expiredSendDate);
        final var requestBody = JsonConverter.convert(noticeRequest);

        this.mockMvc.perform(post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidDateException))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage(), equalTo("A data precisa ser maior que agora")));
    }

    @Test
    public void should_find_notice_by_id() throws Exception {
        final var responseBody = this.mockMvc.perform(get(BASE_PATH + createdNotice.getId().toString()))
                .andExpect(status().isOk())
                .andReturn();

        final var noticeResponse = (NoticeResponse) JsonConverter.convert(responseBody.getResponse().getContentAsString(), NoticeResponse.class);
        assertThat(noticeResponse, equalTo(createdNotice));
    }

    @Test
    public void should_not_find_notice_because_id_does_not_exist() throws Exception {
        final var id = UUID.randomUUID().toString();
        this.mockMvc.perform(get(BASE_PATH + id))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage(), equalTo("Mensagem não encontrada")));
    }

    @Test
    public void should_find_notice_by_sendDate_between() throws Exception {
        final var startDate = LocalDateTime.now().minusMinutes(10);
        final var endDate = LocalDateTime.now().plusMinutes(10);
        final var page = 0;
        final var size = 5;

        final var queryParameters = createQueryParameters(startDate, endDate, size, page);

        this.mockMvc.perform(get(BASE_PATH)
                .queryParams(queryParameters))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.pageable.pageSize", equalTo(5)))
                .andExpect(jsonPath("$.content[0].id", equalTo(createdNotice.getId().toString())));

    }

    @Test
    public void should_find_no_content_by_sendDate_between() throws Exception {
        final var startDate = LocalDateTime.now().minusMinutes(30);
        final var endDate = LocalDateTime.now().plusMinutes(5);

        final var queryParameters = createQueryParameters(startDate, endDate);

        this.mockMvc.perform(get(BASE_PATH)
                .queryParams(queryParameters))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.pageable.pageSize", equalTo(10)));

    }

    @Test
    public void should_update_notice_without_publish() throws Exception {
        var noticeToUpdate = convertNoticeResponseToNoticeRequest();
        noticeToUpdate.setMessage("Mensagem de teste - update");

        final var id = noticeToUpdate.getId().toString();
        final var requestBody = JsonConverter.convert(noticeToUpdate);

        final var responseBody = this.mockMvc.perform(put(BASE_PATH + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        final var noticeUpdated = foundNoticeById(id);
        assertThat(noticeToUpdate.getId(), equalTo(noticeUpdated.getId()));
        assertThat(noticeToUpdate.getMessage(), equalTo(noticeUpdated.getMessage()));

    }

    @Test
    public void should_not_update_notice_because_of_invalid_date() throws Exception {
        var noticeToUpdate = convertNoticeResponseToNoticeRequest();
        noticeToUpdate.setMessage("Mensagem de teste - update");
        noticeToUpdate.setSendDate(LocalDateTime.now().minusMinutes(1));

        final var id = noticeToUpdate.getId().toString();
        final var requestBody = JsonConverter.convert(noticeToUpdate);

        this.mockMvc.perform(put(BASE_PATH + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidDateException))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage(), equalTo("A data precisa ser maior que agora")));
    }

    @Test
    public void should_not_update_notice_because_of_invalid_id() throws Exception {
        var noticeToUpdate = convertNoticeResponseToNoticeRequest();
        noticeToUpdate.setMessage("Mensagem de teste - update");
        noticeToUpdate.setSendDate(LocalDateTime.now().plusMinutes(1));

        final var id = UUID.randomUUID().toString();
        final var requestBody = JsonConverter.convert(noticeToUpdate);

        this.mockMvc.perform(put(BASE_PATH + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage(), equalTo("Mensagem não encontrada")));
    }

    @Test
    public void should_delete_notice() throws Exception {
        final NoticeResponse noticeToDelete;
        final var noticeRequest = createNoticeRequest(null, null);
        final var requestBody = JsonConverter.convert(noticeRequest);
        final var responseBody = this.mockMvc.perform(post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();
        noticeToDelete = (NoticeResponse) JsonConverter.convert(responseBody.getResponse().getContentAsString(), NoticeResponse.class);

        this.mockMvc.perform(delete(BASE_PATH + noticeToDelete.getId().toString())).andExpect(status().isOk());
    }

    @Test
    public void should_not_delete_notice_because_of_invalid_id() throws Exception {
        final var id = UUID.randomUUID().toString();

        this.mockMvc.perform(delete(BASE_PATH + "/" + id))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage(), equalTo("Mensagem não encontrada")));
    }

    private NoticeRequest createNoticeRequest(final UUID id) {
        final Faker faker = new Faker();
        final LocalDateTime sendDate = LocalDateTime.now().plusMinutes(5);
        final Integer senderId = faker.number().randomDigitNotZero();
        final Integer recipientId = faker.number().randomDigitNotZero();
        final String recipient = faker.phoneNumber().cellPhone();
        final MessageType messageType = MessageType.WHATSAPP;
        final String message = faker.elderScrolls().dragon();
        final MessageStatus status = MessageStatus.SCHEDULED;
        return NoticeRequest.of(id, sendDate, senderId, recipientId, recipient, messageType, message, status);
    }

    private NoticeRequest createNoticeRequest(final UUID id, MessageStatus status) {
        final var noticeRequest = createNoticeRequest(id);
        noticeRequest.setStatus(status);
        return noticeRequest;
    }

    private NoticeRequest createNoticeRequest(final UUID id, MessageStatus status, final LocalDateTime sendDate) {
        final var noticeRequest = createNoticeRequest(id, status);
        noticeRequest.setSendDate(sendDate);
        return noticeRequest;
    }

    private MultiValueMap<String, String> createQueryParameters(final LocalDateTime startDate, final LocalDateTime endDate) {
        final String pattern = "yyyy-MM-dd HH:mm";
        final MultiValueMap<String, String> queryParameters = new LinkedMultiValueMap<>();
        queryParameters.add("startDate", startDate.format(DateTimeFormatter.ofPattern(pattern)));
        queryParameters.add("endDate", endDate.format(DateTimeFormatter.ofPattern(pattern)));
        return queryParameters;
    }

    private MultiValueMap<String, String> createQueryParameters(final LocalDateTime startDate, final LocalDateTime endDate, final int size, final int page) {
        var queryParameters = createQueryParameters(startDate, endDate);
        queryParameters.add("size", String.valueOf(size));
        queryParameters.add("page", String.valueOf(page));
        return queryParameters;
    }

    private NoticeRequest convertNoticeResponseToNoticeRequest() {
        return NoticeRequest.of(createdNotice.getId(),
                createdNotice.getSendDate(),
                createdNotice.getSenderId(),
                createdNotice.getRecipientId(),
                createdNotice.getRecipient(),
                createdNotice.getMessageType(),
                createdNotice.getMessage(),
                createdNotice.getStatus());
    }

    private NoticeResponse foundNoticeById(final String id) throws Exception {
        final var responseBody = this.mockMvc.perform(get(BASE_PATH + createdNotice.getId().toString()))
                .andExpect(status().isOk())
                .andReturn();

        return (NoticeResponse) JsonConverter.convert(responseBody.getResponse().getContentAsString(), NoticeResponse.class);
    }
}