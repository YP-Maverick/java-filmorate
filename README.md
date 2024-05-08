# java-filmorate

Данный проект разрабатывает API для оценки фильмов, позволяя пользователям выражать своё мнение о кинофильмах и делиться рецензиями. API включает функционал по созданию, редактированию и просмотру отзывов, а также управлению личными и фильмовыми предпочтениями пользователя.

![image](https://github.com/YP-Maverick/java-filmorate/assets/147594756/29d786fc-2d53-4223-a25a-47b40e6f8b8d)

## Примеры запросов:
Получить список всех фильмов:
```
SELECT * 
FROM film;
```
Получить список всехопльзователей:
```
SELECT * 
FROM user;
```
Получить топ фильмов:
```
SELECT *
FROM film
ORDER BY likes DESC;
```
Получить список всех друзей пользователя с id x:
```
SELECT *
FROM user AS u
WHERE u.user_id IN (SELECT f.friend_id
		    FROM friend AS f
		    WHERE f.user_id = x AND
		    f.status = TRUE);
```
