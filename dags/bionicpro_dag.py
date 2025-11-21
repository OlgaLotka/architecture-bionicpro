from airflow import DAG
from airflow.sdk import Connection
from airflow.providers.standard.operators.python import PythonOperator
from airflow.providers.common.sql.operators.sql import SQLExecuteQueryOperator
from datetime import datetime
import logging
from airflow.providers.postgres.hooks.postgres import PostgresHook

logger = logging.getLogger(__name__)
# Аргументы по умолчанию: владелец процесса и время отсчёта для задачи
default_args = {
    'owner': 'airflow',
    'start_date': datetime(2025, 10, 16),
    'run_id': 1
}
sql1="SELECT device_name, device_number, client_id, sensor_name, sensor_number, s.device_id as device_id,date, action_type, duration, sd.sensor_id as sensor_id FROM device d left join sensor s on s.device_id=d.id left join sensor_data sd on sd.sensor_id=s.id;"



# Функция для чтения данных и генерации SQL-запросов
def generate_insert_queries():
    my_connection = Connection.get("postgres_default")
    print(f"Connection Host: {my_connection}")
    pg_hook = PostgresHook(postgres_conn_id="postgres_default")

    # Use the hook's get_conn method to get a raw connection object.
    with pg_hook.get_conn() as conn:
        with conn.cursor() as cursor:
            cursor.execute(sql1)
            result = cursor.fetchall()
            print("Query Result:", result)

    get_all_data = result

    # Генерим запросы
    insert_queries = []
    is_header = True
    for row in get_all_data:
        print(row)
        if is_header:
            is_header = False
            continue
        insert_query = f"INSERT INTO public.data(device_name, device_number, client_id, sensor_name, sensor_number, device_id, date, action, duration, sensor_id) VALUES('{row[0]}', '{row[1]}', {row[2]}, '{row[3]}', '{row[4]}', {row[5]}, '{row[6]}', '{row[7]}', {row[8]}, {row[9]});"
        insert_queries.append(insert_query)

        # Сохраняем запросы
    with open('./insert_queries.sql', 'w') as f:
        for query in insert_queries:
            print(query)
            f.write(f"{query}\n")

def run_insert_queries():
    pg_hook = PostgresHook(postgres_conn_id="my_conn")
    FILE_PATH = './insert_queries.sql'
    with open(FILE_PATH, 'r') as f:
        sql = f.read()

    # Use the hook's get_conn method to get a raw connection object.
    with pg_hook.get_conn() as conn:
        with conn.cursor() as cursor:
            cursor.execute(sql)
            #result = cursor.fetchall()
            #print("Query Result:", result)


# Определяем DAG
with DAG('loa_dag',
         default_args=default_args, #аргументы по умолчанию в начале скрипта
         catchup=False) as dag: #предотвращает повторное выполнение DAG для пропущенных расписаний.


    #Опеределяем оператор для вставки данных
    generate_queries = PythonOperator(
        task_id='generate_insert_queries',
        python_callable=generate_insert_queries
    )

    run_insert_queries = PythonOperator(
        task_id='run_insert_queries',
        python_callable=run_insert_queries
    )

    generate_queries>>run_insert_queries
    # Тут дальше можно продолжать пайплайн