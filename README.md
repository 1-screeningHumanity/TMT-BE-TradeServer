# Trade Server

## 📖 Description
매수 매도 및 예약 매매에 관련된 서비스를 처리 합니다. <br>
현재 보유 자산과, 실시간 주식 정보 서버, 알림 서버와 Messaging Queue로 연동 되어 데이터 동기를 기반하여 작업 처리 됩니다.

## ⚙ Function
1. 회원의 매수/매도 이력 관리
2. 실제 주식 현재가에 의한 매매 체결
3. 변동되는 실시간 주식 데이터를 활용한, 예약 매수/매도 체결
4. 매매 상태에 따른 알림 서버로의 FCM 알림 생성 요청

## 🏴󠁧󠁢󠁥󠁮󠁧󠁿 Running
<p float="left">
    <img src="https://i.ibb.co/TgNhXCD/1.png" width=200 />
    <img src="https://i.ibb.co/TgNhXCD/1.png" width=200 />
    <img src="https://i.ibb.co/TgNhXCD/1.png" width=200 />
    <img src="https://i.ibb.co/zS47pZH/image.png" width=200 />
</p>
   
## 🔧 Stack
 - **Language** : Java 17
 - **Library & Framework** : Spring Boot 3.2.5
 - **Database** : Mysql
 - **ORM** : Hibernate Jpa
 - **Deploy** : AWS EC2 / Jenkins
 - **Dependencies** : Lombok, Springdoc(Swagger), Model Mapper, Kafka, Feign Client

## 🔧 Architecture
- **Design Patter** : Hexa Gonal
- **Micro Service Architecture** : Spring Cloud
- **Event-Driven Architecture** : Kafka

## 👨‍👩‍👧‍👦 Developer
*  **강성욱** ([KangBaekGwa](https://github.com/KangBaekGwa))
*  **김도형** ([ddohyeong](https://github.com/ddohyeong))
*  **박태훈** ([hoontaepark](https://github.com/hoontaepark))
