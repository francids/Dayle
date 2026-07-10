import { createOpenAICompatible } from "@ai-sdk/openai-compatible";
import { generateText } from "ai";
import { Hono } from "hono";
import { z } from "zod";

interface Env {
  OPENAI_API_KEY: string;
  OPENAI_BASE_URL: string;
  OPENAI_MODEL: string;
}

const BodySchema = z.object({
  previousMissions: z.array(z.string().max(200)).max(10).default([]),
});

const app = new Hono<{ Bindings: Env }>();

app.post("/mission", async (c) => {
  let parsed: z.infer<typeof BodySchema>;
  try {
    const json = await c.req.json();
    parsed = BodySchema.parse(json);
  } catch {
    return c.json({ error: "Invalid request body" }, 400);
  }

  const apiKey = c.env.OPENAI_API_KEY;
  const baseURL = c.env.OPENAI_BASE_URL;
  const modelId = c.env.OPENAI_MODEL;
  if (!apiKey || !baseURL || !modelId) {
    return c.json({ error: "Server missing AI configuration" }, 500);
  }

  const provider = createOpenAICompatible({
    name: "dayle-provider",
    baseURL,
    apiKey,
  });

  const previousList = parsed.previousMissions.map((m, i) => `${i + 1}. ${m}`).join("\n");

  const system =
    "Eres un generador de misiones diarias para la app Dayle. " +
    "Cada misión es una acción sencilla, concreta y realizable en un día, " +
    "que empuja al usuario a salir de su rutina: probar algo nuevo, " +
    "conectar con alguien, moverse, explorar o cuidarse. " +
    "Responde ÚNICAMENTE con el texto de la misión en español, " +
    "en una sola frase corta (máximo 12 palabras), sin comillas, " +
    'sin prefijos como "Misión:" y sin explicaciones.';

  const user =
    previousList.length > 0
      ? `Genera UNA misión nueva y diferente a estas ya completadas:\n${previousList}`
      : "Genera UNA misión nueva para hoy.";

  try {
    const { text } = await generateText({
      model: provider(modelId),
      instructions: system,
      prompt: user,
      temperature: 1.1,
    });

    const mission = text.trim().replace(/^["']|["']$/g, "");
    if (!mission) {
      return c.json({ error: "Empty mission generated" }, 502);
    }
    return c.json({ mission });
  } catch (err) {
    const message = err instanceof Error ? err.message : "Unknown error";
    return c.json({ error: "AI generation failed", detail: message }, 502);
  }
});

app.get("/", (c) => c.text("Hi!"));

app.notFound((c) => c.text("404", 404));

export default app;
