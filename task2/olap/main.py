from flask import Flask, request, jsonify
from flask_cors import CORS
import requests, logging
import psycopg2
#import pkce
from minio import Minio
from minio.error import S3Error
import csv
import io, os
import clickhouse_connect

logger = logging.getLogger(__name__)
app = Flask(__name__)
CORS(app, origins=["http://localhost:3000"], supports_credentials=True) 

#code_verifier, code_challenge = pkce.generate_pkce_pair()

db_host = os.environ.get("DB_HOST", "localhost")
db_port = os.environ.get("DB_PORT", 9000)
cdn = os.environ.get("CDN_DOMAIN", 'localhost:8089') 
clickhouse = os.environ.get("CLICKHOUSE_HOST", "localhost")
auth = os.environ.get("AUTH",'http://localhost:8084')


minio_endpoint = os.environ.get("MINIO_ENDPOINT", "localhost:9000")#""  # Replace with your MinIO endpoint
access_key = "minio_user"          # Replace with your access key
secret_key = "minio_password"          # Replace with your secret key
secure_connection = False          # Set to True if using HTTPS

    # Bucket and object details
target_bucket = "my-test-bucket"
target_object_name = "my-uploaded-file"


client = Minio(
        minio_endpoint,
        access_key=access_key,
        secret_key=secret_key,
        secure=secure_connection
)

@app.after_request
def add_cors_headers(response):
    # Set the specific origin of your frontend application
    response.headers['Access-Control-Allow-Origin'] = 'http://localhost:3000'
    # Add other necessary headers for CORS to work fully (like methods allowed)
    response.headers['Access-Control-Allow-Methods'] = 'GET, POST, OPTIONS'
    response.headers['Access-Control-Allow-Headers'] = 'Content-Type, Authorization'
    return response

@app.route('/reports', methods=['GET', 'OPTIONS'])
def reports():
    response = jsonify({"message": "Report generated successfully"}) 

    # Handle the preflight OPTIONS request
    if request.method == 'OPTIONS':
        return response
    code_verifier = request.headers.get('code_verifier')
    session_id = request.headers.get('session_id')
    response = requests.get(auth+"/report", headers=request.headers)
    if (response.status_code == requests.codes.ok):
        #id_token = request.headers.get('Authorization')
        logger.info(f"Start reports ")
        #decoded_token = jwt.decode(id_token, options={"verify_signature": False})
        #print(decoded_token)
    # val = ''+ 'client_id=1'
        user_id = response.headers.get("name")
        file_name = target_object_name + user_id + ".csv"
        file_from_s3 = find_file_to_minio(client, target_bucket, file_name, user_id)
        if file_from_s3 is not None:
            report_data = {
                "id": user_id,
                "name": file_name,
                "data": file_from_s3,
                "direct_url": get_cdn_url(file_name)
                }
            return jsonify(report_data)
        param  = request.data
        #cur = conn.cursor()
        rows = get_data(user_id)
        #cur.execute("SELECT * FROM data where %s", val)
        #cur.execute("SELECT * FROM data where client_id= %s ", (user_id,))
        #rows = cur.fetchall()
        
        output_file = io.StringIO()
        writer = csv.writer(output_file)
        if rows != None:
            writer.writerows(rows)
        output_file.seek(0)
        output_file.flush() 
        csv_content_string = output_file.getvalue()
        output_file.close()
        upload_file_to_minio(client, target_bucket, file_name, csv_content_string)
        report_data = {
            "id": user_id,
            "name": file_name,
            "data": rows,
            "direct_url": get_cdn_url(file_name) #"http://"+minio_endpoint+"/"+target_bucket+"/"+file_name
        }

        return jsonify(report_data, user_id)
    
def get_data(user_id):
    try:
        client = clickhouse_connect.get_client(host=clickhouse, port=8123, database= "default")
        print("Successfully connected to ClickHouse!")
        params = {
            'client_id': 1
        }
        # Example: Execute a simple query
        result = client.query("SELECT * FROM emg_sensor_data where user_id =%(client_id)s ", params)
        print(f"Query result: {result.result_rows}")

    except Exception as e:
        print(f"Error connecting to ClickHouse: {e}")

    finally:
        if 'client' in locals() and client:
            client.close() # Close the connection when done
            print("Connection closed.")

def find_file_to_minio(minio_client, bucket_name, object_name, user_id):

    try:
        # Check if the bucket exists, create it if not

        response = minio_client.get_object(target_bucket, object_name)
        
        # Read data from the response stream
        print(f"'{object_name}' successfully find ' to bucket '{bucket_name}'.")
        bytes_data = response.read()

        # 3. Decode the bytes to a string (assuming UTF-8 encoding)
        csv_string = bytes_data.decode('utf-8')

        # 4. Wrap the string in a StringIO object to treat it as a file
        csv_file = io.StringIO(csv_string)

        # 5. Use the csv.reader to parse the data
        csv_reader = csv.reader(csv_file, delimiter=',')
        
        # Iterate over rows
        data_as_array = []
        for row in csv_reader:
            data_as_array.append(row)
        return data_as_array
        

    except S3Error as err:
        print(f"Error uploading file: {err}")
    #finally:
    # 4. CRITICAL: Always close the response object to release the connection
        #if response:
            #response.close()
            #response.release_conn()

def upload_file_to_minio(minio_client, bucket_name, object_name, csv_content_string):
    data_stream = None 
    data_length = None 
    try:
        # Check if the bucket exists, create it if not
        if not minio_client.bucket_exists(bucket_name):
            minio_client.make_bucket(bucket_name)
            print(f"Bucket '{bucket_name}' created successfully.")
        if not csv_content_string:
            print("No data provided to upload.")
            return
        csv_content_bytes = csv_content_string.encode('utf-8')

            # Wrap the bytes in an io.BytesIO buffer to act as an in-memory file stream
        data_stream = io.BytesIO(csv_content_bytes)

            # Get the exact length of the bytes data (required by put_object)
        data_length = len(csv_content_bytes)
        # Upload the file
        result = minio_client.put_object(
            bucket_name,
            object_name,
            data_stream,
            data_length,
            content_type='text/csv'
        )
        print(f"'File successfully uploaded as '{object_name}' to bucket '{bucket_name}'.")

    except S3Error as err:
        print(f"Error uploading file: {err}")
    finally:
        # The finally block can safely check if the variable was ever assigned
        if data_stream:
            data_stream.close()
def get_cdn_url(object_name):
    """
    Генерирует URL для доступа к файлу через CDN.
    """
    # Формат URL для CloudFront: https://<CDN_DOMAIN>/<OBJECT_NAME>
    cdn_url = f"https://{cdn}/{object_name}"
    return cdn_url

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=8085, debug=True)