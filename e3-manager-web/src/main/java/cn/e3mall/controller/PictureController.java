package cn.e3mall.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.e3mall.common.utils.FastDFSClient;
import cn.e3mall.common.utils.JsonUtils;

@Controller
public class PictureController {

	@Value("${image.server.url}")
	private String IMAGE_SERVER_URL;
	
	@RequestMapping("/pic/upload")
	@ResponseBody
	public String fileUpload(MultipartFile uploadFile) {
		Map map = new HashMap<>();
		try {
			//1.获取文件名的扩展名
			String originalFilename = uploadFile.getOriginalFilename();
			String ext_fileName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
			//2.创建工具类对象
			FastDFSClient fastDFSClient = new FastDFSClient("classpath:conf/config.properties");
			//3.创建文件
			String address = fastDFSClient.uploadFile(uploadFile.getBytes(), ext_fileName);
			//4.将地址补充完整
			address = IMAGE_SERVER_URL+address;
			map.put("error", 0);
			map.put("url", address);
			String result = JsonUtils.objectToJson(map);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			map.put("error", 1);
			map.put("message", "上传失败!");
			String result = JsonUtils.objectToJson(map);
			return result;
		}
		
	}
}
