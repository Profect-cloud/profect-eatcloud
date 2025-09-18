# 🍽 Eat Cloud Project
Goorm 프로펙트 클라우드 엔지니어링 과정 3기 – 1차 프로젝트  

## 📌 프로젝트 소개
**Eat Cloud**는 ‘배달의 민족’을 벤치마킹한 **주문 관리 플랫폼**입니다.  
기존 오프라인 음식 주문 과정을 온라인으로 전환하여 주문·결제·배달 관리의 자동화를 목표로 **모놀리식 애플리케이션**을 개발했습니다.

## 📆 개발 기간
- 25.07.21 ~ 25.08.06

## 👥 멤버 구성
- 오해인 [@badarang]
- 강능요 [@teadmu]
- 정연주 [@racoi]
- 정민영 [@minmaker-komu]
- 홍성문 [@HHsungmoon]

## 🛠 기술 스택
`Java` `Spring Boot` `Spring Security` `PostgreSQL` `PostGIS` `Redis` `QueryDSL`

## ✨ 주요 기능
- Spring Security + JWT 기반 사용자 인증/인가, 회원가입 시 이메일 인증 기능
- 거리 기반 매장/메뉴 카테고리 별 매장 조회
- AI 기반 메뉴 설명 자동 생성 기능
- 토스 API 연동 결제 시스템
- JPA Entity Listener를 활용한 생성, 수정 시간 자동 관리
- 공통 응답 구조 및 예외 처리

## 🏗 아키텍처
```
profect-eatcloud/
  src/
    main/
      java/
        profect/
          eatcloud/
            common/                  - 공통 응답/예외/유틸
            config/                  - 전역 설정(Async, QueryDSL, Redis 등)
            domain/                  
              admin/                 - 관리자/카테고리/매장 관리
              customer/              - 고객/주소/장바구니/주문 요청
              globalCategory/        - 공통 카테고리·주문상태 코드
              manager/               - 점주(매니저) 계정 및 매장 신청
              order/                 - 주문/픽업·배달/리뷰
              payment/               - 결제/결제요청/콜백 처리
              store/                 - 매장/메뉴/매출/AI 설명
            global/                  - 공용 인프라(queryDSL, timeData)
            login/                   - 로그인/인증
            security/                - Spring Security/JWT/필터
            EatcloudApplication.java - 애플리케이션 진입점
```
