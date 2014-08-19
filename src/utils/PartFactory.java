package utils;

/**
 * author: 康乐
 * tine: 2014/8/19
 * function: 构建POST请求中的正文内容，包括对中间Part内容地构建和对最后一项的构建
 */
public class PartFactory {

	//　边界，在构建post请求的时候需要使用
	static final String BOUNDARY = "---------------------------7de8c1a80910";

	static final String CUTLINE = "-----------------------------7de8c1a80910\r\n";

	// 行尾结束符
	static final String ENDLINE = "\r\n-----------------------------7de8c1a80910--\r\n";

	/**
	 * 用于构建不是最后的part，需要注意的是content需要是被填充满的字节数组，如果不是这样，后台
	 * 有可能会出现意想不到的错误，也会造成许多的网络流量浪费和宽带压力
	 *
	 * @param partName    part的名字，这个名字将会被后台用作提取和分别资源的标志
	 * @param fileName    照片、文件的名字
	 * @param contentType part本身的类型，为image/jpeg或者text/plain
	 * @param content     part的内容
	 * @return 完整的part，包括描述和内容
	 */
	public static byte[] PartBuilder(String partName, String fileName, String contentType, byte[] content) {

		StringBuilder sb = new StringBuilder();

		sb.append("\r\n");
		sb.append(CUTLINE);
		sb.append("Content-Disposition:form-data;");
		sb.append("name=");
		sb.append("\"");
		sb.append(partName);
		sb.append("\";");
		sb.append("filename=");
		sb.append("\"");
		sb.append(fileName);
		sb.append("\"\r\n");
		sb.append("Content-Type:");
		sb.append(contentType);
		sb.append("\r\n\r\n");

		byte[] desContent = sb.toString().getBytes();

		byte[] part = new byte[desContent.length + content.length];

		unionByteArray(desContent, content, part);

		return part;
	}


	/**
	 * @see utils.PartFactory
	 *
	 * @param partName    part的名字，这个名字将会被后台用作提取和分别资源的标志
	 * @param fileName    照片、文件的名字
	 * @param contentType part本身的类型，为image/jpeg或者text/plain
	 * @param content     part的内容
	 * @param lastPart    是否是最后一个part，如果调用此函数，该值必须为真,否则返回空
	 * @return 完成的part，或者为null
	 */
	public static byte[] PartBuilder(String partName, String fileName, String contentType, byte[] content, boolean lastPart) {

		if (!lastPart) {
			System.err.println("这个方法是用来添加最后一个Part，所以参数lastPart必须为真!");
			return null;
		}

		byte[] part = PartBuilder(partName, fileName, contentType, content);

		byte[] endLineBytes = ENDLINE.getBytes();

		byte[] result = new byte[part.length + endLineBytes.length];

		unionByteArray(part, endLineBytes, result);

		return result;
	}

	private static void unionByteArray(byte[] source1, byte[] source2, byte[] result) {
		System.arraycopy(source1, 0, result, 0, source1.length);
		System.arraycopy(source2, 0, result, source1.length, source2.length);
	}
}
