package com.watsoo.dms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.watsoo.dms.dto.PaginatedRequestDto;
import com.watsoo.dms.entity.Vehicle;

import jakarta.persistence.criteria.Predicate;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

	public static Specification<Vehicle> search(PaginatedRequestDto paginatedRequest) {
		return (root, cq, cb) -> {
			Predicate predicate = cb.conjunction();

			if (paginatedRequest.getVehicleName() != null) {
				predicate = cb.and(predicate, cb.equal(root.get("name"), paginatedRequest.getVehicleName()));
			}
			if (paginatedRequest.getVehicleNo() != null) {
				predicate = cb.and(predicate, cb.equal(root.get("vehicleNumber"), paginatedRequest.getVehicleNo()));
			}

			return predicate;
		};
	}

	Page<Vehicle> findAll(Specification<Vehicle> parentData, Pageable pageable);

	default Page<Vehicle> findAll(PaginatedRequestDto paginatedRequest, Pageable pageable) throws Exception {
		try {
			return findAll(search(paginatedRequest), pageable);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error occurred while fetching data.");

		}
	}

}
