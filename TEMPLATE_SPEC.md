# 📋 Template 스펙 정의서

## 🎯 3단계 Template 구성

### 스펙 비교표

| Template | 이름 | vCPU | RAM | Root Disk | Swap Disk | OpenStack Flavor | 타겟 용도 |
|----------|------|------|-----|-----------|-----------|------------------|-----------|
| **1** | Nano | 1 Core | 512MB | 10GB | 1GB | `m1.nano` | 개인 프로젝트 |
| **2** | Standard | 1 Core | 1GB | 20GB | 2GB | `m1.tiny` | 해커톤 |
| **3** | Pro | 2 Cores | 3GB | 40GB | 4GB | `m1.small` | 데모데이 |

**공통 사항:**
- ✅ OS: Ubuntu 22.04 LTS (고정)
- ✅ 용도별 맞춤 스펙

---

## 📊 각 Template 상세

### 🥉 Template 1: Nano (개인 프로젝트)

```yaml
이름: Ubuntu 22.04 Nano
vCPU: 1 Core
RAM: 512MB
Root Disk: 10GB
Swap Disk: 1GB
Flavor: m1.nano
```

**특징 및 설명:**
- 💡 **최소 사양**: 가장 저렴하고 가벼운 옵션
- ⚠️ **512MB RAM**: 리눅스 커널만 띄워도 빡빡하므로 **Swap 1GB 필수**
- ✅ **적합한 용도**:
  - 가벼운 정적 웹 서버
  - 코드 실습 및 학습
  - 간단한 스크립트 실행
  - 개인 블로그

**예상 워크로드:**
```bash
# 적당한 예시
- Nginx 정적 파일 서빙
- Python Flask 간단한 API
- 간단한 Shell 스크립트

# 무리한 예시
- Spring Boot 애플리케이션 ❌
- 데이터베이스 서버 ❌
- Docker 여러 개 ❌
```

---

### 🥈 Template 2: Standard (해커톤)

```yaml
이름: Ubuntu 22.04 Standard
vCPU: 1 Core
RAM: 1GB
Root Disk: 20GB
Swap Disk: 2GB
Flavor: m1.tiny
```

**특징 및 설명:**
- 💡 **기본 사양**: Spring Boot 하나 띄우거나 Node.js 백엔드 돌리기 적당
- 🎯 **vCPU는 1개면 충분**: 대부분 I/O 대기 시간이 많음
- ✅ **Swap 2GB로 안정성 확보**

**적합한 용도:**
- ✅ 해커톤 프로젝트
- ✅ Spring Boot 단일 애플리케이션
- ✅ Node.js + Express 백엔드
- ✅ 소규모 REST API 서버
- ✅ 경량 데이터베이스 (SQLite, H2)

**예상 워크로드:**
```bash
# 적당한 예시
- Spring Boot (단일 인스턴스)
- Node.js + Express
- Django/Flask (Python)
- PostgreSQL (소규모)

# 무리한 예시
- Spring Boot + MySQL + Redis 동시 ❌
- Elasticsearch ❌
- 대용량 이미지 처리 ❌
```

**해커톤 시나리오:**
```
팀원 4명이 협업
├─ 백엔드 개발자: Spring Boot API 개발
├─ 프론트엔드: 별도 서버에서 React 개발
└─ 1GB RAM으로 충분히 돌아감 ✅
```

---

### 🥇 Template 3: Pro (데모데이)

```yaml
이름: Ubuntu 22.04 Pro
vCPU: 2 Cores
RAM: 3GB
Root Disk: 40GB
Swap Disk: 4GB
Flavor: m1.small
```

**특징 및 설명:**
- 💡 **고성능**: 발표 중 렉이 걸리면 안 됨
- 🎯 **2 vCPU 할당**: 멀티태스킹을 위해 2 vCPU 할당
- ✅ **넉넉한 스왑으로 안정성 확보**
- 🎤 **데모데이는 안정성이 최우선!**

**적합한 용도:**
- ✅ 데모데이 발표
- ✅ Spring Boot + MySQL 동시 실행
- ✅ Node.js + Redis + PostgreSQL
- ✅ Docker Compose 멀티 컨테이너
- ✅ 실시간 데이터 처리

**예상 워크로드:**
```bash
# 적당한 예시
- Spring Boot + MySQL + Redis
- Node.js + PostgreSQL + Nginx
- Django + Celery + Redis
- Docker Compose (3~4개 컨테이너)

# 무리한 예시
- Kubernetes 클러스터 ❌
- 빅데이터 분석 (Spark 등) ❌
- 머신러닝 학습 ❌
```

**데모데이 시나리오:**
```
발표 준비
├─ MySQL 데이터베이스 실행 (1 vCPU)
├─ Spring Boot 백엔드 실행 (1 vCPU)
├─ Redis 캐시 실행 (메모리만 사용)
└─ Nginx 프록시 (메모리만 사용)
    ↓
2 vCPU로 안정적으로 멀티태스킹 가능 ✅
발표 중 렉 없음 ✅
```

---

## 💡 Template 선택 가이드

### 질문으로 찾는 내 Template

```
Q1: 어떤 용도인가요?
├─ 가벼운 실습/학습 → Template 1 (Nano)
├─ 해커톤 프로젝트 → Template 2 (Standard) ⭐ 추천
└─ 데모데이 발표 → Template 3 (Pro) ⭐ 안정성

Q2: 무엇을 돌릴 건가요?
├─ 정적 웹사이트 → Template 1
├─ Spring Boot 하나 → Template 2 ⭐ 추천
└─ Spring Boot + DB → Template 3 ⭐ 안정성

Q3: 얼마나 안정적이어야 하나요?
├─ 테스트/실습 → Template 1
├─ 개발 중 → Template 2 ⭐ 추천
└─ 발표/데모 → Template 3 ⭐ 렉 없음
```

---

## ⚠️ 주의사항

### Template 1 (Nano) 사용 시

```bash
# Swap이 필수인 이유
512MB RAM - 200MB (OS 기본) = 300MB 사용 가능
→ Spring Boot 실행 시 최소 500MB 필요
→ Swap 없이는 OOM(Out of Memory) 발생! ❌
→ Swap 1GB로 메모리 부족 해결 ✅
```

**권장하지 않는 용도:**
- ❌ Spring Boot 애플리케이션
- ❌ MySQL/PostgreSQL
- ❌ Docker 컨테이너

---

### Template 2 (Standard) 사용 시

```bash
# 1 vCPU가 충분한 이유
대부분의 웹 애플리케이션은:
- DB 쿼리 대기 (I/O)
- 네트워크 응답 대기 (I/O)
- 파일 읽기/쓰기 대기 (I/O)
→ CPU를 100% 사용하는 시간은 적음
→ 1 vCPU로 충분! ✅
```

**단, 다음은 피하세요:**
- ❌ CPU 집약적 작업 (이미지 처리, 동영상 인코딩)
- ❌ 여러 백엔드 동시 실행 (Spring Boot + Node.js + ...)

---

### Template 3 (Pro) 사용 시

```bash
# 2 vCPU의 장점
동시 처리 가능:
- vCPU 1: MySQL 쿼리 처리
- vCPU 2: Spring Boot API 요청 처리
→ 멀티태스킹으로 렉 없음! ✅
```

**데모데이 필수 체크리스트:**
- ✅ 발표 30분 전에 서버 재시작
- ✅ 불필요한 프로세스 종료
- ✅ 로그 레벨 WARNING 이상으로 설정 (디스크 I/O 감소)
- ✅ 데이터베이스 커넥션 풀 적정 설정

---

## 📈 실제 사용 예시

### Case 1: 개인 학습 (Nano)

```yaml
사용자: 컴퓨터공학과 2학년 학생
목적: 리눅스 명령어 실습
선택: Template 1 (Nano)

결과:
- Vim으로 코드 작성 ✅
- Python 스크립트 실행 ✅
- Git 사용 ✅
- 비용 절감 ✅
```

---

### Case 2: 해커톤 프로젝트 (Standard)

```yaml
사용자: 해커톤 참가 팀
목적: Spring Boot REST API 개발
선택: Template 2 (Standard)

실행 내용:
- Spring Boot 2.7 실행 (JVM Heap: 512MB)
- H2 인메모리 DB 사용
- Swagger UI 제공
- 동시 접속 10명 테스트

결과:
- API 응답 속도 양호 ✅
- 메모리 사용률 70% (안정적) ✅
- Swap 사용 최소화 ✅
```

---

### Case 3: 데모데이 발표 (Pro)

```yaml
사용자: 최종 프로젝트 팀
목적: 데모데이 실시간 발표
선택: Template 3 (Pro)

실행 내용:
- MySQL 8.0 (메모리: 1GB)
- Spring Boot (JVM Heap: 1GB)
- Redis (메모리: 512MB)
- Nginx 리버스 프록시

결과:
- 발표 중 렉 없음 ✅
- 실시간 데이터 처리 원활 ✅
- 교수님 질문 대응 시간 확보 ✅
- 좋은 성적 받음 ✅
```

---

## 🔧 OpenStack Flavor 설정

실제 OpenStack에서 Flavor를 생성할 때는 다음과 같이 설정하세요:

```bash
# Template 1: Nano
openstack flavor create \
  --id 1 \
  --ram 512 \
  --disk 10 \
  --vcpus 1 \
  --swap 1024 \
  m1.nano

# Template 2: Standard
openstack flavor create \
  --id 2 \
  --ram 1024 \
  --disk 20 \
  --vcpus 1 \
  --swap 2048 \
  m1.tiny

# Template 3: Pro
openstack flavor create \
  --id 3 \
  --ram 3072 \
  --disk 40 \
  --vcpus 2 \
  --swap 4096 \
  m1.small
```

---

**작성일:** 2025-11-18  
**버전:** 2.0  
**기준:** 개인 프로젝트 / 해커톤 / 데모데이