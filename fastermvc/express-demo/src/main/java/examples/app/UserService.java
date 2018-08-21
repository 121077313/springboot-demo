package examples.app;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	public User getUser(Integer id) {
		User user = new User();
		
		user.setName("test......................");
		return user;
	}
}
