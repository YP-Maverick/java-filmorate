package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Mpa {

    //G,  у фильма нет возрастных ограничений
    //PG,  детям рекомендуется смотреть фильм с родителями
    //PG12, детям до 13 лет просмотр не желателен
    //R,  лицам до 17 лет просматривать фильм можно только в присутствии взрослого,
    //NC17 лицам до 18 лет просмотр запрещён

    private Long id;
    private String name;
}

