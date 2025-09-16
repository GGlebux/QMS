package most.qms.services;


import jakarta.persistence.EntityNotFoundException;
import most.qms.dtos.requests.UserRequest;
import most.qms.dtos.responses.UserResponse;
import most.qms.exceptions.VerificationException;
import most.qms.models.User;
import most.qms.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepo;
    private final ModelMapper mapper;
    private final PhoneVerificationService verifyService;

    @Autowired
    public UserService(UserRepository userRepo, ModelMapper mapper, PhoneVerificationService verifyService) {
        this.userRepo = userRepo;
        this.mapper = mapper;
        this.verifyService = verifyService;
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public User findEntityById(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with id=%d not found!".formatted(userId)));
    }
    public ResponseEntity<UserResponse> findDtoById(Long userId){
        return ok(convertToDto(this.findEntityById(userId)));
    }

    @Transactional
    public ResponseEntity<UserResponse> create(UserRequest dto) {
        boolean isExist = userRepo.existsByPhoneNumber(dto.getPhoneNumber());
        if (isExist) {
            throw new RuntimeException("User with number %s already exists!"
                    .formatted(dto.getPhoneNumber()));
        }
        User saved = userRepo.save(convertToEntity(dto));
        return ok(convertToDto(saved));
    }

    @Transactional
    public void save(User entity) {
        userRepo.save(entity);
    }

    @Transactional
    public void sendCodeToUserPhone(Long userId) {
        User user = this.findEntityById(userId);
        this.isPhoneVerifiedOrElseThrow(user);
        verifyService.sendVerificationCode(user.getPhoneNumber());
    }

    @Transactional
    public void verifyCode(Long userId, String code) {
        User user = this.findEntityById(userId);
        this.isPhoneVerifiedOrElseThrow(user);
        verifyService.verifyCode(user.getPhoneNumber(), code);
        user.setIsPhoneVerified(true);
        userRepo.save(user);
    }

    @Transactional
    public User update(Long id, UserRequest dto) {
        User dbEntity = findEntityById(id);
        return userRepo.save(convertToEntity(dbEntity, dto));
    }

    @Transactional
    public void delete(Long id) {
        userRepo.deleteById(id);
    }


    private User convertToEntity(UserRequest dto) {
        User user = new User();
        mapper.map(dto, user);
        return user;
    }

    private User convertToEntity(User entity, UserRequest dto) {
        if (!entity.getPhoneNumber().equals(dto.getPhoneNumber())) {
            entity.setIsPhoneVerified(false);
        }
        entity.setName(dto.getName());
        entity.setPhoneNumber(dto.getPhoneNumber());
        return entity;
    }

    private UserResponse convertToDto(User user){
        return mapper.map(user, UserResponse.class);
    }

    private void isPhoneVerifiedOrElseThrow(User user) {
        if (user.getIsPhoneVerified()) {
            throw new VerificationException(
                    "User with id=%d already verified!"
                            .formatted(user.getId()));
        }
    }

}
