package kr.nexters.onepage.domain.page;

import kr.nexters.onepage.domain.util.LocalDateRange;

import java.time.LocalDate;
import java.util.List;

public interface PageRepositoryCustom {
	List<Page> findByLocationIdAndPageable(Long locationId, int offset, int perSize);

	List<Page> findByEmailAndPageable(String email, int offset, int perSize);

	List<Page> findByHeartAndPageable(String email, int offset, int perSize);

	long countByLocationIdAndRange(Long locationId, LocalDateRange range);

	long countByLocationIdAndDay(Long locationId, LocalDate today);

	List<Page> findCircleByEmailAndHeart(String email, int pageNumber, int perPageSize);

	int countByEmailAndHeart(String email);
}
