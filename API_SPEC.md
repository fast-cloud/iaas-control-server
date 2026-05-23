# IaaS Bucket API 명세서

Base URL: `http://<host>:8080`

---

## 1. 버킷 생성

**`POST /bucket`**

### Request

- Content-Type: `application/json`

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `name` | string | Y | 생성할 버킷 이름 |

```json
{
  "name": "my-bucket"
}
```

### Response `200 OK`

```json
{
  "code": 20001,
  "message": "버킷이 성공적으로 생성되었습니다.",
  "data": {
    "name": "my-bucket",
    "status": "PENDING",
    "created_at": "2026-05-23T10:30Z"
  }
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `name` | string | 생성된 버킷 이름 |
| `status` | string | 버킷 상태 (`PENDING`) |
| `created_at` | string (ISO 8601) | 생성 시각 |

### Error

| 상황 | HTTP | 설명 |
|------|------|------|
| 이미 존재하는 버킷 이름 | `409 Conflict` | 동일 사용자의 중복 버킷 이름 |
| Swift 연결 실패 | `500 Internal Server Error` | OpenStack Swift 오류 |

---

## 2. 버킷 목록 조회

**`GET /bucket`**

Query Parameter 없이 호출 시 자신의 버킷 전체 목록을 반환합니다.

### Request

없음

### Response `200 OK`

```json
{
  "code": 20002,
  "message": "버킷 목록을 성공적으로 조회했습니다.",
  "data": [
    {
      "bucket_id": "550e8400-e29b-41d4-a716-446655440000",
      "name": "my-bucket",
      "status": "PENDING",
      "created_at": "2026-05-23T10:30Z"
    },
    {
      "bucket_id": "661f9511-f30c-52e5-b827-557766551111",
      "name": "another-bucket",
      "status": "PENDING",
      "created_at": "2026-05-23T11:00Z"
    }
  ]
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `bucket_id` | string (UUID) | 버킷 고유 ID |
| `name` | string | 버킷 이름 |
| `status` | string | 버킷 상태 |
| `created_at` | string (ISO 8601) | 생성 시각 |

> 버킷이 없으면 `data`는 빈 배열 `[]`로 반환됩니다.

---

## 3. 버킷 내 파일 목록 조회

**`GET /bucket?bucket={bucketName}`**

지정한 버킷에 저장된 파일 목록을 Swift에서 실시간으로 조회합니다.

### Request

| Query Parameter | 타입 | 필수 | 설명 |
|-----------------|------|------|------|
| `bucket` | string | Y | 조회할 버킷 이름 |

### Response `200 OK`

```json
{
  "code": 20003,
  "message": "파일 목록을 성공적으로 조회했습니다.",
  "data": {
    "bucket": "my-bucket",
    "objects": [
      {
        "name": "photo.jpg",
        "size": 204800,
        "last_modified": "2026-05-23T09:15Z"
      },
      {
        "name": "document.pdf",
        "size": 51200,
        "last_modified": "2026-05-22T14:00Z"
      }
    ]
  }
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `bucket` | string | 버킷 이름 |
| `objects[].name` | string | 파일 이름 |
| `objects[].size` | number | 파일 크기 (bytes) |
| `objects[].last_modified` | string (ISO 8601) | 마지막 수정 시각 |

### Error

| 상황 | HTTP | 설명 |
|------|------|------|
| 버킷 미존재 또는 소유권 없음 | `404 Not Found` | 해당 사용자의 버킷 없음 |
| Swift 연결 실패 | `500 Internal Server Error` | OpenStack Swift 오류 |

---

## 공통 응답 구조

```json
{
  "code": 20001,
  "message": "...",
  "data": { }
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `code` | number | 응답 코드 |
| `message` | string | 응답 메시지 |
| `data` | object \| array \| null | 응답 데이터 (없으면 생략) |

### 응답 코드 목록

| code | 설명 |
|------|------|
| `20001` | 버킷 생성 성공 |
| `20002` | 버킷 목록 조회 성공 |
| `20003` | 파일 목록 조회 성공 |

---

## 엔드포인트 요약

| Method | Path | Query Param | 설명 |
|--------|------|-------------|------|
| `POST` | `/bucket` | — | 버킷 생성 |
| `GET` | `/bucket` | — | 버킷 목록 조회 |
| `GET` | `/bucket` | `?bucket=name` | 버킷 내 파일 목록 조회 |
