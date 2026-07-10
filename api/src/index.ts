import { Hono } from "hono";

const app = new Hono();

app.get("/", (c) => {
  return c.text("Hi!");
});

app.notFound((c) => {
  return c.text("404", 404);
});

export default app;
