# Loagun Backend

> 로스트아크 게임 정보를 빠르고 안정적으로 제공하는 RESTful API 서비스

[![CI/CD](https://github.com/gunwoda/loagun_backend/actions/workflows/cicd.yml/badge.svg)](https://github.com/gunwoda/loagun_backend/actions/workflows/cicd.yml)

---

## 목차
- [프로젝트 소개](#프로젝트-소개)
- [기술 스택 & 선택 이유](#기술-스택--선택-이유)
- [아키텍처](#아키텍처)
- [CI/CD 파이프라인](#cicd-파이프라인)
- [API 명세](#api-명세)
- [로컬 개발 환경 설정](#로컬-개발-환경-설정)

---

## 프로젝트 소개

로스트아크 공식 Open API를 활용해 캐릭터 정보, 경매장, 공지사항, 컨텐츠 정보를 제공하는 백엔드 서비스입니다.

외부 API 의존성이 높은 서비스 특성상 **Rate Limit 대응**과 **응답 속도 최적화**를 핵심 설계 기준으로 삼았습니다.

### 구현 기능
| 기능 | 설명 | 상태 |
|------|------|------|
| 캐릭터 조회 | 캐릭터 프로필, 원정대 목록 조회 | ✅ |
| 공지/이벤트 | 게임 공지사항 및 이벤트 정보 | ✅ |
| 컨텐츠 정보 | 이번 주 컨텐츠 캘린더 (스케줄 + 보상) | ✅ |
| 경매장 조회 | 아이템 경매장 정보 조회 | 🔜 |

---

## 기술 스택 & 선택 이유

### Java 21 + Spring Boot 3.4

**왜 Java 21인가?**

로스트아크 API 호출은 기본적으로 I/O 블로킹 작업입니다. 기존 Thread-per-request 모델에서는 API 호출 대기 동안 스레드가 점유되어 동시 요청이 많아질수록 스레드 풀이 고갈됩니다.

Java 21의 **Virtual Threads(Project Loom)** 는 JVM이 경량 스레드를 OS 스레드와 분리해 관리하므로, 블로킹 I/O 중 OS 스레드를 반납하고 다른 작업을 처리할 수 있습니다. 별도의 리액티브 프로그래밍 전환 없이도 높은 동시성을 확보할 수 있어 선택했습니다.

### Spring MVC (WebFlux 아님)

**왜 WebFlux를 쓰지 않았는가?**

외부 API 의존성은 WebFlux의 논블로킹이 유리하지만, 학습 곡선과 디버깅 복잡도가 높습니다. 대신 **Spring MVC + Virtual Threads** 조합으로 동일한 동시성 효과를 얻으면서 익숙한 동기 코드 스타일을 유지했습니다. 외부 API 호출에는 WebClient를 사용해 연결 타임아웃, 읽기 타임아웃을 명시적으로 관리합니다.

### Redis (캐시 레이어)

**왜 Redis를 캐시로 도입했는가?**

로스트아크 Open API는 분당 요청 수 제한(Rate Limit)이 있습니다. 동일한 캐릭터 정보는 수 분 내에 변경되지 않으므로, 첫 요청 결과를 Redis에 저장하고 이후 요청은 캐시에서 응답합니다.

데이터 특성에 따라 TTL을 다르게 설정했습니다:

| 캐시 키 | TTL | 이유 |
|---------|-----|------|
| `character` | 5분 | 스펙/장비는 자주 바뀌지 않음 |
| `auction` | 1분 | 실시간 가격 변동이 잦음 |
| `notices` | 10분 | 공지는 실시간성 불필요 |
| `contents` | 1시간 | 컨텐츠 정보는 패치 전까지 고정 |

**왜 로컬 캐시(Caffeine 등)가 아닌가?**

인스턴스가 여러 개 뜰 경우 로컬 캐시는 인스턴스마다 API를 따로 호출합니다. Redis는 외부 공유 캐시이므로 스케일 아웃 시에도 Rate Limit를 안전하게 지킬 수 있습니다.

### MariaDB (JPA)

**왜 MariaDB인가?**

MySQL과 완전히 호환되면서 오픈소스로 운영 비용을 절감할 수 있습니다. 추후 유저 즐겨찾기, 조회 기록 등 영속 데이터 저장에 활용할 예정이며, JPA를 통해 도메인 모델을 객체 중심으로 관리합니다.

### Docker + GitHub Actions (GitOps)

**왜 GitOps 전략인가?**

배포 프로세스를 코드로 관리하면 배포 과정이 투명해지고 실수가 줄어듭니다. `feature` 브랜치는 빌드·테스트만, `main` 머지 시에만 Docker 이미지를 빌드해 Docker Hub에 푸쉬하도록 단계를 분리했습니다.

```
feature/**  →  build → test
develop     →  build → test → docker push (:develop)
main        →  build → test → docker push (:latest)
```

**왜 Docker 멀티스테이지를 CI에서 단일 스테이지로 변경했는가?**

CI에서 이미 `gradle bootJar`로 JAR를 빌드하므로, Docker 이미지 내에서 다시 빌드하는 것은 리소스 낭비입니다. CI가 빌드한 JAR를 artifact로 전달해 런타임 이미지에만 복사하는 방식으로 **빌드 시간을 단축**하고 **이미지 크기를 최소화**했습니다.

---

## 아키텍처

```
Client
  │
  ▼
[Spring MVC Controller]
  │
  ▼
[Service]
  │          ┌──────────────┐
  ├─ Cache Hit─▶   Redis     │
  │          └──────────────┘
  │
  └─ Cache Miss
        │
        ▼
  [LostarkApiClient]  ──▶  Lostark Open API
        │
        ▼
  Redis에 결과 저장 (TTL)
        │
        ▼
  응답 반환
```

### 패키지 구조
```
com.loagun.backend
├── global
│   ├── config          # Redis, WebClient, Swagger, CORS 설정
│   ├── common
│   │   ├── response    # ApiResponse (공통 응답 포맷)
│   │   └── exception   # ErrorCode, CustomException, GlobalExceptionHandler
│   └── client          # LostarkApiClient (WebClient 기반)
├── character           # 캐릭터 조회 도메인
├── auction             # 경매장 조회 도메인
├── notice              # 공지/이벤트 도메인
└── content             # 컨텐츠 정보 도메인
```

---

## CI/CD 파이프라인

```
Push / PR
    │
    ▼
┌─────────┐     ┌─────────┐     ┌──────────────────────┐
│  Build  │────▶│  Test   │────▶│  Docker Build & Push │
│bootJar  │     │ gradle  │     │  (main/develop만)     │
│JAR 저장 │     │  test   │     │                      │
└─────────┘     └─────────┘     └──────────────────────┘
```

| 브랜치 | Build | Test | Docker Push |
|--------|-------|------|-------------|
| feature/**, fix/** | ✅ | ✅ | ❌ |
| develop | ✅ | ✅ | ✅ `:develop` |
| main | ✅ | ✅ | ✅ `:latest` |

### 필요한 GitHub Secrets
| Secret | 설명 |
|--------|------|
| `DOCKER_USERNAME` | Docker Hub 계정명 |
| `DOCKER_TOKEN` | Docker Hub Access Token |
| `LOSTARK_API_KEY` | 로스트아크 Open API 키 |

---

## API 명세

Swagger UI: `http://localhost:8080/swagger-ui.html`

### 캐릭터 조회

#### GET `/api/v1/characters/{characterName}/armory` ★ 통합 조회
캐릭터의 모든 정보를 **단일 API 호출**로 조회합니다.

**왜 통합 엔드포인트인가?**
로스트아크 API는 분당 100회 Rate Limit이 있습니다. 장비·스킬·각인·카드·보석 등을 개별 호출하면 한 캐릭터 조회에 8번의 API 호출이 필요합니다. 공식 API의 `filters` 파라미터를 활용해 1번 호출로 모든 데이터를 수신하여 Rate Limit 소모를 87.5% 절감합니다.

**포함 데이터**
| 섹션 | 설명 |
|------|------|
| `profile` | 기본 프로필, 스탯, 원정대레벨 |
| `equipment` | 장비 6슬롯 + 장신구 (품질, 강화 수치는 Tooltip에 포함) |
| `avatars` | 착용 아바타 목록 |
| `skills` | 전투 스킬 (레벨, 트라이포드, 룬) |
| `engravings` | 각인 슬롯 + 활성 각인 효과 + 아크패시브 각인 |
| `cards` | 카드 슬롯 + 세트 효과 |
| `gems` | 보석 슬롯 + 스킬별 보석 효과 |
| `collectibles` | 수집형 포인트 (섬의 마음, 모코코 씨앗 등) |
| `arkPassive` | 아크패시브 포인트 및 활성 효과 |

**Response**
```json
{
  "success": true,
  "message": "OK",
  "data": {
    "profile": { "characterName": "깜지직", "itemAvgLevel": "1,680.00", ... },
    "equipment": [{ "type": "무기", "name": "...", "grade": "에스더", "tooltip": "..." }],
    "skills": [{ "name": "스킬명", "level": 10, "tripods": [...], "rune": {...} }],
    "engravings": { "engravings": [...], "effects": [...] },
    "cards": { "cards": [...], "effects": [...] },
    "gems": { "gems": [...], "effects": {...} },
    "collectibles": [{ "type": "섬의 마음", "point": 95, "maxPoint": 95 }],
    "arkPassive": { "isArkPassive": true, "points": [...], "effects": [...] }
  }
}
```

#### GET `/api/v1/characters/{characterName}/profile`
기본 프로필만 필요할 때 사용하는 경량 엔드포인트

#### GET `/api/v1/characters/{characterName}/siblings`
계정 내 원정대 캐릭터 전체 목록 조회

#### DELETE `/api/v1/characters/{characterName}/cache`
캐시 강제 삭제 (armory + profile + siblings 일괄 삭제)

---

### 공지/이벤트 조회

#### GET `/api/v1/news/notices`
공지사항 목록 조회. `type`, `searchText` 쿼리 파라미터로 필터링 가능. 10분 캐싱.

| 파라미터 | 설명 | 예시 |
|---------|------|------|
| `type` | 공지 유형 (선택) | `공지` \| `점검` \| `상점` \| `이벤트` |
| `searchText` | 제목 검색 (선택) | `업데이트` |

#### GET `/api/v1/news/events`
현재 진행 중인 이벤트 목록 조회. 10분 캐싱.

---

### 컨텐츠 정보

#### GET `/api/v1/contents/calendar`
이번 주 운영 컨텐츠 캘린더 조회. 1시간 캐싱.

**왜 TTL을 1시간으로 설정했는가?**
컨텐츠 캘린더는 주간 리셋(매주 수요일) 기반으로 편성되며, 하루 중 스케줄이 바뀌지 않습니다. 캐릭터(5분), 공지(10분)보다 긴 1시간 TTL을 적용해 API 호출을 최소화합니다.

**포함 정보:** 카오스게이트, 필드보스, 모험 섬, 군단장 레이드, 최소 아이템레벨, 시작 시간, 보상 아이템

---

## 로컬 개발 환경 설정

### 사전 요구사항
- Java 21
- Docker & Docker Compose

### 1. 인프라 실행 (MariaDB + Redis)
```bash
docker compose -f docker/docker-compose.dev.yml up -d
```

### 2. 환경변수 설정
`src/main/resources/application-dev.yml`에서 로스트아크 API 키 설정:
```yaml
lostark:
  api:
    key: your-api-key  # https://developer-lostark.game.onstove.com
```

### 3. 애플리케이션 실행
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### 4. API 문서 확인
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs
