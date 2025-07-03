# 보안서비스개발팀 서버 과제
## 지원자 정보
서버 (Backend) Developer 지원자 이해원


## tech stack
-   kotlin : 1.9.25
-   java : 21 (21.0.6 활용)
-   spring boot : 3.5.3
-   gradle : 8.13
-   docker-compose
    - mysql:8.0.42
    - redis:7
    - rabbitmq:3-management

(test)
-   mockito
-   junit 5
- mockwebserver
- okhttp
- testcontainer
- awaitility

그 외 라이브러리

- spring-security
- jpa
- spring-validation
- ktlint
- redisson
-   com.googlecode.libphonenumber : 휴대전화번호 포맷 검증을 위해 활용
-   springdoc-openapi-starter-webmvc-ui : swagger ui 를 사용하기 위함


## 디렉토리 구조

```
autoever-security
├─ README.md
├─ docker-compose.yml
├─ ...
└─ src
   ├─ main
   │  ├─ kotlin
   │  │  └─ com
   │  │     └─ task
   │  │        └─ autoeversecurity
   │  │           ├─ AutoeverSecurityApplication.kt
   │  │           ├─ aspect
   │  │           ├─ client
   │  │           ├─ component
   │  │           ├─ config
   │  │           ├─ controller
   │  │           ├─ domain
   │  │           │  ├─ embeddable
   │  │           │  └─ entity
   │  │           ├─ dto
   │  │           │  ├─ api
   │  │           │  └─ message
   │  │           ├─ exception
   │  │           ├─ property
   │  │           ├─ repository
   │  │           │  └─ redis
   │  │           ├─ service
   │  │           └─ util
   │  └─ resources
   │     ├─ application-prod.yml
   │     ├─ application.yml
   │     └─ logback-spring.xml
   └─ test
      ├─ kotlin
      │  └─ com
      │     └─ task
      │        └─ autoeversecurity
      │           ├─ component
      │           ├─ config
      │           ├─ controller
      │           ├─ repository
      │           ├─ service
      │           └─ util
      └─ resources
         └─ application.yml

```

#### 1 depth

-   client : 외부 api client 의 로직이 담겨 있음
-  component: mq, redis 같은 biz or encoder 등 util 성 bean
-   config : configuration 등 설정 관련 bean
-   controller : 컨트롤러 관련
-   domain : entity 에 활용하는 클래스
-   dto : api req & res 또는 내부에서 횔용하는 객체를 정의
-   exception : 핸들링을 하거나 커스터마이징한 exception 클래스
-   property : application 설정이 담긴 bean
-   repository : DB or redis connection 하는 layer
-   service : 서비스 관련
-   util : 확장 함수, constant 등 유틸성
-

## API 명세서
서버를 띄우면 swagger UI 에 접근 가능합니다.
http://localhost:8080/v1/swagger-ui/index.html


## 체크 포인트
- redisson 을 활용해서 redis cache 분산 락으로 분당 횟수 limit 제어
- rabbitMQ 를 활용해 메세지 큐 방식으로 카카오톡 메세지 & SMS 발송을 non-blocking 으로 스트리밍하게 대응
- password 는 BCrypt 로 일방향 복호화
- rrn, phoneNumber 는 AES256 알고리즘 + iv 방식을 사용해 대칭키 암호화
- admin 계정은 redis 로 저장
    - initialize 시 redis 에 admin / 1212 정보 set
    - 비밀번호는 BCrypt
- user 계정은 RDBMS 에 저장
- Basic Auth 방식으로 admin / user 권한 분리
    - admin Authority 는 /admin/* API 만 호출 가능
    - user Authority 는 /user/* API 만 호출 가능
- rrn 을 가지고 age 를 얻어내 저장. 그 후 연령대 별 카카오톡 메세지 발송 시 해당 age 를 가지고 연령대에 속한 사용자들을 조회할 수 있다.
-



## Local 환경 테스트

docker compose 명령어

```
docker compose up -d
```