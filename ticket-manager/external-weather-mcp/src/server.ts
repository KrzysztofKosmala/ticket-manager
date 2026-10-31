import express from "express";
import { McpServer } from "@modelcontextprotocol/sdk/server/mcp.js";
import { SSEServerTransport } from "@modelcontextprotocol/sdk/server/sse.js";
import { z } from "zod";

const PORT = Number(process.env.PORT ?? 8110);
const NumberInput = z.coerce.number();
const OptionalStringArray = z.preprocess(
  (value) => value == null ? undefined : value,
  z.array(z.string()).default([])
);

const transports: Record<string, SSEServerTransport> = {};

function createMcpServer() {
  const server = new McpServer({
    name: "external-weather-mcp",
    version: "1.0.0"
  });

  server.registerTool(
    "weather_geocode_city",
    {
      title: "Geocode city",
      description:
        "Finds latitude, longitude and timezone for a city using Open-Meteo Geocoding API. Use this before weather_forecast_get when the user provides a city name instead of coordinates.",
      inputSchema: {
        name: z.string().min(2).describe("City name or postal code, for example Warsaw, Berlin or Paris."),
        countryCode: z.string().length(2).optional().describe("Optional ISO-3166-1 alpha2 country code, for example PL, DE or FR."),
        language: z.string().default("en").describe("Response language, for example en or pl."),
        count: z.coerce.number().int().min(1).max(10).default(5).describe("Maximum number of matching locations to return.")
      }
    },
    async ({ name, countryCode, language, count }) => {
      const params = new URLSearchParams({
        name,
        count: String(count),
        language,
        format: "json"
      });

      if (countryCode) {
        params.set("countryCode", countryCode.toUpperCase());
      }

      const response = await fetchJson(`https://geocoding-api.open-meteo.com/v1/search?${params}`);
      const results = Array.isArray(response.results) ? response.results : [];

      return jsonContent({
        locations: results.map((location: OpenMeteoLocation) => ({
          id: location.id,
          name: location.name,
          latitude: location.latitude,
          longitude: location.longitude,
          timezone: location.timezone,
          countryCode: location.country_code,
          country: location.country,
          admin1: location.admin1,
          population: location.population
        }))
      });
    }
  );

  server.registerTool(
    "weather_forecast_get",
    {
      title: "Get weather forecast",
      description:
        "Gets weather forecast from Open-Meteo for coordinates. Use latitude, longitude and timezone returned by weather_geocode_city when the user asks about weather in a city.",
      inputSchema: {
        latitude: NumberInput.min(-90).max(90).describe("WGS84 latitude."),
        longitude: NumberInput.min(-180).max(180).describe("WGS84 longitude."),
        timezone: z.string().default("auto").describe("Timezone, for example Europe/Warsaw or auto."),
        forecastDays: z.coerce.number().int().min(1).max(16).default(7).describe("Number of forecast days."),
        hourly: OptionalStringArray.describe("Hourly variables, for example temperature_2m, precipitation_probability, wind_speed_10m."),
        daily: z.preprocess(
          (value) => value == null ? undefined : value,
          z.array(z.string()).default(["temperature_2m_max", "temperature_2m_min", "precipitation_sum"])
        ).describe("Daily variables, for example temperature_2m_max, temperature_2m_min, precipitation_sum, wind_speed_10m_max."),
        current: z.preprocess(
          (value) => value == null ? undefined : value,
          z.array(z.string()).default(["temperature_2m", "wind_speed_10m"])
        ).describe("Current variables, for example temperature_2m, precipitation, wind_speed_10m.")
      }
    },
    async ({ latitude, longitude, timezone, forecastDays, hourly, daily, current }) => {
      const params = new URLSearchParams({
        latitude: String(latitude),
        longitude: String(longitude),
        timezone,
        forecast_days: String(forecastDays)
      });

      if (current.length > 0) {
        params.set("current", current.join(","));
      }
      if (hourly.length > 0) {
        params.set("hourly", hourly.join(","));
      }
      if (daily.length > 0) {
        params.set("daily", daily.join(","));
      }

      const forecast = await fetchJson(`https://api.open-meteo.com/v1/forecast?${params}`);

      return jsonContent({
        latitude: forecast.latitude,
        longitude: forecast.longitude,
        timezone: forecast.timezone,
        current: forecast.current ?? null,
        daily: forecast.daily ?? null,
        hourly: forecast.hourly ?? null
      });
    }
  );

  return server;
}

const app = express();
app.use(express.json());

app.get("/health", (_request, response) => {
  response.json({ status: "UP" });
});

app.get("/sse", async (_request, response) => {
  const server = createMcpServer();
  const transport = new SSEServerTransport("/messages", response);
  transports[transport.sessionId] = transport;

  response.on("close", () => {
    delete transports[transport.sessionId];
  });

  await server.connect(transport);
});

app.post("/messages", async (request, response) => {
  const sessionId = request.query.sessionId;
  if (typeof sessionId !== "string") {
    response.status(400).json({ error: "Missing sessionId query parameter" });
    return;
  }

  const transport = transports[sessionId];
  if (!transport) {
    response.status(404).json({ error: `Session not found: ${sessionId}` });
    return;
  }

  await transport.handlePostMessage(request, response, request.body);
});

app.listen(PORT, () => {
  console.log(`external-weather-mcp listening on port ${PORT}`);
});

async function fetchJson(url: string): Promise<Record<string, any>> {
  const response = await fetch(url, {
    headers: {
      "accept": "application/json",
      "user-agent": "ticket-manager-external-weather-mcp/1.0"
    }
  });

  const body = await response.text();
  if (!response.ok) {
    throw new Error(`Open-Meteo request failed: status=${response.status}, body=${body}`);
  }

  return JSON.parse(body);
}

function jsonContent(value: unknown) {
  return {
    content: [
      {
        type: "text" as const,
        text: JSON.stringify(value)
      }
    ]
  };
}

type OpenMeteoLocation = {
  id?: number;
  name?: string;
  latitude?: number;
  longitude?: number;
  timezone?: string;
  country_code?: string;
  country?: string;
  admin1?: string;
  population?: number;
};
