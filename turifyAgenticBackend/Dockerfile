FROM --platform=linux/amd64 python:3.9-slim

# Install system dependencies
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy project files
COPY requirements.txt ./
COPY src/leetcode_problem_recommender.py ./

# Install Python dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Expose application port
EXPOSE 8000

# Command to run the application
CMD ["uvicorn", "leetcode_problem_recommender:app", "--host", "0.0.0.0", "--port", "8000"]
