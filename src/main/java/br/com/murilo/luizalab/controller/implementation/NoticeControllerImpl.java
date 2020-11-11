package br.com.murilo.luizalab.controller.implementation;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import br.com.murilo.luizalab.controller.NoticeController;
import br.com.murilo.luizalab.dto.request.NoticeRequest;
import br.com.murilo.luizalab.dto.response.NoticeResponse;
import br.com.murilo.luizalab.facade.NoticeFacade;
import br.com.murilo.luizalab.types.MessageStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort.Direction;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notice")
public class NoticeControllerImpl implements NoticeController {

    private final NoticeFacade noticeFacade;

    @Autowired
    public NoticeControllerImpl(final NoticeFacade noticeFacade) {
        this.noticeFacade = noticeFacade;
    }

    @Override
    @PostMapping
    public ResponseEntity<NoticeResponse> save(@RequestBody final NoticeRequest noticeRequest) {
        noticeRequest.setStatus(MessageStatus.SCHEDULED);
        final var savedNotice = this.noticeFacade.save(noticeRequest);
        savedNotice.add(linkTo(methodOn(NoticeControllerImpl.class).findById(savedNotice.getId())).withSelfRel());
        return new ResponseEntity<>(savedNotice, HttpStatus.CREATED);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponse> findById(@PathVariable(value = "id") final UUID id) {
        final var notice = this.noticeFacade.findById(id);
        return new ResponseEntity<>(notice, HttpStatus.OK);
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<NoticeResponse>> findNoticeBySendDateBetween(@RequestParam(name = "startDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") final LocalDateTime startDate,
                                                                            @RequestParam(name = "startDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")final LocalDateTime endDate,
                                                                            @PageableDefault(direction = Direction.ASC, sort = "sendDate")final Pageable page) {

        final var notices = this.noticeFacade.findNoticeBySendDateBetween(startDate, endDate, page);
        final var status = (notices.isEmpty()) ? HttpStatus.NO_CONTENT : HttpStatus.OK;
        notices.stream().forEach(notice -> notice.add(
                linkTo(
                        methodOn(NoticeControllerImpl.class).findById(notice.getId()
                        )
                ).withSelfRel()));

        return new ResponseEntity<>(notices, status);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<NoticeResponse> update(@PathVariable(value = "id") final UUID id,
                                                 @RequestBody final NoticeRequest noticeRequest) {
        final var updatedNotice = this.noticeFacade.update(id, noticeRequest);
        updatedNotice.add(linkTo(methodOn(NoticeControllerImpl.class).findById(updatedNotice.getId())).withSelfRel());
        return new ResponseEntity<>(updatedNotice, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> delete(final UUID id) {
        this.noticeFacade.delete(id);
        return ResponseEntity.ok().build();
    }
}
