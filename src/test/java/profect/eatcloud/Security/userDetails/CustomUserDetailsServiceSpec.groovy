package profect.eatcloud.Security.userDetails

import org.springframework.security.core.userdetails.UsernameNotFoundException
import profect.eatcloud.Domain.Admin.Repository.AdminRepository
import profect.eatcloud.Domain.Customer.Repository.CustomerRepository
import profect.eatcloud.Domain.Manager.Repository.ManagerRepository
import profect.eatcloud.Domain.Customer.Entity.Customer
import profect.eatcloud.Domain.Manager.Entity.Manager
import profect.eatcloud.Domain.Admin.Entity.Admin
import spock.lang.Specification

class CustomUserDetailsServiceSpec extends Specification {

    def customerRepository = Mock(CustomerRepository)
    def managerRepository = Mock(ManagerRepository)
    def adminRepository = Mock(AdminRepository)
    def service = new CustomUserDetailsService(customerRepository, managerRepository, adminRepository)

    def "should return Customer when found in customerRepository"() {
        given:
        def customer = Mock(Customer)
        customerRepository.findByUsername("user1") >> Optional.of(customer)

        when:
        def result = service.loadUserByUsername("user1")

        then:
        result == customer
    }

    def "should return Manager when not found in customerRepository but found in managerRepository"() {
        given:
        def manager = Mock(Manager)
        customerRepository.findByUsername("user2") >> Optional.empty()
        managerRepository.findByUsername("user2") >> Optional.of(manager)

        when:
        def result = service.loadUserByUsername("user2")

        then:
        result == manager
    }

    def "should return Admin when not found in customerRepository and managerRepository but found in adminRepository"() {
        given:
        def admin = Mock(Admin)
        customerRepository.findByUsername("user3") >> Optional.empty()
        managerRepository.findByUsername("user3") >> Optional.empty()
        adminRepository.findByUsername("user3") >> Optional.of(admin)

        when:
        def result = service.loadUserByUsername("user3")

        then:
        result == admin
    }

    def "should throw UsernameNotFoundException when user is not found in any repository"() {
        given:
        customerRepository.findByUsername("user4") >> Optional.empty()
        managerRepository.findByUsername("user4") >> Optional.empty()
        adminRepository.findByUsername("user4") >> Optional.empty()

        when:
        service.loadUserByUsername("user4")

        then:
        thrown(UsernameNotFoundException)
    }
}
