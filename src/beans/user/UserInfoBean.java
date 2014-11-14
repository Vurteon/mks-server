package beans.user;

/**
 * author：康乐
 * time：2014/11/13
 * function：用户个人信息bean，其实就是主页上所需要的信息
 */


public class UserInfoBean {


	private int ID;

	private String name;

	private String brief_intro;

	private String bg_photo;

	private String main_head_photo;

	private String home_head_photo;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrief_intro() {
		return brief_intro;
	}

	public void setBrief_intro(String brief_intro) {
		this.brief_intro = brief_intro;
	}

	public String getBg_photo() {
		return bg_photo;
	}

	public void setBg_photo(String bg_photo) {
		this.bg_photo = bg_photo;
	}

	public String getMain_head_photo() {
		return main_head_photo;
	}

	public void setMain_head_photo(String main_head_photo) {
		this.main_head_photo = main_head_photo;
	}

	public String getHome_head_photo() {
		return home_head_photo;
	}

	public void setHome_head_photo(String home_head_photo) {
		this.home_head_photo = home_head_photo;
	}

	public int getID() {
		return ID;
	}

	public void setID(int ID) {
		this.ID = ID;
	}
}
