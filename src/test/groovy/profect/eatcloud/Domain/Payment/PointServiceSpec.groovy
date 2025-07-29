package profect.eatcloud.Domain.Payment

import profect.eatcloud.Domain.Payment.Service.PointService
import profect.eatcloud.Domain.Customer.Repository.CustomerRepository
import profect.eatcloud.Domain.Customer.Entity.Customer
import spock.lang.Specification

class PointServiceSpec extends Specification {

    CustomerRepository customerRepository = Mock()
    PointService pointService = new PointService(customerRepository)

    def "포인트가 충분한 고객이 포인트를 사용하면 차감되어야 한다"() {
        given: "5000포인트를 가진 고객이 있을 때"
        UUID customerId = UUID.randomUUID()
        Customer customer = Customer.builder()
                .id(customerId)
                .points(5000)
                .build()

        and: "Repository Mock 설정"
        customerRepository.findById(customerId) >> Optional.of(customer)
        customerRepository.save(_ as Customer) >> { Customer c -> c }

        when: "2000포인트를 사용하면"
        def result = pointService.usePoints(customerId, 2000)

        then: "포인트 사용이 성공해야 한다"
        result.isSuccess()
        result.getUsedPoints() == 2000
        result.getRemainingPoints() == 3000
        customer.getPoints() == 3000
        println "✅ 포인트 사용 성공 테스트 통과"
    }

    def "포인트가 부족한 고객이 포인트를 사용하면 실패해야 한다"() {
        given: "1000포인트를 가진 고객이 있을 때"
        UUID customerId = UUID.randomUUID()
        Customer customer = Customer.builder()
                .id(customerId)
                .points(1000)
                .build()

        and: "Repository Mock 설정"
        customerRepository.findById(customerId) >> Optional.of(customer)

        when: "2000포인트를 사용하려고 하면"
        def result = pointService.usePoints(customerId, 2000)

        then: "포인트 사용이 실패해야 한다"
        !result.isSuccess()
        result.getErrorMessage().contains("포인트가 부족합니다")
        customer.getPoints() == 1000  // 원래 포인트 유지
        println "✅ 포인트 부족 테스트 통과: ${result.getErrorMessage()}"
    }
}