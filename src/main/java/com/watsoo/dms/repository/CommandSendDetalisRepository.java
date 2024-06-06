package com.watsoo.dms.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.watsoo.dms.dto.PaginatedRequestDto;
import com.watsoo.dms.entity.Command;
import com.watsoo.dms.entity.CommandSendDetails;

import jakarta.persistence.criteria.Predicate;

public interface CommandSendDetalisRepository extends JpaRepository<CommandSendDetails, Long> {

	List<CommandSendDetails> findAllByIdIn(Set<Long> commandSendIds);

	public static Specification<CommandSendDetails> search(PaginatedRequestDto paginatedRequest) {
		return (root, cq, cb) -> {
			Predicate predicate = cb.conjunction();

			return predicate;
		};
	}

	Page<CommandSendDetails> findAll(Specification<CommandSendDetails> parentData, Pageable pageable);

	default Page<CommandSendDetails> findAll(PaginatedRequestDto paginatedRequest, Pageable pageable) throws Exception {
		try {
			return findAll(search(paginatedRequest), pageable);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error occurred while fetching data.");

		}
	}

}
