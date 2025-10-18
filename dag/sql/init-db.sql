CREATE DATABASE bionicpro;
GRANT ALL PRIVILEGES ON DATABASE bionicpro TO airflow;
\c bionicpro;
CREATE TABLE public.data (
	device_name varchar NULL,
	device_number varchar NULL,
	client_id varchar NULL,
	sensor_name varchar NULL,
	sensor_number varchar NULL,
	device_id varchar NULL,
	"date" timestamp NULL,
	"action" varchar NULL,
	duration varchar NULL,
	sensor_id varchar NULL
);