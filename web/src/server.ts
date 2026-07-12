import { AngularAppEngine } from "@angular/ssr";
import { Elysia } from "elysia";
import { CloudflareAdapter } from "elysia/adapter/cloudflare-worker";
import api from "./api";
import {
  createNodeRequestHandler,
  createWebRequestFromNodeRequest,
  writeResponseToNodeResponse,
} from "@angular/ssr/node";

const app = new Elysia({
  adapter: CloudflareAdapter,
});
const angularApp = new AngularAppEngine();

app.use(api);

app.all("*", async ({ request }) => {
  const response = await angularApp.handle(request);
  if (response) {
    return response;
  }
  return new Response(null, { status: 404 });
});

export default app.compile();

export const reqHandler = createNodeRequestHandler(async (req, res, next) => {
  try {
    const webRes = await app.fetch(createWebRequestFromNodeRequest(req));
    if (webRes) {
      await writeResponseToNodeResponse(webRes, res);
    } else {
      next();
    }
  } catch (error) {
    next(error);
  }
});
