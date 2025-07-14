REM Activate the virtual environment (Windows uses Scripts folder)
venv\Scripts\activate

REM Start Uvicorn server
uvicorn main:app --reload --host 0.0.0.0 --port 8000