# FusionFall: ReStart HTTP Server

Hyper-Text Transfer Protocol (HTTP) server for FusionFall: ReStart.

## Requirements

- **Java -** Version 21
- **Database engine -** PostgreSQL 17.x
- **Redis -** Version 7.x
- **Environment variables:**

    | Variable                                         | Type    | Description                                                             | Required | Default                  | Example                      |
    |--------------------------------------------------|---------|-------------------------------------------------------------------------|----------|--------------------------|------------------------------|
    | `ISSUER_ACCOUNT_RELATED_NAME`                    | String  | Name related to the account from which the emails will be sent          | No       | `$ISSUER_NAME`           | `Example User`               |
    | `ISSUER_NAME`                                    | String  | Name of the issuer of the emails                                        | Yes      | None                     | `FusionFall: ReStart`        |
    | `ISSUER_SUPPORT_ADDRESS`                         | String  | Email or link address for support related to the issuer of the emails   | Yes      | None                     | `support@example.com`        |
    | `JWT_SECRET`                                     | String  | Secret key for signing and verifying JSON Web Tokens (JWTs)             | Yes      | None                     | `<JWT Secret>`               |
    | `MAIL_SERVER_FROM_ACCOUNT_RELATED_EMAIL_ADDRESS` | String  | Email address related to the account from which the emails will be sent | No       | `$MAIL_SERVER_USERNAME`  | `example-user@gmail.com`     |
    | `MAIL_SERVER_HOST`                               | String  | Host address of the mail server                                         | No       | `smtp.gmail.com`         | `smtp.example.com`           |
    | `MAIL_SERVER_PASSWORD`                           | String  | Password of the mail server                                             | Yes      | None                     | `<Mail Server Password>`     |
    | `MAIL_SERVER_PORT`                               | Integer | Port of the mail server                                                 | No       | `587`                    | `465`                        |
    | `MAIL_SERVER_USERNAME`                           | String  | Username of the mail server                                             | Yes      | None                     | `<Mail Server Username>`     |
    | `PORT`                                           | Integer | Port where the server will be running                                   | No       | `8080`                   | `8443`                       |
    | `POSTGRES_DATABASE_NAME`                         | String  | Name of the PostgreSQL database                                         | Yes      | None                     | `<PostgreSQL Database Name>` |
    | `POSTGRES_HOST`                                  | String  | Host address of the PostgreSQL server                                   | Yes      | None                     | `localhost`                  |
    | `POSTGRES_PASSWORD`                              | String  | Password of the PostgreSQL user                                         | Yes      | None                     | `<PostgreSQL Password>`      |
    | `POSTGRES_PORT`                                  | Integer | Port of the PostgreSQL server                                           | Yes      | None                     | `5432`                       |
    | `POSTGRES_USER`                                  | String  | Username of the PostgreSQL user                                         | Yes      | None                     | `<PostgreSQL Username>`      |
    | `PUBLIC_ADDRESS`                                 | String  | Public address of the server                                            | No       | `http://localhost:$PORT` | `https://example.com`        |
    | `REDIS_HOST`                                     | String  | Host address of the Redis server                                        | Yes      | None                     | `localhost`                  |
    | `REDIS_PASSWORD`                                 | String  | Password of the Redis server                                            | Yes      | None                     | `<Redis Password>`           |
    | `REDIS_PORT`                                     | Integer | Port of the Redis server                                                | Yes      | None                     | `6379`                       |
    | `REDIS_SSL`                                      | Boolean | Whether to use SSL for the Redis connection                             | No       | `false`                  | `true`                       |
    | `REDIS_USERNAME`                                 | String  | Username of the Redis server                                            | Yes      | None                     | `<Redis Username>`           |
    | `VERIFICATION_TOKENS_SALT_KEY`                   | String  | Salt key for generating verification tokens                             | Yes      | None                     | `<Salt Key>`                 |
    | `VERIFICATION_TOKENS_SECURITY_KEY`               | String  | Security key for generating verification tokens                         | Yes      | None                     | `<Security Key>`             |

    <br>
    
    **Notes:**
    - All required environment variables must be set before running the application.
    - Never give environment variable values to non-administrator users,
      since they could hack your server instance.
    - You can generate secrets using tools,
      such as [Vondy](https://www.vondy.com/random-key-generator--ZzGGMYgS),
      or [Random Key Gen](https://randomkeygen.com/).
      256-bit keys are recommended.
    - For setting the `PUBLIC_ADDRESS` environment variable,
      you can use your domain name or your public IP address.
    - For setting up the `MAIL_SERVER_*` environment variables,
      check out the SMTP Server setup section.
    - If the value of the environment variable `MAIL_SERVER_USERNAME`
      is not an email address, then `MAIL_SERVER_FROM_*_EMAIL_ADDRESS`-like
      environment variables must be set.
    - Salt and security keys must be 256-bit keys, must be different from each other,
      and must be in hexadecimal format.

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

### Application Properties

You can also configure some parts from the logic of the application
by setting application properties custom values
by passing them as JVM arguments when running the application.

- **Windows**

    ```shell
    # Development
    > java -jar .\target\restart-http-server-<version number>.jar \
        -D<property name>=<property value>

    # Production
    > java -jar .\restart-http-server-<version number>.jar \
        -D<property name>=<property value>
    ```

- **MacOS/Linux**

    ```shell
    # Development
    $ java -jar ./target/restart-http-server-<version number>.jar \
        -D<property name>=<property value>

    # Production
    $ java -jar ./restart-http-server-<version number>.jar \
        -D<property name>=<property value>
    ```

You can see the list of customizable properties
in the [Application Properties page from the Wiki](https://github.com/Juansecu/ReStart-HTTP-Server/wiki/Application-Properties).

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
