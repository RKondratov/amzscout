# Тестовое задание AMZScout
____________________________________

### Задание

1. Написать spring-boot приложение, которое будет содержать один контроллер с одним методом, метод возвращает HTTP 200 и
   пустое тело.
2. Написать функционал, который будет ограничивать количество запросов с одного IP адреса на этот метод в размере N штук
   в X минут. Если количество запросов больше, то должен возвращаться 502 код ошибки, до тех пор, пока количество
   обращений за заданный интервал не станет ниже N.
3. Должна быть возможность настройки этих двух параметров через конфигурационный файл.
4. Сделать так, чтобы это ограничение можно было применять быстро к новым методам и не только к контроллерам, а также к
   методам классов сервисного слоя.
5. Реализация должна учитывать многопоточную высоконагруженную среду исполнения и потреблять как можно меньше
   ресурсов (**!важно**).
6. Также написать простой JUnit-тест, который будет эмулировать работу параллельных запросов с разных IP.

*** 

#### Важно!

Не использовать сторонних библиотек для троттлинга.
***

**_Список технологий и инструментов_**

- Код должен быть описан на [JDK 11](http://jdk.java.net/11/) (или выше)
- Фреймворки: Spring + Spring Boot
- Для сборки использовать Gradle
- Возможны другие вспомогательные библиотеки.
- Написать JUnit тест с использованием JUnit 5.x (Junit Jupiter)
- Написать простой dockerfile для обёртки данного приложения в докер

***

Для запуска контейнера `docker run -p 8080:8080 image id`


<img src="https://fbamap.com/wp-content/uploads/2021/01/Amzscout-Pro-min-min-300x119.png"/>

