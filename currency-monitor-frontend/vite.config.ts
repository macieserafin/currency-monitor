import { fileURLToPath } from "node:url";
import { defineConfig, loadEnv } from "vite";
import react from "@vitejs/plugin-react";

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const envDir = fileURLToPath(new URL("..", import.meta.url));
  const env = loadEnv(mode, envDir, "");

  return {
    envDir,
    plugins: [
      react({
        babel: {
          plugins: [["babel-plugin-react-compiler"]],
        },
      }),
    ],
    server: {
      proxy: {
        "/api": {
          target: `http://localhost:${env.BACKEND_PORT || "8080"}`,
          changeOrigin: true,
          secure: false,
        },
      },
    },
  };
});
