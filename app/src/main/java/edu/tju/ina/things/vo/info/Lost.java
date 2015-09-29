package edu.tju.ina.things.vo.info;

import org.parceler.Parcel;

@Parcel
public class Lost extends Info {

	boolean owner;
	String ownerid;
	String contact;
	String place;
	boolean complete;
	
	public boolean isOwner() {
		return owner;
	}
	public void setOwner(boolean owner) {
		this.owner = owner;
	}
	public String getOwnerid() {
		return ownerid;
	}
	public void setOwnerid(String ownerid) {
		this.ownerid = ownerid;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public boolean isComplete() {
		return complete;
	}
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
}