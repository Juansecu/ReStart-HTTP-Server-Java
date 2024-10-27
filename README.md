# FusionFall: ReStart HTTP Server

Hyper-Text Transfer Protocol (HTTP) server for FusionFall: ReStart.

## Requirements

- **Java -** Version 17
- **Database engine -** PostgreSQL 17.x
  - **Environment variables:**

      | Variable                 | Type    | Description                           | Required | Default                 | Example                      |
      |--------------------------|---------|---------------------------------------|----------|-------------------------|------------------------------|
      | `MAIL_SERVER_HOST`       | String  | Host address of the mail server       | No       | `smtp.gmail.com`        | `smtp.example.com`           |
      | `MAIL_SERVER_PASSWORD`   | String  | Password of the mail server           | Yes      | None                    | `<Mail Server Password>`     |
      | `MAIL_SERVER_PORT`       | Integer | Port of the mail server               | No       | `587`                   | `465`                        |
      | `MAIL_SERVER_USERNAME`   | String  | Username of the mail server           | Yes      | None                    | `<Mail Server Username>`     |
      | `PORT`                   | Integer | Port where the server will be running | No       | `8080`                  | `8443`                       |
      | `POSTGRES_DATABASE_NAME` | String  | Name of the PostgreSQL database       | Yes      | None                    | `<PostgreSQL Database Name>` |
      | `POSTGRES_HOST`          | String  | Host address of the PostgreSQL server | Yes      | None                    | `localhost`                  |
      | `POSTGRES_PASSWORD`      | String  | Password of the PostgreSQL user       | Yes      | None                    | `<PostgreSQL Password>`      |
      | `POSTGRES_PORT`          | Integer | Port of the PostgreSQL server         | Yes      | None                    | `5432`                       |
      | `POSTGRES_USER`          | String  | Username of the PostgreSQL user       | Yes      | None                    | `<PostgreSQL Username>`      |
      | `PUBLIC_ADDRESS`         | String  | Public address of the server          | No       | `http://localhost:8080` | `https://example.com`        |

## Configuration

### SSL Configuration

To enable SSL, you can use any of the following methods:

- [Java KeyStore file](https://docs.spring.io/spring-boot/how-to/webserver.html#howto.webserver.configure-ssl).
- [PEM files](https://docs.spring.io/spring-boot/how-to/webserver.html#howto.webserver.configure-ssl.pem-files).
- [SSL Bundle](https://docs.spring.io/spring-boot/reference/features/ssl.html)

Once you have chosen a method to enable SSL,
you can pass the necessary JVM arguments to the application
by passing each argument following the `-D` flag when running the application.

Note that you can also use a reverse proxy (e.g. Nginx, Apache, etc.).

## API Documentation

The API documentation is available at the endpoint `/api/docs`.

## Running

### Using Docker (Recommended)

In order to run the application using [Docker](https://www.docker.com/),
you must have Docker installed on your machine.

For running the application with Docker, you will need
to bind port `8080` or the one configured by yourself
through environment variable `PORT` to your host machine.

Before pulling the image from the GitHub Container Registry,
you must log in to the registry using the following command:

```shell
$ docker login ghcr.io
```

#### Docker CLI

- **Windows**

    ```shell
    # For running the application without SSL
    > docker run -dp 8080:8080 \
        --env-file <path\to\env\file> \
        --name <container-name> \
        ghcr.io/juansecu/restart-http-server:<version number>

    # For running the application with SSL
    > docker run -dp 8080:8080 \
        <volumes for SSL certificates> \
        --env-file <path\to\env\file> \
        --name <container-name> \
        --entrypoint "java <JVM arguments> -jar /app/app.jar" \
        ghcr.io/juansecu/restart-http-server:<version number>
    ```

- **MacOS/Linux**

    ```shell
    # For running the application without SSL
    $ docker run -dp 8080:8080 \
        --env-file <path/to/env/file> \
        --name <container-name> \
        ghcr.io/juansecu/restart-http-server:<version number>

    # For running the application with SSL
    $ docker run -dp 8080:8080 \
        <volumes for SSL certificates> \
        --env-file <path/to/env/file> \
        --name <container-name> \
        --entrypoint "java <JVM arguments> -jar /app/app.jar" \
        ghcr.io/juansecu/restart-http-server:<version number>
    ```

#### Docker Compose

For running the application with Docker Compose,
you can use the
[Docker Compose file](https://github.com/Juansecu/ReStart-HTTP-Server/blob/main/docker-compose.yml)
provided in this repository for development and production,
but for production, you will also need to
remove the `build` property from the `restart-http-server` service,
and change the `image` property to `ghcr.io/juansecu/restart-http-server:<version number>`.

##### Adding SSL

For running the application with SSL,
you will need to set the necessary JVM arguments
in the `command` property of the `restart-http-server` service,
and mount the SSL certificate files using Docker volumes.

- **Windows**

```yaml
services:
  restart-http-server:
    ...
    volumes:
      # Volumes for SSL certificates...
    command: java <JVM arguments> -jar /app/app.jar
```

- **MacOS/Linux**

```yaml
services:
  restart-http-server:
    ...
    volumes:
      # Volumes for SSL certificates...
    command: java <JVM arguments> -jar /app/app.jar
```

After configuring the environment variables, you can run the application using the following command:

```shell
$ docker compose up -d
```

### Using Java

For running the application with Java,
you will need to set the necessary environment variables
and follow the instructions below.

#### For Development

In order to run the application for development,
you will need to clone this repository,
compile the application and run it using the following commands:

- **Windows**

    ```shell
    # --- BUILDING ---

    # For building the application without running tests
    > .\mvnw package -DskipTests

    # For building the application and running tests (not working yet)
    > .\mvnw package


    # --- RUNNING ---

    # For running the application
    > java -jar .\target\restart-http-server-<version number>.jar
    ```
  
- **MacOS/Linux**

    ```shell
    # --- BUILDING ---

    # For building the application without running tests
    $ ./mvnw package -DskipTests

    # For building the application and running tests (not working yet)
    $ ./mvnw package


    # --- RUNNING ---

    # For running the application
    $ java -jar ./target/restart-http-server-<version number>.jar
    ```

#### For Production

In order to run the application for production,
you will only need to download the latest release
from the [releases page](https://github.com/Juansecu/ReStart-HTTP-Server/releases),
set the necessary environment variables
and run the application using the following command:

- **Windows**

    ```shell
    > java -jar .\restart-http-server-<version number>.jar
    ```

- **MacOS/Linux**

    ```shell
    $ java -jar ./restart-http-server-<version number>.jar
    ```
