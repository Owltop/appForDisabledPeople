Как запустить сервер:

`docker-compose build`

`docker-compose up`

В соседнем окне запускаем команды: 

После регистрации и логина будет получен token, все действия с заявками выполнять передавая его

Регистрация

Волонтер
`curl -d '{"login":"aaroy",  "password":"1234", "email":"aaroy@xxx.com", "age":"11", "name":"Andrew", "account_type":"volunteer"}' -H "Content-Type: application/json" -X POST http://localhost:5050/register`

Пользователь
`curl -d '{"login":"aaroy1",  "password":"1234", "email":"aaroy1@xxx.com", "age":"11", "name":"Andrew", "account_type":"customer"}' -H "Content-Type: application/json" -X POST http://localhost:5050/register`

Обновление данных о пользователе

`curl -d '{"login":"aaroy1",  "password":"1234", "email":"aaroy1@xxx.com", "age":"11", "name":"Andrew", "account_type":"customer", "token": "OOApw6wjZ94muzFgzLx5c6h5mxW80T"}' -H "Content-Type: application/json" -X POST http://localhost:5050/update`

Вход в аккаунт
`curl -d '{"login_or_email":"aaroy",  "password":"1234"}' -H "Content-Type: application/json" -X POST http://localhost:5050/login`


Создание заявки
`curl -d '{"author":"aaroy",  "description":"test", "latitude":"14.58", "longitude":"11.44", "region":"Moscow", "account_type":"customer", "token": "OOApw6wjZ94muzFgzLx5c6h5mxW80T"}' -H "Content-Type: application/json" -X POST http://localhost:5050/create_request`


Принятие заявки в работу
`curl -d '{"executor":"aaroy", "region":"Moscow", "account_type":"volunteer", "token": "OOApw6wjZ94muzFgzLx5c6h5mxW80T"}' -H "Content-Type: application/json" -X POST http://localhost:5050/accept_request`


Завершение заявки
`curl -d '{"executor":"aaroy", "request_id":"1", "account_type":"volunteer", "token": "OOApw6wjZ94muzFgzLx5c6h5mxW80T"}' -H "Content-Type: application/json" -X POST http://localhost:5050/finish_request`

Получение всех заявок пользователя
`curl -G -i "http://localhost:5050/get_customer_requests" -d "customer=aaroy1" -d "token=wM1zcXN2xH2NbnLwomR1x6eGVucUAo"`

Получение всех заявок без исполнителя
`curl -G -i "http://localhost:5050/get_active_requests" -d "volunteer=aaroy" -d "region=Moscow" -d "token=e9TJoxjx3nLRAJ52UA2L0ZdF0wgGjU"`

Получение рейтинга пользователя
`curl -G -i "http://localhost:5053/get_rating" -d "volunteer=aaroy" -d "region=Moscow" -d "token=e9TJoxjx3nLRAJ52UA2L0ZdF0wgGjU"`

Демонстрация работы бета-версии приложения: https://disk.yandex.ru/i/JxGvBdUOpg9AAQ
