package kr.nexters.onepage.domain.location;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LocationService {

	@Autowired
	private LocationRepository locationRepository;

	@Transactional(readOnly = false)
	public void saveLocation(Double latitude, Double longitude, String name, String address) {
		if(latitude>90.1 || longitude>180.1)
			return;
		locationRepository.save(Location.of(latitude,longitude,name,address));
	}

	public Location findById(Long locationId) {
		return locationRepository.findOne(locationId);
	}

	public List<LocationDto> findByLocationName(String locationName) {
		List<Location> byName = locationRepository.findByName(locationName);
		if (CollectionUtils.isEmpty(byName)) {
			return Lists.newArrayList();
		}
		return byName.stream().map(location -> LocationDto.of(location)).collect(Collectors.toList());
	}
}
