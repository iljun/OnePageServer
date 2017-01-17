package kr.nexters.onepage.domain.pageImage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;

import kr.nexters.onepage.domain.common.OnePageServiceException;
import kr.nexters.onepage.domain.location.LocationService;
import kr.nexters.onepage.domain.page.Page;
import kr.nexters.onepage.domain.page.PageService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PageImageService {
	@Autowired
	private PageImageRepository pageImageRepository;
	@Autowired
	private PageService pageService;
	@Autowired
	private LocationService locationService;
	@Autowired
	private Cloudinary cloudinary;

	@Transactional(readOnly = false)
	public void savePageImage(Long pageId, MultipartFile multipartFile) {
		Page page = pageService.findById(pageId);
		Map<String, String> uploadInfo = upload(multipartFile);
		pageImageRepository.save(PageImage.of(page, uploadInfo));
	}

	private Map<String, String> upload(MultipartFile multipartFile) {
		try {
			Map<String, String> uploadInfo = cloudinary.uploader().upload(multipartFile.getBytes(), new HashMap() {{
				put("resource_type", "auto");
			}});
			uploadInfo.put("name", multipartFile.getName());
			return uploadInfo;
		} catch (IOException e) {
			log.error("savePageImage : " + e.getMessage());
			throw new OnePageServiceException(e);
		}
	}

	public List<PageImageDto> findByPageId(Long pageId) {
		List<PageImage> byPageImages = pageImageRepository.findByPageId(pageId);
		return byPageImages.stream().map(image -> PageImageDto.of(image)).collect(Collectors.toList());
	}

	@Transactional
	public void removeByPageId(Long pageId){
		pageImageRepository.delete(pageId);
	}
}
