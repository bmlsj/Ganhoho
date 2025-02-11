package com.ssafy.ganhoho.domain.schedule.repository;

import com.ssafy.ganhoho.domain.schedule.entity.OCRSchedule;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OCRScheduleRepository {
    @Query(value = "{'memberId': ?0}")
    List<OCRSchedule> findAllByMemberId(Long memberId);

}
