package com.shopme.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Service
@Transactional
public class UserService {

	public static final int USERS_PER_PAGE = 5;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<User> listAll() {
		return (List<User>) userRepository.findAll(Sort.by("firstName").ascending());
	}
	
	public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword){
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		var page = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);
		
		if(keyword != null)
			return userRepository.findAll(keyword, page);
		
		return userRepository.findAll(page);
	}

	public List<Role> listRoles() {
		return (List<Role>) roleRepository.findAll();
	}

	public User save(User user) {
		setExistingPasswordIfNeeded(user);
		return userRepository.save(user);
	}

	public boolean isEmailUnique(Integer id, String email) {

		var user = userRepository.getUserByEmail(email);

		if (user == null)
			return true;

		if (id == null && user != null)
			return false;

		else {
			if (user.getId() != id)
				return false;
		}

		return true;
	}
	
	public void delete(Integer id) throws UserNotFoundException {
		var countUserById = userRepository.countById(id);
		if(countUserById == null || countUserById == 0)
			throw new UserNotFoundException("Can't found any user with id " + id);
		
		userRepository.deleteById(id);
	}

	public User get(Integer id) throws UserNotFoundException {
		try {
			return userRepository.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new UserNotFoundException("Could not found user");
		}
	}

	public void updateUserEnabledStatus(Integer id, boolean enabled) throws UserNotFoundException {
		var countUserById = userRepository.countById(id);
		if(countUserById == null || countUserById == 0)
			throw new UserNotFoundException("Can't found any user with id " + id);
		
		userRepository.updateUserEnabledStatus(id, enabled);
		
	}
	
	private void setExistingPasswordIfNeeded(User user) {
		if (user.getId() != null && user.getPassword().isEmpty()) {
			user.setPassword(userRepository.findById(user.getId()).get().getPassword());
		} else {
			encodePassword(user);
		}
	}
	
	public User getUserByEmail(String email) {
		return userRepository.getUserByEmail(email);
	}
	
	public User updateUserAccount(User userInForm) {
		User user = userRepository.findById(userInForm.getId()).get();
		
		if(!userInForm.getPassword().isEmpty()) {
			user.setPassword(userInForm.getPassword());
			encodePassword(user);
		}
		
		
		if(userInForm.getPhotos() != null) {
			user.setPhotos(userInForm.getPhotos());
		}
		
		user.setFirstName(userInForm.getFirstName());
		user.setLastName(user.getLastName());
		
		return userRepository.save(user);
		
	}

	private void encodePassword(User user) {
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
	}

}
