# 자바 동시성 제어 방식 및 장단점

## 1. synchronized 키워드
`synchronized` 키워드는 Java에서 기본적으로 제공하는 동기화 메커니즘으로, 특정 코드 블록이나 메서드에 대해 한 번에 하나의 스레드만 접근하도록 보장합니다.

### **동작 원리**
- Java의 모니터(lock) 기반 동기화 방식으로, 하나의 스레드가 `synchronized` 블록이나 메서드에 진입하면 해당 객체의 모니터를 획득하고, 다른 스레드는 대기해야 합니다.
- 메서드 레벨 동기화는 해당 객체 전체에 대한 락을 의미하며, 블록 레벨 동기화는 특정 객체에 대한 락을 의미합니다.
- 스레드가 블록을 빠져나가거나 예외가 발생하면 자동으로 락이 해제됩니다.

### **사용법**
```java
public synchronized void method() {
    // 동기화된 메서드
}

public void method() {
    synchronized (this) {
        // 동기화된 블록
    }
}
```

### **장점**
- 간단한 문법으로 동기화를 쉽게 구현할 수 있습니다.
- JVM 수준에서 관리되므로 별도의 객체를 생성할 필요가 없습니다.

### **단점**
- 성능 저하: synchronized를 사용하면 스레드가 대기해야 하므로 성능이 저하될 수 있습니다.
- 세밀한 제어 어려움: 동기화의 범위를 세밀하게 조정하기 어렵습니다.

---

## 2. Queue와 Concurrent 컬렉션
### **Concurrent 컬렉션**
Java는 `java.util.concurrent` 패키지를 통해 동시성을 고려한 컬렉션을 제공합니다.

### **동작 원리**
- `ConcurrentHashMap`, `ConcurrentLinkedQueue`와 같은 Concurrent 컬렉션은 내부적으로 락을 최소화하면서도 동시성을 보장하도록 설계되어 있습니다.
- `ConcurrentHashMap`의 경우, 특정 버킷(부분적인 락)만을 잠그는 세그먼트 락을 사용하여 성능을 높입니다.
- `ConcurrentLinkedQueue`는 CAS(Compare-And-Swap) 연산을 이용하여 비블로킹 방식으로 동시성을 처리합니다.

### **예제: ConcurrentHashMap**
```java
import java.util.concurrent.ConcurrentHashMap;

ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
map.put("key", 1);
map.compute("key", (k, v) -> v + 1);
```

### **예제: ConcurrentLinkedQueue**
```java
import java.util.concurrent.ConcurrentLinkedQueue;

ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();
queue.offer(1);
queue.poll();
```

### **장점**
- Lock을 최소화하여 성능을 개선할 수 있습니다.
- 여러 스레드가 동시에 접근하더라도 안전하게 동작합니다.

### **단점**
- 일부 연산(예: `size()` 호출)은 정확하지 않을 수 있습니다.
- 구조적인 변경이 필요한 경우 성능이 저하될 수 있습니다.

---

## 3. ReentrantLock
`ReentrantLock`은 `synchronized`보다 세밀한 동기화 제어를 제공하는 클래스입니다.

### **동작 원리**
- 내부적으로 `AbstractQueuedSynchronizer (AQS)`를 사용하여 FIFO 큐 기반의 공정한 락을 제공합니다.
- 재진입이 가능하여 같은 스레드가 여러 번 락을 획득할 수 있으며, 락 획득 횟수만큼 `unlock()`을 호출해야 합니다.
- `tryLock()`을 제공하여 블로킹 없이 락을 시도할 수 있습니다.

### **사용법**
```java
import java.util.concurrent.locks.ReentrantLock;

ReentrantLock lock = new ReentrantLock();

try {
    lock.lock();
    // 동기화된 작업 수행
} finally {
    lock.unlock();
}
```

### **장점**
- `tryLock()` 등의 기능을 통해 비블로킹 락을 구현할 수 있습니다.
- 공정한 락(Fair Lock) 설정이 가능합니다.

### **단점**
- 명시적으로 `unlock()`을 호출해야 하므로 실수로 락을 해제하지 않을 위험이 있습니다.
- synchronized보다 코드가 길어지고 복잡해질 수 있습니다.

---

# 데이터베이스 동시성 제어

## 1. 낙관적 락 (Optimistic Locking)
낙관적 락은 충돌이 적을 것으로 예상되는 경우 사용되며, 주로 `version` 컬럼을 사용하여 변경을 감지합니다.

### **사용법 (JPA 예제)**
```java
@Entity
public class Item {
    @Id @GeneratedValue
    private Long id;
    
    @Version
    private int version;
}
```

### **장점**
- 충돌이 적은 환경에서 성능이 뛰어납니다.
- 락을 유지하지 않으므로 자원 낭비가 적습니다.

### **단점**
- 충돌이 발생하면 롤백해야 하므로 처리 비용이 증가할 수 있습니다.

---

## 2. 비관적 락 (Pessimistic Locking)
비관적 락은 데이터의 충돌이 자주 발생하는 경우 유용하며, `SELECT ... FOR UPDATE`를 사용하여 트랜잭션 동안 데이터를 잠급니다.

### **사용법 (JPA 예제)**
```java
Item item = entityManager.find(Item.class, id, LockModeType.PESSIMISTIC_WRITE);
```

### **장점**
- 충돌 발생 가능성이 높은 환경에서 데이터 정합성을 유지할 수 있습니다.

### **단점**
- 불필요한 락으로 인해 성능이 저하될 수 있습니다.
- 데드락 발생 가능성이 있습니다.

---

# Redis 및 Queue를 이용한 동시성 제어

## 1. Redis 분산 락
Redis의 `SETNX` 또는 Redisson 라이브러리를 사용하여 락을 구현할 수 있습니다.

### **예제 (Redisson 사용)**
```java
import org.redisson.api.RedissonClient;
import org.redisson.api.RLock;

RLock lock = redissonClient.getLock("lockKey");
try {
    lock.lock();
    // 락이 걸린 상태에서 작업 수행
} finally {
    lock.unlock();
}
```

### **장점**
- 분산 환경에서도 동시성 제어 가능합니다.
- 락 만료 시간을 설정하여 데드락 방지 가능합니다.

### **단점**
- 네트워크 지연이 발생할 경우 성능이 저하 됩니다.
- 정확한 TTL 관리가 필요합니다.

---

## 2. 메시지 큐 (Message Queue)
메시지 큐를 사용하여 동시성을 제어하는 방법도 있습니다. 예를 들어, RabbitMQ 또는 Kafka를 사용하여 작업을 순차적으로 처리할 수 있습니다.

### **사용법 (RabbitMQ 예제)**
```java
import org.springframework.amqp.rabbit.core.RabbitTemplate;

rabbitTemplate.convertAndSend("queueName", message);
```

### **장점**
- 트래픽이 높은 환경에서 부하를 분산할 수 있습니다.
- 요청을 병렬로 처리하면서도 동시성을 제어할 수 있습니다.

### **단점**
- 메시지 중복 처리를 위한 추가적인 로직이 필요할 수 있습니다.
- 시스템 설계가 복잡해질 수 있습니다.

---

# 결론
Java에서 동시성을 제어하는 다양한 방법을 제공하며, 사용 목적과 환경에 따라 적절한 방식을 선택해야 합니다.
- `synchronized`와 `ReentrantLock`은 단순한 스레드 동기화에 유용
- `Concurrent` 컬렉션은 성능을 고려한 락이 필요 없는 구조 제공
- DB의 낙관적/비관적 락은 데이터 정합성을 위한 동시성 제어
- Redis 분산 락과 메시지 큐는 분산 시스템에서 동시성을 제어하는데 적합

각 방식의 특성을 고려하여 상황에 맞는 동시성 제어 방법을 선택하는 것이 중요합니다.