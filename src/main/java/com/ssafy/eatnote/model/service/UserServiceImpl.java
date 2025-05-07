package com.ssafy.eatnote.model.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.eatnote.model.dao.MemberDao;
import com.ssafy.eatnote.model.dao.TrainerDao;
import com.ssafy.eatnote.model.dao.UserDao;
import com.ssafy.eatnote.model.dto.MemberDetails;
import com.ssafy.eatnote.model.dto.TrainerDetails;
import com.ssafy.eatnote.model.dto.User;
import com.ssafy.eatnote.model.dto.request.UserRegisterRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserDao userDao;
    private final TrainerDao trainerDao;
    private final MemberDao memberDao;
    private final PasswordEncoder passwordEncoder;

    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @Override
    @Transactional
    public boolean registerUser(UserRegisterRequest request, MultipartFile file) {
    	// 이메일 중복 검사
        if (userDao.existsByEmail(request.getEmail())) {
            return false;
        }

        // 닉네임 중복 검사
        if (userDao.countByNickname(request.getNickname()) > 0) {
            return false;
        }

        // 비밀번호 암호화
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // 프로필 이미지 경로 설정
        String profileImagePath = "/images/default_profile.png";

        // 이미지 업로드 처리
        if (file != null && !file.isEmpty()) {
            try {
            	String fileName = "profile_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();

                // ✅ 디렉토리 존재 확인 및 생성
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs(); // 없으면 생성!!
                }

                File dest = new File(dir, fileName);
                file.transferTo(dest);

                profileImagePath = "/uploads/profile/" + fileName;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        // User 객체 생성 및 저장
        User user = User.builder()
                .email(request.getEmail())
                .password(hashedPassword)
                .name(request.getName())
                .nickname(request.getNickname())
                .gender(request.getGender())
                .userType(request.getUserType())
                .status("ACTIVE")
                .profileImage(profileImagePath)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        boolean userResult = userDao.insertUser(user);
        if (!userResult) return false;
        
        Long userId = user.getUserId();

        // 상세 정보 저장
        if (request.getUserType() == 2 && request.getTrainerDetails() != null) {
        	MemberDetails md = request.getMemberDetails();
            md.setUserId(userId);
            boolean mdResult = memberDao.insertMemberDetails(md);
            if (!mdResult) return false;
        } else if (request.getUserType() == 1 && request.getMemberDetails() != null) {
            TrainerDetails td = request.getTrainerDetails();
            td.setUserId(userId);
            boolean tdResult = trainerDao.insertTrainerDetails(td);
            if (!tdResult) return false;
        }

        return true;
    }

//    @Override
//    public User login(String userId, String userPwd) throws Exception {
//        return userDao.selectUserByIdAndPwd(userId, userPwd);
//    }

	@Override
	public User getUserById(Long userId) {
		return userDao.selectUserById(userId);
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
	public boolean updateUser(User user) {
		return userDao.updateUser(user);
	}

	@Override
	public boolean deleteUser(Long userId) {
		return userDao.deleteUserById(userId);
	}

	@Override
	public boolean isEmailDuplicated(String email) {
		return userDao.existsByEmail(email);
	}
	
	@Override
	public boolean isNicknameAvailable(String nickname) {
	    return userDao.countByNickname(nickname) == 0;
	}
	
}