package edu.tju.ina.things.vo.user;

import org.parceler.Parcel;

import edu.tju.ina.things.vo.info.Info;

@Parcel
public class Thumbup {
	int id;
	User user;
	Info info;
	String addTime;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Info getInfo() {
		return info;
	}
	public void setInfo(Info info) {
		this.info = info;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	
	
}
