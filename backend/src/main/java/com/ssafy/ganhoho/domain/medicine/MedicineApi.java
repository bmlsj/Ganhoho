package com.ssafy.ganhoho.domain.medicine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.springframework.http.converter.StringHttpMessageConverter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class MedicineApi {

    // 절대 경로 사용
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    private static final String API_URL = "https://apis.data.go.kr/1471000/DrugPrdtPrmsnInfoService06/getDrugPrdtPrmsnInq06";
    private static final String SERVICE_KEY = "XL9JhLT9v1u5i%2FVtgewWnWuPjSZhTwSPkvPW8Sq9bQsV%2BPB3iTCcrB8FLs%2F%2Fy0bJ6icVJ9f6m4PaMyA8RhcIvw%3D%3D";

    // 1. 품목일련번호(itemSeq)로 검색
    @GetMapping("/api/medicines/{itemSeq}")
    public Map<String, Object> getMedicineById(@PathVariable String itemSeq) {
        return fetchMedicineDataBySeq(itemSeq);
    }

    // 2. 품목명(itemName)으로 검색
    @GetMapping("/api/medicines/search")
    public Map<String, Object> searchMedicineByName(@RequestParam String itemName) {
        return fetchMedicineDataByName(itemName);
    }

    // 3. 이미지 업로드 (POST /api/medicines/upload-image)
    @PostMapping("/api/medicines/upload-image")
    public ResponseEntity<Map<String, Object>> uploadMedicineImage(@RequestParam("imageFile") MultipartFile imageFile) {
        if (imageFile.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "파일이 업로드되지 않았습니다."));
        }

        try {
            //  업로드 디렉토리 생성 (존재하지 않으면 생성)
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                boolean dirCreated = uploadDir.mkdirs();
                log.info("업로드 폴더 생성 여부: {}", dirCreated);
            }

            // 파일 저장
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            imageFile.transferTo(filePath.toFile());

            log.info("이미지 업로드 성공: {}", filePath.toString());

            return ResponseEntity.ok(Map.of("message", "이미지 업로드 성공", "filePath", filePath.toString()));
        } catch (IOException e) {
            log.error("이미지 업로드 실패: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "이미지 업로드 중 오류 발생: " + e.getMessage()));
        }
    }

    //  API 요청 (itemSeq 기반)
    private Map<String, Object> fetchMedicineDataBySeq(String itemSeq) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(API_URL)
                    .queryParam("serviceKey", SERVICE_KEY)
                    .queryParam("type", "xml")
                    .queryParam("numOfRows", "100000000")
                    .queryParam("itemSeq", itemSeq);

            return callExternalApi(builder, itemSeq, null);
        } catch (Exception e) {
            log.error("API 호출 중 오류 발생: {}", e.getMessage(), e);
            return Map.of("error", "API 호출 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    //  API 요청 (itemName 기반) + URL 인코딩 적용
    private Map<String, Object> fetchMedicineDataByName(String itemName) {
        try {
            String encodedItemName = URLEncoder.encode(itemName, StandardCharsets.UTF_8);
            log.info("Encoded itemName: {}", encodedItemName);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(API_URL)
                    .queryParam("serviceKey", SERVICE_KEY)
                    .queryParam("type", "xml")
                    .queryParam("numOfRows", "100")
                    .queryParam("itemName", encodedItemName);

            return callExternalApi(builder, null, itemName);
        } catch (Exception e) {
            log.error("API 호출 중 오류 발생: {}", e.getMessage(), e);
            return Map.of("error", "API 호출 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // API 호출 및 XML 응답 처리
    private Map<String, Object> callExternalApi(UriComponentsBuilder builder, String targetItemSeq, String targetItemName) {
        try {
            String urlWithParams = builder.build(false).toUriString();
            log.info("API 요청 URL: {}", urlWithParams);

            RestTemplate restTemplate = new RestTemplate();
            StringHttpMessageConverter converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
            converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.TEXT_PLAIN));
            restTemplate.getMessageConverters().set(1, converter);

            String xmlResponse = restTemplate.getForObject(URI.create(urlWithParams), String.class);
            log.info("API 응답: {}", xmlResponse);

            return parseXmlToJson(xmlResponse, targetItemSeq, targetItemName);
        } catch (Exception e) {
            log.error("API 호출 중 오류 발생: {}", e.getMessage(), e);
            return Map.of("error", "API 호출 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    //  XML 응답을 파싱하여 정확한 항목 찾기
    private Map<String, Object> parseXmlToJson(String xml, String targetItemSeq, String targetItemName) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new java.io.ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

            Element root = doc.getDocumentElement();
            NodeList items = root.getElementsByTagName("item");

            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);
                String itemSeq = getTagValue("ITEM_SEQ", item);
                String itemName = getTagValue("ITEM_NAME", item);

                if ((targetItemSeq != null && targetItemSeq.equals(itemSeq)) ||
                        (targetItemName != null && itemName.toLowerCase().trim().equals(targetItemName.toLowerCase().trim()))) {

                    Map<String, Object> medicineData = new HashMap<>();
                    medicineData.put("itemSeq", itemSeq);
                    medicineData.put("itemName", itemName);
                    medicineData.put("ENTP_NAME", getTagValue("ENTP_NAME", item));
                    medicineData.put("PRDUCT_TYPE", getTagValue("PRDUCT_TYPE", item));
                    medicineData.put("SPCLTY_PBLC", getTagValue("SPCLTY_PBLC", item));
                    medicineData.put("STORAGE_METHOD", getTagValue("STORAGE_METHOD", item));
                    medicineData.put("BIG_PRDT_IMG_URL", getTagValue("BIG_PRDT_IMG_URL", item));
                    medicineData.put("ITEM_INGR_NAME", getTagValue("ITEM_INGR_NAME", item));

                    return medicineData;
                }
            }

            return Map.of("message", "검색된 데이터가 없습니다.");
        } catch (Exception e) {
            log.error("XML 파싱 중 오류 발생: {}", e.getMessage(), e);
            return Map.of("error", "XML 파싱 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        return nodeList.getLength() == 0 ? "정보 없음" : nodeList.item(0).getTextContent().trim();
    }
}
