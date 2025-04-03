import os
import ollama
import requests
import logging
import json
import re
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import uvicorn

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(levelname)s - %(message)s",
    handlers=[
        logging.FileHandler("ollama_api_handler.log"),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)

LOCAL_API_URL = os.environ.get("LOCAL_API_URL", "http://host.docker.internal:3000/problems")

class StudentRequest(BaseModel):
    studentId: int
    scoreLevel: int
    prompt: str = "Recommend topics for improving problem-solving skills."

app = FastAPI()

class OllamaTopicRecommender:
    def __init__(self, model: str = "llama2", max_retries: int = 3, timeout: float = 10.0):
        self.model = model
        self.max_retries = max_retries
        self.timeout = timeout

    def extract_json_array(self, response_text):
        """Extracts the first valid JSON array from the response text."""
        # First try to find a JSON array pattern with the expected structure directly
        # Look for arrays with two elements - topics array, accuracy rate, and difficulty string
        pattern1 = r'\[\s*\[\s*"[^"]+"\s*,\s*"[^"]+"\s*\]\s*,\s*\d+(?:\.\d+)?\s*,\s*"(?:Easy|Medium|Hard)"\s*\]'
        matches = re.findall(pattern1, response_text, re.DOTALL)
        
        for match in matches:
            try:
                parsed_data = json.loads(match)
                # Validate the structure: [["Topic1", "Topic2"], acRate, "Difficulty"]
                if (isinstance(parsed_data, list) and 
                    len(parsed_data) == 3 and 
                    isinstance(parsed_data[0], list) and 
                    isinstance(parsed_data[1], (int, float)) and 
                    isinstance(parsed_data[2], str)):
                    return parsed_data
            except json.JSONDecodeError:
                continue
        
        # Try to find any JSON structure in the text as a fallback
        pattern2 = r'\[\s*\[.*?\]\s*,\s*\d+(?:\.\d+)?\s*,\s*"(?:Easy|Medium|Hard)"\s*\]'
        matches = re.findall(pattern2, response_text, re.DOTALL)
        
        for match in matches:
            try:
                parsed_data = json.loads(match)
                if isinstance(parsed_data, list) and len(parsed_data) == 3:
                    return parsed_data
            except json.JSONDecodeError:
                continue
        
        # If we still couldn't find a match, try to extract the components separately
        topics_pattern = r'\[\s*"([^"]+)"\s*,\s*"([^"]+)"\s*\]'
        topics_match = re.search(topics_pattern, response_text)
        
        ac_rate_pattern = r'(\d+(?:\.\d+)?)'
        ac_rate_match = re.search(ac_rate_pattern, response_text)
        
        difficulty_pattern = r'"(Easy|Medium|Hard)"'
        difficulty_match = re.search(difficulty_pattern, response_text)
        
        if topics_match and ac_rate_match and difficulty_match:
            topics = [topics_match.group(1), topics_match.group(2)]
            ac_rate = float(ac_rate_match.group(1))
            difficulty = difficulty_match.group(1)
            return [topics, ac_rate, difficulty]
        
        logger.warning("Could not extract valid JSON data from response")
        return None

    def get_topics(self, score_level: int):
        custom_instruction = f"""
        You are a topic recommendation system for LeetCode problems. Your ONLY task is to respond with the exact JSON array format shown below based on the user's score level.

        IMPORTANT: YOUR ENTIRE RESPONSE MUST BE ONLY THE JSON ARRAY. NO GREETING TEXT. NO EXPLANATIONS. NOTHING ELSE.

        Format: [["Topic1", "Topic2"], acRate, "Difficulty"]

        Rules:
        1. Score Level 0-25: Use topics from [Arrays, Sorting, PrefixSum, Stack, String]
           - acRate: 30-60
           - Difficulty: "Easy" or "Medium"

        2. Score Level 26-50: Use topics from [String, SlidingWindow, Stack, Queue, SuffixSum]
           - acRate: 30-40
           - Difficulty: "Easy", "Medium", or "Hard"

        3. Score Level 51-75: Use topics from [DFS, BFS, LinkedList, Greedy, Matrix, Recursion, Heap]
           - acRate: 20-40
           - Difficulty: "Easy", "Medium", or "Hard"

        4. Score Level 76-100: Use topics from [BinarySearch, Matrix, Backtracking, MonotonicStack, Tree, Graph, DynamicProgramming]
           - acRate: 10-20
           - Difficulty: "Easy", "Medium", or "Hard"

        EXAMPLE:
        If score level is 95, your complete response must be exactly: [["BinarySearch", "Matrix"], 15, "Hard"]

        DO NOT INCLUDE ANYTHING ELSE IN YOUR RESPONSE.
        """
        
        try:
            for attempt in range(self.max_retries):
                response = ollama.chat(
                    model=self.model,
                    messages=[
                        {"role": "system", "content": "You must return ONLY a JSON array in the format [['Topic1', 'Topic2'], acRate, 'Difficulty'] - nothing else. No explanations. No extra text."},
                        {"role": "user", "content": custom_instruction + f"\nUser's score level: {score_level}"}
                    ]
                )

                content = response['message']['content']
                logger.info(f"Raw Ollama Response (Attempt {attempt+1}): {content}")
                
                response_data = self.extract_json_array(content)
                
                if response_data and isinstance(response_data, list) and len(response_data) == 3:
                    topics, ac_rate, difficulty = response_data
                    logger.info(f"Successfully extracted: Topics: {topics}, acRate: {ac_rate}, Difficulty: {difficulty}")
                    return topics, ac_rate, difficulty
                
                logger.warning(f"Attempt {attempt+1}: Failed to extract valid JSON array from response")
            
            # If all attempts fail, fall back to default values based on score level
            fallback_topics, fallback_ac_rate, fallback_difficulty = self.generate_fallback_data(score_level)
            logger.warning(f"Using fallback data: {fallback_topics}, {fallback_ac_rate}, {fallback_difficulty}")
            return fallback_topics, fallback_ac_rate, fallback_difficulty

        except Exception as e:
            logger.error(f"Error in get_topics: {str(e)}")
            fallback_topics, fallback_ac_rate, fallback_difficulty = self.generate_fallback_data(score_level)
            return fallback_topics, fallback_ac_rate, fallback_difficulty

    def generate_fallback_data(self, score_level):
        """Generate fallback data based on score level if LLM response parsing fails."""
        if score_level <= 25:
            return ["Arrays", "String"], 45, "Easy"
        elif score_level <= 50:
            return ["String", "Stack"], 35, "Medium"
        elif score_level <= 75:
            return ["DFS", "Greedy"], 30, "Medium"
        else:
            return ["DynamicProgramming", "BinarySearch"], 15, "Hard"

    def fetch_problems(self, topics, ac_rate, difficulty):
        if not topics:
            return ["https://leetcode.com/problemset"]

        tag_value = "+".join(topics)
        logger.info(f"Tag values: {topics}")
        problem_url = f"{LOCAL_API_URL}?tags={tag_value}&limit=30"
        logger.info(f"Fetching problems from: {problem_url}")

        try:
            response = requests.get(problem_url, timeout=self.timeout)
            response.raise_for_status()
            problems_data = response.json()

            problem_urls = [
                {
                    "url": f"https://leetcode.com/problems/{problem.get('titleSlug', '')}",
                    "acRate": problem.get("acRate", 100),
                    "difficulty": problem.get("difficulty", "Easy")
                }
                for problem in problems_data.get("problemsetQuestionList", [])
                if not problem.get("isPaidOnly", False)
            ]

            filtered_problem_urls = [
                problem for problem in problem_urls
                if problem["acRate"] >= ac_rate and problem["difficulty"] == difficulty
            ]

            return filtered_problem_urls if filtered_problem_urls else problem_urls

        except requests.exceptions.RequestException as e:
            logger.error(f"Error fetching problems: {e}")
            return []

@app.post("/recommend-problems")
async def recommend_problems(request: StudentRequest):
    try:
        recommender = OllamaTopicRecommender()
        topics, ac_rate, difficulty = recommender.get_topics(request.scoreLevel)
        problem_list = recommender.fetch_problems(topics, ac_rate, difficulty)
        return {
            "studentId": request.studentId,
            "topics": topics,
            "acRate": ac_rate,
            "difficulty": difficulty,
            "problems": problem_list
        }
    except Exception as e:
        logger.error(f"Problem recommendation error: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)