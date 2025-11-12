from flask import Flask, request, jsonify
import requests, logging
import psycopg2
#import pkce
import jwt

logger = logging.getLogger(__name__)
app = Flask(__name__)

#code_verifier, code_challenge = pkce.generate_pkce_pair()

conn = psycopg2.connect(
    host="localhost",
    port=5434,
    database="bionicpro",
    user="airflow",
    password="airflow"
)

@app.route('/reports', methods=['GET'])
def reports():
    code_verifier = request.headers.get('code_verifier')
    session_id = request.headers.get('session_id')
    response = requests.get("http://localhost:8084/report", headers=request.headers)
    if (response.status_code == requests.codes.ok):
        #id_token = request.headers.get('Authorization')
        logger.info(f"Start reports ")
        #decoded_token = jwt.decode(id_token, options={"verify_signature": False})
        #print(decoded_token)
    # val = ''+ 'client_id=1'
        user_id = response.headers.get("name")
        param  = request.data
        cur = conn.cursor()

        #cur.execute("SELECT * FROM data where %s", val)
        cur.execute("SELECT * FROM data where client_id= %s ", (user_id,))
        rows = cur.fetchall()
        return rows

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=8085, debug=True)