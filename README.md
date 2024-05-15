## 실행 방법
- 데이터베이스 설정
  - Local MySql에 notice_management 데이터베이스 생성
  - application.yml의 spring.jpa.hibernate.ddl-auto: create로 변경
  - 루트 폴더에 .env 파일 생성 후 LOCAL_MYSQL_PORT, LOCAL_MYSQL_USERNAME, LOCAL_MYSQL_PASSWORD 입력 하기
- 파일 저장 설정
  - application.yml의 spring.file.save.path: 로컬에 저장할 파일 위치 설정
- 레디스 설정
  - 루트 폴더에 생성한 .env 파일에 LOCAL_REDIS_PORT 입력 후 레디스 로컬에서 실행

## 문제 해결
- 파일 저장
  - MultiPartFile 인터페이스를 이용해 저장할 수 있었습니다.
  - 파일 저장 시 디렉토리 명을 공지사항 제목으로 설정하여 파일명이 겹쳐 섞이는 오류가 발생하지 않도록 방지하였습니다.
  - Local 폴더에 저장되도록 설정하였습니다.

- 코드, 쿼리 최적화
  - N + 1 문제를 해결하기 위해 기존에 SpringDataJpa에서 기본으로 제공하는 메서드를 사용하지 않고 fetch join을 이용해 쿼리문이 발생하는 횟수를 줄였습니다.
  - 공지 사항 전체 조회, 단건 조회에서 기존에는 공지사항 조회, 첨부 파일 조회가 따로 일어났지만 한번에 처리되도록 변경했습니다.
  ```java
    @Query("select n from Notice n join fetch n.member")
    Page<Notice> findAll(Pageable pageable);
    
    @Query("select n from Notice n join fetch n.fileList where n.id = :id")
    Optional<Notice> findByIdWithFile(Long id);
  ```
  
  - 공지사항 단건 조회 시 JPQL 프로젝션을 통해 바로 responseDto를 이용해 조회해서 불필요한 영속화를 없앴습니다.
  - 공지사항의 title 컬럼을 통한 조회가 많이 일어나 인덱스를 걸어 성능을 향상시켰습니다.

- 아키텍처 변경으로 성능 향상
  - 조회 성능을 향상시키기 위해 캐시 메모리로 Redis를 이용했습니다.
  - 공지사항 단건 조회 시 Redis에 캐싱해 수정, 삭제가 일어나기 전까지 Redis에서 조회 하도록 해서 데이터베이스 부하를 줄였습니다.
  - 조회수를 Redis에 저장시켜 공지사항 조회 마다 발생하는 조회수 update 쿼리를 제거했습니다. 
