package com.ssafy.ganhoho.domain.schedule.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.ssafy.ganhoho.domain.schedule.entity.OCRSchedule;

@Repository
public interface OCRScheduleRepository  extends MongoRepository<OCRSchedule, String> {
    // 회원 ID로 OCR 스케줄 조회
    @Query("{'memberId': ?0}")
    List<OCRSchedule> findByMemberId(Long memberId);


    // 회원 ID로 OCR 스케줄 삭제 (이후 추가 업로드 들어왔을떄 사용)
    @Query(value = "{ 'memberId': ?0 }", delete = true)
    void deleteByMemberId(Long memberId);
}
