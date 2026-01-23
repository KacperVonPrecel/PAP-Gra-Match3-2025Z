# Początkowe założenia projektu

W ramach projektu stworzymy grę, w której użytkownik będzie mógł losować postacie i używać ich do rywalizacji z innymi graczami, lub ewentualnie do samodzielnej gry. Rywalizację graczy planujemy zrealizować w formie rozgrywek typu match 3. Przewidujemy stworzenie możliwości rejestracji użytkowników, przechowywania danych o tym jakie postacie posiadają, oraz zapisywać rezultaty rozgrywek. Na podstawie rezulatów rozgrywek, statystyk gracza stworzone będą rankingi, albo będzie możliwość przeglądania historii wyników rozgrywek.

# Realizacja

Projekt Zrealizowaliśmy używając Spring Boota i Angulara.

# Skład zespołu

Norbert Drabiński, Szymon Mucha, Kacper Skrodzki, Wiktoria Parzych

# Linki do dokumentacji technicznej

[Dokumentacja techniczna frontend](frontend/README.md)
[Dokumentacja techniczna backend](backend/README.md)

# Dokumentacja dla użytkownika

## Uruchamianie programu

Aby uruchomić program trzeba jednocześnie uruchomić serwer backendu i frontend. W projekcie używamy Javy w wersji 24 i Angulara w wersji 20, a więc aby używać aplikację należy mieć JDK Javy 24, zainstalowanego Node.js. Frtontend uruchamia się używając komendy:

> npm install
> ng s
> będąc w folderze frontend. Przy kolejnych uruchomieniach można używać samej komendy ng s.

Backend uruchamia się będąc w folderze backend.Zaleca się użycie środowiska programistycznego takiego jak IntelliJ IDEA w celu łatwiejszego uruchomienia projektu.
Po otwarciu projektu w IntelliJ IDEA i kliknięciu 'run' środowisko automatycznie:

- buduje projekt przy użyciu Gradle,
- pobiera i konfiguruje wszystkie wymagane zależności,
- ustawia odpowiedni classpath,
- uruchamia metodę `main` klasy `Application`
  Gradle można zbudować także komendą
  > ./gradlew build

Po uruchomieniu frontendu i backendu należy wejść na adress http://localhost:4200/ w przeglądarce.

## Logowanie i zakładanie konta

Po wejściu na podany adres, użytkownik zostaje przekierowany do http://localhost:4200/auth/login. Jeśli użytkownik nie ma konta może je zalożyć klikając link 'Sign Up'. Podczas rejestracji użytkownik musi podać swojego maila oraz nazwę użytkownika. Nazwy użytkownika są unikalne w ramach gry - więcej niż dwóch użytkowników nie może mieć danej nazwy.

## Ekran główny

## Losowanie i ulepszanie posatci

## Ranking i historia gry

## Wybieranie postaci

## Rozgrywka

### Jak uruchomić rozgrywkę

### Zasady gry
