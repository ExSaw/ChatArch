![image](https://github.com/ExSaw/ChatArch/assets/86077011/a0219216-d8bf-4456-b61c-564b6613395e)
<img align="left" src="https://github.com/ExSaw/ChatArch/assets/86077011/00ad538e-4dff-46f6-8ad3-db2af05d6c25">
<br>
<br>
<br>

* HeaderModifierInterceptor - подставляет jwtToken в ATHORIZATION HEADER
* NetworkProvider - предоставляет OkHttp client
* TokenHandler - временно хранит jwtToken и предоставляет его в HeaderModifierInterceptor
* IDispatchersProvider, StandardDispatchers - предоставляют диспетчеры короутин
* Resource - обертка респонза для репозитория
<br>
<br>

* ChatEnities - классы для хранения данных в локальной БД
* ChatDao - интерфейс локальной БД Room
* ChatDatabase - локальная БД Room
<br>
<br>
<br>

* DTO - серриализуемые классы получния данных с сервера
<br>

* ChatApi - интерфейс общения с api для Retrofit
* ChatRemoteMediator - класс реализующий логику Paging 3 библиотеки, работает как с БД так и с remote API
* ChatRepository - общий репозиторий, оркестрирует данными БД и API
* ChatModule - создание и предоставление зависимостей Dagger Hilt
<br>

* Mappers - мапперы выполнены упрощенно ч/з extension функции (многи встроены прямо в data классы)
<br>

* Models - Dto классы преобразуются в Entity, Entity в модели с которыми работает UI
<br>

* IRepository - интерфейс репозитория
<br>

* SystemMessage - события в виде enum, на которые реагирует UI, коллбэк от вью-модели
* UiEvent - все события, которые возможны получить от UI (клики, свайпы ...), коллбэк от UI
* ScreenRouter - простейшая навигация м/у экранами
<br>
<br>

* Чат состоит из 3х экранов(фрагментов).
* Chat Fragment - фрагмент со списком (с пагинацией, 1 страница 10 сообщений) сообщений переписки с выбранным контактом.
<br>

* Contacts Fragment - фрагмент со списком контактов пользователя.
* Login Fragment - фрагмент ввода логина и пароля. После их ввода Api выдаст jwtToken, с которым уже будет все запросы к бэку.

* States - data классы возможных состояний логики приложения.
* StageState - хранит инфо, какой сейчас этап приложения (Авторизация, Работа с контактами, Переписка/Сообщения)
* UserDataState - кэш для данных с бэка
* MainActivity - single activity
* MainViewmodel - shared viewmodel м/у 3 фрагментами, содержит основную бизнес логику.



  


