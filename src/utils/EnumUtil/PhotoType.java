package utils.EnumUtil;

/**
 * author: 康乐
 * change-time :2014/8/28
 * function: 图片本身所属的带下类型，目前有上传的原图和用户在个人中心浏览时的压缩小图
 */

public enum PhotoType {

	// 上传图片的原图
	ORIGINAL_PHOTO,

	// 个人中心浏览时被压缩后的小图
	RESIZE_PHOTO,

	// 缩略图
	MORE_SMALL_PHOTO

}
