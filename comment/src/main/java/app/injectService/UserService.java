package app.injectService;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pojo.user;

@FeignClient("user-service")
@LoadBalancerClient
public interface UserService {
	@GetMapping(value = "/query/user", headers = {"proxy-rule=inject"})
	user query(@RequestParam("id") Long id);
	
	@GetMapping(value = "/inject/cardId", headers = {"proxy-rule=inject"})
	@LoadBalanced
	Long queryCardId(@RequestParam("id") Long id);
}
