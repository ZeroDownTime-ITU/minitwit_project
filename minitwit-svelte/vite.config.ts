import { sveltekit } from '@sveltejs/kit/vite';
import { defineConfig } from 'vite';

export default defineConfig({
  plugins: [sveltekit()],
  server: {
    proxy: {
      // 1. Intercept anything starting with '/api'
      '/api': {
        // 2. Where the Java server is actually hiding
        target: 'http://localhost:7070',
        
        // 3. Tells the Java server the request came from its own origin
        // (Helps with some strict security filters)
        changeOrigin: true,
        
        // 4. OPTIONAL: If your Java endpoints DON'T have "/api" in them 
        // (e.g., Java is just /msgs), this removes "/api" before sending.
        // rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
});
