package app.inject;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("reader-service")
public interface InjectBook {
	
	@PostMapping(value = "/inject/stocks", headers = "proxy-rule=inject")
	@LoadBalanced
	List<Integer> queryBooks(@RequestBody List<Long> books);
	
	@PostMapping(value = "/inject/inventory/deduction", headers = "proxy-rule=inject")
	@LoadBalanced
	Boolean deductionOfInventory(@RequestBody List<Long> bookIds);
	
	@PostMapping(value = "/inject/inventory/increment", headers = "proxy-rule=inject")
	@LoadBalanced
	Boolean incrementOfInventory(@RequestBody List<Long> suc);
	
	@GetMapping(value = "/inject/book/name", headers = "proxy-rule=inject")
	@LoadBalanced
	String queryBookName(@RequestParam("bookId") Long bookId);
}
