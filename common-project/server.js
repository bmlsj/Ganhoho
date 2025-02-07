import express from 'express';

const jsonServer = await import('json-server');

const server = express();
const router = jsonServer.default.router('db.json'); // json-server 모듈 동적 임포트
const middlewares = jsonServer.default.defaults();

server.use(middlewares);

// `/api/schedules/ocr` 엔드포인트를 `/schedules`로 리디렉션
server.use('/api/schedules/ocr', (req, res, next) => {
  req.url = '/schedules';
  next();
});

// JSON Server 라우터 적용
server.use(router);

// 서버 실행
const PORT = 5000;
server.listen(PORT, () => {
  console.log(`JSON Server is running at http://localhost:${PORT}`);
});
