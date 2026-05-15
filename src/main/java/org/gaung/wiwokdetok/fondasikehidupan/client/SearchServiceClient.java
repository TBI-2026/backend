package org.gaung.wiwokdetok.fondasikehidupan.client;

import lombok.RequiredArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookSummaryDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SearchServiceClient {

    private final WebClient searchServiceWebClient;

    public List<BookSummaryDTO> semanticSearch(String query, int limit, double threshold) {
        try {
            List<SearchHit> hits = searchServiceWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", query)
                            .queryParam("limit", limit)
                            .queryParam("threshold", threshold)
                            .build())
                    .retrieve()
                    .bodyToFlux(SearchHit.class)
                    .collectList()
                    .block();

            if (hits == null) return List.of();

            return hits.stream()
                    .map(h -> new BookSummaryDTO(UUID.fromString(h.bookId()), h.title(), null))
                    .toList();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Search service error: " + e.getMessage(), e);
        }
    }

    private record SearchHit(String book_id, String title, double score) {
        String bookId() { return book_id; }
    }
}
