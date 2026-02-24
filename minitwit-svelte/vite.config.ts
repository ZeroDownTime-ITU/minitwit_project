import tailwindcss from '@tailwindcss/vite';
import { sveltekit } from '@sveltejs/kit/vite';
import { defineConfig } from 'vite';

export default defineConfig({
  plugins: [tailwindcss(), sveltekit()],
  server: {
    proxy: {
      '/api': {
        target: 'http://java-backend:7070',
        changeOrigin: true,
      }
    },
    watch: {
      usePolling: true,
      interval: 100   // Poll every 100ms
    }
  }
});