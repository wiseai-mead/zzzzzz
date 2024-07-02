package project.zzzz.common.infrastructure

import org.junit.jupiter.api.DisplayNameGeneration
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import project.zzzz.wow.CompanyRepository
import project.zzzz.wow.SvcCompany
import project.zzzz.wow.UpdateService
import java.time.Duration


@DisplayNameGeneration(ReplaceUnderscores::class)
@SpringBootTest
class CrawlerTest @Autowired constructor(
        private val companyRepository: CompanyRepository,
        private val updateService: UpdateService
) {

    /**
     * 1. docker-compose 실행
     * 2. hibernate ddl - auto설정 주의 !! 실수로 table drop 하지 않도록 잘 설정
     *
     */
    @Test
    fun `업데이트하기`() {
        val driver = ChromeDriver()
        val companies = companyRepository.findAll()
        for (svcCompany in companies) {
            updateBlog(driver, svcCompany)
        }
    }

    @Test
    fun `디버그용`() {
        // given
        val company = SvcCompany(
            homepage = null,
            instagram = null,
            youtube = null,
            talkTalk = null,
            name = "리얼라인치과",
            doro = "서울 서대문구 신촌로 73")
        val driver = ChromeDriver()
        // when
        updateBlog(driver, company)
        // then
    }

    private fun updateBlog(driver: ChromeDriver, svcCompany: SvcCompany) {
        driver["https://map.naver.com/v5/search/${svcCompany.doro} ${svcCompany.name}"]

        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500))
        driver.switchTo().defaultContent()
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(1000))
        try {
            driver.switchTo().frame(driver.findElement(By.cssSelector("iframe#entryIframe")));
        } catch (e: Exception) {
            directFalseCase(driver, svcCompany)
            return
        }
        update(driver, svcCompany)
    }

    private fun directFalseCase(driver: ChromeDriver, svcCompany: SvcCompany) {
        try {
            driver.findElement(By.className("link_more")).click()
        } catch (e: Exception) {
            updateService.update(svcCompany.id, null, null, null, null)
            return
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(1000))
        var has = false
        for (findElement in driver.findElements(By.className("search_title"))) {
            if (findElement.text.contains(svcCompany.name)) {
                has = true
                findElement.click()
                driver.manage().timeouts().implicitlyWait(Duration.ofMillis(2000))
                driver.switchTo().frame(driver.findElement(By.cssSelector("iframe#entryIframe")))
                update(driver, svcCompany)
                break
            }
        }
        if (has) {
            return
        }
        updateService.update(svcCompany.id, null, null, null, null)
    }

    private fun update(driver: ChromeDriver, svcCompany: SvcCompany) {
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(2000))
        val talkTalkUrl = getTalkTalkUrl(driver)
        val instagramUrl = getInstagramUrl(driver)
        val homePageUrl = getHomePageUrl(driver)
        val youtubeUrl = getYoutubeUrl(driver)
        updateService.update(svcCompany.id, instagramUrl, homePageUrl, youtubeUrl, talkTalkUrl)
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(1000))
        println("치과 이름 : ${svcCompany.name} \n" +
            "homePageUrl :  $homePageUrl \n" +
            "instagramUrl $instagramUrl \n" +
            "talk-talkUrl $talkTalkUrl")
    }

    private fun getTalkTalkUrl(driver: ChromeDriver): String? {
        for (element in driver.findElements(By.className("gR5KI"))) {
            if (element == null) {
                return null
            }
            for (findElement in element.findElements(By.tagName("span"))) {
                try {
                    val aTag = findElement.findElement(By.tagName("a")) ?: continue
                    val href = aTag.getAttribute("href") ?: continue
                    if (href.contains("talk.naver")) {
                        return href
                    }
                } catch (e : Exception) {
                    continue
                }
            }
        }

        return null
    }

    private fun getYoutubeUrl(driver: ChromeDriver): String? {
        for (findElement in driver.findElements(By.cssSelector(".S8peq a"))) {
            val value = findElement.getAttribute("href")
            if (value != null && value.contains("youtube")) {
                return value
            }

        }

        return null
    }

    private fun getInstagramUrl(driver: ChromeDriver): String? {
        for (findElement in driver.findElements(By.cssSelector(".S8peq a"))) {
            val value = findElement.getAttribute("href")
            if (value != null && value.contains("instagram")) {
                return value
            }

        }

        return null
    }

    private fun getHomePageUrl(driver: ChromeDriver): String? {
        try {
            val findElement = driver.findElement(By.className("jO09N")) ?: return null
            return findElement.text
        } catch (e : Exception) {
            return null
        }
    }
}
