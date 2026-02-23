## Лабораторная работа 
Клиент-серверное приложение для управления коллекцией `Person`

### Описание

Проект реализует консольное многопользовательское клиент-серверное приложение для управления коллекцией объектов `Person` в формате XML.  
Объекты и команды передаются между клиентом и сервером в сериализованном виде по TCP с использованием неблокирующих NIO-каналов.

### Структура проекта

- `common` – общие классы:
  - модели `Person`, `Coordinates`, `Location`, `EyeColor`, `HairColor`, `Country`
  - транспортные объекты `Request`, `UserRequest`, `Response`, `ResponseStatus`, `User`
  - базовая инфраструктура команд (`Command`, `CommandType`, `CommandManager`)
- `server` – серверное приложение:
  - `Server` – главный класс сервера (selector + NIO, многопоточная обработка)
  - `ServerConnectionHandler` – приём подключений
  - `ServerReadingHandler` – чтение и десериализация запросов (Fixed thread pool)
  - `ServerProcessHandler` – разбор и выполнение команд (отдельный `Thread` на обработку)
  - `ServerRespondingHandler` – отправка ответов клиенту (`ForkJoinPool`)
  - `CollectionManager` – работа с коллекцией `PriorityQueue<Person>` с `ReadWriteLock` и Stream API
  - `CollectionFileManager` – чтение/запись коллекции в XML (`FileReader` / `PrintWriter`)
  - `AuthManager` – аутентификация пользователей на основе файла `users.txt`
  - серверные команды: добавление, обновление, удаление и просмотр элементов, статистика и служебные команды  
    (`AddCommand`, `AddIfMaxCommand`, `UpdateCommand`, `RemoveByIdCommand`, `RemoveLowerCommand`,
    `RemoveGreaterCommand`, `RemoveHeadCommand`, `AverageOfHeightCommand`, `MinByNameCommand`,
    `PrintFieldDescendingNationalityCommand`, `InfoCommand`, `ShowCommand`, `PrintAscendingCommand`,
    `PrintDescendingCommand`, `ClearCommand`, `ExecuteScriptCommand`, `HistoryCommand`, `HelpCommand`,
    `SaveCommand`, `AuthCommand`, `RegisterCommand`)
- `client` – клиентское приложение:
  - `Client` – управление подключением к серверу (NIO `SocketChannel`, `Selector`,
    корректная обработка временной недоступности сервера)
  - `Main` – точка входа, регистрация клиентских команд
  - `Console` / `BasicConsole` – абстракция ввода/вывода в консоль
  - `ClientCommandManager` и клиентские команды (`AddCommand`, `AddIfMaxCommand`, `UpdateCommand`,
    `RemoveLowerCommand`, `RemoveGreaterCommand`, `ExitCommand`, `RegisterCommand`, `LoginCommand` и др.)
  - пакет `util.requests` – интерактивный ввод составных объектов (`PersonRequest`, `CoordinatesRequest`, `LocationRequest`)

### Реализация основных требований ТЗ

- **Многопоточность:**
  - чтение запросов – `ExecutorService` (Fixed thread pool) в `Server`
  - обработка запроса – новый поток `Thread` в `Server.scheduleReading`
  - отправка ответов – `ForkJoinPool` в `Server.responseToClient`
  - доступ к коллекции – `ReadWriteLock` (`ReentrantReadWriteLock`) в `CollectionManager`
- **Сеть и протокол:**
  - сервер: `ServerSocketChannel` + `Selector` в неблокирующем режиме (поддержка нескольких клиентов)
  - клиент: `SocketChannel` в неблокирующем режиме, обработка временной недоступности сервера
  - обмен объектами – через `ObjectInputStream` / `ObjectOutputStream`
- **Коллекция и хранение:**
  - основная коллекция: `PriorityQueue<Person>`
  - данные хранятся в XML-файле, имя файла передаётся серверу аргументом командной строки
  - чтение – `FileReader` + DOM, запись – `PrintWriter`
  - при каждом изменении коллекции файл синхронно обновляется; при завершении работы сервера коллекция также сохраняется
- **Права доступа:**
  - каждый `Person` хранит логин создателя (`ownerLogin`)
  - модификация и удаление элементов (`remove_by_id`, `remove_lower`, `remove_greater`, `remove_head`, `update`, `clear`)
    разрешены только пользователю, который создал объект
  - просмотр (`show`, `info`, статистические команды) доступен всем авторизованным пользователям
- **Ввод данных:**
  - простые аргументы команд вводятся в одной строке с именем команды
  - составные объекты (`{element}`) вводятся по одному полю в строку с приглашением вида  
    `Введите имя (name):`, `Введите координаты (coordinates):` и т.п.
  - enum-поля (`EyeColor`, `HairColor`, `Country`) – выводится список констант, при ошибке ввода запрашивается повтор
  - пустая строка при вводе опциональных полей трактуется как `null`

### Команды (после авторизации)

- **Работа с коллекцией:**
  - `help` – вывести справку по доступным командам
  - `info` – вывести информацию о коллекции
  - `show` – вывести все элементы (отсортированы по названию)
  - `add {element}` – добавить элемент
  - `update id {element}` – обновить элемент с указанным `id`
  - `remove_by_id id` – удалить элемент по `id`
  - `clear` – удалить из коллекции все элементы текущего пользователя
  - `remove_head` – вывести первый элемент коллекции и удалить его (если он принадлежит пользователю)
  - `remove_greater {element}` – удалить элементы, превышающие заданный (только свои)
  - `remove_lower {element}` – удалить элементы, меньшие заданного (только свои)
  - `average_of_height` – вывести среднее значение поля `height`
  - `min_by_name` – вывести объект, значение поля `name` которого минимально
  - `print_field_descending_nationality` – вывести значения поля `nationality` всех элементов в порядке убывания
  - `print_ascending` / `print_descending` – вывести коллекцию в порядке возрастания / убывания
- **Скрипты и история:**
  - `execute_script file_name` – выполнить команды из файла
  - `history` – вывести историю последних команд
- **Авторизация и выход:**
  - `register` – регистрация нового пользователя (логин + пароль)
  - `auth` – вход под существующим пользователем
  - `exit` – завершение работы клиентского приложения

Команда `save` реализована только на сервере и не регистрируется в клиентском модуле.

### Сборка и запуск

1. Установите JDK 17+ и Maven.
2. В корне проекта выполните:

```bash
mvn clean package
```

3. Запуск сервера:

```bash
cd server
java -jar target/server-1.0-SNAPSHOT-shaded.jar path/to/collection.xml path/to/users.txt
```

- `path/to/collection.xml` – путь к XML-файлу коллекции (если файла нет, сервер стартует с пустой коллекцией);
- `path/to/users.txt` – путь к файлу пользователей (опционально, по умолчанию `users.txt` в рабочей директории).

4. Запуск клиента (можно несколько экземпляров одновременно):

```bash
cd client
java -jar target/client-1.0-SNAPSHOT-shaded.jar [host] [port]
```

- `host` – адрес сервера (по умолчанию `127.0.0.1`);
- `port` – порт сервера (по умолчанию `8080`).

После запуска клиента введите `auth` или `register`, затем используйте команды для работы с коллекцией.

