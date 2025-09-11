package most.qms.services;


import jakarta.persistence.EntityNotFoundException;
import most.qms.dtos.requests.UserRequest;
import most.qms.models.User;
import most.qms.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Set.of;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepo;
    private final ModelMapper mapper;
    private final PhoneVerificationService verifyService;
    private final GroupService groupService;

    @Autowired
    public UserService(UserRepository userRepo, ModelMapper mapper, PhoneVerificationService verifyService, GroupService groupService) {
        this.userRepo = userRepo;
        this.mapper = mapper;
        this.verifyService = verifyService;
        this.groupService = groupService;
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public User findByUserId(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with id=%d not found!".formatted(userId)));
    }

    public List<User> findByPhoneNumber(String phoneNumber) {
        return userRepo.findByPhoneNumber(phoneNumber);
    }


    @Transactional
    public User save(UserRequest dto) {
        User toSave = convertToEntity(dto);
        toSave.setGroups(of(groupService.findLastAvailable()));
        return userRepo.save(toSave);
    }

    @Transactional
    public void save(User entity) {
        userRepo.save(entity);
    }

    public void sendCodeToUserPhone(Long userId){
        User user = this.findByUserId(userId);
        verifyService.sendVerificationCode(user.getPhoneNumber());
    }

    public Boolean verifyCode(Long userId, String code) {
        User user = this.findByUserId(userId);
        return verifyService.verifyCode(user.getPhoneNumber(), code);
    }

    @Transactional
    public User update(Long id, UserRequest dto) {
        User dbEntity = findByUserId(id);
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

}
