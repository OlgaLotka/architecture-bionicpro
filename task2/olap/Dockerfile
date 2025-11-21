FROM python:3.12.1-slim
WORKDIR /app
EXPOSE 8085
COPY requirements.txt ./
# Устанавливаем зависимости python не пересобирая их
RUN pip install --no-cache --no-cache-dir -r requirements.txt
# Копирование кода приложения
COPY main.py /app/
#ENTRYPOINT ["uvicorn"]
CMD ["gunicorn", "--bind", "0.0.0.0:8085", "main:app"]