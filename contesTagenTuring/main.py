import os
import json
import asyncio
from typing import List, Dict, Any, Union
from enum import Enum
from dotenv import load_dotenv

from fastapi import FastAPI, HTTPException, Body
from pydantic import BaseModel, Field, validator

# Load environment variables
load_dotenv()

# --- LLM Clients ---
# Google Gemini
try:
    import google.generativeai as genai
    from google.api_core import exceptions as google_exceptions
    GEMINI_AVAILABLE = True
except ModuleNotFoundError:
    print("Google Gemini API not available - will use fallback if configured")
    genai = None
    google_exceptions = None
    GEMINI_AVAILABLE = False

# --- Configuration & Initialization ---

app = FastAPI(
    title="DSA Problem Generator API",
    version="1.0.0",
    description="Generates DSA problems and formatted test cases using LLMs.",
)

# Configure Gemini Client
gemini_model = None
if GEMINI_AVAILABLE:
    try:
        api_key = os.getenv("GEMINI_API_KEY")
        if api_key:
            genai.configure(api_key=api_key)
            # Using gemini-1.5-flash-latest for free tier access
            gemini_model = genai.GenerativeModel('gemini-1.5-flash-latest')
            # Optional configuration for more deterministic output
            generation_config = genai.types.GenerationConfig(
                temperature=0.2  # Lower temperature for more deterministic JSON structure
            )
            gemini_model = genai.GenerativeModel('gemini-1.5-flash-latest', generation_config=generation_config)
            print("Gemini client configured successfully.")
        else:
            print("Gemini API key not found in environment variables.")
    except Exception as e:
        print(f"Error configuring Gemini client: {e}")
        gemini_model = None  # Ensure it's None if config fails

# --- Models and Enums ---

class DifficultyLevel(str, Enum):
    EASY = "easy"
    MEDIUM = "medium" 
    HARD = "hard"

class ProblemRequest(BaseModel):
    topicTag: str = Field(..., description="DSA topic like 'Arrays', 'Strings', 'Stack', 'Queue', 'DynamicProgramming'")
    difficulty: DifficultyLevel = Field(DifficultyLevel.EASY, description="Difficulty level")
    count: int = Field(1, ge=1, le=5, description="Number of problems to generate (1-5)")
    
    @validator('topicTag')
    def validate_topic(cls, v):
        # Convert to lowercase for case-insensitive comparison
        v_lower = v.lower()
        
        # Define allowed and disallowed topics
        allowed_topics = ['arrays', 'strings', 'stack', 'queue', 'dynamic programming', 
                         'sorting', 'searching', 'recursion', 'backtracking', 'greedy', 
                         'bit manipulation', 'math', 'hashing']
        
        disallowed_topics = ['tree', 'graph', 'linked list']
        
        # Check if topic contains any disallowed keywords
        for topic in disallowed_topics:
            if topic in v_lower:
                raise ValueError(f"Topic '{v}' is not allowed. Tree, Graph, and LinkedList topics are excluded.")
        
        # Check if it's a recognized topic
        if not any(topic in v_lower for topic in allowed_topics):
            print(f"Warning: '{v}' may not be a standard DSA topic")
            
        return v

# --- Prompt Generation Functions ---

def create_problem_prompt(topic: str, difficulty: str) -> str:
    """Creates the prompt for generating a DSA problem."""
    # Make sure to exclude Tree/Graph/LinkedList topics
    prompt = f"""
You are an expert Data Structures and Algorithms (DSA) problem generator.

IMPORTANT CONSTRAINTS:
- Do NOT create problems involving Tree, Graph, or LinkedList data structures
- Focus ONLY on the following data structures and algorithms: Arrays, Strings, Stack, Queue, Dynamic Programming, Sorting, Searching, Recursion, Backtracking, Greedy Algorithms, Bit Manipulation, Math, and Hashing
- Stay strictly within the requested topic: {topic}
- Match the requested difficulty level: {difficulty}

Create a unique programming challenge based on the provided topic and difficulty level.

Generate the following details for the problem:
1. **problemName**: A concise title for the problem (e.g., "Find Peak Element").
2. **problemSlug**: A URL-friendly slug with underscores and 5 random digits (e.g., "Find_Peak_Element_12345").
3. **description**: A clear problem statement with the task, inputs, expected output, and examples. Use markdown formatting.
4. **constraints**: A string listing realistic constraints for input parameters, using '\\n- ' for bullet points.
5. **inputFields**: A string defining the input types using notation: <int>, <float>, <string>, <boolean>, <int[]>, <string[]>, etc.
6. **outputField**: A string defining the output type using the same notation.

Your response MUST be ONLY a single, valid JSON object containing exactly these six keys.
Do not include any text outside of the JSON object structure.

Example JSON structure:
{{
  "problemName": "Sum of Two Integers",
  "problemSlug": "Sum_Of_Two_Integers_98765",
  "description": "Given two integers, `a` and `b`, return their sum.",
  "constraints": "- -1000 <= a <= 1000\\n- -1000 <= b <= 1000",
  "inputFields": "<int> <int>",
  "outputField": "<int>"
}}
"""
    return prompt

def create_test_case_prompt(problem_data: Dict[str, Any]) -> str:
    """Creates the prompt for generating test cases for a given problem."""
    prompt = f"""
You are an expert DSA Test Case Generator.
Your task is to generate exactly 5 diverse test cases for the following programming problem. The first test case MUST be marked as a sample.

Problem Context:
- Name: {problem_data['problemName']}
- Description: {problem_data['description']}
- Constraints: {problem_data['constraints']}
- Input Fields Structure: {problem_data['inputFields']}
- Output Field Structure: {problem_data['outputField']}

Generate raw input values matching the Input Fields Structure and the corresponding correct raw output value.

Your response MUST be ONLY a single, valid JSON array containing exactly 5 test case objects. Each object must have:
- "tcName": string (Use "tc01", "tc02", "tc03", "tc04", "tc05")
- "isSample": boolean (true for "tc01", false for others)
- "input": The raw input value(s) (can be primitive types or arrays as needed)
- "output": The corresponding raw correct output value
- "problemSlug": Copy the problemSlug value: "{problem_data['problemSlug']}"

Example JSON structure (for a hypothetical problem):
[
  {{
    "tcName": "tc01",
    "isSample": true,
    "input": [2, 7, 11, 15],
    "output": [0, 1],
    "problemSlug": "Two_Sum_12345"
  }},
  {{
    "tcName": "tc02",
    "isSample": false,
    "input": [3, 2, 4],
    "output": [1, 2],
    "problemSlug": "Two_Sum_12345"
  }},
  ...
]
"""
    return prompt

# --- Input/Output Formatting Functions ---

def format_primitive(value) -> str:
    """Formats primitive types into string."""
    if isinstance(value, str):
        # Remove any quotes added by LLM
        if len(value) >= 2 and value.startswith('"') and value.endswith('"'):
            return value[1:-1]
        if len(value) >= 2 and value.startswith("'") and value.endswith("'"):
            return value[1:-1]
    # Booleans as lowercase strings
    return str(value).lower() if isinstance(value, bool) else str(value)

def format_1d_array_input(arr) -> str:
    """Formats a 1D list for input."""
    if not isinstance(arr, list):
        raise ValueError(f"Input for 1D array must be a list, got {type(arr)}")
    size = len(arr)
    elements = " ".join(format_primitive(item) for item in arr)
    return f"{size}\n[{elements}]"

def format_1d_array_output(arr) -> str:
    """Formats a 1D list for output."""
    if not isinstance(arr, list):
        raise ValueError(f"Output for 1D array must be a list, got {type(arr)}")
    elements = " ".join(format_primitive(item) for item in arr)
    return f"[{elements}]"

def format_2d_array_input(arr) -> str:
    """Formats a 2D list for input."""
    if not isinstance(arr, list) or (len(arr) > 0 and not all(isinstance(row, list) for row in arr)):
        raise ValueError(f"Input for 2D array must be a list of lists, got {type(arr)}")
    rows = len(arr)
    cols = len(arr[0]) if rows > 0 else 0
    
    # Check consistent column counts
    if rows > 0 and not all(len(row) == cols for row in arr):
        raise ValueError("Input for 2D array has inconsistent column counts.")

    formatted_rows = [f"[{' '.join(format_primitive(item) for item in row)}]" for row in arr]
    
    # Handle empty 2D array
    if rows == 0:
        return "0\n0"
    
    return f"{rows}\n{cols}\n" + "\n".join(formatted_rows)

def format_2d_array_output(arr) -> str:
    """Formats a 2D list for output."""
    if not isinstance(arr, list) or (len(arr) > 0 and not all(isinstance(row, list) for row in arr)):
        raise ValueError(f"Output for 2D array must be a list of lists, got {type(arr)}")
    
    formatted_rows = [f"[{' '.join(format_primitive(item) for item in row)}]" for row in arr]
    return "\n".join(formatted_rows)

def parse_type_string(type_str: str) -> List[str]:
    """Parses type string like '<int> <string>' into parts."""
    return type_str.strip().split(' ')

def format_value(value: Any, type_sig: str, is_input: bool) -> str:
    """Formats a single value based on its type signature."""
    try:
        # Detect array types
        if '[][]' in type_sig:  # 2D array
            formatter = format_2d_array_input if is_input else format_2d_array_output
            return formatter(value)
        elif '[]' in type_sig:  # 1D array
            formatter = format_1d_array_input if is_input else format_1d_array_output
            return formatter(value)
        else:  # Primitive type
            return format_primitive(value)
    except (ValueError, TypeError) as e:
        # Add more context to the error
        raise ValueError(f"Error formatting value for type '{type_sig}': {e}") from e

def format_input_data(raw_input_data: Any, input_fields_sig: str) -> str:
    """Formats potentially multi-part input data."""
    type_sigs = parse_type_string(input_fields_sig)
    
    # Handle single input case
    if len(type_sigs) == 1:
        return format_value(raw_input_data, type_sigs[0], is_input=True)
    
    # Handle multi-input case
    if not isinstance(raw_input_data, list) or len(raw_input_data) != len(type_sigs):
        raise ValueError(f"Expected {len(type_sigs)} inputs but got {len(raw_input_data) if isinstance(raw_input_data, list) else 'non-list'}")
    
    formatted_parts = [
        format_value(raw_input_data[i], type_sigs[i], is_input=True)
        for i in range(len(type_sigs))
    ]
    
    return "\n".join(formatted_parts)

def format_output_data(raw_output_data: Any, output_field_sig: str) -> str:
    """Formats output data."""
    type_sigs = parse_type_string(output_field_sig)
    
    # Use first type signature for output
    return format_value(raw_output_data, type_sigs[0], is_input=False)

def format_testcase(raw_tc: Dict[str, Any], input_fields_sig: str, output_field_sig: str) -> Dict[str, Any]:
    """Formats the input and output within a raw test case dictionary."""
    tc_name = raw_tc.get('tcName', 'UnknownTC')
    try:
        # Validate raw test case
        if 'input' not in raw_tc:
            raise KeyError("'input' key missing in raw test case")
        if 'output' not in raw_tc:
            raise KeyError("'output' key missing in raw test case")

        formatted_input = format_input_data(raw_tc['input'], input_fields_sig)
        formatted_output = format_output_data(raw_tc['output'], output_field_sig)

        # Structure per user spec
        return {
            "tcName": tc_name,
            "inputFields": formatted_input,
            "outputField": formatted_output,
            "sample": 1 if raw_tc.get('isSample', False) else 0,
            "explanation": "",  # Corrected the typo in the field name
            "problemSlug": raw_tc.get('problemSlug', "Unknown_Slug")
        }
    except (ValueError, TypeError, KeyError) as e:
        raise ValueError(f"Error formatting test case '{tc_name}': {e}") from e

# --- LLM Interaction Functions ---

async def call_gemini(prompt: str) -> str:
    """Calls the Gemini API and handles errors."""
    if not gemini_model:
        raise ValueError("Gemini model not configured")
    
    try:
        response = await gemini_model.generate_content_async(prompt)
        
        if not response.parts:
            block_reason = "Unknown"
            try:
                if response.prompt_feedback:
                    block_reason = response.prompt_feedback.block_reason
            except Exception:
                pass
            raise ValueError(f"Gemini response blocked. Reason: {block_reason}")
        
        return response.text
    except Exception as e:
        raise ValueError(f"Gemini API error: {type(e).__name__} - {e}")

async def safe_llm_call(prompt: str) -> str:
    """Call the available LLM model."""
    # Try Gemini if available
    if gemini_model:
        try:
            return await call_gemini(prompt)
        except ValueError as e:
            raise HTTPException(status_code=503, detail=f"LLM generation failed: {e}")
    
    # If no models available
    raise HTTPException(status_code=503, detail="No LLM models available")

async def extract_json_safely(text: str) -> Any:
    """Extract valid JSON from text that might contain non-JSON content."""
    # Clean up the text to handle potential formatting issues
    text = text.strip()
    
    # Try parsing the entire text as JSON first
    try:
        return json.loads(text)
    except json.JSONDecodeError:
        pass
    
    # Look for JSON within the text
    start_idx = text.find('{')
    end_idx = text.rfind('}')
    
    if start_idx >= 0 and end_idx > start_idx:
        json_text = text[start_idx:end_idx+1]
        try:
            return json.loads(json_text)
        except json.JSONDecodeError:
            pass
    
    # Try to find array JSON
    start_idx = text.find('[')
    end_idx = text.rfind(']')
    
    if start_idx >= 0 and end_idx > start_idx:
        json_text = text[start_idx:end_idx+1]
        try:
            return json.loads(json_text)
        except json.JSONDecodeError:
            pass
    
    # If we get here, couldn't extract valid JSON
    raise ValueError(f"Failed to extract valid JSON from response")

async def generate_single_problem(topic: str, difficulty: str) -> Dict[str, Any]:
    """Generate a single problem with test cases."""
    # Step 1: Generate Problem
    problem_prompt = create_problem_prompt(topic, difficulty)
    problem_data_raw = await safe_llm_call(problem_prompt)
    
    try:
        problem_data = await extract_json_safely(problem_data_raw)
        required_keys = {"problemName", "problemSlug", "description", "constraints", "inputFields", "outputField"}
        if not required_keys.issubset(problem_data.keys()):
            missing = required_keys - set(problem_data.keys())
            raise ValueError(f"LLM problem response missing required keys: {missing}")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error generating problem: {e}")
    
    # Step 2: Generate Test Cases
    test_case_prompt = create_test_case_prompt(problem_data)
    test_cases_raw_data = await safe_llm_call(test_case_prompt)
    
    try:
        raw_testcases_list = await extract_json_safely(test_cases_raw_data)
        if not isinstance(raw_testcases_list, list):
            raise ValueError(f"Expected a JSON array of test cases, got {type(raw_testcases_list)}")
        if len(raw_testcases_list) < 5:
            print(f"Warning: Received only {len(raw_testcases_list)} test cases, expected 5")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error generating test cases: {e}")
    
    # Step 3: Format Test Cases
    formatted_testcases = []
    problem_slug = problem_data.get("problemSlug", "Unknown_Slug")

    #print(raw_testcases_list)
    
    for i, raw_tc in enumerate(raw_testcases_list):
        try:
            # Ensure proper test case structure
            raw_tc['tcName'] = f"tc{i+1:02d}"
            raw_tc['isSample'] = raw_tc.get('isSample', (i == 0))
            raw_tc['problemSlug'] = problem_slug
            
            formatted_tc = format_testcase(raw_tc, problem_data['inputFields'], problem_data['outputField'])
            formatted_testcases.append(formatted_tc)
        except Exception as e:
            print(f"Error formatting test case {i+1}: {e}")
            # Continue with other test cases instead of failing
    
    # Step 4: Create Final Response
    final_response = {
        "problemName": problem_data["problemName"],
        "description": problem_data["description"],
        "inputFields": problem_data["inputFields"],
        "outputField": problem_data["outputField"],
        "constraints": problem_data["constraints"],
        "points": 10,
        "testcases": formatted_testcases
    }
    
    return final_response

# --- FastAPI Endpoints ---

@app.get("/")
async def read_root():
    """Root endpoint providing API status."""
    status = "Ready" if gemini_model else "No LLM models configured"
    return {
        "status": status,
        "availableModels": ["Gemini"] if gemini_model else [],
        "version": "1.0.0"
    }

@app.post("/generate", summary="Generate DSA Problems")
async def generate_problems_endpoint(request: ProblemRequest = Body(...)):
    """
    Generates DSA problems based on topic, difficulty, and count.
    
    - Excludes Tree, Graph, and LinkedList topics
    - Provides formatted problem descriptions and test cases
    """
    if not gemini_model:
        raise HTTPException(
            status_code=503, 
            detail="No LLM models available. Configure GEMINI_API_KEY in your .env file."
        )
    
    # Convert enum to string
    difficulty = request.difficulty.value
    topic = request.topicTag
    count = request.count
    
    print(f"Generating {count} '{difficulty}' difficulty problems for topic '{topic}'")
    
    # Generate the requested number of problems
    problems = []
    for i in range(count):
        try:
            problem = await generate_single_problem(topic, difficulty)
            problems.append(problem)
        except Exception as e:
            print(f"Error generating problem {i+1}: {e}")
            # If we have at least one problem, return what we have
            if problems:
                break
            # Otherwise re-raise the exception
            raise
    
    return {
        "count": len(problems),
        "topic": topic,
        "difficulty": difficulty,
        "problems": problems
    }

# --- Run with Uvicorn if executed directly ---
if __name__ == "__main__":
    import uvicorn
    print("Starting DSA Problem Generator API...")
    
    # Check configuration status
    if not gemini_model:
        print("\nWARNING: No LLM models configured. API endpoints will fail.")
        print("Set GEMINI_API_KEY in your .env file.\n")
    
    # Run the server
    uvicorn.run("main:app", host="127.0.0.1", port=8001, reload=True)