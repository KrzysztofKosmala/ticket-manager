# External Weather MCP

Demo MCP server backed by the public Open-Meteo APIs.

Tools:

- `weather_geocode_city` - finds coordinates and timezone for a city.
- `weather_forecast_get` - fetches weather forecast for coordinates.

Local run:

```bash
npm install
npm run dev
```

SSE endpoint:

```text
http://localhost:8110/sse
```
