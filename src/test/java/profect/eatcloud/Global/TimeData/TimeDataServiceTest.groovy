package profect.eatcloud.Global.TimeData

import spock.lang.Specification
import java.time.Instant

class TimeDataServiceSpec extends Specification {

    TimeDataRepository timeDataRepository = Mock()
    TimeDataService timeDataService = new TimeDataService(timeDataRepository)

    def "새 TimeData 생성 테스트"() {
        given: "사용자 정보"
        String createdBy = "testUser"

        and: "저장될 TimeData"
        TimeData savedTimeData = TimeData.builder()
                .pTimeId(UUID.randomUUID())
                .createdAt(Instant.now())
                .createdBy(createdBy)
                .updatedAt(Instant.now())
                .updatedBy(createdBy)
                .build()

        when: "TimeData를 생성하면"
        TimeData result = timeDataService.createTimeData(createdBy)

        then: "리포지토리의 save 메서드가 호출된다"
        1 * timeDataRepository.save(_ as TimeData) >> { TimeData td ->
            // 빌더로 생성된 TimeData의 필드들을 검증
            assert td.createdBy == createdBy
            assert td.updatedBy == createdBy
            assert td.createdAt != null
            assert td.updatedAt != null
            assert td.deletedAt == null
            assert td.deletedBy == null
            return savedTimeData
        }

        and: "올바른 TimeData가 반환된다"
        result == savedTimeData
    }

    def "TimeData 업데이트 테스트"() {
        given: "기존 TimeData와 업데이트 정보"
        UUID pTimeId = UUID.randomUUID()
        String updatedBy = "updater"
        Instant originalTime = Instant.now().minusSeconds(3600)

        TimeData existingTimeData = TimeData.builder()
                .pTimeId(pTimeId)
                .createdAt(originalTime)
                .createdBy("creator")
                .updatedAt(originalTime)
                .updatedBy("creator")
                .build()

        TimeData updatedTimeData = TimeData.builder()
                .pTimeId(pTimeId)
                .createdAt(originalTime)
                .createdBy("creator")
                .updatedAt(Instant.now())
                .updatedBy(updatedBy)
                .build()

        when: "TimeData를 업데이트하면"
        TimeData result = timeDataService.updateTimeData(pTimeId, updatedBy)

        then: "리포지토리에서 기존 데이터를 조회한다"
        1 * timeDataRepository.findById(pTimeId) >> Optional.of(existingTimeData)

        and: "업데이트된 데이터를 저장한다"
        1 * timeDataRepository.save(_ as TimeData) >> { TimeData td ->
            assert td.pTimeId == pTimeId
            assert td.updatedBy == updatedBy
            assert td.updatedAt > originalTime
            return updatedTimeData
        }

        and: "업데이트된 TimeData가 반환된다"
        result == updatedTimeData
    }

    def "존재하지 않는 TimeData 업데이트 시 예외 발생"() {
        given: "존재하지 않는 UUID"
        UUID nonExistentId = UUID.randomUUID()

        when: "존재하지 않는 TimeData를 업데이트하면"
        timeDataService.updateTimeData(nonExistentId, "user")

        then: "리포지토리에서 빈 Optional을 반환한다"
        1 * timeDataRepository.findById(nonExistentId) >> Optional.empty()

        and: "IllegalArgumentException이 발생한다"
        IllegalArgumentException ex = thrown()
        ex.message.contains("TimeData not found")

        and: "save는 호출되지 않는다"
        0 * timeDataRepository.save(_)
    }

    def "Soft Delete 테스트"() {
        given: "기존 TimeData와 삭제 정보"
        UUID pTimeId = UUID.randomUUID()
        String deletedBy = "deleter"

        TimeData existingTimeData = TimeData.builder()
                .pTimeId(pTimeId)
                .createdAt(Instant.now().minusSeconds(3600))
                .createdBy("creator")
                .updatedAt(Instant.now().minusSeconds(1800))
                .updatedBy("updater")
                .build()

        TimeData deletedTimeData = TimeData.builder()
                .pTimeId(pTimeId)
                .createdAt(existingTimeData.createdAt)
                .createdBy(existingTimeData.createdBy)
                .updatedAt(existingTimeData.updatedAt)
                .updatedBy(existingTimeData.updatedBy)
                .deletedAt(Instant.now())
                .deletedBy(deletedBy)
                .build()

        when: "Soft Delete를 수행하면"
        TimeData result = timeDataService.softDeleteTimeData(pTimeId, deletedBy)

        then: "리포지토리에서 기존 데이터를 조회한다"
        1 * timeDataRepository.findById(pTimeId) >> Optional.of(existingTimeData)

        and: "삭제 정보가 설정된 데이터를 저장한다"
        1 * timeDataRepository.save(_ as TimeData) >> { TimeData td ->
            assert td.pTimeId == pTimeId
            assert td.deletedBy == deletedBy
            assert td.deletedAt != null
            return deletedTimeData
        }

        and: "삭제 정보가 설정된 TimeData가 반환된다"
        result == deletedTimeData
    }

    def "존재하지 않는 TimeData Soft Delete 시 예외 발생"() {
        given: "존재하지 않는 UUID"
        UUID nonExistentId = UUID.randomUUID()

        when: "존재하지 않는 TimeData를 삭제하면"
        timeDataService.softDeleteTimeData(nonExistentId, "user")

        then: "리포지토리에서 빈 Optional을 반환한다"
        1 * timeDataRepository.findById(nonExistentId) >> Optional.empty()

        and: "IllegalArgumentException이 발생한다"
        IllegalArgumentException ex = thrown()
        ex.message.contains("TimeData not found")

        and: "save는 호출되지 않는다"
        0 * timeDataRepository.save(_)
    }
}