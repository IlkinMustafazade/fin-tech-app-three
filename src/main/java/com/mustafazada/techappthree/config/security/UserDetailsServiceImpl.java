package com.mustafazada.techappthree.config.security;

import com.mustafazada.techappthree.dto.response.CommonResponseDTO;
import com.mustafazada.techappthree.dto.response.Status;
import com.mustafazada.techappthree.dto.response.StatusCode;
import com.mustafazada.techappthree.entity.TechUser;
import com.mustafazada.techappthree.exception.NoSuchUserExist;
import com.mustafazada.techappthree.repository.UserRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    Logger logger;

    @Override
    public UserDetails loadUserByUsername(String pin) throws UsernameNotFoundException {
        Optional<TechUser> byPin = userRepository.findByPin(pin);
        if (byPin.isPresent())
            return new UserDetailsImpl(byPin.get());
        else {
            logger.error("There is no user with pin: " + pin);
            throw NoSuchUserExist.builder().responseDTO(CommonResponseDTO.builder().status(Status.builder()
                    .statusCode(StatusCode.USER_NOT_EXIST)
                    .message("There is no user with pin: " + pin)
                    .build()).build()).build();
        }
    }
}
