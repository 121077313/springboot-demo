package app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import app.po.User;

@Repository
public class UserDao {

	Logger log = LoggerFactory.getLogger(UserDao.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Cacheable(value = "User", key = "#id")
	public User getUser(Integer id) {
//		List<User>list= jdbcTemplate.queryForList("select * from user where id = ?",
//				User.class, id);
		log.error("从数据库查询:" + id);
//		return list.size()==1?list.get(0):null;
	
		return jdbcTemplate.queryForObject("select * from user where id = ?",
				new BeanPropertyRowMapper<User>(User.class), id);
	}

	@CachePut(value = "User", key = "#user.id")
	public User saveUser(User user) {
		log.error("从数据库save:");
		// jdbcTemplate.update("insert into user(name, gold) values(?, ?)",
		// user.name, user.gold);

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(
						"insert into user(name, gold) values(?, ?)",
						Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, user.name);
				ps.setInt(2, user.gold);
				return ps;
			}
		}, keyHolder);
		int generatedId = keyHolder.getKey().intValue();
		user.id = generatedId;

		return user;
	}

	@CachePut(value = "User", key = "#user.id")
	public User updateUser(User user) {
		log.error("从数据库update:");
		int uid = jdbcTemplate.update(
				"update user set name = ?, gold = ? where id = ?", user.name,
				user.gold, user.id);
		return user;
	}

}