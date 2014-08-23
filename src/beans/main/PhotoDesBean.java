package beans.main;

/**
 * Created by leon on 2014/8/22.
 */
public class PhotoDesBean {

	private String albumName;

	private long ID;

	private String olderWords;

	private String myWords;

	private String photoLocation;

	//
	private String viewPhotoPath;

	//
	private String detailPhotoPath;

	private String photoClass;

	private String photoAt;

	private String photoTopic;

	public long getID() {
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

	public void setID(long ID) {
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
}
