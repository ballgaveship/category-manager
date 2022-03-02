# category-manager

Category 기본적인 기능을 제공하는 Spring Boot Kotlin 서버입니다.

상위 Category를 이용해 해당 Category의 하위의 모든 Category를 조회합니다.

상위 Category를 지정하지 않을 시, 전체 Category를 조회합니다.

----

## intro

기본 서버 구현 정보

**currently implemented :**

- Category 조회
- Category 등록
- Category 수정
- Category 삭제

----

## API 문서

OpenAPI 3.0 Spec 을 기준하여 작성 된 API 명세서를 볼 수 있습니다.

Request, Response 에 관한 각종 예시와 테스트 할 수 있는 방법을 확인할 수 있습니다.

[API 문서로 이동](https://gaveship.stoplight.io/docs/category/YXBpOjI5NTE5OTg-category-api)

----

## 프로젝트 구조

```bash
├── main
│   ├── application
│   ├── domain
│   └── interfaces
│       └── web
└── test
    ├── application
    ├── domain
    └── interfaces
```
- main
  - application : Category Manager의 유스케이스가 작성된 영역
  - domain : Category의 업무 로직이 수행되는 핵심 도메인 영역
  - interfaces : Category Manager가 외부와의 통신을 담당하는 어댑터 영역
- test
  - application : 유스케이스를 기반으로 한 Feature Test 영역
  - domain : Category Repository 레벨의 Unit Test 영역
  - interfaces : SpringBootTest가 적용하여 Interface를 Test 하는 Integration Test 영역 

----

## 사용법

**docker Image** 또는 **clone the project**를 선택할 수 있습니다.

### **docker image 를 사용하는 경우**

[docker image로 이동](https://hub.docker.com/r/ballgaveship/category-manager)

> docker run -p 8080:8080 ballgaveship/category-manager:2022.0.1

image tag 중 arm64를 선택할 수 있습니다.

> docker run -p 8080:8080 ballgaveship/category-manager:2022.0.1-arm64

##    

### **clone the project 를 사용하는 경우**

binary source를 이용하여 어플리케이션을 빌드 할 수 있습니다.

어플리케이션을 실행 시키기 위하여 Java JDK 11+ 이상의 환경이 필요로 합니다.

> git clone https://github.com/ballgaveship/category-manager.git

gradle project을 실행시키기 위해서는 :

> gradlew :category:bootRun

##  

만약에 kubernetes에 배포를 한다면 :

```
> kubectl apply -f kube-create-app.yaml 
> deployment.apps/category-manager created
> kubectl expose deployment category-manager --port=8080 --name=category-manager
> service/category-manager exposed
```