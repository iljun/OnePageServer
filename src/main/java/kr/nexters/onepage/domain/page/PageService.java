package kr.nexters.onepage.domain.page;

import kr.nexters.onepage.domain.common.OnePageServiceException;
import kr.nexters.onepage.domain.location.Location;
import kr.nexters.onepage.domain.location.LocationService;
import kr.nexters.onepage.domain.pageImage.PageImageDto;
import kr.nexters.onepage.domain.pageImage.PageImageService;
import kr.nexters.onepage.domain.support.Created;
import kr.nexters.onepage.domain.user.User;
import kr.nexters.onepage.domain.user.UserService;
import kr.nexters.onepage.domain.util.LocalDateRange;
import kr.nexters.onepage.domain.util.functional.F2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static kr.nexters.onepage.domain.common.NumericConstant.ZERO;

@Slf4j
@Service
public class PageService {
	@Autowired
	private PageRepository pageRepository;
	@Autowired
	private PageImageService pageImageService;
	@Autowired
	private LocationService locationService;
	@Autowired
	private UserService userService;

	@Transactional(readOnly = false)
	public PageDto savePage(Long locationId, String email, String content) {
		Location location = locationService.findById(locationId);
		User user = userService.findByEmail(email);
		Page page;
		try {
			page = pageRepository.save(Page.of(location, user, content));
		} catch (SQLException e) {
			log.error("savePage : " + e.getMessage());
			throw new OnePageServiceException(e);
		}
		List<PageImageDto> pageImageDto = pageImageService.findByPageId(page.getId());
		int pageNum = pageRepository.countByLocationIdAndId(locationId, page.getId());
		return PageDto.of(page, pageImageDto, pageNum);
	}

	public Page findById(Long pageId) {
		Page page = pageRepository.findOne(pageId);
		if(Objects.isNull(page)) {
			throw new OnePageServiceException("페이지가 존재하지 않습니다.");
		}
		return page;
	}

	public PagesResponseDto findCircleByLocationId(Long locationId, Integer pageNumber, Integer perPageSize) {
		F2<Integer, Integer, List<Page>> callback = (num, size) -> pageRepository.findByLocationIdAndPageable(locationId, num, size);
		return findCommonCircleBy(pageNumber, perPageSize, totalCountByLocationId(locationId), callback);
	}

	public PagesResponseDto findCircleByEmail(String email, Integer pageNumber, Integer perPageSize) {
		F2<Integer, Integer, List<Page>> callback = (num, size) -> pageRepository.findByEmailAndPageable(email, num, size);
		return findCommonCircleBy(pageNumber, perPageSize, totalCountByEmail(email), callback);
	}

	/**
	 * example
	 * @param pageNumber
	 * @param perPageSize
	 * @param totalSize
	 * @param callback
	 * @return
	 */
	private PagesResponseDto findCommonCircleBy(Integer pageNumber, Integer perPageSize, Integer totalSize, F2<Integer, Integer, List<Page>> callback) {
		// 1. 0 미만일 경우. 2. totalSize 초과할 경우. -> 페이지 범위 내로 변경.
		pageNumber = (totalSize + pageNumber) % totalSize;
		List<Page> pages = callback.apply(pageNumber, perPageSize).stream().collect(Collectors.toSet()).stream().sorted(
			Comparator.comparing(Created::getCreatedAt)).collect(Collectors.toList());
		// 조회한 페이지 사이즈가 per 페이지 사이즈보다 작으면 0페이지부터 조회하여 더함.
		if (CollectionUtils.isNotEmpty(pages) && pages.size() < perPageSize) {
			pages.addAll(callback.apply(ZERO, perPageSize - pages.size()));
		}
		return PagesResponseDto.of(
			PageDtoBuilder.transformPagesToDtos(pages, pageNumber, totalSize, (id) -> pageImageService.findByPageId(id)),
			pageNumber,
			perPageSize,
			totalSize);
	}

	public int totalCountByLocationId(Long locationId) {
		return pageRepository.countByLocationId(locationId);
	}

	public int totalCountByEmail(String email) {
		User user = userService.findByEmail(email);
		return pageRepository.countByUserId(user.getId());
	}

	@Transactional(readOnly = false)
	public void remove(Long pageId){
		Page page = pageRepository.findOne(pageId);
		page.deleted();
		pageImageService.deleted(pageId);
	}

	public int countByLocationIdAndRange(Long locationId, LocalDateRange range) {
		return (int) pageRepository.countByLocationIdAndRange(locationId, range);
	}

	public PagesResponseDto findCircleByEmailAndHeart(String email, Integer pageNumber, Integer perPageSize){
		F2<Integer, Integer, List<Page>> callback = (num, size) -> pageRepository.findByHeartAndPageable(email, pageNumber, perPageSize);
		return findCommonCircleBy(pageNumber, perPageSize, totalCountByEmailAndHeart(email), callback);
	}

	public int totalCountByEmailAndHeart(String email){
		return pageRepository.countByEmailAndHeart(email);
	}
}