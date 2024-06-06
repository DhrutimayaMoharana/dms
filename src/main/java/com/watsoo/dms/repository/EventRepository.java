package com.watsoo.dms.repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.watsoo.dms.dto.PaginatedRequestDto;
import com.watsoo.dms.entity.Event;
import com.watsoo.dms.enums.EventType;

import jakarta.persistence.criteria.Predicate;

public interface EventRepository extends JpaRepository<Event, Long> {

	@Query(value = "SELECT * FROM event e WHERE e.event_time BETWEEN :startDate AND :endDate", nativeQuery = true)
	List<Event> findEventsBetweenDates(@Param("startDate") String startDate, @Param("endDate") String endDate);

	public static Specification<Event> search(PaginatedRequestDto paginatedRequest) {
		return (root, cq, cb) -> {
			Predicate predicate = cb.conjunction();

			if (paginatedRequest.getFromDate() != null && paginatedRequest.getToDate() != null) {
			    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			    try {
			        Date fromDate = dateFormat.parse(paginatedRequest.getFromDate());
			        Date toDate = dateFormat.parse(paginatedRequest.getToDate());
			        
			        Calendar calendar = Calendar.getInstance();
					calendar.setTime(toDate);
					calendar.set(Calendar.HOUR_OF_DAY, 23);
			        calendar.set(Calendar.MINUTE, 59);
			        calendar.set(Calendar.SECOND, 59);
			        calendar.set(Calendar.MILLISECOND, 999);
			        toDate = calendar.getTime();
			        // For fromDate and toDate
			        predicate = cb.and(
			            predicate,
			            cb.greaterThanOrEqualTo(root.get("eventServerCreateTime"), fromDate),
			            cb.lessThanOrEqualTo(root.get("eventServerCreateTime"), toDate)
			        );
			    } catch (ParseException e) {
			    }
			}

			

			if (paginatedRequest.getDlNumber() != null) {
				predicate = cb.and(predicate, cb.equal(root.get("dlNo"), paginatedRequest.getDlNumber()));
			}

			if (paginatedRequest.getDriverName() != null) {
				predicate = cb.and(predicate, cb.equal(root.get("driverName"), paginatedRequest.getDriverName()));
			}
			if (paginatedRequest.getVehicleNo() != null) {
				predicate = cb.and(predicate, cb.equal(root.get("vehicleNo"), paginatedRequest.getVehicleNo()));
			}
			if (paginatedRequest.getEventType() != null) {
				try {
					EventType eventType = EventType.valueOf(paginatedRequest.getEventType());
					predicate = cb.and(predicate, cb.equal(root.get("eventType"), eventType));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}

			if (paginatedRequest.getSearchKey() != null && !paginatedRequest.getSearchKey().equals("")) {
				Predicate searchPredicate = cb.or(
						cb.like(root.get("driverName"), "%" + paginatedRequest.getSearchKey() + "%"),
						cb.like(root.get("vehicleNo"), "%" + paginatedRequest.getSearchKey() + "%"),
						cb.like(root.get("eventType"), "%" + paginatedRequest.getSearchKey() + "%"));

				predicate = cb.and(predicate, searchPredicate);
			}

			return predicate;
		};
	}

	Page<Event> findAll(Specification<Event> parentData, Pageable pageable);

	default Page<Event> findAll(PaginatedRequestDto paginatedRequest, Pageable pageable) throws Exception {
		try {
			return findAll(search(paginatedRequest), pageable);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error occurred while fetching data.");

		}
	}

}
