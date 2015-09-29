package edu.tju.ina.things.vo.info;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class Album extends Info {

	List<Photo> photos;
	
	public List<Photo> getPhotos() {
		return photos;
	}
	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}
}