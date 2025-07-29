import spock.lang.Specification
import reactor.core.publisher.Mono
import org.springframework.web.reactive.function.client.WebClient
import profect.eatcloud.Domain.Store.Dto.AiDescriptionRequestDto
import profect.eatcloud.Domain.Store.Service.AiDescriptionService

class AiDescriptionServiceSpec extends Specification {

    def webClient = Mock(WebClient)
    def requestBodyUriSpec = Mock(WebClient.RequestBodyUriSpec)
    def requestBodySpec = Mock(WebClient.RequestBodySpec)
    def responseSpec = Mock(WebClient.ResponseSpec)

    def "메뉴 설명 요청 DTO를 기반으로 AI 설명을 생성할 수 있다"() {
        given:
        def dto = new AiDescriptionRequestDto(
                "고추장 삼겹살 덮밥",
                "한식",
                ["삼겹살", "고추장", "양파", "마늘"],
                ["매콤함", "짭짤함"]
        )
        def expected = "매콤한 고추장 양념에 볶아낸 삼겹살이 입맛을 돋우는 한 그릇 요리입니다."

        webClient.post() >> requestBodyUriSpec
        requestBodyUriSpec.uri(_) >> requestBodySpec
        requestBodySpec.bodyValue(_) >> requestBodySpec
        requestBodySpec.retrieve() >> responseSpec

        def mockResponse = [
                candidates: [[
                                     content: [
                                             parts: [[text: expected]]
                                     ]
                             ]]
        ]
        responseSpec.bodyToMono(Map) >> Mono.just(mockResponse)

        def service = new AiDescriptionService(webClient, "fake-api-key")

        when:
        def result = service.generateDescription(dto)

        then:
        result == expected
    }

    def "메뉴 이름이 없으면 예외가 발생한다"() {
        given:
        def dto = new AiDescriptionRequestDto(
                null, "KOREAN", ["소고기", "된장"], ["구수한"]
        )

        def mockWebClient = Mock(WebClient)
        def mockPost = Mock(WebClient.RequestBodyUriSpec)
        def mockSpec = Mock(WebClient.RequestBodySpec)
        def mockRetrieve = Mock(WebClient.ResponseSpec)

        mockWebClient.post() >> mockPost
        mockPost.uri(_) >> mockSpec
        mockSpec.bodyValue(_) >> mockSpec
        mockSpec.retrieve() >> mockRetrieve
        mockRetrieve.bodyToMono(Map) >> Mono.just([:]) // 내용은 중요하지 않음

        def aiDescriptionService = new AiDescriptionService(mockWebClient, "dummy-key")

        when:
        aiDescriptionService.generateDescription(dto)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "메뉴명은 필수입니다"
    }


    def "응답 파싱이 실패하면 RuntimeException이 발생한다"() {
        given:
        def mockDto = new AiDescriptionRequestDto(
                "불완전 응답 유도 메뉴", "KOREAN", ["재료"], ["맛"]
        )

        def mockWebClient = Mock(WebClient)
        def mockRequest = Mock(WebClient.RequestBodyUriSpec)
        def mockSpec = Mock(WebClient.RequestBodySpec)
        def mockRetrieve = Mock(WebClient.ResponseSpec)

        mockWebClient.post() >> mockRequest
        mockRequest.uri(_) >> mockSpec
        mockSpec.bodyValue(_) >> mockSpec
        mockSpec.retrieve() >> mockRetrieve

        // 일부러 candidates를 누락해서 파싱 오류 유도
        def invalidResponse = [:]
        mockRetrieve.bodyToMono(Map) >> Mono.just(invalidResponse)

        def faultyService = new AiDescriptionService(mockWebClient, "dummy")

        when:
        faultyService.generateDescription(mockDto)

        then:
        def e = thrown(RuntimeException)
        e.message.contains("AI 설명 응답 파싱 실패")
    }
}
