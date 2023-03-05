package app.inject;

import Message.RestResponse;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pojo.user;

@FeignClient("user-service")
public interface InjectService {
	/**
	 * 查询整个card数据
	 *
	 * @param id 用户id
	 * @return card
	 */
	
	@GetMapping(value = "/v2/card", headers = {"proxy-rule=inject"})
	@LoadBalanced
	RestResponse queryCard(@RequestParam("id") Long id);
	
	/**
	 * 获取用户 cardId
	 *
	 * @param id 用户id
	 * @return cardId
	 */
	
	@GetMapping(value = "/inject/cardId", headers = {"proxy-rule=inject"})
	@LoadBalanced
	Long query(@RequestParam("id") Long id);
	
	@PostMapping(value = "/inject/borrowed/deduction/{userId}/{size}",
				 headers = {"proxy-rule=inject"})
	Boolean deductionOfBorrowed(@PathVariable Long userId, @PathVariable Integer size);
	
	/**
	 * 使用cardId查询用户
	 *
	 * @param cardId cardId
	 * @return user
	 */
	@GetMapping(value = "/inject/user/id",
				headers = {"proxy-rule=inject"})
	user queryUser(@RequestParam("cardId") Long cardId);
}
