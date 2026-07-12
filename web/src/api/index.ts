import { Elysia } from "elysia";

const api = new Elysia({ prefix: "/api" });

api.get("/", function () {
  return { message: "Hello" };
});

api.get("/health", function () {
  return { status: "ok", timestamp: new Date().toISOString() };
});

export default api;
