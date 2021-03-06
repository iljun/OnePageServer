package kr.nexters.onepage.api;

import com.google.common.base.Preconditions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import kr.nexters.onepage.api.common.ResponseDto;
import kr.nexters.onepage.domain.page.PageResponseDto;
import kr.nexters.onepage.domain.page.PageService;
import kr.nexters.onepage.domain.page.PagesResponseDto;
import kr.nexters.onepage.domain.util.LocalDateRange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static kr.nexters.onepage.domain.common.NumericConstant.ZERO;

@Slf4j
@Api(value = "페이지 API", description = "페이지 API", basePath = "/api/v1/page")
@RestController
@RequestMapping("/api/v1/page")
public class PageApiController {
	@Autowired
	private PageService pageService;

	@ApiOperation(value = "페이지 저장", notes = "페이지 저장")
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public PageResponseDto save(@RequestParam Long locationId, @RequestParam String email, @RequestParam String content) {
		Preconditions.checkNotNull(locationId, "locationId parameter가 존재하지않음");
		Preconditions.checkNotNull(email, "email paramter가 존재하지않음");
		Preconditions.checkNotNull(content, "content parameter가 존재하지않음");
		try {
			return PageResponseDto.of(pageService.savePage(locationId, email, content));
		} catch (Exception e) {
			log.error("page findBy : " + e.getMessage(), e);
			return PageResponseDto.empty();
		}
	}

	@ApiOperation(value = "장소 기반 페이지 조회", notes = "장소 기반 페이지 조회")
	@RequestMapping(value = "/location", method = RequestMethod.GET)
	public PagesResponseDto findByLocation(@RequestParam Long locationId,
		@ApiParam(value = "현재 페이지 넘버버0부터시작)") @RequestParam Integer pageIndex,
		@ApiParam(value = "가져올 페이지 사이즈") @RequestParam Integer perPageSize) {
		Preconditions.checkNotNull(locationId, "locationId parameter가 존재하지 않음");
		Preconditions.checkNotNull(pageIndex, "pageIndex parameter가 존재하지 않음");
		Preconditions.checkNotNull(perPageSize, "perPageSize parameter가 존재하지 않음");
		try {
			return pageService.findCircleByLocationId(locationId, pageIndex, perPageSize);
		} catch (Exception e) {
			log.error("findByLocation : " + e.getMessage());
			return PagesResponseDto.empty();
		}
	}

	@ApiOperation(value = "유저 기반 페이지 조회", notes = "유저 기반 페이지 조회")
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public PagesResponseDto findByUser(@RequestParam String email,
		@ApiParam(value = "현재 페이지 넘버버0부터시작)") @RequestParam Integer pageIndex,
		@ApiParam(value = "가져올 페이지 사이즈") @RequestParam Integer perPageSize) {
		Preconditions.checkNotNull(email, "email paramter가 존재하지 않음");
		Preconditions.checkNotNull(pageIndex, "pageIndex parameter가 존재하지 않음");
		Preconditions.checkNotNull(perPageSize, "perPageSize parameter가 존재하지 않음");

		try {
			return pageService.findCircleByEmail(email, pageIndex, perPageSize);
		} catch (Exception e) {
			log.error("findByUser : " + e.getMessage());
			return PagesResponseDto.empty();
		}
	}

	@ApiOperation(value = "유저별 북마크 페이지 조회", notes = "우저별 북마크 페이지 조회")
	@RequestMapping(value="/heart", method=RequestMethod.GET)
	public PagesResponseDto heart(@RequestParam String email,
		@ApiParam(value = "현재 페이지 넘버버0부터시작") @RequestParam Integer pageIndex,
		@ApiParam(value = "가져올 페이지 사이즈") @RequestParam Integer perPageSize){
		Preconditions.checkNotNull(email, "email parameter가 존재하지 않음");
		Preconditions.checkNotNull(pageIndex, "pageIndex 존재하지 않음");
		Preconditions.checkNotNull(perPageSize, "perPageSize가 존재하지 않음");
		try{
			return pageService.findCircleByEmailAndHeart(email, pageIndex, perPageSize);
		} catch(Exception e){
			log.error("user heart page : " + e.getMessage(), e);
			return PagesResponseDto.empty();
		}
	}

	@ApiOperation(value = "장소별 페이지 총 갯수", notes = "장소별 페이지 총 갯수")
	@RequestMapping(value="/count/total", method=RequestMethod.GET)
	public Integer countTotal(@RequestParam Long locationId){
		try{
			return pageService.totalCountByLocationId(locationId);
		} catch(Exception e){
			log.error("page image removeById : " + e.getMessage(), e);
			return ZERO;
		}
	}

	@ApiOperation(value = "장소, 날짜 기간별 페이지 갯수", notes = "장소, 날짜 기간별 페이지 갯")
	@RequestMapping(value="/count", method=RequestMethod.GET)
	public Integer count(@RequestParam Long locationId,
		@ApiParam(value = "시작날짜(yyyy-MM-dd)") @RequestParam LocalDate startDate,
		@ApiParam(value = "마지막날짜(yyyy-MM-dd)") @RequestParam LocalDate endDate){
		try {
			return pageService.countByLocationIdAndRange(locationId, LocalDateRange.of(startDate, endDate));
		} catch (Exception e) {
			log.error("page image removeById : " + e.getMessage(), e);
			return ZERO;
		}
	}

	@ApiOperation(value = "페이지 삭제", notes = "페이지 삭제")
	@RequestMapping(value="/remove", method=RequestMethod.GET)
	public ResponseDto remove(@RequestParam Long pageId){
		Preconditions.checkNotNull(pageId, "pageId paramter가 존재하지 않음");
		try{
			pageService.removeById(pageId);
		} catch(Exception e){
			log.error("pagex image removeById : " + e.getMessage(), e);
			return ResponseDto.ofFail(e.getMessage());
		}
		return ResponseDto.ofSuccess("page 삭제 성공");
	}

}
