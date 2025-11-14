CREATE TABLE IF NOT EXISTS emg_sensor_data (
    user_id UInt32,
    prosthesis_type String,
    muscle_group String,
    signal_frequency UInt32,
    signal_duration UInt32,
    signal_amplitude Decimal(5,2),
    signal_time DateTime
) ENGINE = MergeTree()
ORDER BY (user_id, prosthesis_type, signal_time);

INSERT INTO emg_sensor_data
SELECT *
FROM file('olap.csv', 'CSV');


CREATE TABLE IF NOT EXISTS events_kafka
(
    user_id UInt32,
    prosthesis_type String,
    muscle_group String,
    signal_frequency UInt32,
    signal_duration UInt32,
    signal_amplitude Decimal(5, 2),
    signal_time DateTime
)
ENGINE = Kafka()
SETTINGS
    kafka_broker_list = 'localhost:29092',
    kafka_topic_list = 'my_events_topic',
    kafka_group_name = 'clickhouse_consumer_group',
    kafka_format = 'JSONEachRow',
    kafka_skip_broken_messages = 10



CREATE MATERIALIZED VIEW emg_sensor_data_mv TO emg_sensor_data AS
SELECT
    user_id,
    prosthesis_type,
    muscle_group,
    signal_frequency,
    signal_duration,
    signal_amplitude,
    signal_time
FROM
    events_kafka