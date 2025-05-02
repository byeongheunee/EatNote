package com.ssafy.eatnote.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssafy.eatnote.model.dao.UserDao;
import com.ssafy.eatnote.model.dto.User;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao; // UserDao 주입받기

	@Override
	public void registerUser(User user) {
		userDao.insertUser(user);
	}

//    @Override
//    public User login(String userId, String userPwd) throws Exception {
//        return userDao.selectUserByIdAndPwd(userId, userPwd);
//    }

	@Override
	public User getUserById(Long userId) {
		return userDao.selectUserBySeq(userId);
	}

	@Override
	public User getUserByEmail(String email) {
		return userDao.selectUserByEmail(email);
	}

	@Override
	public List<User> getAllUsers() {
		return userDao.selectAllUsers();
	}

	@Override
	public void updateUser(User user) {
		userDao.updateUser(user);
	}

	@Override
	public void deleteUser(Long userId) {
		userDao.deleteUserById(userId);
	}

}