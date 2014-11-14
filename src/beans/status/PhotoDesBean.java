package beans.status;

import java.sql.Timestamp;

/**
 * author: 康乐
 * time: 2014/8/26
 * function: 照片上传向后台数据库提供的相关的信息
 */

public class PhotoDesBean {

	// 用户的ID，不能为空
	private int ID;

	// 由服务器生成的时间
	private Timestamp Time;

	// 临时生成的rs_id值
	private int rs_id;

	// 专辑的名称，如果没有专辑那么其值为null
	private String albumName;

	// 用户转发状态时，原本的描述；如果不是转发的，那么值为null
	private String olderWords;

	// 用户转发状态或者发新状态时自己的描述
	private String myWords;

	// 照片的位置，如果是专辑，那么就会是一个Json字符串，如果是单张照片
	// 其中json的内容便会只有一项
	private String photoLocation;

	//  缩略图的位置
	private String moreSmallPhotoPath;

	// 个人中心查看图片时图片所在的路径
	private String viewPhotoPath;

	// 详细查看图片时图片所在的路径
	private String detailPhotoPath;

	// 照片的分类
	private String photoClass;

	// 图片中at的人
	private String photoAt;

	// 图片的话题
	private String photoTopic;

	// 此次照片存入的数量
	private int photoNumber;

	public int getID() {
		return ID;
	}

	public String getOlderWords() {
		return olderWords;
	}

	public String getMyWords() {
		return myWords;
	}

	public String getViewPhotoPath() {
		return viewPhotoPath;
	}

	public String getPhotoLocation() {
		return photoLocation;
	}

	public String getDetailPhotoPath() {
		return detailPhotoPath;
	}

	public String getPhotoClass() {
		return photoClass;
	}

	public String getPhotoAt() {
		return photoAt;
	}

	public String getPhotoTopic() {
		return photoTopic;
	}

	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public void setOlderWords(String olderWords) {
		this.olderWords = olderWords;
	}

	public void setMyWords(String myWords) {
		this.myWords = myWords;
	}

	public void setPhotoLocation(String photoLocation) {
		this.photoLocation = photoLocation;
	}

	public void setViewPhotoPath(String viewPhotoPath) {
		this.viewPhotoPath = viewPhotoPath;
	}

	public void setDetailPhotoPath(String detailPhotoPath) {
		this.detailPhotoPath = detailPhotoPath;
	}

	public void setPhotoClass(String photoClass) {
		this.photoClass = photoClass;
	}

	public void setPhotoAt(String photoAt) {
		this.photoAt = photoAt;
	}

	public void setPhotoTopic(String photoTopic) {
		this.photoTopic = photoTopic;
	}

	public Timestamp getTime() {
		return Time;
	}

	public int getRs_id() {
		return rs_id;
	}

	public void setTime(Timestamp time) {
		Time = time;
	}

	public void setRs_id(int rs_id) {
		this.rs_id = rs_id;
	}

	public int getPhotoNumber() {
		return photoNumber;
	}

	public void setPhotoNumber(int photoNumber) {
		this.photoNumber = photoNumber;
	}

	public String getMoreSmallPhotoPath() {
		return moreSmallPhotoPath;
	}

	public void setMoreSmallPhotoPath(String moreSmallPhotoPath) {
		this.moreSmallPhotoPath = moreSmallPhotoPath;
	}
}
