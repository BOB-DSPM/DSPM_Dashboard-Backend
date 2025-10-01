from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
import httpx
import os

app = FastAPI(
    title="DSPM Dashboard Backend",
    description="Backend API for DSPM Dashboard",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"], 
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 환경변수에서 analyzer URL 가져오기 (기본값: http://analyzer:8080)
ANALYZER_BASE_URL = os.getenv("ANALYZER_URL", "http://analyzer:8080")


@app.get("/")
async def root():
    """루트 엔드포인트"""
    return {
        "message": "DSPM Dashboard Backend",
        "version": "1.0.0",
        "analyzer_url": ANALYZER_BASE_URL
    }


@app.get("/health")
async def health_check():
    """백엔드 헬스체크"""
    return {"status": "healthy", "service": "dspm-backend"}


@app.get("/analyzer/health")
async def get_analyzer_health():
    """Analyzer 서비스 헬스체크를 프록시"""
    try:
        async with httpx.AsyncClient() as client:
            response = await client.get(f"{ANALYZER_BASE_URL}/health", timeout=10.0)
            return response.json()
    except httpx.ConnectError:
        raise HTTPException(status_code=503, detail="Analyzer service unreachable")
    except httpx.TimeoutException:
        raise HTTPException(status_code=504, detail="Analyzer service timeout")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error: {str(e)}")


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)