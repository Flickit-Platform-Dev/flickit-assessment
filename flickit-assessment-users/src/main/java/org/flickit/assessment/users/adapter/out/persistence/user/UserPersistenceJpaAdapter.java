package org.flickit.assessment.users.adapter.out.persistence.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;
import org.flickit.assessment.data.jpa.users.user.UserJpaRepository;
import org.flickit.assessment.users.application.domain.User;
import org.flickit.assessment.users.application.port.out.user.LoadUserEmailByUserIdPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserProfilePort;
import org.flickit.assessment.users.application.port.out.user.UpdateUserPort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.UPDATE_USER_ID_NOT_FOUND;
import static org.flickit.assessment.users.common.ErrorMessageKey.USER_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class UserPersistenceJpaAdapter implements
    LoadUserEmailByUserIdPort,
    LoadUserPort,
    LoadUserProfilePort,
    UpdateUserPort {

    private final UserJpaRepository repository;

    @Override
    public String loadEmail(UUID userId) {
        return repository.findEmailByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException(USER_ID_NOT_FOUND));
    }

    @Override
    public Optional<UUID> loadUserIdByEmail(String email) {
        return repository.findUserIdByEmail(email.toLowerCase());
    }

    @Override
    public User loadUserProfile(UUID id) {
        UserJpaEntity userEntity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(USER_ID_NOT_FOUND));

        return UserMapper.mapToDomainModel(userEntity);
    }

    @Override
    public User updateUser(Param param) {
        UserJpaEntity userEntity = repository.findById(param.userId())
            .orElseThrow(() -> new ResourceNotFoundException(UPDATE_USER_ID_NOT_FOUND));
        userEntity.setDisplayName(param.displayName());
        userEntity.setBio(param.bio());
        userEntity.setLinkedin(param.linkedin());
        UserJpaEntity savedUserEntity = repository.save(userEntity);

        return UserMapper.mapToDomainModel(savedUserEntity);
    }
}

