package profect.eatcloud.Global.TimeData

import org.springframework.context.ApplicationContext
import spock.lang.Specification

class BeanUtilSpec extends Specification {

    ApplicationContext applicationContext = Mock()
    BeanUtil beanUtil = new BeanUtil()

    def "ApplicationContext 설정 테스트"() {
        when: "ApplicationContext를 설정하면"
        beanUtil.setApplicationContext(applicationContext)

        then: "정상적으로 설정된다"
        // private static 필드이므로 직접 검증하기 어려움
        // 실제로는 getBean 호출을 통해 간접적으로 검증
        noExceptionThrown()
    }

    def "빈 가져오기 테스트"() {
        given: "ApplicationContext가 설정되고"
        beanUtil.setApplicationContext(applicationContext)

        and: "특정 서비스 빈"
        TimeDataService mockService = Mock(TimeDataService)

        when: "getBean을 호출하면"
        TimeDataService result = BeanUtil.getBean(TimeDataService)

        then: "ApplicationContext에서 빈을 가져온다"
        1 * applicationContext.getBean(TimeDataService) >> mockService

        and: "올바른 빈이 반환된다"
        result == mockService
    }

    def "존재하지 않는 빈 요청 시 예외 전파"() {
        given: "ApplicationContext가 설정되고"
        beanUtil.setApplicationContext(applicationContext)

        when: "존재하지 않는 빈을 요청하면"
        BeanUtil.getBean(String)

        then: "ApplicationContext에서 예외를 던진다"
        1 * applicationContext.getBean(String) >> {
            throw new RuntimeException("No such bean")
        }

        and: "예외가 전파된다"
        thrown(RuntimeException)
    }
}