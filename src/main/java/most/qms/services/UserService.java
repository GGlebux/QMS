package most.qms.services;


import jakarta.persistence.EntityNotFoundException;
import most.qms.dtos.requests.LoginRequest;
import most.qms.dtos.requests.UserRequest;
import most.qms.dtos.responses.UserResponse;
import most.qms.exceptions.AuthException;
import most.qms.exceptions.VerificationException;
import most.qms.models.User;
import most.qms.models.UserStatus;
import most.qms.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static most.qms.models.UserStatus.ACTIVE;
import static most.qms.models.UserStatus.PENDING;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

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

    public ResponseEntity<UserResponse> findDtoById(Long userId) {
        return ok(convertToDto(this.findEntityById(userId)));
    }


    @Transactional
    public ResponseEntity<UserResponse> create(UserRequest dto) {
        User toSave;
        String phoneNumber = dto.getPhoneNumber();
        Optional<User> optional = userRepo.findByPhoneNumber(phoneNumber);

        if (optional.isEmpty()) {
            toSave = convertToEntity(dto);
        } else {
            User fromDB = optional.get();
            switch (fromDB.getStatus()) {
                case ACTIVE -> throw new AuthException(
                        "Active user with number %s already exists!"
                                .formatted(phoneNumber));
                default -> toSave = convertToEntity(fromDB, dto);
            }
        }

        verifyService.sendVerificationCode(phoneNumber);
        return ok(convertToDto(userRepo.save(toSave)));
    }

    public ResponseEntity<String> login(LoginRequest login) {
        String phoneNumber = login.getPhoneNumber();
        User user = userRepo
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User with phone number '%s' not found!"
                                        .formatted(phoneNumber)));

        if (user.getStatus().equals(PENDING)){
            throw new AuthException("User with phone number '%s' is not verified!");
        }

        return switch (login.getPassword().equals(user.getPassword())) {
            case true -> ok("Success login");
            case false -> status(FORBIDDEN).body("Invalid login or password!");
        };
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
    public void verifyUserCode(Long userId, String code) {
        User user = this.findEntityById(userId);
        this.isPhoneVerifiedOrElseThrow(user);
        verifyService.verifyCode(user.getPhoneNumber(), code);
        user.setStatus(ACTIVE);
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

    // ToDo: дополнить для всех случаев!
    private User convertToEntity(User entity, UserRequest dto) {
        if (!entity.getPhoneNumber().equals(dto.getPhoneNumber())) {
            entity.setIsPhoneVerified(false);
        }
        entity.setName(dto.getName());
        entity.setPhoneNumber(dto.getPhoneNumber());
        return entity;
    }

    private UserResponse convertToDto(User user) {
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
