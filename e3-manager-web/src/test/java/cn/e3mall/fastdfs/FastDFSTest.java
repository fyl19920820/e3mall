package cn.e3mall.fastdfs;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

import cn.e3mall.common.utils.FastDFSClient;

public class FastDFSTest {

	@Test
	public void testFileUpload() throws Exception {
		// 1、加载配置文件，配置文件中的内容就是tracker服务的地址。
		ClientGlobal.init("K:/git_repository/e3mall/e3-manager-web/src/main/resources/conf/config.properties");
		// 2、创建一个TrackerClient对象。直接new一个。
		TrackerClient trackerClient = new TrackerClient();
		// 3、使用TrackerClient对象创建连接，获得一个TrackerServer对象。
		TrackerServer trackerServer = trackerClient.getConnection();
		// 4、创建一个StorageServer的引用，值为null
		StorageServer storageServer = null;
		// 5、创建一个StorageClient对象，需要两个参数TrackerServer对象、StorageServer的引用
		StorageClient storageClient = new StorageClient(trackerServer, storageServer);
		// 6、使用StorageClient对象上传图片。
		String[] strings = storageClient.upload_file("I:/个人资料/个人相册/贺迪、福哥、我/P1060679.JPG", "jpg", null);
		//扩展名不带“.”
		StorageClient1 storageClient1 = new StorageClient1(trackerServer, storageServer);
		String string1 = storageClient1.upload_file1("I:/个人资料/个人相册/贺迪、福哥、我/P1060679.JPG", "jpg", null);
		// 7、返回数组。包含组名和图片的路径。
		for (String string : strings) {
			System.out.println(string);
		}
		System.out.println(string1);
	}
	
	@Test
	public void testUploadUtils() throws Exception{
		FastDFSClient fastDFSClient = new FastDFSClient("K:/git_repository/e3mall/e3-manager-web/src/main/resources/conf/config.properties");
		String string = fastDFSClient.uploadFile("I:/个人资料/个人相册/贺迪、福哥、我/P1060679.JPG", "jpg");
		System.out.println(string);
	}

}
