CREATE DATABASE bionicpro;
GRANT ALL PRIVILEGES ON DATABASE bionicpro TO airflow;
\c bionicpro;

CREATE TABLE public.device (
	id int NOT NULL,
	device_name varchar NULL,
	device_number varchar NULL,
	client_id int null,
	CONSTRAINT device_pk PRIMARY KEY (id)
);

CREATE TABLE public.sensor (
	id int NOT NULL,
	sensor_name varchar NULL,
	sensor_number varchar NULL,
	device_id int NULL,
	CONSTRAINT sensor_pk PRIMARY KEY (id)
);

CREATE TABLE public.sensor_data (
	id int NOT NULL,
	"date" timestamp NULL,
	action_type varchar NULL,
	duration varchar NULL,
	sensor_id int NULL,
	CONSTRAINT sensor_data_pk PRIMARY KEY (id)
);



