from flask import Flask, request, jsonify
import requests, logging
import psycopg2
import jwt

logger = logging.getLogger(__name__)
app = Flask(__name__)

conn = psycopg2.connect(
    host="localhost",
    port=5434,
    database="bionicpro",
    user="airflow",
    password="airflow"
)

@app.route('/reports', methods=['GET'])
def reports():
    id_token = request.headers.get('Authorization')
    logger.info(f"Start reports ")
    decoded_token = jwt.decode(id_token, options={"verify_signature": False})
    print(decoded_token)
   # val = ''+ 'client_id=1'
    cur = conn.cursor()

    #cur.execute("SELECT * FROM data where %s", val)
    cur.execute("SELECT * FROM data")
    rows = cur.fetchall()
    return rows

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=8085, debug=True)