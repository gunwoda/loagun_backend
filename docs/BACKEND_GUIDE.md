# Loagun Backend 기술 문서

> 백엔드 수정 및 기능 추가 시 참고하는 내부 기술 문서

---

## 목차
1. [프로젝트 스펙](#1-프로젝트-스펙)
2. [패키지 구조](#2-패키지-구조)
3. [설정 파일 구조](#3-설정-파일-구조)
4. [핵심 컴포넌트](#4-핵심-컴포넌트)
5. [Redis 캐시 전략](#5-redis-캐시-전략)
6. [API 전체 목록](#6-api-전체-목록)
7. [에러 처리](#7-에러-처리)
8. [도메인별 구현 상세](#8-도메인별-구현-상세)
9. [새 기능 추가 가이드](#9-새-기능-추가-가이드)
10. [CI/CD 파이프라인](#10-cicd-파이프라인)
11. [로컬 개발 환경](#11-로컬-개발-환경)
12. [트러블슈팅 기록](#12-트러블슈팅-기록)

---

## 1. 프로젝트 스펙

| 항목 | 버전 / 값 |
|------|----------|
| Java | 21 (LTS) |
| Spring Boot | 3.4.3 |
| Build | Gradle 8.x (Groovy DSL) |
| DB | MariaDB 11.4 |
| Cache | Redis 7.4 |
| HTTP Client | WebFlux WebClient |
| API 문서 | springdoc-openapi 2.8.6 (Swagger UI) |
| 컨테이너 | Docker + Docker Compose |
| CI/CD | GitHub Actions |

---

## 2. 패키지 구조

```
src/main/java/com/loagun/backend/
│
├── LoagunBackendApplication.java       # @SpringBootApplication, @EnableCaching, @EnableScheduling
│
├── global/                             # 전역 공통 컴포넌트
│   ├── config/
│   │   ├── RedisConfig.java            # RedisTemplate, CacheManager, TTL 설정
│   │   ├── SwaggerConfig.java          # OpenAPI 문서 설정
│   │   ├── WebClientConfig.java        # WebClient 빌더 (버퍼 10MB, 타임아웃)
│   │   └── WebConfig.java             # CORS 설정
│   ├── common/
│   │   ├── response/
│   │   │   └── ApiResponse.java        # 공통 응답 래퍼 {success, message, data}
│   │   └── exception/
│   │       ├── ErrorCode.java          # 에러 코드 enum (HTTP 상태 + 메시지)
│   │       ├── CustomException.java    # RuntimeException 래퍼
│   │       └── GlobalExceptionHandler.java  # @RestControllerAdvice
│   └── client/
│       └── LostarkApiClient.java       # 로스트아크 Open API WebClient 래퍼
│
├── character/                          # 캐릭터 조회 도메인
│   ├── controller/CharacterController.java
│   ├── service/CharacterService.java
│   └── dto/
│       ├── CharacterArmoryResponse.java    # 통합 응답 (armory 전체)
│       ├── CharacterProfileResponse.java   # 프로필
│       ├── CharacterSiblingResponse.java   # 원정대 목록
│       ├── ArmoryEquipmentResponse.java    # 장비
│       ├── ArmorySkillResponse.java        # 전투 스킬
│       ├── ArmoryEngravingResponse.java    # 각인
│       ├── ArmoryCardResponse.java         # 카드
│       ├── ArmoryGemResponse.java          # 보석
│       ├── ArmoryAvatarResponse.java       # 아바타
│       ├── ArkPassiveResponse.java         # 아크패시브
│       └── CollectibleResponse.java        # 수집형 포인트
│
├── auction/                            # 경매장 도메인
│   ├── controller/AuctionController.java
│   ├── service/AuctionService.java
│   └── dto/
│       ├── AuctionSearchRequest.java   # POST 검색 요청 바디
│       ├── AuctionResponse.java        # 검색 결과 (페이지네이션 + 아이템 목록)
│       └── AuctionOptionResponse.java  # 검색 옵션 메타데이터
│
├── notice/                             # 공지/이벤트 도메인
│   ├── controller/NoticeController.java
│   ├── service/NoticeService.java
│   └── dto/
│       ├── NoticeResponse.java         # 공지사항
│       └── EventResponse.java          # 이벤트
│
└── content/                            # 컨텐츠 정보 도메인
    ├── controller/ContentController.java
    ├── service/ContentService.java
    └── dto/
        └── ContentsCalendarResponse.java  # 이번 주 컨텐츠 캘린더
```

---

## 3. 설정 파일 구조

```
src/main/resources/
├── application.yml          # 공통 설정 (포트 8080, JPA, Swagger, Actuator)
├── application-dev.yml      # 로컬 개발 (DB: loagun_dev, Redis: localhost, SQL 로그 ON)
├── application-prod.yml     # 운영 (모든 값 환경변수로 주입)
└── application-local.yml    # ⚠️ gitignore됨 - API 키 등 민감 정보 (로컬 전용)
```

### 프로파일 실행 방법
```bash
# 로컬 개발 (dev + local 함께 사용 필수 — local에 API 키 있음)
./gradlew bootRun --args='--spring.profiles.active=dev,local'

# 운영 (환경변수로 주입)
SPRING_PROFILES_ACTIVE=prod java -jar app.jar
```

### 운영 환경변수 목록 (application-prod.yml 참고)
| 환경변수 | 설명 |
|---------|------|
| `DB_HOST` | MariaDB 호스트 |
| `DB_PORT` | MariaDB 포트 (기본 3306) |
| `DB_NAME` | DB 이름 |
| `DB_USERNAME` | DB 사용자 |
| `DB_PASSWORD` | DB 비밀번호 |
| `REDIS_HOST` | Redis 호스트 |
| `REDIS_PORT` | Redis 포트 (기본 6379) |
| `REDIS_PASSWORD` | Redis 비밀번호 |
| `LOSTARK_API_KEY` | 로스트아크 Open API JWT 키 |

---

## 4. 핵심 컴포넌트

### 4-1. LostarkApiClient

로스트아크 Open API 호출의 단일 진입점. `WebClient` 기반.

```java
// 경로 변수 방식 (path parameter)
lostarkApiClient.get(
    "/armories/characters/{name}/profiles",
    CharacterProfileResponse.class,
    Map.of("name", characterName)
);

// URI 빌더 방식 (쿼리 파라미터, 선택적 파라미터)
lostarkApiClient.get(
    uriBuilder -> uriBuilder
        .path("/news/notices")
        .queryParam("type", type)   // 선택적으로 추가 가능
        .build(),
    NoticeResponse[].class
);

// POST 방식
lostarkApiClient.post(
    "/auctions/items",
    requestBody,
    AuctionResponse.class,
    Map.of()  // path variable 없으면 빈 Map
);
```

**HTTP 상태코드별 에러 처리:**
| 상태코드 | 던지는 예외 |
|---------|-----------|
| 404 | `CustomException(ErrorCode.CHARACTER_NOT_FOUND)` |
| 429 | `CustomException(ErrorCode.RATE_LIMIT_EXCEEDED)` |
| 4xx (기타) | `CustomException(ErrorCode.EXTERNAL_API_ERROR)` |
| 5xx | `CustomException(ErrorCode.EXTERNAL_API_ERROR)` |

**WebClient 설정값 (WebClientConfig.java):**
- 연결 타임아웃: 5초
- 읽기 타임아웃: 10초
- 응답 버퍼 최대: **10MB** (armory 통합 응답이 1MB 이상)

### 4-2. ApiResponse

모든 API의 공통 응답 포맷.

```json
{
  "success": true,
  "message": "OK",
  "data": { ... }
}
```

```java
ApiResponse.ok(data)              // 200 성공
ApiResponse.ok("메시지", data)     // 200 커스텀 메시지
ApiResponse.ok()                  // 200 데이터 없음
ApiResponse.fail("에러 메시지")    // 실패
```

### 4-3. ErrorCode

새 에러 코드 추가 시 `ErrorCode.java`에 추가:

```java
NEW_ERROR(HttpStatus.BAD_REQUEST, "에러 메시지");
```

현재 등록된 에러 코드:
| 코드 | HTTP 상태 | 메시지 |
|------|----------|--------|
| `INVALID_INPUT` | 400 | 잘못된 요청입니다. |
| `CHARACTER_NOT_FOUND` | 404 | 캐릭터를 찾을 수 없습니다. |
| `RATE_LIMIT_EXCEEDED` | 429 | 로스트아크 API 요청 한도를 초과했습니다. |
| `EXTERNAL_API_ERROR` | 502 | 외부 API 호출에 실패했습니다. |
| `AUCTION_SEARCH_FAILED` | 502 | 경매장 조회에 실패했습니다. |
| `NOTICE_NOT_FOUND` | 404 | 공지사항을 찾을 수 없습니다. |
| `CONTENT_NOT_FOUND` | 404 | 컨텐츠 정보를 찾을 수 없습니다. |
| `INTERNAL_SERVER_ERROR` | 500 | 서버 내부 오류가 발생했습니다. |

---

## 5. Redis 캐시 전략

### 캐시 이름별 TTL (RedisConfig.java)

| 캐시 이름 | TTL | 적용 도메인 | 이유 |
|----------|-----|-----------|------|
| `character` | **5분** | 캐릭터 프로필/armory/siblings | 스펙 변경은 잦지 않지만 실시간성 필요 |
| `auction` | **1분** | 경매장 아이템 검색 | 가격이 실시간으로 변동 |
| `notices` | **10분** | 공지사항, 이벤트 | 실시간성 불필요 |
| `contents` | **1시간** | 컨텐츠 캘린더, 경매 옵션 | 주간 리셋 기반, 패치 전 불변 |

### 캐시 키 패턴

| 키 | 설명 |
|----|------|
| `character::armory:{characterName}` | 캐릭터 전체 정보 |
| `character::profile:{characterName}` | 캐릭터 프로필 |
| `character::siblings:{characterName}` | 원정대 목록 |
| `notices::notices:{type}:{searchText}` | 공지사항 (null이면 null 문자열) |
| `notices::events` | 이벤트 목록 |
| `contents::calendar` | 컨텐츠 캘린더 |
| `contents::auction-options` | 경매 검색 옵션 |
| `auction::items:{categoryCode}:{itemName}:{itemGrade}:{itemTier}:{sort}:{sortCondition}:{pageNo}` | 경매장 검색 |

### 캐시 무효화

```bash
# 특정 캐릭터 캐시 삭제
DELETE /api/v1/characters/{characterName}/cache
```

Redis CLI로 직접 삭제:
```bash
redis-cli DEL "character::armory:캐릭터명"
redis-cli DEL "contents::calendar"        # 주간 리셋 후 수동 삭제
```

---

## 6. API 전체 목록

### 캐릭터 (`/api/v1/characters`)

| 메서드 | 경로 | 설명 | 캐시 |
|-------|------|------|------|
| GET | `/{name}/armory` | 전체 정보 통합 조회 (장비/스킬/각인/카드/보석/수집형/아크패시브) | 5분 |
| GET | `/{name}/profile` | 기본 프로필만 조회 (경량) | 5분 |
| GET | `/{name}/siblings` | 원정대 캐릭터 목록 | 5분 |
| DELETE | `/{name}/cache` | 캐시 강제 삭제 | - |

### 공지/이벤트 (`/api/v1/news`)

| 메서드 | 경로 | 설명 | 캐시 |
|-------|------|------|------|
| GET | `/notices` | 공지사항 목록 (`?type=공지\|점검\|상점\|이벤트`, `?searchText=검색어`) | 10분 |
| GET | `/events` | 진행 중 이벤트 목록 | 10분 |

### 컨텐츠 (`/api/v1/contents`)

| 메서드 | 경로 | 설명 | 캐시 |
|-------|------|------|------|
| GET | `/calendar` | 이번 주 컨텐츠 캘린더 (스케줄 + 보상) | 1시간 |

### 경매장 (`/api/v1/auctions`)

| 메서드 | 경로 | 설명 | 캐시 |
|-------|------|------|------|
| GET | `/options` | 검색 필터 메타데이터 (카테고리 코드 등) | 1시간 |
| POST | `/items` | 아이템 검색 (필터/정렬/페이지네이션) | 1분 |

### Swagger UI
- 로컬: `http://localhost:8080/swagger-ui.html`
- API Docs JSON: `http://localhost:8080/api-docs`

---

## 7. 에러 처리

### 응답 형식

```json
// 성공
{ "success": true, "message": "OK", "data": { ... } }

// 실패
{ "success": false, "message": "캐릭터를 찾을 수 없습니다." }
```

### GlobalExceptionHandler 처리 흐름

```
요청
 │
 ├─ CustomException → ErrorCode.status + ErrorCode.message 반환
 ├─ MethodArgumentNotValidException → 400 + 유효성 검증 실패 필드 메시지
 └─ Exception (기타) → 500 + "서버 내부 오류가 발생했습니다." (로그에 스택트레이스)
```

---

## 8. 도메인별 구현 상세

### 8-1. 캐릭터 (character)

**armory 통합 조회 설계:**
- 로스트아크 API의 `filters` 쿼리 파라미터로 단일 호출에 모든 섹션 수신
- `filters=profiles+equipment+avatars+combat-skills+engravings+cards+gems+collectibles+arkpassive`
- 개별 엔드포인트 8번 → 1번으로 줄여 Rate Limit 87.5% 절감

**Tooltip 필드:**
- 장비, 스킬, 각인 등의 `Tooltip`은 로스트아크 API가 복잡한 JSON 문자열로 반환
- 현재 `String` 타입으로 그대로 전달 → 프론트에서 파싱

### 8-2. 경매장 (auction)

**캐시 키 구성:**
```
auction::items:{categoryCode}:{itemName}:{itemGrade}:{itemTier}:{sort}:{sortCondition}:{pageNo}
```
- `SkillOptions`, `EtcOptions`는 키에 미포함 (복잡도 대비 효용 낮음)
- 정교한 옵션 검색은 캐시 미적중 → 직접 API 호출

**페이지네이션:**
- `PageNo`는 1부터 시작 (`@Min(1)` 검증)
- 응답에 `TotalCount`, `PageSize` 포함

### 8-3. 공지/이벤트 (notice)

**캐시 키 주의사항:**
- `type=null`, `searchText=null`이면 캐시 키가 `notices:null:null`이 됨
- 전체 조회와 필터 조회가 다른 캐시에 저장됨 → 정상 동작

---

## 9. 새 기능 추가 가이드

### 새 도메인 추가 시 체크리스트

1. **브랜치 생성**
   ```bash
   git checkout main && git pull origin main
   git checkout -b feature/{기능명}
   ```

2. **디렉토리 구조 생성**
   ```
   src/main/java/com/loagun/backend/{도메인}/
   ├── controller/
   ├── service/
   └── dto/
   ```

3. **구현 순서**
   - DTO (Lostark API 응답 필드명 맞춰 `@JsonProperty` 사용)
   - Service (`@Cacheable` 적용, 캐시 TTL은 데이터 특성에 맞게)
   - Controller (`@Tag`, `@Operation` Swagger 문서화)

4. **에러 코드 추가** (`ErrorCode.java`)

5. **Redis 캐시 이름 추가** (`RedisConfig.java`의 `cacheConfigs`)
   - 새 캐시 이름은 반드시 `cacheConfigs`에 TTL 명시

6. **README.md 업데이트** (구현 현황 테이블, API 명세 섹션)

7. **PR 생성 → main merge**

### LostarkApiClient 사용 패턴 선택 기준

| 상황 | 사용할 메서드 |
|------|-------------|
| 경로 변수만 있는 경우 | `get(pathTemplate, responseType, Map.of("key", value))` |
| 쿼리 파라미터가 있는 경우 | `get(uriBuilder -> ..., responseType)` |
| POST 요청 | `post(pathTemplate, body, responseType, Map.of())` |

### 새 캐시 추가 시 RedisConfig 수정

```java
// RedisConfig.java - cacheConfigs에 추가
cacheConfigs.put("새캐시이름", defaultConfig.entryTtl(Duration.ofMinutes(N)));
```

---

## 10. CI/CD 파이프라인

### 워크플로우 파일
`.github/workflows/cicd.yml`

### 실행 조건 및 Job 흐름

```
push / PR
    │
    ▼
[build]  gradle bootJar → JAR artifact 업로드
    │
    ▼
[test]   gradle test → 실패 시 리포트 artifact 업로드
    │
    ▼ (main/develop push 시에만)
[docker] JAR 다운로드 → docker build → Docker Hub push
```

| 브랜치 | build | test | docker push |
|--------|-------|------|-------------|
| `feature/**`, `fix/**` | ✅ | ✅ | ❌ |
| `develop` | ✅ | ✅ | ✅ `:develop` 태그 |
| `main` | ✅ | ✅ | ✅ `:latest` + `:<sha>` 태그 |

### 필요한 GitHub Secrets

| Secret | 설명 |
|--------|------|
| `DOCKER_USERNAME` | Docker Hub 계정명 |
| `DOCKER_TOKEN` | Docker Hub Access Token |
| `LOSTARK_API_KEY` | 로스트아크 Open API 키 (prod 이미지 빌드 시 불필요, 런타임 주입) |

---

## 11. 로컬 개발 환경

### 인프라 실행 (Docker)
```bash
# MariaDB + Redis만 띄우기
docker compose -f docker/docker-compose.dev.yml up -d

# 확인
docker ps
```

### 개발 서버 실행
```bash
./gradlew bootRun --args='--spring.profiles.active=dev,local'
```

### application-local.yml 예시 (gitignore, 직접 생성)
```yaml
lostark:
  api:
    key: eyJ0eXAiOiJKV1Qi...  # https://developer-lostark.game.onstove.com 에서 발급
```

### Dev DB 접속 정보 (application-dev.yml)
```
URL: jdbc:mariadb://localhost:3306/loagun_dev
User: loagun
Password: loagun1234
```

### Swagger 접속
`http://localhost:8080/swagger-ui.html`

---

## 12. 트러블슈팅 기록

### 1. gradle-wrapper.jar 누락으로 CI 빌드 실패
- **증상**: `Error: Unable to access jarfile gradle-wrapper.jar`
- **원인**: `.gitignore`에서 `!gradle/wrapper/gradle-wrapper.jar` 예외 규칙이 `*.jar` 규칙보다 앞에 위치 → 덮어씌워짐
- **해결**: `.gitignore`에서 예외 규칙을 `*.jar` 뒤로 이동

```
# 잘못된 순서
!gradle/wrapper/gradle-wrapper.jar  ← 먼저 선언
*.jar                               ← 뒤에서 다시 무시

# 올바른 순서
*.jar
!gradle/wrapper/gradle-wrapper.jar  ← *.jar 뒤에 위치해야 함
```

### 2. URL 더블 인코딩 (캐릭터명 한글)
- **증상**: `%EA%B9%9C` → `%25EA%25B9%259C`로 이중 인코딩
- **원인**: 서비스에서 `URLEncoder.encode()`로 인코딩 후 WebClient에 String으로 전달 → WebClient가 `%`를 다시 `%25`로 인코딩
- **해결**: `URLEncoder.encode()` 제거, WebClient URI 템플릿 변수(`Map.of("name", characterName)`)로 전달 → WebClient가 인코딩 담당

### 3. DataBufferLimitException (armory 응답 크기 초과)
- **증상**: `Exceeded limit on max bytes to buffer: 262144`
- **원인**: WebClient 기본 버퍼 256KB, armory 통합 응답 1MB 이상
- **해결**: `WebClientConfig`에서 `maxInMemorySize(10 * 1024 * 1024)` (10MB)로 증가

### 4. dev 프로파일이 API 키를 덮어쓰는 문제
- **증상**: `application.yml`에 API 키를 넣어도 401 에러
- **원인**: `application-dev.yml`에 `key: ${LOSTARK_API_KEY:your-dev-api-key}` 설정이 있어 dev 프로파일이 기본값으로 덮어씀
- **해결**: `application-dev.yml`에서 `lostark.api.key` 설정 제거, API 키는 gitignore된 `application-local.yml`에서 관리
