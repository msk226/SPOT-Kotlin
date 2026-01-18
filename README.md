# SPOT

- 스터디 매칭 및 관리 플랫폼, SPOT의 백엔드 서비스입니다.
- 본 프로젝트는 `Kotlin`으로 포팅된 버전이며, 이벤트 기반 아키텍처를 채택하고 있습니다.
- Version 1과 2는 `Java`로 작성된 모놀리식 및 모듈러 모놀리식 아키텍처를 각각 구현한 버전입니다.
  - 현재 Version 2를 기준으로 애플리케이션 리뉴얼 진행 중 입니다.  
- 각 버전의 아키텍처 변화와 설계 결정을 아래에 상세히 설명합니다.


## Tech Stack

| Category | Stack |
|----------|-------|
| Language | Kotlin 2.0, Java 21 |
| Framework | Spring Boot 3.4 |
| Database | MySQL 8.0, Redis 7 |
| ORM | Spring Data JPA, QueryDSL |
| Messaging | Apache Kafka |
| Monitoring | Prometheus, Grafana |
| Build | Gradle (Kotlin DSL) |
| Code Quality | ktlint, detekt |

---

### v1. Monolithic - Java

레포지토리 링크 : https://github.com/SPOTeam/Server
```
┌─────────────────────────────────────┐
│              SPOT API               │
│  ┌───────┬───────┬───────┬───────┐  │
│  │Member │ Study │ Post  │ ...   │  │
│  └───────┴───────┴───────┴───────┘  │
│         (강한 결합, 단일 DB)           │
└─────────────────────────────────────┘
```

**문제점**
- 모듈 간 강한 결합으로 변경 시 영향 범위 예측 어려움
- 단일 장애점(Single Point of Failure)
- 특정 도메인만 스케일 아웃 불가

---

### v2. Modular Monolith (1차 개선) - Java

> 기존 모놀리식의 한계를 극복하기 위해 모듈러 모놀리식으로 전환

레포지토리 링크: https://github.com/SPOTeam/Server-V2
```
┌─────────────────────────────────────────────────────┐
│                     SPOT API                        │
│  ┌─────────────────────────────────────────────┐    │
│  │                  common                     │    │
│  └─────────────────────────────────────────────┘    │
│        ▲              ▲              ▲              │
│  ┌──────────┐   ┌──────────┐   ┌──────────┐         │
│  │  member  │   │  study   │   │   post   │         │
│  │  module  │   │  module  │   │  module  │         │
│  └──────────┘   └──────────┘   └──────────┘         │
│                 (모듈 분리, 경계 명확화)                 │
└─────────────────────────────────────────────────────┘
```

**배경**
- 수도권 대학생 약 1% 타겟 (DAU 10,000+ 예상) 및 빠른 기능 확장 필요
- 기존 모놀리식 구조에서 도메인 간 강한 결합으로 리팩토링 시 사이드 이펙트 빈발
- 트래픽이 몰리는 특정 서비스만 확장 불가 → 유연성 부족

**의사결정**

| 아키텍처 | 판단 |
|---------|------|
| Monolithic | 도메인 간 결합도가 높아 유지보수 비용 증가 |
| MSA | 오버 엔지니어링. 배포 복잡도, 네트워크 오버헤드, 인프라 비용 증가. 서버 개발자 1명으로 관리 어려움 |
| **Modular Monolith** | ✅ 채택. 운영 단순성(단일 JAR) 유지하면서 도메인 격리. MSA 필요 시 점진적 전환 가능 |

**설계 원칙**
- 도메인 간 직접 의존 금지 → API(Port Interface)를 통한 의존성만 허용
- 의존성 방향 단방향 유지 → 구현체가 아닌 인터페이스에 의존
- 도메인 간 데이터 직접 접근 차단 → 타 도메인 엔티티 직접 참조 및 JOIN 금지

**기대 효과**
- 도메인 변경 시 영향 범위 최소화
- 기능 확장 속도 향상 및 리팩터링 안정성 확보
- 운영 단순성을 유지하면서 점진적 MSA 전환 가능


---

### v3. Event-Driven Architecture (2차 개선, 진행 중) - Kotlin

> Kotlin으로 포팅하며 이벤트 기반 아키텍처 도입. 완전한 MSA가 아닌 배포 유연성과 운영 단순성의 균형점

```
    ┌──────────────────────────────────────────────────────┐
    │                    Core Service                      │
    │  ┌────────┐ ┌────────┐ ┌────────┐ ┌──────────────┐   │
    │  │ member │ │  post  │ │ point  │ │ notification │   │
    │  └────────┘ └────────┘ └────────┘ └──────────────┘   │
    │           (내부: Spring Event 기반 통신)                │
    └──────────────────────────────────────────────────────┘
                               │
                               │ Kafka (배포 단위 간 통신)
                               ▼
    ┌──────────────────────────────────────────────────────┐
    │                   Study Service                      │
    │  ┌────────┐ ┌──────────┐ ┌────────┐ ┌──────┐         │
    │  │  core  │ │ schedule │ │ review │ │ todo │         │
    │  └────────┘ └──────────┘ └────────┘ └──────┘         │
    │           (내부: Spring Event 기반 통신)                │
    └──────────────────────────────────────────────────────┘
                               │
                               │ Kafka
                               ▼
    ┌──────────────────────────────────────────────────────┐
    │                   Worker Service                     │
    │             (배치 처리, 알림 발송 등)                     │
    └──────────────────────────────────────────────────────┘
```

**배경**
- v2에서 모듈 격리는 달성했으나, 여전히 단일 배포 단위로 인한 한계 존재
- 특정 도메인만 스케일 아웃하거나 독립 배포 필요성 증가
- 완전한 MSA는 인프라 복잡도 대비 이점이 적다고 판단

**의사결정**

| 아키텍처                        | 판단 |
|-----------------------------|------|
| 전체 MSA                      | 서비스마다 독립 배포 시 인프라 복잡도 급증. API Gateway, 서비스 디스커버리 등 추가 부담 |
| **Bounded Deployment Unit** | ✅ 채택. 성격이 비슷한 모듈끼리 배포 단위로 그룹화. 운영 단순성 유지 |

**통신 전략**
- **배포 단위 내부**: Spring ApplicationEvent (동기/비동기 선택 가능, 트랜잭션 공유)
- **배포 단위 간**: Kafka 이벤트 (느슨한 결합, 장애 격리)

**기대 효과**
- 도메인별 독립적인 확장 (Scale Out) 가능
- 장애 격리 (Core 장애 시 Study는 정상 운영)
- 배포 단위 수를 최소화하여 운영 복잡도 관리

**주요 변경점**

| 항목 | v2 (Modular Monolith) | v3 (Event-Driven) |
|------|-----------------------|-------------------|
| 배포 단위 | 단일 애플리케이션             | 서비스 그룹별 독립 배포 |
| 내부 통신 | 직접 메서드 호출             | Spring Event |
| 외부 통신 | Port-Adapter 구조       | Kafka 이벤트 |
| 데이터 정합성 | 트랜잭션                  | 내부: 트랜잭션 / 외부: 최종 일관성 |
| 장애 영향 | 전체 서비스                | 해당 배포 단위만 |

---

## Modules

| Module | Port | Description |
|--------|------|-------------|
| `common` | - | 공통 라이브러리 (이벤트, 예외, ID 생성) |
| `core:boot` | 8081 | 회원 관리, 인증, 게시글, 포인트, 출석 |
| `study:boot` | 8082 | 스터디 관리, 일정, 리뷰, 할일 |
| `worker:boot` | 8083 | 백그라운드 작업 처리 (배치, 알림) |

## Quick Start

### Prerequisites

- Docker & Docker Compose
- JDK 21

### Run with Docker

```bash
# 전체 스택 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f
```

### Run Locally

```bash
# 인프라만 실행 (MySQL, Redis, Kafka)
docker-compose up -d mysql redis kafka

# 애플리케이션 실행
./gradlew :core:boot:bootRun
./gradlew :study:boot:bootRun
./gradlew :worker:boot:bootRun
```

## Endpoints

| Service | URL |
|---------|-----|
| Core API | http://localhost:8081 |
| Study API | http://localhost:8082 |
| Swagger (Core) | http://localhost:8081/swagger-ui.html |
| Swagger (Study) | http://localhost:8082/swagger-ui.html |
| Grafana | http://localhost:3000 |
| Prometheus | http://localhost:9090 |

## Project Structure

```
spot-kotlin/
├── common/                 # 공통 모듈
│   └── src/main/kotlin/kr/spot/common/
│       ├── api/            # API 응답, 예외 처리
│       ├── event/          # 이벤트 인프라
│       └── id/             # ID 생성 (Snowflake)
│
├── core/                   # Core 서비스
│   ├── boot/               # 애플리케이션 진입점
│   ├── member/             # 회원 도메인
│   ├── post/               # 게시글 도메인
│   ├── point/              # 포인트 도메인
│   ├── attendance/         # 출석 도메인
│   └── notification/       # 알림 도메인
│
├── study/                  # Study 서비스
│   ├── boot/               # 애플리케이션 진입점
│   ├── core/               # 스터디 핵심 도메인
│   ├── schedule/           # 일정 도메인
│   ├── review/             # 리뷰 도메인
│   └── todo/               # 할일 도메인
│
├── worker/                 # Worker 서비스
│   └── boot/               # 애플리케이션 진입점
│
├── monitoring/             # 모니터링 설정
│   ├── prometheus/
│   └── grafana/
│
└── docker-compose.yml
```



## Build & Test

```bash
# 빌드
./gradlew build

# 테스트
./gradlew test

# 특정 모듈 테스트
./gradlew :core:member:test

# 린트
./gradlew ktlintCheck detekt

# 린트 자동 수정
./gradlew ktlintFormat
```
