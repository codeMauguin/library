package app.views;

import Message.ResponseCode;
import Message.RestResponse;
import app.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
public class UserControllerImpl implements UserController {
	
	private final UserService userService;
	private final RedisTemplate<String, String> redisTemplate;
	
	public UserControllerImpl(@Autowired UserService userService, @Autowired RedisTemplate<String,
																								  String> redisTemplate) {
		this.userService = userService;
		this.redisTemplate = redisTemplate;
	}
	
	
	@Override
	public RestResponse getCard(Long id) {
		return RestResponse.response(userService.queryCard(id), ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse revise(String name, String password, Long id) {
		if (!StringUtils.hasText(name) && !StringUtils.hasText(password))
			return RestResponse.response(ResponseCode.PARAM_ERROR);
		return RestResponse.response(userService.revise(name, password, id), ResponseCode.SUCCESS);
	}
	
	@Override
	public void code(String username, HttpServletResponse response) throws IOException {
		BufferedImage img = new BufferedImage(100,
				40,
				BufferedImage.TYPE_INT_RGB);
// 获取Graphics对象
		Graphics graphics = img.getGraphics();
// 设置背景色
		graphics.setColor(Color.WHITE);
		int width = 100;
		int height = 40;
		graphics.fillRect(0,
				0,
				width,
				height);
// 设置边框
		graphics.setColor(Color.BLACK);
		graphics.drawRect(0,
				0,
				width - 1,
				height - 1);
// 设置字体
		graphics.setFont(new Font("宋体",
				Font.BOLD + Font.ITALIC,
				20));
// 生成随机数或符号
		Random random = new Random();
		StringBuilder code = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			// 随机生成数字或字母
			int num = random.nextInt(10 + 26 * 2);
			char c;
			if (num < 10) {
				c = (char) ('0' + num);
			} else if (num < 36) {
				c = (char) ('A' + num - 10);
			} else {
				c = (char) ('a' + num - 36);
			}
			// 将字符添加到code字符串中
			code.append(c);
			// 设置随机颜色
			graphics.setColor(new Color(random.nextInt(256),
					random.nextInt(256),
					random.nextInt(256)));
			// 将字符绘制到图片上
			graphics.drawString(String.valueOf(c),
					width / 6 * (i + 1),
					height / 2 + random.nextInt(height / 4));
		}
// 添加干扰线或点（可选）
		for (int i = 0; i < random.nextInt(10); i++) {
			// 设置随机颜色
			graphics.setColor(new Color(random.nextInt(256),
					random.nextInt(256),
					random.nextInt(256)));
			// 画一条线或一个点
			graphics.drawLine(random.nextInt(width),
					random.nextInt(height),
					random.nextInt(width),
					random.nextInt(height));
		}
// 将验证码内容存储到session中
		String key = username + ":code";
		redisTemplate.opsForValue()
					 .set(key,
							 code.toString());
		redisTemplate.expire(key, 30, TimeUnit.SECONDS);
// 将图片输出到响应流中
		response.setContentType(MediaType.IMAGE_JPEG_VALUE);
		ImageIO.write(img,
				"jpg",
				response.getOutputStream());
	}
	
	@Override
	public RestResponse code_valid(String username, String code) {
		String key = username + ":code";
		String s = redisTemplate.opsForValue().get(key);
		redisTemplate.delete(key);
		if (s != null && s.equalsIgnoreCase(code)) {
			String s1 = username + ":valid_code";
			redisTemplate.opsForValue().set(s1, "true");
			redisTemplate.expire(s1, 30, TimeUnit.SECONDS);
			return RestResponse.response(ResponseCode.SUCCESS);
		}
		return RestResponse.response(ResponseCode.CODE_ERROR);
	}
}
