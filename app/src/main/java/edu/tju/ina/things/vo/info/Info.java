package edu.tju.ina.things.vo.info;

import org.parceler.Parcel;

import java.util.List;

import edu.tju.ina.things.vo.user.User;

@Parcel
public class Info {

	int id;
	String title;
	String desc;
	int status;
	String picture;
	int viewCount;
	int commentCount;
	int thumbupCount;
	int collectCount;
	String addTime;
	int type;

	User user;

	boolean hasCollected;
	boolean hasThumbuped;
    boolean deleted;

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    List<String> images;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public int getViewCount() {
		return viewCount;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public int getThumbupCount() {
		return thumbupCount;
	}

	public void setThumbupCount(int thumbupCount) {
		this.thumbupCount = thumbupCount;
	}

	public int getCollectCount() {
		return collectCount;
	}

	public void setCollectCount(int collectCount) {
		this.collectCount = collectCount;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isHasCollected() {
		return hasCollected;
	}

	public void setHasCollected(boolean hasCollected) {
		this.hasCollected = hasCollected;
	}

	public boolean isHasThumbuped() {
		return hasThumbuped;
	}

	public void setHasThumbuped(boolean hasThumbuped) {
		this.hasThumbuped = hasThumbuped;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

}