package org.gaung.wiwokdetok.fondasikehidupan.service;

import org.gaung.wiwokdetok.fondasikehidupan.dto.BookRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookResponseDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookSummaryDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.UpdateBookRequest;

import java.util.List;
import java.util.UUID;

public interface BookService {

    String createBook(BookRequestDTO dto, UUID userId);

    BookResponseDTO getBookById(UUID bookId, UUID userId);

    List<BookSummaryDTO> advancedSearch(String keyword, int limit);

    List<BookSummaryDTO> semanticSearch(String query, int limit, double threshold);

    void updateBook(UUID bookId, UpdateBookRequest request);
}
