package kr.nexters.onepage.domain.locationImage;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Where;

import kr.nexters.onepage.domain.location.Location;
import kr.nexters.onepage.domain.support.Modified;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(catalog = "onepage", name = "locationImage")
@Where(clause = "deleted = 0")
public class LocationImage extends Modified{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "locationImageId")
	private Long id;

	@JoinColumn(name = "locationId")
	@NotNull
	@ManyToOne
	private Location location;

	@Column(name = "objectkey")
	@NotNull
	private String objectkey;

	@Column(name = "url")
	@NotNull
	private String url;

	@Column(name = "name")
	@NotNull
	private String name;

	@Column(name = "weatherType")
	@NotNull
	private WeatherType weatherType;

	@Column(name = "deleted")
	private boolean deleted;

	public static LocationImage of(Long id, Location location, String objectkey, String url, WeatherType weatherType){
		return LocationImage.builder()
				.id(id)
				.location(location)
				.objectkey(objectkey)
				.url(url)
				.weatherType(weatherType).build();
	}

	public void deleted(){
		this.deleted=true;
	}
}