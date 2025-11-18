Запуск сервисов в докере
```bash
docker compose -f docker-compose-airflow.yaml up -d
```
```bash
docker compose -f docker-compose-bionicpro.yaml up -d
```
Сервис авторизации: bionicpro-auth
Фронт: frontend
Сервис отчетов: [task2/olap](task2/olap)

Инит бд: [dag/sql](dag/sql)
Добавила скрин что даг работает

Даг: [dags/bionicpro_dag.py](dags/bionicpro_dag.py)

keyclock realm: [task1/realm-export.json](task1/realm-export.json)


Ендоинты для проверки сервисов

**Получение токена /обновление** 

curl -X POST "http://localhost:8084/auth" -H "Content-Type: application/json" -H "Accept: application/json" -H "Authorization: Basic YWRtaW4xOmFkbWluMTIz

**Получение отчета** 

curl -X GET "http://localhost:8085/reports" -H "Content-Type: application/json" -H "Accept: application/json" -H "Authorization: Basic YWRtaW4xOmFkbWluMTIz

curl -X GET "http://localhost:8085/reports" -H "Content-Type: application/json" -H "Accept: application/json" -H "Cookie:session_id=**Ид Сессии**" 