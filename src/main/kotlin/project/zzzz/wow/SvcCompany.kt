package project.zzzz.wow

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class SvcCompany(
    var homepage: String?,
    var instagram: String?,
    var youtube: String?,
    var talkTalk: String?,
    val name: String,
    val doro: String,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
) {

    fun update(instagram: String?, homepage: String?, youtube: String?, talkTalk: String?) {
        this.instagram = instagram
        this.homepage = homepage
        this.youtube = youtube
        this.talkTalk = talkTalk

    }
}
