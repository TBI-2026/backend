package org.gaung.wiwokdetok.fondasikehidupan.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookResponseDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookSummaryDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.UpdateBookRequest;
import org.gaung.wiwokdetok.fondasikehidupan.dto.UserPrincipal;
import org.gaung.wiwokdetok.fondasikehidupan.dto.WebResponse;
import org.gaung.wiwokdetok.fondasikehidupan.security.annotation.AllowedRoles;
import org.gaung.wiwokdetok.fondasikehidupan.security.annotation.CurrentUser;
import org.gaung.wiwokdetok.fondasikehidupan.service.BookService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @AllowedRoles({"USER"})
    @PostMapping(
            path = "/books",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> createBook(
            @Valid @RequestBody BookRequestDTO dto,
            @Valid @CurrentUser UserPrincipal user) {

        String bookUrl = bookService.createBook(dto, user.getId());

        return ResponseEntity
                .created(URI.create(bookUrl))
                .body(WebResponse.<String>builder()
                        .data("Created")
                        .build());
    }

    @GetMapping(
            path = "/books/{bookId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<BookResponseDTO>> getBookById(
            @CurrentUser UserPrincipal user,
            @PathVariable UUID bookId) {

        return ResponseEntity.ok(WebResponse.<BookResponseDTO>builder()
                .data(bookService.getBookById(bookId, user.getId()))
                .build());
    }

    @PatchMapping(
            path = "/books/{bookId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> updateBook(
            @Valid @RequestBody UpdateBookRequest request,
            @PathVariable UUID bookId) {

        bookService.updateBook(bookId, request);

        WebResponse<String> response = WebResponse.<String>builder()
                .data("OK")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping(
            path = "/books",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<List<BookSummaryDTO>>> search(
            @RequestParam(required = false) String k,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Double threshold) {

        int effectiveLimit = (limit == null || limit <= 0) ? 5 : limit;

        if (q != null && !q.isBlank()) {
            double effectiveThreshold = (threshold == null) ? 0.0 : threshold;
            return ResponseEntity.ok(WebResponse.<List<BookSummaryDTO>>builder()
                    .data(bookService.semanticSearch(q, effectiveLimit, effectiveThreshold))
                    .build());
        }

        if (k == null || k.isBlank()) {
            return ResponseEntity.badRequest().body(
                    WebResponse.<List<BookSummaryDTO>>builder()
                            .errors("Provide either 'k' (keyword) or 'q' (semantic) search parameter")
                            .build());
        }

        return ResponseEntity.ok(WebResponse.<List<BookSummaryDTO>>builder()
                .data(bookService.advancedSearch(k, effectiveLimit))
                .build());
    }
}
