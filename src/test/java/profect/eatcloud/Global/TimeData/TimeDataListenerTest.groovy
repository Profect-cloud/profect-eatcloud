package profect.eatcloud.Global.TimeData

import spock.lang.Specification
import java.lang.reflect.Field
import java.time.Instant
import java.util.UUID
import org.springframework.context.ApplicationContext

class TimeDataListenerSpec extends Specification {

    TimeDataListener listener = new TimeDataListener()
    TimeDataService timeDataService = Mock()
    BaseTimeEntity entity = Mock()
    ApplicationContext applicationContext = Mock()

    def setup() {
        // BeanUtil의 static context 필드에 Mock ApplicationContext 주입
        Field contextField = BeanUtil.getDeclaredField("context")
        contextField.setAccessible(true)
        contextField.set(null, applicationContext)

        // ApplicationContext가 TimeDataService를 반환하도록 설정
        applicationContext.getBean(TimeDataService.class) >> timeDataService
    }

    def cleanup() {
        // 테스트 후 context를 null로 되돌림
        Field contextField = BeanUtil.getDeclaredField("context")
        contextField.setAccessible(true)
        contextField.set(null, null)
    }

    def "prePersist 테스트 - 새 엔티티 저장 시 TimeData 생성"() {
        given: "새로 생성될 TimeData"
        TimeData newTimeData = TimeData.builder()
                .pTimeId(UUID.randomUUID())
                .createdBy("SYSTEM")
                .updatedBy("SYSTEM")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build()

        when: "prePersist가 호출되면"
        listener.prePersist(entity)

        then: "TimeDataService를 통해 새 TimeData를 생성한다"
        1 * timeDataService.createTimeData("SYSTEM") >> newTimeData

        and: "엔티티에 TimeData를 설정한다"
        1 * entity.setTimeData(newTimeData)
    }

    def "preUpdate 테스트 - 엔티티 업데이트 시 TimeData 업데이트"() {
        given: "기존 TimeData"
        UUID existingTimeId = UUID.randomUUID()

        TimeData existingTimeData = TimeData.builder()
                .pTimeId(existingTimeId)
                .createdBy("creator")
                .updatedBy("creator")
                .build()

        TimeData updatedTimeData = TimeData.builder()
                .pTimeId(existingTimeId)
                .createdBy("creator")
                .updatedBy("SYSTEM")
                .build()

        when: "preUpdate가 호출되면"
        listener.preUpdate(entity)

        then: "엔티티에서 기존 TimeData를 가져온다"
        1 * entity.getTimeData() >> existingTimeData

        and: "TimeDataService를 통해 TimeData를 업데이트한다"
        1 * timeDataService.updateTimeData(existingTimeId, "SYSTEM") >> updatedTimeData

        and: "엔티티에 업데이트된 TimeData를 설정한다"
        1 * entity.setTimeData(updatedTimeData)
    }

    def "SecurityUtil이 인증된 사용자 정보를 반환하는 경우 테스트"() {
        given: "인증된 사용자가 있고"
        String authenticatedUser = "john.doe"

        and: "SecurityContext에 인증 정보 설정"
        def auth = Mock(org.springframework.security.core.Authentication)
        auth.isAuthenticated() >> true
        auth.getName() >> authenticatedUser
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth)

        and: "새로 생성될 TimeData"
        TimeData newTimeData = TimeData.builder()
                .pTimeId(UUID.randomUUID())
                .createdBy(authenticatedUser)
                .updatedBy(authenticatedUser)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build()

        when: "prePersist가 호출되면"
        listener.prePersist(entity)

        then: "인증된 사용자로 TimeData를 생성한다"
        1 * timeDataService.createTimeData(authenticatedUser) >> newTimeData
        1 * entity.setTimeData(newTimeData)

        cleanup:
        // SecurityContext 정리
        org.springframework.security.core.context.SecurityContextHolder.clearContext()
    }

    def "인증 정보가 없는 경우 SYSTEM 사용자로 처리"() {
        given: "SecurityContext가 비어있고"
        org.springframework.security.core.context.SecurityContextHolder.clearContext()

        and: "새로 생성될 TimeData"
        TimeData newTimeData = TimeData.builder()
                .pTimeId(UUID.randomUUID())
                .createdBy("SYSTEM")
                .updatedBy("SYSTEM")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build()

        when: "prePersist가 호출되면"
        listener.prePersist(entity)

        then: "SYSTEM 사용자로 TimeData를 생성한다"
        1 * timeDataService.createTimeData("SYSTEM") >> newTimeData
        1 * entity.setTimeData(newTimeData)
    }

    def "익명 사용자인 경우 SYSTEM 사용자로 처리"() {
        given: "익명 인증 토큰이 설정되고"
        def anonymousAuth = Mock(org.springframework.security.authentication.AnonymousAuthenticationToken)
        anonymousAuth.isAuthenticated() >> true
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(anonymousAuth)

        and: "새로 생성될 TimeData"
        TimeData newTimeData = TimeData.builder()
                .pTimeId(UUID.randomUUID())
                .createdBy("SYSTEM")
                .updatedBy("SYSTEM")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build()

        when: "prePersist가 호출되면"
        listener.prePersist(entity)

        then: "SYSTEM 사용자로 TimeData를 생성한다"
        1 * timeDataService.createTimeData("SYSTEM") >> newTimeData
        1 * entity.setTimeData(newTimeData)

        cleanup:
        org.springframework.security.core.context.SecurityContextHolder.clearContext()
    }
}