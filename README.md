# DSPM Dashboard Backend

FastAPI 기반의 DSPM(Data Security Posture Management) 대시보드 백엔드 API 서버입니다.

## 🚀 주요 기능

- React 프론트엔드와의 API 통신
- Analyzer 서비스 프록시 기능 (`http://analyzer:8080/health`)
- 자동 API 문서화 (Swagger UI)
- CORS 지원

## 📋 API 엔드포인트

- `GET /` - 백엔드 정보
- `GET /health` - 백엔드 헬스체크  
- `GET /analyzer/health` - Analyzer 서비스 헬스체크 프록시
- `GET /docs` - API 문서 (Swagger UI)

## 🛠 로컬 개발

### Python 직접 실행

```bash
# 의존성 설치
pip install -r requirements.txt

# 서버 실행
python main.py
```

### Docker 실행

```bash
# 이미지 빌드
docker build -t dspm-backend .

# 컨테이너 실행
docker run -p 8000:8000 dspm-backend
```

## 🌍 환경변수

| 변수명 | 기본값 | 설명 |
|--------|--------|------|
| `ANALYZER_BASE_URL` | `http://analyzer:8080` | Analyzer 서비스 URL |

## 🚀 배포

GitHub Actions를 통해 자동 배포가 설정되어 있습니다:

1. `main` 브랜치에 푸시
2. Docker 이미지 빌드 및 DockerHub 푸시
3. EKS 클러스터에 자동 배포

### 필요한 GitHub Secrets

- `DOCKERHUB_USERNAME`: DockerHub 사용자명
- `DOCKERHUB_TOKEN`: DockerHub 액세스 토큰
- `AWS_ACCESS_KEY_ID`: AWS 액세스 키
- `AWS_SECRET_ACCESS_KEY`: AWS 시크릿 키
- `EKS_CLUSTER_NAME`: EKS 클러스터 이름

## 🔧 기술 스택

- **FastAPI**: 현대적이고 빠른 Python 웹 프레임워크
- **Uvicorn**: ASGI 서버
- **HTTPx**: 비동기 HTTP 클라이언트
- **Docker**: 컨테이너화
- **GitHub Actions**: CI/CD