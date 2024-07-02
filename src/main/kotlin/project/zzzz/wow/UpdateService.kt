package project.zzzz.wow

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import project.zzzz.wow.CompanyRepository

@Service
@Transactional
class UpdateService(
    private val companyRepository: CompanyRepository
) {

    fun update(id: Long, instagramUrl: String?, homePageUrl: String?, youtube: String?, talkTalkUrl: String?) {
        val company = companyRepository.findById(id).get()
        var homepage = homePageUrl
        if (homePageUrl == null) {
            homepage = "x"
        }
        company.update(instagramUrl, homepage, youtube, talkTalkUrl)
    }

}
