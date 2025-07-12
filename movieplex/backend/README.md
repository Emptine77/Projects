# movieplex

## Description
The Cinema Management System is an advanced platform designed to streamline the operations of a modern cinema.
It encompasses a comprehensive set of features catering to both users and administrators.
The system aims to enhance the movie-going experience, from ticket booking to movie recommendations,
while providing administrators with valuable insights for efficient cinema management.

## Table of Contents

- [Quick Start](#quick-start)
- [Swagger](#swagger)
- [Postman](#postman)
- [Keycloak](#keycloak)

## Quick Start

### Prerequisites
- Docker installed on your system

### Getting Started

1. Clone this repository to your local machine:

    ```bash
    git clone https://github.com/NikSsh/movieplex.git
    cd backend
    ```

2. Set environment variables (optional):
- You can modify environment variables in the `docker-compose.yml` file if needed. Ensure the configurations match your requirements.
- Adjust the variables in the `.env` file to match your specific configurations.
3. Start the services using Docker Compose:

    ```bash
    docker-compose up -d
    ```

   This command will launch the defined services in detached mode (`-d`), allowing them to run in the background.

### Accessing Services

After running your spring boot app, you can access next services:

- **movieplex:** Access the Internstore service at [http://localhost:8090](http://localhost:8090)
- **Keycloak:** Access the Keycloak admin console at [http://localhost:8080](http://localhost:8080) (Credentials: admin/admin)
- **Postgres:** Access Postgres database on `localhost:5432` (if needed, adjust configurations as per your setup)

### Stopping Services

To stop the services when finished, run:

```bash
docker-compose down
```

## Swagger
The Swagger UI provides a user-friendly interface for exploring and testing the API endpoints. Navigate through the available resources and endpoints to understand the functionalities provided by the API.
### Accessing Swagger UI
To view the Swagger documentation ensure that the project is up and running.

Next, open your web browser and navigate to the following URL:
```
http://[actual_host]:[actual_port]/swagger-ui/index.html
```

Replace [actual_host] with the actual host where your application is running, and [actual_port] with the actual port.

For example:
```
http://localhost:8080/swagger-ui/index.html
```

### Authorize
1) Open your Swagger UI in a web browser.
2) Click on "Authorize" Button
3) Enter the value in the following format:
   ```
   [your_token]
   ```
Replace [your_token] with the actual token.
4) Once the token is entered, click the "Authorize" button within the dialog

## Postman

You can access the shared collection by this link:

https://solar-star-389743.postman.co/workspace/My-Workspace~7e56602a-211d-434a-8e2f-34fd2bb1a063/collection/31108386-a7d6aab7-794d-4b2d-b631-c64a7db808ca?action=share&creator=22330378

## Keycloak

### Obtain JWT token via Keycloak auth UI and Postman 

First you should authenticate by this link to obtain a code
from a redirected path:
1) navigate to -> http://localhost:8080/realms/movieplex/protocol/openid-connect/auth?client_id=multiplex-client&response_type=code&scope=openid&redirect_uri=http://localhost:4200/redirect&state=h4u8fF2okGBio38uE&code_challenge=bv-hWmaJ5weXPQkZTu3AV3y53DpLChNpvtBMHZC0TNA&code_challenge_method=S256

2) after successful authentication you will be redirected to another page,
which will contain needed code in parameters:
E.g. http://localhost:8080/redirect?&code=38ef48ea-a8c1-44db-b802-bff60a26dc40.a2f06fde-c44d-4474-9957-f6a73c7f3f0f.74047fa8-a563-4ef5-bd91-f1de64bcfd60

Next, you should paste this code in such request body of type x-www-form-urlencoded:
http://localhost:8080/realms/movieplex/protocol/openid-connect/token

body params:
grant_type=authorization_code
client_id=multiplex-client
code=obtained code
code_verifier=443c4ccc25e8c4296b164dc699f7f61e01f2657ef6eb5fa7d55eecaa
redirect_uri=http://localhost:4200/redirect

### Usage of jwt token via postman
Create a new request and open section 'Authorization',
select type 'Bearer token' and paste obtained token from the previous request