package com.ssafy.ganhoho.domain.schedule.repository;

import com.ssafy.ganhoho.domain.schedule.entity.OCRSchedule;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface OCRScheduleRepository  extends MongoRepository<OCRSchedule, String> {
    // 회원 ID로 OCR 스케줄 조회
    @Query("{'memberId': ?0}")
    List<OCRSchedule> findByMemberId(Long memberId);


    // 회원 ID로 OCR 스케줄 삭제 (이후 추가 업로드 들어왔을떄 사용)
    @Query(value = "{ 'memberId': ?0 }", delete = true)
    void deleteByMemberId(Long memberId);
}
