# Fimorate-BD-Diagram

![image](https://github.com/YP-Maverick/Fimorate-BD-Diagram/assets/147594756/f51fc2d3-b342-49d5-8c58-308851b6274d)

ER диаграмма сервиса оценкиц фильмов

1. Получение фильма по ID
```sql

SELECT * FROM film WHERE film_id = filmId;
```
Замените filmId на конкретный id фильма при использовании запроса.

2. Получение всех фильмов
```sql

SELECT * FROM film;
```
3. Получение всех пользователей
```sql

SELECT * FROM "user";
```
4. Получение пользователя по ID
```sql

SELECT * FROM "user" WHERE id = userId;
```
Замените userId на конкретный id пользователя при использовании запроса.

5. Получение популярных фильмов
Для этого метода, где необходимо сортировать фильмы по количеству лайков, сперва нужно подсчитать количество лайков для каждого фильма и затем отсортировать список в порядке убывания. SQL запрос будет следующим:

```sql

SELECT film.*, COUNT(filmlikes.film_id) AS likes_count
FROM film
LEFT JOIN film_likes AS filmlikes ON film.film_id = filmlikes.film_id
GROUP BY film.film_id
ORDER BY likes_count DESC
LIMIT count;
```
