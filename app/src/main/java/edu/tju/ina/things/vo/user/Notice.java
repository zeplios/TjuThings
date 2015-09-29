package edu.tju.ina.things.vo.user;

import org.parceler.Parcel;

import edu.tju.ina.things.vo.info.Info;

@Parcel
public class Notice {

	int id;
	User fromUser;
	User toUser;
	Info info;
	String content;
	String addTime;
	boolean hasRead;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public User getFromUser() {
		return fromUser;
	}

	public void setFromUser(User fromUser) {
		this.fromUser = fromUser;
	}

	public User getToUser() {
		return toUser;
	}

	public void setToUser(User toUser) {
		this.toUser = toUser;
	}

	public Info getInfo() {
		return info;
	}

	public void setInfo(Info info) {
		this.info = info;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public boolean isHasRead() {
		return hasRead;
	}

	public void setHasRead(boolean hasRead) {
		this.hasRead = hasRead;
	}

}