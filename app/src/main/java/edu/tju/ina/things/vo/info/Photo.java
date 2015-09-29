package edu.tju.ina.things.vo.info;

import org.parceler.Parcel;

@Parcel
public class Photo extends Info {

	Album album;
	
	public Album getAlbum() {
		return album;
	}
	public void setAlbum(Album album) {
		this.album = album;
	}
	
}