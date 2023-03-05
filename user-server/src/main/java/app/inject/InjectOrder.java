package app.inject;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("order")
public interface InjectOrder {
	
	@GetMapping(value = "/inject/borrowed", headers = {"poxy-rule=inject"})
	@LoadBalanced
	Integer getBorrowed(@RequestParam("id") Long id);
}
