package com.ssafy.eatnote.model.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.eatnote.model.dao.FollowDao;
import com.ssafy.eatnote.model.dao.MealDao;
import com.ssafy.eatnote.model.dao.MealFeedbackDao;
import com.ssafy.eatnote.model.dao.MemberDao;
import com.ssafy.eatnote.model.dao.TrainerDao;
import com.ssafy.eatnote.model.dao.UserDao;
import com.ssafy.eatnote.model.dto.MemberDetails;
import com.ssafy.eatnote.model.dto.TrainerDetails;
import com.ssafy.eatnote.model.dto.User;
import com.ssafy.eatnote.model.dto.UserFollow;
import com.ssafy.eatnote.model.dto.request.CommonUserUpdateRequest;
import com.ssafy.eatnote.model.dto.request.MemberDetailsRequest;
import com.ssafy.eatnote.model.dto.request.MemberUpdateRequest;
import com.ssafy.eatnote.model.dto.request.TrainerDetailsRequest;
import com.ssafy.eatnote.model.dto.request.TrainerUpdateRequest;
import com.ssafy.eatnote.model.dto.request.UserRegisterRequest;
import com.ssafy.eatnote.model.dto.response.MemberProfileResponse;
import com.ssafy.eatnote.model.dto.response.TrainerProfileResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserDao userDao;
    private final TrainerDao trainerDao;
    private final MemberDao memberDao;
    private final PasswordEncoder passwordEncoder;
    private final FollowDao followDao;
    private final MealDao mealDao;
    private final MealFeedbackDao mealFeedbackDao;

    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @Override
    @Transactional
    public boolean registerUser(UserRegisterRequest request, MultipartFile profileImageFile, MultipartFile certFile) {
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
        if (profileImageFile != null && !profileImageFile.isEmpty()) {
            try {
            	String fileName = "profile_" + System.currentTimeMillis() + "_" + profileImageFile.getOriginalFilename();

                // 디렉토리 존재 확인 및 생성
                File dir = new File(uploadDir + "/profile");
                if (!dir.exists()) {
                    dir.mkdirs(); // 없으면 생성!!
                }

                File dest = new File(dir, fileName);
                profileImageFile.transferTo(dest);

                profileImagePath = "/uploads/profile/" + fileName;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        // User 객체 생성 및 저장
        User user = User.builder()
                .email(request.getEmail())
                .age(request.getAge())
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
        if (request.getUserType() == 2 && request.getMemberDetails() != null) {
        	MemberDetailsRequest req = request.getMemberDetails();
        	
        	MemberDetails md = new MemberDetails();
        	md.setUserId(userId);
            md.setHeight(req.getHeight());
            md.setWeight(req.getWeight());
            md.setBodyWater(req.getBodyWater());
            md.setProtein(req.getProtein());
            md.setMineral(req.getMineral());
            md.setBodyFat(req.getBodyFat());
            md.setSkeletalMuscle(req.getSkeletalMuscle());
            md.setBodyFatMass(req.getBodyFatMass());
            md.setBmi(req.getBmi());
            md.setBodyFatPercentage(req.getBodyFatPercentage());
            md.setGoal(req.getGoal());
            md.setAllergyIds(req.getAllergyIds());
            
            // 트레이너 닉네임 기반 자동 팔로우 생성
            if (req.getTrainerNickname() != null && !req.getTrainerNickname().isBlank()) {
                Optional<User> optionalTrainer = userDao.findByNickname(req.getTrainerNickname());
                if (optionalTrainer.isEmpty() || optionalTrainer.get().getUserType() != 1) {
                    throw new IllegalArgumentException("해당 닉네임의 트레이너가 존재하지 않습니다.");
                }

                Long trainerId = optionalTrainer.get().getUserId();
                md.setTrainerId(trainerId);
                
                // 자동 팔로우 생성
                UserFollow follow = UserFollow.builder()
                        .fromUserId(userId)
                        .toUserId(trainerId)
                        .status("ACCEPTED")
                        .requestedAt(LocalDateTime.now())
                        .build();

                followDao.insertFollowObject(follow);
            }

            boolean mdResult = memberDao.insertMemberDetails(md);
            if (!mdResult) return false;
            
            // 알레르기 정보 저장
            List<Integer> allergyIds = md.getAllergyIds();
            if (allergyIds != null && !allergyIds.isEmpty()) {
                memberDao.insertUserAllergies(userId, allergyIds);
            }
        } else if (request.getUserType() == 1 && request.getTrainerDetails() != null) {
        	TrainerDetailsRequest req = request.getTrainerDetails();

            TrainerDetails td = new TrainerDetails();
            td.setUserId(userId);
            td.setPhone(req.getPhone());
            td.setAddress(req.getAddress());
            td.setGymName(req.getGymName());
            td.setGymWebsite(req.getGymWebsite());
            td.setCertificationNumber(req.getCertificationNumber());
            td.setBusinessNumber(req.getBusinessNumber());
            td.setIntroduction(req.getIntroduction());
            td.setCareer(req.getCareer());
            td.setInstagramUrl(req.getInstagramUrl());

            // 인증 여부는 서버 기본값으로 설정 (초기값 false 또는 검증 후 true)
            td.setIsVerified(false); // 혹은 로직에 따라 true 처리 가능

            // 자격증 이미지 파일 업로드
            if (certFile != null && !certFile.isEmpty()) {
                try {
                    String certFileName = "cert_" + System.currentTimeMillis() + "_" + certFile.getOriginalFilename();
                    File certDir = new File(uploadDir + "/cert");
                    if (!certDir.exists()) certDir.mkdirs();

                    File certDest = new File(certDir, certFileName);
                    certFile.transferTo(certDest);

                    String certificationImagePath = "/uploads/cert/" + certFileName;
                    td.setCertificationImage(certificationImagePath);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            boolean tdResult = trainerDao.insertTrainerDetails(td);
            if (!tdResult) return false;
        }

        return true;
    }

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
	
	@Override
	public boolean isMyNicknameOrAvailable(Long userId, String nickname) {
	    String currentNickname = userDao.findNicknameByUserId(userId);
	    if (nickname.equals(currentNickname)) return true; // 본인 닉네임이면 OK

	    return userDao.countByNickname(nickname) == 0;
	}
	
	@Override
	public boolean checkPassword(Long userId, String inputPassword) {
	    User user = userDao.selectUserById(userId);
	    if (user == null) return false;
	    return passwordEncoder.matches(inputPassword, user.getPassword());
	}
	
	@Override
	@Transactional
	public void updateMember(Long userId, CommonUserUpdateRequest common, MemberUpdateRequest member, MultipartFile file) {
	    // 공통 정보 수정
	    updateCommonUserInfo(userId, common, file);

		// 1. 기존 객체 조회
	    MemberDetails details = memberDao.findByUserId(userId);

	    // 2. 필드 업데이트
	    details.setHeight(member.getHeight());
	    details.setWeight(member.getWeight());
	    details.setBodyWater(member.getBodyWater());
	    details.setProtein(member.getProtein());
	    details.setMineral(member.getMineral());
	    details.setBodyFat(member.getBodyFat());
	    details.setSkeletalMuscle(member.getSkeletalMuscle());
	    details.setBodyFatMass(member.getBodyFatMass());
	    details.setBmi(member.getBmi());
	    details.setBodyFatPercentage(member.getBodyFatPercentage());
	    details.setGoal(member.getGoal());
	    details.setAllergyIds(member.getAllergyIds());

	    memberDao.updateMemberDetails(details);
	    
	    // 알레르기 정보 전체 삭제 후 재등록
	    memberDao.deleteUserAllergies(userId);
	    if (member.getAllergyIds() != null && !member.getAllergyIds().isEmpty()) {
	        memberDao.insertUserAllergies(userId, member.getAllergyIds());
	    }
	}
	
	@Override
	@Transactional
	public void updateTrainer(Long userId, CommonUserUpdateRequest common, TrainerUpdateRequest trainer, MultipartFile profileImageFile, MultipartFile certFile) {
	    // 공통 정보 수정
	    updateCommonUserInfo(userId, common, profileImageFile);

	    // 트레이너 상세 정보 수정
	    TrainerDetails original = trainerDao.findByUserId(userId);

	    // 변경 정보 반영
	    original.setPhone(trainer.getPhone());
	    original.setAddress(trainer.getAddress());
	    original.setGymName(trainer.getGymName());
	    original.setGymWebsite(trainer.getGymWebsite());
	    original.setCertificationNumber(trainer.getCertificationNumber());
	    original.setBusinessNumber(trainer.getBusinessNumber());
	    original.setIntroduction(trainer.getIntroduction());
	    original.setCareer(trainer.getCareer());
	    original.setInstagramUrl(trainer.getInstagramUrl());

	    // 자격증 이미지 파일 처리
	    if (certFile != null && !certFile.isEmpty()) {
	        try {
	            String certFileName = "cert_" + System.currentTimeMillis() + "_" + certFile.getOriginalFilename();
	            File certDir = new File(uploadDir + "/cert");
	            if (!certDir.exists()) certDir.mkdirs();

	            File dest = new File(certDir, certFileName);
	            certFile.transferTo(dest);

	            String certPath = "/uploads/cert/" + certFileName;
	            original.setCertificationImage(certPath);
	        } catch (IOException e) {
	            throw new RuntimeException("자격증 이미지 저장 실패", e);
	        }
	    }
	    
	    trainerDao.updateTrainerDetails(original);
	}
	
	private void updateCommonUserInfo(Long userId, CommonUserUpdateRequest request, MultipartFile file) {
		String encodedPassword = null;
	    if (request.getPassword() != null && !request.getPassword().isBlank()) {
	        encodedPassword = passwordEncoder.encode(request.getPassword());
	    }

	    String profileImagePath;

	    if (file != null && !file.isEmpty()) {
	        try {
	            String fileName = "profile_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
	            File dir = new File(uploadDir + "/profile");
	            if (!dir.exists()) dir.mkdirs();
	            File dest = new File(dir, fileName);
	            file.transferTo(dest);
	            profileImagePath = "/uploads/profile/" + fileName;
	        } catch (IOException e) {
	            throw new RuntimeException("프로필 이미지 저장 실패", e);
	        }
	    } else {
	        // 입력된 파일이 없는경우 기존 경로 유지
	        profileImagePath = userDao.selectUserById(userId).getProfileImage();
	    }
	    
	    userDao.updateUserProfile(userId, encodedPassword, request.getNickname(), profileImagePath);
	}
	
	@Override
    @Transactional
    public void markUserAsDeleted(Long userId) {
        int updatedRows = userDao.updateUserStatus(userId, "DELETED");

        if (updatedRows == 0) {
            throw new IllegalStateException("회원 탈퇴 처리에 실패했습니다. 존재하지 않는 사용자일 수 있습니다.");
        }
    }
	
	@Override
    @Transactional(readOnly = true)
    public MemberProfileResponse getMemberProfile(Long viewerId, Long targetUserId) {
        MemberProfileResponse dto = new MemberProfileResponse();

        // 공통 정보
        var user = userDao.selectUserById(targetUserId);
        dto.setUserId(user.getUserId());
        dto.setUserType(user.getUserType());
        dto.setNickname(user.getNickname());
        dto.setGender(user.getGender());
        dto.setProfileImage(user.getProfileImage());

        // 상세 정보
        var memberDetails = memberDao.findByUserId(targetUserId);
        dto.setGoal(memberDetails.getGoal());
        dto.setAllergyIds(memberDetails.getAllergyIds());
        
        // 연속 기록일
    	List<LocalDate> mealDates = mealDao.findMealDatesByUserId(targetUserId);
    	int consecutiveDays = calculateConsecutiveDays(mealDates);
        
        
        // 통계 정보
        dto.setTotalMeals(mealDao.countByUserId(targetUserId));
        dto.setAverageScore(mealDao.getAverageAutoScore(targetUserId));
        dto.setConsecutiveDays(consecutiveDays);
        dto.setFollowingCount(followDao.countFollowing(targetUserId));
        
        String status = followDao.getFollowStatus(viewerId, targetUserId);
        if (status == null) {
            status = "NONE"; // 아예 팔로우 기록이 없다면
        }
        dto.setFollowStatus(status);

        return dto;
    }
	
	public int calculateConsecutiveDays(List<LocalDate> mealDates) {
        Set<LocalDate> dateSet = new HashSet<>(mealDates);

        // 오늘 식단이 있는지 확인
        LocalDate today = LocalDate.now();
        boolean hasToday = dateSet.contains(today);

        // 시작일: 오늘 포함 여부에 따라 다르게 설정
        LocalDate startDay = hasToday ? today : today.minusDays(1);

        int count = 0;
        LocalDate current = startDay;

        // 연속되는 날짜를 역순으로 체크
        while (dateSet.contains(current)) {
            count++;
            current = current.minusDays(1);
        }

        return count;
    }
	
	@Override
    @Transactional(readOnly = true)
    public TrainerProfileResponse getTrainerProfile(Long viewerId, Long targetUserId) {
        TrainerProfileResponse dto = new TrainerProfileResponse();

        var user = userDao.selectUserById(targetUserId);
        dto.setUserId(user.getUserId());
        dto.setUserType(user.getUserType());
        dto.setNickname(user.getNickname());
        dto.setGender(user.getGender());
        dto.setProfileImage(user.getProfileImage());

        var trainerDetails = trainerDao.findByUserId(targetUserId);
        dto.setIntroduction(trainerDetails.getIntroduction());
        dto.setInstagramUrl(trainerDetails.getInstagramUrl());
        dto.setGymName(trainerDetails.getGymName());
        dto.setGymWebsite(trainerDetails.getGymWebsite());

        dto.setTotalFeedbacks(mealFeedbackDao.countByTrainerId(targetUserId));
        dto.setFollowingCount(followDao.countFollowing(targetUserId));

        String status = followDao.getFollowStatus(viewerId, targetUserId);
        if (status == null) {
            status = "NONE"; // 아예 팔로우 기록이 없다면
        }
        dto.setFollowStatus(status);
        
        return dto;
    }
	
	@Override
	public Integer getUserType(Long userId) {
	    return userDao.selectUserTypeByUserId(userId);
	}
	
	@Override
	public boolean existsTrainerNickname(String nickname) {
	    return userDao.existsTrainerNickname(nickname);
	}
}