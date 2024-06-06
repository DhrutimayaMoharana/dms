package com.watsoo.dms.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.watsoo.dms.dto.PaginatedRequestDto;
import com.watsoo.dms.entity.Command;

import jakarta.persistence.criteria.Predicate;

public interface CommandRepository extends JpaRepository<Command, Long> {

	

	public static Specification<Command> search(PaginatedRequestDto paginatedRequest) {
		return (root, cq, cb) -> {
			Predicate predicate = cb.conjunction();

			if (paginatedRequest.getDeviceModel() != null) {
				predicate = cb.and(predicate,
						cb.equal(root.get("ddevicModelNumber"), paginatedRequest.getDeviceModel()));
			}

			return predicate;
		};
	}

	Page<Command> findAll(Specification<Command> parentData, Pageable pageable);

	default Page<Command> findAll(PaginatedRequestDto paginatedRequest, Pageable pageable) throws Exception {
		try {
			return findAll(search(paginatedRequest), pageable);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error occurred while fetching data.");

		}
	}

}
