# 1. Node.js 베이스 이미지 사용
FROM node:lts-alpine AS build-stage

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. package.json과 package-lock.json을 복사 후 의존성 설치
COPY package*.json ./
RUN npm install

# 4. 프로젝트 소스 코드 복사 후 Vue 빌드
COPY . .
RUN npm run build

# 5. Nginx를 사용하여 빌드된 파일을 제공
FROM nginx:alpine AS production-stage

COPY --from=build-stage /app/dist /usr/share/nginx/html

COPY nginx.conf /etc/nginx/nginx.conf

# 6. Nginx 포트 노출
EXPOSE 80

# 7. Nginx 실행
CMD ["nginx", "-g", "daemon off;"]
