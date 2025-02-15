// 📌 src/utils/mask.js

/**
 * 예: https://i12d209.p.ssafy.io -> https://**** 로 변환
 */
export function maskURL(url) {
  if (!url) return '';
  return url.replace(/^(https?:\/\/[^/]+)/, '$1/****');
}

/**
 * 예: 토큰의 앞 5글자 + ... + 뒤 5글자만 보여주기
 */
export function maskToken(token) {
  if (!token) return '';
  if (token.length <= 10) return '****';
  return token.substring(0, 5) + '...' + token.substring(token.length - 5);
}
