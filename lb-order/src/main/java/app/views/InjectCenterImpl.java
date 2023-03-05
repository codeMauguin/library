package app.views;

import app.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public non-sealed class InjectCenterImpl implements InjectCenter {
	private final AdminService adminService;
	
	public InjectCenterImpl(@Autowired AdminService adminService) {
		this.adminService =
				adminService;
	}
	
	@Override
	public Integer getBorrowed(Long id) {
		return adminService.getBorrowed(id);
	}
}
