package com.ssafy.ganhoho.domain.schedule.repository;

import com.ssafy.ganhoho.domain.schedule.entity.OCRSchedule;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OCRScheduleRepository  extends MongoRepository<OCRSchedule, String> {
    // 회원 ID로 OCR 스케줄 조회
    @Query("{'memberId': ?0}")
    List<OCRSchedule> findByMemberId(Long memberId);

}
