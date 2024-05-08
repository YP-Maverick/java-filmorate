# java-filmorate

Данный проект разрабатывает API для оценки фильмов, позволяя пользователям выражать своё мнение о кинофильмах и делиться рецензиями. API включает функционал по созданию, редактированию и просмотру отзывов, а также управлению личными и фильмовыми предпочтениями пользователя.

![image](https://github.com/YP-Maverick/java-filmorate/assets/147594756/29d786fc-2d53-4223-a25a-47b40e6f8b8d)

# API endpoints:

## User

```json
{
	"id": 5,
	"email": "mail@mail.ru",
	"login": "dolore",
	"name": "Nick Name",
	"birthday": "1946-08-20"
}
```

```GET /users```

```PUT /users```

```POST /users```
___________
```PUT /users/{id}/friends/{friendId}```

```DELETE /users/{id}/friends/{friendId}```

```GET /users/{id}/friends```

```GET /users/{id}/friends/common/{otherId}```
_______________
```GET /users/{id}/recommendations```

____
EVENT
```json
{
	"timestamp": 123344556, // Время события
	"userId": 123,
	"eventType": "LIKE", // ENUM: LIKE, REVIEW или FRIEND
		  "operation": "REMOVE", // ENUM: REMOVE, ADD, UPDATE
	"eventId": 1234, //Primary key
	"entityId": 1234 // Id сущности, с которой произошло событие
}
```

```GET /users/{id}/feed```
_____________

```GET /users/{id}```

```DELETE  /users/{id}```
_______
______
## Review

```json
{
    "reviewId": 123,
    "content": "This film is sooo baad.",
    "isPositive": false, // Положительный отзыв?
    "userId": 123, // Пользователь
    "filmId": 2, // Фильм
    "useful": 20 // Рейтинг полезности 
}
```

```GET /reviews```

```PUT  /reviews```

```POST  /reviews```

```DELETE  /reviews/{id}```
______
```PUT /reviews/{id}/like/{userId}```
```DELETE /reviews/{id}/like/{userId}```

```PUT /reviews/{id}/dislike/{userId}```
```DELETE /reviews/{id}/dislike/{userId}```
____
_____

## FILM

```json
{
	"id": 1,
	"name": "Film Updated",
	"description": "New film update decription",
	"releaseDate": "1989-04-17",
	"duration": 190,
	"likes": 1,
	"genres": [],
	"mpa": {
		"id": 5,
		"name": "NC-17"
	},
	"directors": []
}
```

```GET /films```
```PUT /films```
```POST /films```
```DELETE /films/{id}```

```GET /films/{id}```
____
```PUT /films/{id}/like/{userId}```
```DELETE /films/{id}/like/{userId}```
_______

```GET /films/search```

```GET /films/popular```

```GET /films/common```

```GET /films/director/{directorId}```
_____________
## Director 

```json
{
	"name": "New film",
	"releaseDate": "1999-04-30",
	"description": "New film about friends",
	"duration": 120,
	"mpa": { "id": 3},
	"genres": [{ "id": 1}],
	"director": [{ "id": 1}]
}
```

```GET /directors```

```PUT /directors```

```POST /directors```

```GET /directors/{id}```

```DELETE /directors/{id}```
___________
MPA 

```json
{
	"id": 5,
	"name": "NC-17"
}
```

```GET /mpa```

```GET /mpa/{id}```
___________________
GENRE

```json
{
	"id": 1,
	"name": "Комедия"
}
```

```GET /genres```

```GET /genres/{id}```
