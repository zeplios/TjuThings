package edu.tju.ina.things.vo.info;

import org.parceler.Parcel;

@Parcel
public class Recruit extends Info {

	String area;
	String time;
	String comName;
	String comIntro;
	String website;
	
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getComName() {
		return comName;
	}
	public void setComName(String comName) {
		this.comName = comName;
	}
	public String getComIntro() {
		return comIntro;
	}
	public void setComIntro(String comIntro) {
		this.comIntro = comIntro;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}

}