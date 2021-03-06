package kr.nexters.onepage.domain.heart;

import kr.nexters.onepage.domain.page.PageService;
import kr.nexters.onepage.domain.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
public class HeartService {
	@Autowired
	private HeartRepository heartRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private PageService pageService;

	@Transactional(readOnly = false)
	public void saveOrRemoveHeart(Long pageId, String email) {
		Heart heart = Heart.of(pageService.findById(pageId), userService.findByEmail(email));
		if(existsByPageIdAndEmail(pageId, email)) {
			heart = heartRepository.findByPageIdAndEmail(pageId, email);
			heart.deleted();
			heartRepository.save(heart);
			return;
		}
		heartRepository.save(heart);
	}

	public boolean existsByPageIdAndEmail(Long pageId, String email) {
		return Objects.nonNull(heartRepository.findByPageIdAndEmail(pageId, email));
	}

	public Long countByPageId(Long pageId) {
		return heartRepository.countByPageId(pageId);
	}
}
