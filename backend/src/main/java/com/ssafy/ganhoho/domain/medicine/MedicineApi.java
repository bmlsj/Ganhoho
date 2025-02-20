package com.ssafy.ganhoho.domain.medicine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@Slf4j
@RestController
@Component
public class MedicineApi {
    private static final String BASE_URL = "https://apis.data.go.kr/1471000/DrugPrdtPrmsnInfoService06/getDrugPrdtPrmsnDtlInq05";
    private static final String SERVICE_KEY = "7hw6itQ0XsLQvJpbmMEBmRnN48OXxRf3SzUE5FpM3zb/FY0N2Q45MR5PUMk1PeNNhJJm9omcPNWHShD9Hs/G6g==";
    private static final String IMAGE_URL = "http://apis.data.go.kr/1471000/MdcinGrnIdntfcInfoService01/getMdcinGrnIdntfcInfoList01";

    @Value("${fastapi.server.url}")
    private String fastApiServerUrl;

    @GetMapping("/api/medicines/{itemSeq}")
    public ResponseEntity<Object> getMedicineById(@PathVariable String itemSeq) {
        try {
            log.info("의약품 일련번호로 검색 시작: {}", itemSeq);
            
            String urlStr = BASE_URL + "?"
                    + "serviceKey=" + SERVICE_KEY
                    + "&pageNo=1"
                    + "&numOfRows=10"
                    + "&type=json"
                    + "&item_seq=" + itemSeq;

            log.info("요청 URL: {}", urlStr);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters()
                    .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

            ResponseEntity<String> response = restTemplate.getForEntity(urlStr, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(response.getBody());
                
                Map<String, Object> medicineData = new HashMap<>();
                JsonNode item = rootNode.path("body").path("items").get(0);
                
                // 기본 정보
                medicineData.put("ITEM_SEQ", item.path("ITEM_SEQ").asText());
                medicineData.put("ITEM_NAME", item.path("ITEM_NAME").asText());
                medicineData.put("ENTP_NAME", item.path("ENTP_NAME").asText());
                medicineData.put("ETC_OTC_CODE", item.path("ETC_OTC_CODE").asText());
                medicineData.put("CHART", item.path("CHART").asText());
                medicineData.put("STORAGE_METHOD", item.path("STORAGE_METHOD").asText());
                medicineData.put("VALID_TERM", item.path("VALID_TERM").asText());
                medicineData.put("NEWDRUG_CLASS_NAME", item.path("NEWDRUG_CLASS_NAME").asText());
                
                // 문서 데이터
                medicineData.put("EE_DOC_DATA", item.path("EE_DOC_DATA").asText());
                medicineData.put("UD_DOC_DATA", item.path("UD_DOC_DATA").asText());
                medicineData.put("NB_DOC_DATA", item.path("NB_DOC_DATA").asText());
                medicineData.put("PN_DOC_DATA", item.path("PN_DOC_DATA").asText());
                
                String itemName = item.path("ITEM_NAME").asText();
                String imageUrl = getImageUrl(itemName);
                medicineData.put("ITEM_IMAGE", imageUrl);
                
                return ResponseEntity.ok(medicineData);
            } else {
                return ResponseEntity.status(response.getStatusCode())
                        .body(Map.of("error", "API 호출 실패: " + response.getStatusCode()));
            }

        } catch (Exception e) {
            log.error("의약품 검색 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "검색 중 오류 발생: " + e.getMessage()));
        }
    }

    @GetMapping("/api/medicines/search")
    public ResponseEntity<Object> searchMedicineByName(@RequestParam String itemName) {
        try {
            log.info("의약품 검색 시작: {}", itemName);
            String encodedItemName = URLEncoder.encode(itemName, StandardCharsets.UTF_8);
            
            String urlStr = BASE_URL + "?"
                    + "serviceKey=" + SERVICE_KEY
                    + "&pageNo=1"
                    + "&numOfRows=10"
                    + "&type=json"
                    + "&item_name=" + encodedItemName;

            log.info("요청 URL: {}", urlStr);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters()
                    .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

            ResponseEntity<String> response = restTemplate.getForEntity(urlStr, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(response.getBody());
                
                List<Map<String, Object>> medicineList = new ArrayList<>();
                JsonNode items = rootNode.path("body").path("items");
                
                for (JsonNode item : items) {
                    Map<String, Object> medicineData = new HashMap<>();
                    
                    // 기본 정보
                    medicineData.put("ITEM_SEQ", item.path("ITEM_SEQ").asText());
                    medicineData.put("ITEM_NAME", item.path("ITEM_NAME").asText());
                    medicineData.put("ENTP_NAME", item.path("ENTP_NAME").asText());
                    medicineData.put("ETC_OTC_CODE", item.path("ETC_OTC_CODE").asText());
                    medicineData.put("CHART", item.path("CHART").asText());
                    medicineData.put("STORAGE_METHOD", item.path("STORAGE_METHOD").asText());
                    medicineData.put("VALID_TERM", item.path("VALID_TERM").asText());
                    medicineData.put("NEWDRUG_CLASS_NAME", item.path("NEWDRUG_CLASS_NAME").asText());
                    
                    // 문서 데이터
                    medicineData.put("EE_DOC_DATA", item.path("EE_DOC_DATA").asText());
                    medicineData.put("UD_DOC_DATA", item.path("UD_DOC_DATA").asText());
                    medicineData.put("NB_DOC_DATA", item.path("NB_DOC_DATA").asText());
                    medicineData.put("PN_DOC_DATA", item.path("PN_DOC_DATA").asText());
                    
                    String medicineName = item.path("ITEM_NAME").asText();
                    String imageUrl = getImageUrl(medicineName);
                    medicineData.put("ITEM_IMAGE", imageUrl);
                    
                    medicineList.add(medicineData);
                }
                
                return ResponseEntity.ok(Map.of("items", medicineList));
            } else {
                return ResponseEntity.status(response.getStatusCode())
                        .body(Map.of("error", "API 호출 실패: " + response.getStatusCode()));
            }

        } catch (Exception e) {
            log.error("의약품 검색 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "검색 중 오류 발생: " + e.getMessage()));
        }
    }

    @PostMapping("/api/medicines/upload-image")
    public ResponseEntity<Map<String, Object>> uploadMedicineImage(@RequestParam("imageFile") MultipartFile imageFile) {
        if (imageFile.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "파일이 업로드되지 않았습니다."));
        }

        try {
            log.info("1. FastAPI 이미지 분석 시작");
            
            RestTemplate restTemplate = new RestTemplate();
            String fastApiUrl = fastApiServerUrl + "/fastapi/";
            log.info("2. FastAPI URL: {}", fastApiUrl);
            
            ByteArrayResource imageResource = new ByteArrayResource(imageFile.getBytes()) {
                @Override
                public String getFilename() {
                    return imageFile.getOriginalFilename();
                }
            };
            
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", imageResource);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            log.info("3. FastAPI 서버로 이미지 전송 시작");
            ResponseEntity<Map> aiResponse = restTemplate.postForEntity(fastApiUrl, requestEntity, Map.class);
            log.info("4. FastAPI 서버 응답 수신: {}", aiResponse.getBody());
            
            Map<String, Object> responseBody = aiResponse.getBody();
            
            if (responseBody == null || 
                !Boolean.TRUE.equals(responseBody.get("success")) || 
                responseBody.get("detections") == null) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "의약품을 인식하지 못했습니다. 다시 시도해주세요.",
                    "aiResult", responseBody
                ));
            }
            
            Map<String, String> detections = (Map<String, String>) responseBody.get("detections");
            String medicineName = detections.get("name");
            
            if (medicineName == null || medicineName.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "의약품 이름을 추출하지 못했습니다.",
                    "aiResult", responseBody
                ));
            }
            
            // mg 제거하고 모든 공백 제거
            medicineName = medicineName.replaceAll("\\s*[mM][gG]\\b", "")  // mg 제거
                              .replaceAll("\\s+", "");  // 모든 공백 제거
            
            log.info("5. 의약품 정보 검색 시작. 정제된 의약품 이름: {}", medicineName);
            
            String encodedItemName = URLEncoder.encode(medicineName, StandardCharsets.UTF_8);
            String urlStr = BASE_URL + "?"
                    + "serviceKey=" + SERVICE_KEY
                    + "&pageNo=1"
                    + "&numOfRows=10"
                    + "&type=json"
                    + "&item_name=" + encodedItemName;

            log.info("의약품 API 요청 URL: {}", urlStr);
            
            restTemplate = new RestTemplate();
            restTemplate.getMessageConverters()
                    .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
            
            ResponseEntity<String> medicineResponse = restTemplate.getForEntity(urlStr, String.class);
            
            // JSON 파싱 및 데이터 추출
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(medicineResponse.getBody());
            JsonNode items = rootNode.path("body").path("items");
            
            List<Map<String, Object>> medicineList = new ArrayList<>();
            if (items.isArray() && items.size() > 0) {
                for (JsonNode item : items) {
                    Map<String, Object> medicineInfo = new HashMap<>();
                    medicineInfo.put("ITEM_SEQ", item.path("ITEM_SEQ").asText());
                    medicineInfo.put("ITEM_NAME", item.path("ITEM_NAME").asText());
                    medicineInfo.put("ENTP_NAME", item.path("ENTP_NAME").asText());
                    medicineInfo.put("ETC_OTC_CODE", item.path("ETC_OTC_CODE").asText());
                    medicineInfo.put("CHART", item.path("CHART").asText());
                    medicineInfo.put("STORAGE_METHOD", item.path("STORAGE_METHOD").asText());
                    medicineInfo.put("VALID_TERM", item.path("VALID_TERM").asText());
                    medicineInfo.put("NEWDRUG_CLASS_NAME", item.path("NEWDRUG_CLASS_NAME").asText());
                    medicineInfo.put("EE_DOC_DATA", item.path("EE_DOC_DATA").asText());
                    medicineInfo.put("UD_DOC_DATA", item.path("UD_DOC_DATA").asText());
                    medicineInfo.put("NB_DOC_DATA", item.path("NB_DOC_DATA").asText());
                    medicineInfo.put("PN_DOC_DATA", item.path("PN_DOC_DATA").asText());
                    medicineInfo.put("ITEM_IMAGE", getImageUrl(item.path("ITEM_NAME").asText()));
                    medicineList.add(medicineInfo);
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("aiResult", responseBody);
            response.put("medicineInfo", medicineList);
            response.put("detectedName", medicineName);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("이미지 처리 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of(
                    "success", false,
                    "error", "이미지 처리 중 오류 발생: " + e.getMessage()
                ));
        }
    }

    private String getImageUrl(String itemName) {
        try {
            String encodedItemName = URLEncoder.encode(itemName, StandardCharsets.UTF_8);
            String imageUrlStr = IMAGE_URL + "?"
                    + "serviceKey=" + SERVICE_KEY
                    + "&pageNo=1"
                    + "&numOfRows=1"
                    + "&type=json"
                    + "&item_name=" + encodedItemName;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters()
                    .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

            ResponseEntity<String> response = restTemplate.getForEntity(imageUrlStr, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(response.getBody());
                JsonNode items = rootNode.path("body").path("items");
                
                if (items.isArray() && items.size() > 0) {
                    return items.get(0).path("ITEM_IMAGE").asText();
                }
            }
        } catch (Exception e) {
            log.error("이미지 URL 조회 중 오류 발생: {}", e.getMessage(), e);
        }
        return "";
    }
}

