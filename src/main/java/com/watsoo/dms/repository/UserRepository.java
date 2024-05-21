package com.watsoo.dms.repository;




import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.watsoo.dms.entity.User;



public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String name);
	
	@Query(value = "select * from user where phone=?1 or email=?2", nativeQuery = true)
	Optional<User> findAllByPhoneNoEmail(String phoneNo, String email);

	List<User> findAll(Specification<User> specification);

	
}