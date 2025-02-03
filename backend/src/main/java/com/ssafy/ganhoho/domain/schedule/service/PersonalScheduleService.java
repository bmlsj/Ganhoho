package com.ssafy.ganhoho.domain.schedule.service;

import com.ssafy.ganhoho.domain.schedule.dto.PersonalScheduleRequestDto;
import com.ssafy.ganhoho.domain.schedule.dto.PersonalScheduleResponseDto;
import com.ssafy.ganhoho.domain.schedule.entity.PersonalSchedule;
import com.ssafy.ganhoho.domain.schedule.entity.ScheduleDetail;
import com.ssafy.ganhoho.domain.schedule.repository.PersonalScheduleRepository;
import com.ssafy.ganhoho.domain.schedule.repository.ScheduleDetailRepository;
import com.ssafy.ganhoho.global.auth.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonalScheduleService {

    private final PersonalScheduleRepository personalScheduleRepository;
    private final ScheduleDetailRepository scheduleDetailRepository;
    private final JWTUtil jwtUtil;

    public PersonalScheduleResponseDto addPersonalSchedule(PersonalScheduleRequestDto requestDto, Long memberId) {
        PersonalSchedule schedule = new PersonalSchedule();
        schedule.setMemberId(memberId);
        personalScheduleRepository.save(schedule);

        // ScheduleDetail 생성 및 저장
        ScheduleDetail detail = new ScheduleDetail();
        detail.setScheduleId(schedule.getScheduleId());
        detail.setStartDt(requestDto.getStartDt() != null ? requestDto.getStartDt() : new Date());
        detail.setEndDt(requestDto.getEndDt() != null ? requestDto.getEndDt() : new Date());
        detail.setScheduleTitle(requestDto.getScheduleTitle());
        detail.setScheduleColor(requestDto.getScheduleColor());
        detail.setIsTimeSet(requestDto.getIsTimeSet() != null ? requestDto.getIsTimeSet() : false);
        detail.setIsPublic(requestDto.getIsPublic() != null ? requestDto.getIsPublic() : false);
        scheduleDetailRepository.save(detail);

        return new PersonalScheduleResponseDto(schedule.getScheduleId(), memberId, List.of(
                new PersonalScheduleResponseDto.ScheduleDetailDto(
                        detail.getDetailId(),
                        detail.getStartDt(),
                        detail.getEndDt(),
                        detail.getScheduleTitle(),
                        detail.getScheduleColor(),
                        detail.getIsTimeSet(),
                        detail.getIsPublic()
                )
        ));
    }

    public List<PersonalScheduleResponseDto> getPersonalSchedules(Long memberId) {
        List<PersonalSchedule> schedules = personalScheduleRepository.findByMemberId(memberId);
        return schedules.stream()
                .map(schedule -> new PersonalScheduleResponseDto(
                        schedule.getScheduleId(),
                        schedule.getMemberId(),
                        null))  // details는 나중에 구현
                .collect(Collectors.toList());
    }

    public PersonalScheduleResponseDto updatePersonalSchedule(Long scheduleId, PersonalScheduleRequestDto requestDto, Long memberId) {
        PersonalSchedule schedule = personalScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        if (!schedule.getMemberId().equals(memberId)) {
            throw new RuntimeException("Unauthorized access");
        }

        // ScheduleDetail 생성 및 저장
        ScheduleDetail detail = new ScheduleDetail();
        detail.setScheduleId(scheduleId);
        detail.setStartDt(requestDto.getStartDt());
        detail.setEndDt(requestDto.getEndDt());
        detail.setScheduleTitle(requestDto.getScheduleTitle());
        detail.setScheduleColor(requestDto.getScheduleColor());
        detail.setIsTimeSet(requestDto.getIsTimeSet());
        detail.setIsPublic(requestDto.getIsPublic());
        scheduleDetailRepository.save(detail);

        personalScheduleRepository.save(schedule);

        return new PersonalScheduleResponseDto(schedule.getScheduleId(), memberId, List.of(
                new PersonalScheduleResponseDto.ScheduleDetailDto(
                        detail.getDetailId(),
                        detail.getStartDt(),
                        detail.getEndDt(),
                        detail.getScheduleTitle(),
                        detail.getScheduleColor(),
                        detail.getIsTimeSet(),
                        detail.getIsPublic()
                )
        ));
    }

    public void deletePersonalSchedule(Long scheduleId, Long memberId) {
        PersonalSchedule schedule = personalScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        if (!schedule.getMemberId().equals(memberId)) {
            throw new RuntimeException("Unauthorized access");
        }

        personalScheduleRepository.delete(schedule);
    }
}