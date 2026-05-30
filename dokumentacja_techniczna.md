# Dokumentacja techniczna

## Ogólne informacje

- Projekt składa się z dwóch głównych warst backendu i frontendu. Oba projekty są w odpowiednich katalogach.
- Frontend jest napisany w Typescripcie, z użyciem Angulara.
- Backend jest napisany w Javie, z użyciem Spring boota.
- Warsty komunikują się za pomocą REST API, i STOMP over WebSocket w trakcie trwania rozgrywki.

## Backend

- Proces budowy aplikacji i uruchamiania testów realizowany jest przy użyciu narzędzia Gradle.
- Wszystki zależności projektu znajdują się w pliku build.gradle
- W projekcie wykorzystano bazę danych h2.
- Program działa na standardowym porcie spring boot 8080

### Uruchomienie

- Należy być w katalogu backend.

- Windows

  ```
  gradlew bootRun
  ```

- Linux/MacOs
  ```
  ./gradlew bootRun
  ```

### Uruchomienie testów

- Należy być w katalogu backend.

- Windows

  ```
  gradlew test
  ```

- Linux/MacOs
  ```
  ./gradlew test
  ```

### Dostęp do bazy dany

1. Należy uruchomić projekt, następnie wejść na `localhost:8080/h2-console`.
2. Uzupełnić poprawnie dane tak aby zgadzało się z konfiguracją. Obecna konfiguracja

| Pole         | Wartość                                            |
| :----------- | :------------------------------------------------- |
| Driver Class | org.h2.Driver                                      |
| JDBC URL     | jdbc:h2: + ścieżka do projektu na dysku + database |
| User name    | sa                                                 |
| Password     | password                                           |

3. Klinknąć przycisk connect.

### Dokumentacja REST api
- Wygenerowana za pomocą swaggera
- Znajduję się pod adresem http://localhost:8080/swagger-ui/index.html

## Frontend
- Program jest uruchomiony na standardowym porcie Angulara 4200
### Uruchomienie

- Należy być w katalogu frontend i mieć zainstalowanego Angulara
    ```
    ng s
    ```
### Urchomienie testów
- Należy być w katalogu frontend i mieć zainstalowanego Angulara
    ```
    ng t
    ```
- Testy są uruchamiane w przeglądarce na porcie 9876
