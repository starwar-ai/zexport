# Swagger API Documentation

This project includes interactive API documentation using Swagger/OpenAPI 3.0.

## Accessing Swagger UI

Once the server is running, you can access the Swagger UI at:

**http://localhost:3000/api-docs**

## Features

### Interactive Documentation
- **Try it out**: Test API endpoints directly from the browser
- **Request/Response schemas**: View detailed request and response structures
- **Parameter descriptions**: See all available parameters with examples
- **Authentication**: No authentication required for this API

### Available Endpoints

All endpoints are documented with:
- Method type (GET, POST)
- Parameters (path, query, body)
- Request body schemas
- Response examples
- Status codes

### Swagger Specification

The Swagger specification is generated using:
- **swagger-jsdoc**: Generates OpenAPI spec from JSDoc comments
- **swagger-ui-express**: Serves interactive Swagger UI

### Customization

The Swagger configuration is in `swagger.js` and includes:
- API metadata (title, version, description)
- Server URLs
- Reusable schemas and parameters
- Security definitions (if needed in the future)

## Using Swagger UI

1. **Start the server**:
   ```bash
   npm start
   ```

2. **Open browser**:
   Navigate to http://localhost:3000/api-docs

3. **Explore endpoints**:
   - Click on any endpoint to expand details
   - View parameters and schemas
   - Click "Try it out" to test the endpoint
   - Fill in parameters
   - Click "Execute" to send the request
   - View the response

## Example: Testing POST /api/rates/fetch

1. Navigate to http://localhost:3000/api-docs
2. Find "POST /api/rates/fetch" endpoint
3. Click "Try it out"
4. Modify the request body:
   ```json
   {
     "date": "2024-01-15",
     "from": "USD",
     "to": "EUR"
   }
   ```
5. Click "Execute"
6. View the response below

## Benefits

- **No additional tools needed**: Test API directly in browser
- **Auto-generated**: Documentation updates automatically from code comments
- **Standard format**: Uses OpenAPI 3.0 specification
- **Shareable**: Send the link to team members or clients
- **Export**: Download OpenAPI spec for use with other tools

## Swagger Spec File

The raw OpenAPI specification can be accessed at:
**http://localhost:3000/api-docs/swagger.json**

This can be imported into tools like:
- Postman
- Insomnia
- API testing frameworks
- Code generators
