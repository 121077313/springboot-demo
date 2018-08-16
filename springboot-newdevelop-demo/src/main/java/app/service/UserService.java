package app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.dao.UserDao;
import app.po.User;

@Service
public class UserService {

	@Autowired
	UserDao userDao;

	public void addGold(int gold, User user) {

		user.gold += gold;

		userDao.updateUser(user);
	}

	public User getUser(Integer id) {

		return userDao.getUser(id);
	}

	public User saveUser(String name, int gold) {

		User user = new User();
		user.name = name;
		user.gold = gold;
		userDao.saveUser(user);
		return user;
	}

}
