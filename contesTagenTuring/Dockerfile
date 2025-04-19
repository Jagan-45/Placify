# 1. Use an official Python runtime as a parent image
# Choose a version that matches your development environment if possible
# Using -slim reduces image size
FROM python:3.10-slim

# 2. Set environment variables for Python best practices
# Prevents Python from writing pyc files to disc (equivalent to python -B)
ENV PYTHONDONTWRITEBYTECODE 1
# Ensures Python output is sent straight to terminal without buffering
ENV PYTHONUNBUFFERED 1

# 3. Set the working directory in the container
WORKDIR /app

# 4. Copy only the requirements file first to leverage Docker cache
COPY requirements.txt .

# 5. Install pip dependencies
# --no-cache-dir reduces image size
# --upgrade pip ensures the latest pip is used
RUN pip install uvicorn

RUN pip install --no-cache-dir --upgrade pip -r requirements.txt

# 6. Copy the rest of the application code into the working directory
# This includes main.py and respects.dockerignore
COPY . .

# 7. Expose the port the app runs on (uvicorn default is 8000)
EXPOSE 8033

# 8. Define the command to run the application
# Use 0.0.0.0 to make it accessible from outside the container
# Use exec form for CMD
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]