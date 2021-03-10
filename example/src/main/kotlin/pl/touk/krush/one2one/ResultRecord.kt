package pl.touk.krush.one2one

import javax.persistence.*

enum class RecordType {
    CALL
}

@Embeddable
data class RecordId(
    @Column(name = "RECORD_ID")
    val id: String,

    @Column(name = "RECORD_TYPE")
    @Enumerated(EnumType.STRING)
    val type: RecordType
)

@Entity
@Table(name = "RESULTS_RECORDS")
data class ResultRecord(
    @Id @GeneratedValue
    var id: Long? = null,

    @Embedded
    val recordId: RecordId,

    @ManyToOne
    @JoinColumn(name = "RUN_ID")
    val summary: RunSummary

)
