package edu.tju.ina.things.vo.info;

import org.parceler.Parcel;

@Parcel
public class Activity extends Info {

	String area;
	String time;
	String orgName;
	String orgIntro;
	
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
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getOrgIntro() {
		return orgIntro;
	}
	public void setOrgIntro(String orgIntro) {
		this.orgIntro = orgIntro;
	}

}