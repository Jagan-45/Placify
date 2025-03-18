package com.murali.placify.Mapper;

import com.murali.placify.entity.Department;
import com.murali.placify.entity.User;
import com.murali.placify.enums.Role;
import com.murali.placify.model.RegistrationDTO;
import com.murali.placify.repository.DepartmentRepository;
import jakarta.validation.ValidationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserMapper {

    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;

    public UserMapper(PasswordEncoder passwordEncoder, DepartmentRepository departmentRepository) {
        this.passwordEncoder = passwordEncoder;
        this.departmentRepository = departmentRepository;
    }

    public User registerDtoToUserMapper(RegistrationDTO registrationDTO){
        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setEnabled(false);
        user.setMailID(registrationDTO.getMailID());
        user.setRole(Role.ROLE_STUDENT);
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));

        String[] mailSplit = registrationDTO.getMailID().split("\\.");

        if(mailSplit.length == 4){
            user.setYear(Integer.parseInt("20" + mailSplit[1].substring(0, 2)));
            Optional<Department> optionalDepartment = departmentRepository.findByDeptName(mailSplit[1].substring(2, 4).toUpperCase());
            System.out.println(mailSplit[1].substring(2, 4).toUpperCase());
            if(optionalDepartment.isEmpty())
                throw new ValidationException("enter valid mail-id, unable to fetch department");
            user.setDepartment(optionalDepartment.get());
        }

        return user;
    }
}
