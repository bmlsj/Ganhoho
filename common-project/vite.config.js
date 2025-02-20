import { fileURLToPath, URL } from 'node:url';
import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

// https://vite.dev/config/
export default defineConfig({
  base: "/",
  plugins: [
    vue(),
  ],
  server: {
    proxy: {
      '/api': {
        target: 'https://i12d209.p.ssafy.io',
        changeOrigin: true,
        secure: false,
      },
    },
  },
  define: {
    __VUE_OPTIONS_API__: true,
    __VUE_PROD_DEVTOOLS__: false,
    'import.meta.env.MODE': '"production"', // ✅ 변경
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
});
