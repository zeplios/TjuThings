package edu.tju.ina.things.vo.info;

import org.parceler.Parcel;

@Parcel
public class Market extends Info {

	MarketCategory category;
	double price;
	String contact;
	boolean complete;
	
	public MarketCategory getCategory() {
		return category;
	}
	public void setCategory(MarketCategory category) {
		this.category = category;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public boolean isComplete() {
		return complete;
	}
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	
}